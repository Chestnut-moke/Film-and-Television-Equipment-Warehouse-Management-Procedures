package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Equipment
import com.example.data.EquipmentRepository
import com.example.data.RentalRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WarehouseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EquipmentRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EquipmentRepository(database.equipmentDao())
        
        // Populate database with mock filmming gear templates on background thread
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // UI state filters
    val searchQuery = MutableStateFlow("")
    val filterCategory = MutableStateFlow("全部")
    val filterStatus = MutableStateFlow("全部") // "全部", "在库", "已借出"

    // Raw streams from DB
    val allEquipment: StateFlow<List<Equipment>> = repository.allEquipment
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRentalRecords: StateFlow<List<RentalRecord>> = repository.allRentalRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered Equipment Stream
    val filteredEquipment: StateFlow<List<Equipment>> = combine(
        allEquipment,
        searchQuery,
        filterCategory,
        filterStatus
    ) { list, query, category, status ->
        list.filter { item ->
            val matchQuery = item.name.contains(query, ignoreCase = true) ||
                    item.model.contains(query, ignoreCase = true) ||
                    item.barcode.contains(query, ignoreCase = true)
            
            val matchCategory = category == "全部" || item.category == category
            
            val matchStatus = status == "全部" || when (status) {
                "在库" -> item.status == Equipment.STATUS_AVAILABLE
                "已借出" -> item.status == Equipment.STATUS_RENTED
                else -> true
            }
            
            matchQuery && matchCategory && matchStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Rental Statistics (Frequencies and Cumulative Durations)
    // Map of equipmentId -> Total count of rentals
    val equipmentUsageCount: StateFlow<Map<Int, Int>> = allRentalRecords
        .combine(allEquipment) { records, _ ->
            records.groupBy { it.equipmentId }
                .mapValues { (_, list) -> list.size }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Map of equipmentId -> Total duration in milliseconds (for completed and active ones)
    val equipmentUsageDuration: StateFlow<Map<Int, Long>> = allRentalRecords
        .combine(allEquipment) { records, _ ->
            records.groupBy { it.equipmentId }
                .mapValues { (_, list) ->
                    list.sumOf { record ->
                        val end = record.returnDate ?: System.currentTimeMillis()
                        val diff = end - record.startDate
                        if (diff > 0) diff else 0L
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Scanner States
    sealed interface ScanResultState {
        object Idle : ScanResultState
        data class Success(val equipment: Equipment, val type: String) : ScanResultState // type: "BORROW" or "RETURN"
        data class Error(val message: String) : ScanResultState
    }

    val scanResult = MutableStateFlow<ScanResultState>(ScanResultState.Idle)

    // Business Action Functions
    fun addEquipment(name: String, model: String, category: String, barcode: String, notes: String) {
        viewModelScope.launch {
            val emptyBarcode = barcode.ifBlank { "BARCODE-${System.currentTimeMillis().toString().takeLast(6)}" }
            val newEquip = Equipment(
                name = name,
                model = model,
                category = category,
                barcode = emptyBarcode,
                notes = notes
            )
            repository.insertEquipment(newEquip)
        }
    }

    fun updateEquipmentNotes(equipment: Equipment, newNotes: String) {
        viewModelScope.launch {
            repository.updateEquipment(equipment.copy(notes = newNotes))
        }
    }

    fun deleteEquipment(equipment: Equipment) {
        viewModelScope.launch {
            repository.deleteEquipment(equipment)
        }
    }

    fun checkoutEquipment(equipmentId: Int, renter: String, days: Int) {
        viewModelScope.launch {
            val startDate = System.currentTimeMillis()
            val endDate = startDate + (days * 24L * 60 * 60 * 1000)
            repository.checkoutEquipment(equipmentId, renter, startDate, endDate)
        }
    }

    fun returnEquipment(equipmentId: Int) {
        viewModelScope.launch {
            repository.returnEquipment(equipmentId, System.currentTimeMillis())
        }
    }

    fun extendLease(equipmentId: Int, extraDays: Int) {
        viewModelScope.launch {
            repository.extendLease(equipmentId, extraDays)
        }
    }

    // Process a scanned code
    fun processBarcodeScan(scannedCode: String) {
        viewModelScope.launch {
            val item = repository.getEquipmentByBarcode(scannedCode)
            if (item == null) {
                scanResult.value = ScanResultState.Error("未找到编号为 [${scannedCode}] 的影视设备，请检查条码或先向仓库登记。")
            } else {
                if (item.status == Equipment.STATUS_AVAILABLE) {
                    scanResult.value = ScanResultState.Success(item, "BORROW")
                } else {
                    scanResult.value = ScanResultState.Success(item, "RETURN")
                }
            }
        }
    }

    fun resetScanResult() {
        scanResult.value = ScanResultState.Idle
    }
}

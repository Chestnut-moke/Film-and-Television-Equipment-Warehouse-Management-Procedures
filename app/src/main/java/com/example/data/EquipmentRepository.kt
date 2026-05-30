package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EquipmentRepository(private val equipmentDao: EquipmentDao) {

    val allEquipment: Flow<List<Equipment>> = equipmentDao.getAllEquipment()
    val allRentalRecords: Flow<List<RentalRecord>> = equipmentDao.getAllRentalRecords()

    fun getRecordsForEquipment(equipmentId: Int): Flow<List<RentalRecord>> {
        return equipmentDao.getRecordsForEquipment(equipmentId)
    }

    suspend fun getEquipmentById(id: Int): Equipment? {
        return equipmentDao.getEquipmentById(id)
    }

    suspend fun getEquipmentByBarcode(barcode: String): Equipment? {
        return equipmentDao.getEquipmentByBarcode(barcode)
    }

    suspend fun insertEquipment(equipment: Equipment): Long {
        return equipmentDao.insertEquipment(equipment)
    }

    suspend fun updateEquipment(equipment: Equipment) {
        equipmentDao.updateEquipment(equipment)
    }

    suspend fun deleteEquipment(equipment: Equipment) {
        equipmentDao.deleteEquipment(equipment)
    }

    /**
     * Completes a check-out transaction in database.
     */
    suspend fun checkoutEquipment(
        equipmentId: Int,
        renter: String,
        startDate: Long,
        endDate: Long
    ): Boolean {
        val equipment = equipmentDao.getEquipmentById(equipmentId) ?: return false
        if (equipment.status == Equipment.STATUS_RENTED) return false

        // 1. Update equipment status to RENTED
        val updatedEquipment = equipment.copy(
            status = Equipment.STATUS_RENTED,
            currentRenter = renter,
            rentStartDate = startDate,
            rentEndDate = endDate
        )
        equipmentDao.updateEquipment(updatedEquipment)

        // 2. Create rental history record
        val record = RentalRecord(
            equipmentId = equipment.id,
            equipmentName = equipment.name,
            equipmentModel = equipment.model,
            equipmentCategory = equipment.category,
            renter = renter,
            startDate = startDate,
            plannedEndDate = endDate,
            status = RentalRecord.STATUS_RENTED
        )
        equipmentDao.insertRentalRecord(record)
        return true
    }

    /**
     * Completes a return transaction in database.
     */
    suspend fun returnEquipment(
        equipmentId: Int,
        returnDate: Long
    ): Boolean {
        val equipment = equipmentDao.getEquipmentById(equipmentId) ?: return false
        if (equipment.status == Equipment.STATUS_AVAILABLE) return false

        // 1. Update equipment status to AVAILABLE
        val updatedEquipment = equipment.copy(
            status = Equipment.STATUS_AVAILABLE,
            currentRenter = null,
            rentStartDate = null,
            rentEndDate = null
        )
        equipmentDao.updateEquipment(updatedEquipment)

        // 2. Look up the active log and update return date
        val activeRecord = equipmentDao.getActiveRecordForEquipment(equipmentId)
        if (activeRecord != null) {
            val updatedRecord = activeRecord.copy(
                returnDate = returnDate,
                status = RentalRecord.STATUS_RETURNED
            )
            equipmentDao.updateRentalRecord(updatedRecord)
        }
        return true
    }

    /**
     * Extends the lease of an equipment by a specified number of days.
     */
    suspend fun extendLease(
        equipmentId: Int,
        extraDays: Int
    ): Boolean {
        val equipment = equipmentDao.getEquipmentById(equipmentId) ?: return false
        if (equipment.status != Equipment.STATUS_RENTED) return false

        val currentEndDate = equipment.rentEndDate ?: System.currentTimeMillis()
        val newEndDate = currentEndDate + (extraDays * 24L * 60 * 60 * 1000)

        // 1. Update equipment end date
        val updatedEquipment = equipment.copy(
            rentEndDate = newEndDate
        )
        equipmentDao.updateEquipment(updatedEquipment)

        // 2. Look up the active log and update planned end date
        val activeRecord = equipmentDao.getActiveRecordForEquipment(equipmentId)
        if (activeRecord != null) {
            val updatedRecord = activeRecord.copy(
                plannedEndDate = newEndDate
            )
            equipmentDao.updateRentalRecord(updatedRecord)
        }
        return true
    }

    /**
     * Quick checkout by Barcode
     */
    suspend fun checkoutByBarcode(
        barcode: String,
        renter: String,
        startDate: Long,
        endDate: Long
    ): Int {
        // Return codes:
        // 1 -> Success
        // 0 -> Not found
        // -1 -> Already rented
        val equipment = equipmentDao.getEquipmentByBarcode(barcode) ?: return 0
        if (equipment.status == Equipment.STATUS_RENTED) return -1
        
        val success = checkoutEquipment(equipment.id, renter, startDate, endDate)
        return if (success) 1 else 0
    }

    /**
     * Quick return by Barcode
     */
    suspend fun returnByBarcode(
        barcode: String,
        returnDate: Long
    ): Int {
        // Return codes:
        // 1 -> Success
        // 0 -> Not found
        // -1 -> Already in stock (not rented)
        val equipment = equipmentDao.getEquipmentByBarcode(barcode) ?: return 0
        if (equipment.status == Equipment.STATUS_AVAILABLE) return -1
        
        val success = returnEquipment(equipment.id, returnDate)
        return if (success) 1 else 0
    }

    /**
     * Populate standard filmmaking device templates if DB is currently empty.
     */
    suspend fun prepopulateIfEmpty() {
        val currentList = equipmentDao.getAllEquipment().first()
        if (currentList.isEmpty()) {
            val templates = listOf(
                Equipment(
                    name = "Sony FX3 摄影机",
                    model = "ILME-FX3",
                    category = "相机/摄影机",
                    barcode = "BARCODE-FX3-001",
                    notes = "全画幅电影摄影机，配备双原生感光度，手柄 XLR 录音套件"
                ),
                Equipment(
                    name = "RED Komodo 6K 摄影机",
                    model = "Komodo 6K",
                    category = "相机/摄影机",
                    barcode = "BARCODE-KOMODO-002",
                    notes = "全局快门电影摄影机，RF卡口，使用BP锂电池"
                ),
                Equipment(
                    name = "Sony SEL2470GM2 镜头",
                    model = "FE 24-70mm F2.8 GM II",
                    category = "大镜头",
                    barcode = "BARCODE-2470GM2-003",
                    notes = "第二代大师级标准变焦镜头，备极佳分辨率，滤镜尺寸 82mm"
                ),
                Equipment(
                    name = "DJI Ronin RS 3 Pro 稳定器",
                    model = "RS 3 Pro",
                    category = "脚架/航拍",
                    barcode = "BARCODE-RS3PRO-004",
                    notes = "三轴稳定系统，支持LiDAR激光跟焦，负重高达4.5kg"
                ),
                Equipment(
                    name = "Aputure LS 300d II 影视灯",
                    model = "Light Storm 300d II",
                    category = "灯光设备",
                    barcode = "BARCODE-LS300D-005",
                    notes = "大功率COB金盏花灯，保荣卡口，5500K色温"
                ),
                Equipment(
                    name = "Sennheiser MKE600 枪麦",
                    model = "MKE 600",
                    category = "音频组件",
                    barcode = "BARCODE-MKE600-006",
                    notes = "高指向性电容式话筒，带超指向录音特性，配热靴卡扣"
                ),
                Equipment(
                    name = "Mavic 3 Cine 航拍无人机",
                    model = "Mavic 3 Pro Cine",
                    category = "脚架/航拍",
                    barcode = "BARCODE-MAVIC3-007",
                    notes = "哈苏三摄系统，支持Apple ProRes 422 HQ录制，自带RC Pro带屏遥控器"
                ),
                Equipment(
                    name = "Atomos Ninja V 监视器",
                    model = "Ninja V",
                    category = "监视器",
                    barcode = "BARCODE-NINJAV-008",
                    notes = "5英寸高亮录制监视器，支持ProRes RAW外录"
                )
            )
            for (item in templates) {
                equipmentDao.insertEquipment(item)
            }
        }
    }
}

package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    // Equipment queries
    @Query("SELECT * FROM equipment ORDER BY createdAt DESC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    suspend fun getEquipmentById(id: Int): Equipment?

    @Query("SELECT * FROM equipment WHERE barcode = :barcode LIMIT 1")
    suspend fun getEquipmentByBarcode(barcode: String): Equipment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: Equipment): Long

    @Update
    suspend fun updateEquipment(equipment: Equipment)

    @Delete
    suspend fun deleteEquipment(equipment: Equipment)

    // Rental Record queries
    @Query("SELECT * FROM rental_records ORDER BY startDate DESC")
    fun getAllRentalRecords(): Flow<List<RentalRecord>>

    @Query("SELECT * FROM rental_records WHERE equipmentId = :equipmentId ORDER BY startDate DESC")
    fun getRecordsForEquipment(equipmentId: Int): Flow<List<RentalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentalRecord(record: RentalRecord): Long

    @Update
    suspend fun updateRentalRecord(record: RentalRecord)

    @Query("SELECT * FROM rental_records WHERE equipmentId = :equipmentId AND status = 'RENTED' LIMIT 1")
    suspend fun getActiveRecordForEquipment(equipmentId: Int): RentalRecord?
}

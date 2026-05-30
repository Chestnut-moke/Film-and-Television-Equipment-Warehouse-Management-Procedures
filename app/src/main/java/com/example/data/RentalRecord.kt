package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rental_records")
data class RentalRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipmentId: Int,
    val equipmentName: String,
    val equipmentModel: String,
    val equipmentCategory: String,
    val renter: String,
    val startDate: Long,
    val plannedEndDate: Long,
    val returnDate: Long? = null, // null if not yet returned
    val status: String = STATUS_RENTED // "RENTED" or "RETURNED"
) {
    companion object {
        const val STATUS_RENTED = "RENTED"
        const val STATUS_RETURNED = "RETURNED"
    }
}

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val model: String,
    val category: String, // E.g., "相机/摄影机", "大镜头", "监视器", "灯光设备", "音频组件", "脚架/航拍"
    val barcode: String, // 扫码用的一维码/二维码数据
    val status: String = STATUS_AVAILABLE, // "AVAILABLE" or "RENTED"
    val currentRenter: String? = null,
    val rentStartDate: Long? = null,
    val rentEndDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    companion object {
        const val STATUS_AVAILABLE = "AVAILABLE"
        const val STATUS_RENTED = "RENTED"
    }
}

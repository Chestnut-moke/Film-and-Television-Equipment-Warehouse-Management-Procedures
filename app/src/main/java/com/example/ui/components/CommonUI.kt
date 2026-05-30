package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActiveMintAccent
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.PaleForestBg
import com.example.ui.theme.PureCleanWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Time utilities in UI components
object TimeFormatter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)
    private val shortDateFormat = SimpleDateFormat("MM-dd", Locale.SIMPLIFIED_CHINESE)
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE)

    fun formatDateTime(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortDateFormat.format(Date(timestamp))
    }

    fun formatDay(timestamp: Long): String {
        return dayFormat.format(Date(timestamp))
    }

    fun formatDurationDays(millis: Long): String {
        val days = millis / (24.0 * 60 * 60 * 1000)
        return if (days < 0.1) {
            val hours = millis / (60 * 60 * 1000)
            if (hours < 1) "小于 1 小时" else "${hours}小时"
        } else {
            String.format(Locale.US, "%.1f 天", days)
        }
    }
}

// Category icons provider
@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "相机/摄影机" -> Icons.Default.Videocam
        "大镜头" -> Icons.Default.Camera
        "监视器" -> Icons.Default.Tv
        "灯光设备" -> Icons.Default.FlashOn
        "音频组件" -> Icons.Default.Mic
        "脚架/航拍" -> Icons.Default.Flight
        else -> Icons.Default.Devices
    }
}

@Composable
fun getCategoryColor(category: String): Color {
    // Elegant Muted Notion colors
    return when (category) {
        "相机/摄影机" -> Color(0xFFE3F2FD) // light blue
        "大镜头" -> Color(0xFFEDE7F6) // light purple
        "监视器" -> Color(0xFFFFF3E0) // light orange
        "灯光设备" -> Color(0xFFFFFDE7) // light yellow
        "音频组件" -> Color(0xFFF1F8E9) // light green
        "脚架/航拍" -> Color(0xFFE0F2F1) // light teal
        else -> PaleForestBg
    }
}

@Composable
fun getCategoryOnColor(category: String): Color {
    return when (category) {
        "相机/摄影机" -> Color(0xFF1565C0)
        "大镜头" -> Color(0xFF6A1B9A)
        "监视器" -> Color(0xFFE65100)
        "灯光设备" -> Color(0xFFF57F17)
        "音频组件" -> Color(0xFF33691E)
        "脚架/航拍" -> Color(0xFF004D40)
        else -> DeepForestGreen
    }
}

@Composable
fun CategoryBadge(category: String, modifier: Modifier = Modifier) {
    val bgColor = getCategoryColor(category)
    val textColor = getCategoryOnColor(category)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = category,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// Custom status badge
@Composable
fun StatusBadge(status: String) {
    val (text, bgColor, textColor) = if (status == "AVAILABLE") {
        Triple("• 在库存", Color(0xFFEAF5EE), Color(0xFF2D6A4F))
    } else {
        Triple("• 借出中", Color(0xFFFEF2F2), Color(0xFFDC2626))
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

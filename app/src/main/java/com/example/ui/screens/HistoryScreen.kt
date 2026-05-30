package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RentalRecord
import com.example.ui.WarehouseViewModel
import com.example.ui.components.TimeFormatter
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: WarehouseViewModel,
    modifier: Modifier = Modifier
) {
    val allRecords by viewModel.allRentalRecords.collectAsStateWithLifecycle()
    val allEquipment by viewModel.allEquipment.collectAsStateWithLifecycle()
    
    val usageCountMap by viewModel.equipmentUsageCount.collectAsStateWithLifecycle()
    val usageDurationMap by viewModel.equipmentUsageDuration.collectAsStateWithLifecycle()

    var activeHistoryViewTab by remember { mutableIntStateOf(0) } // 0: 借还流水, 1: 使用排行榜

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSandBackground)
            .padding(16.dp)
    ) {
        // Notion Style Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "历史台账管理",
                style = MaterialTheme.typography.titleLarge,
                color = DeepForestGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // View Mode Selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaleForestBg)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeHistoryViewTab == 0) SoftForestGreen else Color.Transparent)
                        .clickable { activeHistoryViewTab = 0 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "借还流水",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeHistoryViewTab == 0) PureCleanWhite else TextSecondaryMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeHistoryViewTab == 1) SoftForestGreen else Color.Transparent)
                        .clickable { activeHistoryViewTab = 1 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "频次/时长数据",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeHistoryViewTab == 1) PureCleanWhite else TextSecondaryMuted
                    )
                }
            }
        }

        Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        // High level overview KPI cards (Notion Dashboard layout)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // KPI 1
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, BorderSlateGrey, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("累计出库", fontSize = 11.sp, color = TextSecondaryMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${allRecords.size} 次",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                }
            }

            // KPI 2
            Card(
                modifier = Modifier
                    .weight(1.5f)
                    .border(1.dp, BorderSlateGrey, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("当前借出在役", fontSize = 11.sp, color = TextSecondaryMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${allRecords.count { it.status == RentalRecord.STATUS_RENTED }} 项设备",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralOrangeAlert
                    )
                }
            }

            // KPI 3
            Card(
                modifier = Modifier
                    .weight(1.3f)
                    .border(1.dp, BorderSlateGrey, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("总安全运转率", fontSize = 11.sp, color = TextSecondaryMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "100 %",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActiveMintAccent
                    )
                }
            }
        }

        // Selected panel rendering
        AnimatedContent(
            targetState = activeHistoryViewTab,
            label = "history_tab_panel"
        ) { tab ->
            when (tab) {
                0 -> {
                    // TAB 0: 借还流水
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(Icons.Default.History, "history clock", tint = SoftForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("借还收发存系统日志", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepForestGreen)
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                                .background(PureCleanWhite)
                        ) {
                            if (allRecords.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(40.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.HourglassEmpty, "empty history", tint = BorderSlateGrey, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("暂无设备出入库日志纪录", color = TextSecondaryMuted, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                items(allRecords) { record ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1.5f)) {
                                            Text(record.equipmentName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                            Text("型号: ${record.equipmentModel} • 品类: ${record.equipmentCategory}", fontSize = 11.sp, color = TextSecondaryMuted)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(PaleForestBg)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        "租赁人: ${record.renter}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = SoftForestGreen
                                                    )
                                                }
                                            }
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            val isActive = record.status == RentalRecord.STATUS_RENTED
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isActive) PaleOrangeBg else Color(0xFFF1F3F5))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isActive) "借出中" else "已按时归还",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isActive) CoralOrangeAlert else TextSecondaryMuted
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "借期: ${TimeFormatter.formatDateTime(record.startDate)}",
                                                fontSize = 10.sp,
                                                color = TextSecondaryMuted
                                            )

                                            if (record.returnDate != null) {
                                                Text(
                                                    "还期: ${TimeFormatter.formatDateTime(record.returnDate)}",
                                                    fontSize = 10.sp,
                                                    color = TextSecondaryMuted
                                                )
                                                
                                                val totalTime = record.returnDate - record.startDate
                                                Text(
                                                    "历时: ${TimeFormatter.formatDurationDays(totalTime)}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ActiveMintAccent
                                                )
                                            } else {
                                                // Still active, show dynamic ticking elapsed time
                                                val elapsedMS = System.currentTimeMillis() - record.startDate
                                                Text(
                                                    "已借: ${TimeFormatter.formatDurationDays(elapsedMS)}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = CoralOrangeAlert
                                                )
                                            }
                                        }
                                    }
                                    Divider(color = BorderSlateGrey, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: 使用排行榜 (频次 / 时长数据看板)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Leaderboard 1: Borrow Count Frequencies
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Icon(Icons.Default.BarChart, "stats chart icon", tint = SoftForestGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("租借次数周转排行榜 (Frequency)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepForestGreen)
                            }
                            
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                                    .background(PureCleanWhite)
                            ) {
                                if (allEquipment.isEmpty()) {
                                    item {
                                        Box(modifier = Modifier.padding(20.dp)) { Text("无数据") }
                                    }
                                } else {
                                    val sortedByCount = allEquipment.sortedByDescending { usageCountMap[it.id] ?: 0 }
                                    items(sortedByCount) { item ->
                                        val count = usageCountMap[item.id] ?: 0
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                                Text("型号: ${item.model}", fontSize = 11.sp, color = TextSecondaryMuted)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${count} 次租用",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (count > 0) SoftForestGreen else TextSecondaryMuted
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                // Mini graphical load bar indicator
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 60.dp, height = 8.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFF1F3F5))
                                                ) {
                                                    val fraction = if (sortedByCount.firstOrNull()?.let { usageCountMap[it.id] ?: 0 } ?: 0 > 0) {
                                                        count.toFloat() / (usageCountMap[sortedByCount.first().id] ?: 1)
                                                    } else {
                                                        0f
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .fillMaxWidth(fraction)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(ActiveMintAccent)
                                                    )
                                                }
                                            }
                                        }
                                        Divider(color = BorderSlateGrey, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }

                        // Leaderboard 2: Cumulative Hours/Days Rented
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Icon(Icons.Default.OfflineBolt, "duration timeline icon", tint = SoftForestGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("总借出时长排行榜 (Duration Stats)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepForestGreen)
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                                    .background(PureCleanWhite)
                            ) {
                                if (allEquipment.isEmpty()) {
                                    item {
                                        Box(modifier = Modifier.padding(20.dp)) { Text("无数据") }
                                    }
                                } else {
                                    val sortedByDuration = allEquipment.sortedByDescending { usageDurationMap[it.id] ?: 0L }
                                    items(sortedByDuration) { item ->
                                        val durationMS = usageDurationMap[item.id] ?: 0L
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                                Text("型号: ${item.model}", fontSize = 11.sp, color = TextSecondaryMuted)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = TimeFormatter.formatDurationDays(durationMS),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (durationMS > 0) ActiveMintAccent else TextSecondaryMuted
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                // Mini loading bar
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 60.dp, height = 8.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFF1F3F5))
                                                ) {
                                                    val fraction = if (sortedByDuration.firstOrNull()?.let { usageDurationMap[it.id] ?: 0L } ?: 0L > 0L) {
                                                        durationMS.toFloat() / (usageDurationMap[sortedByDuration.first().id] ?: 1L)
                                                    } else {
                                                        0f
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .fillMaxWidth(fraction)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(SoftForestGreen)
                                                    )
                                                }
                                            }
                                        }
                                        Divider(color = BorderSlateGrey, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

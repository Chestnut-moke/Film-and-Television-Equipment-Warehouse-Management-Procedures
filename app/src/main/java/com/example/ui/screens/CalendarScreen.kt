package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Duo
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertInvitation
import androidx.compose.material.icons.filled.PersonalVideo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Equipment
import com.example.data.RentalRecord
import com.example.ui.WarehouseViewModel
import com.example.ui.components.CategoryBadge
import com.example.ui.components.TimeFormatter
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: WarehouseViewModel,
    modifier: Modifier = Modifier
) {
    val allEquipment by viewModel.allEquipment.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRentalRecords.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: 日程全览, 1: 单个设备日程
    var selectedEquipmentIdForLeaseAction by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSandBackground)
            .padding(16.dp)
    ) {
        // Notion Style Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "租期日程规划",
                style = MaterialTheme.typography.titleLarge,
                color = DeepForestGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Segmented Control
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaleForestBg)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeTab == 0) SoftForestGreen else Color.Transparent)
                        .clickable { activeTab = 0 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "日程全览",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTab == 0) PureCleanWhite else TextSecondaryMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeTab == 1) SoftForestGreen else Color.Transparent)
                        .clickable { activeTab = 1 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "单设备视图",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTab == 1) PureCleanWhite else TextSecondaryMuted
                    )
                }
            }
        }

        Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        AnimatedContent(
            targetState = activeTab,
            label = "tab_content"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        FullScheduleCalendarView(allEquipment, allRecords) { equipId ->
                            selectedEquipmentIdForLeaseAction = equipId
                        }
                    }
                }
                1 -> {
                    SingleEquipmentTimelineView(allEquipment, allRecords) { equipId ->
                        selectedEquipmentIdForLeaseAction = equipId
                    }
                }
            }
        }
    }

    // Beautiful Lease Management Dialog (Return & Extend)
    selectedEquipmentIdForLeaseAction?.let { equipId ->
        val equip = allEquipment.find { it.id == equipId }
        if (equip != null) {
            var isPerformingExtendInput by remember(equipId) { mutableStateOf(false) }
            var extensionDays by remember(equipId) { mutableFloatStateOf(7f) }

            AlertDialog(
                onDismissRequest = {
                    selectedEquipmentIdForLeaseAction = null
                    isPerformingExtendInput = false
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPerformingExtendInput) "🔄 办理设备续租" else "📦 租赁流转管理",
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PaleForestBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(equip.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepForestGreen)
                                Text("型号: ${equip.model}  •  条码: ${equip.barcode}", fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                        }

                        if (!isPerformingExtendInput) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                                border = BorderStroke(1.dp, BorderSlateGrey),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("💡 当前借出详情", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepForestGreen)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("借用人: ${equip.currentRenter ?: "-"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = CoralOrangeAlert)
                                    Text("借出时间: ${equip.rentStartDate?.let { TimeFormatter.formatDateTime(it) } ?: "-"}", fontSize = 11.sp, color = TextSecondaryMuted)
                                    Text("到期期限: ${equip.rentEndDate?.let { TimeFormatter.formatDateTime(it) } ?: "-"}", fontSize = 11.sp, color = TextSecondaryMuted)
                                    
                                    val daysLeft = equip.rentEndDate?.let {
                                        val diff = it - System.currentTimeMillis()
                                        (diff / (24L * 60 * 60 * 1000)).toInt()
                                    } ?: 0
                                    
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (daysLeft >= 0) PaleOrangeBg else PaleRedBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (daysLeft >= 0) "还租倒计时: $daysLeft 天" else "逾期警告: 已逾期 ${-daysLeft} 天",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (daysLeft >= 0) CoralOrangeAlert else CoralRedAlert
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "续租天数: ${extensionDays.toInt()} 天",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryDark
                                    )
                                    
                                    val previousEndDate = equip.rentEndDate ?: System.currentTimeMillis()
                                    val futureEndDate = previousEndDate + (extensionDays.toInt() * 24L * 60 * 60 * 1000)
                                    Text(
                                        text = "新还期: ${TimeFormatter.formatShortDate(futureEndDate)}",
                                        fontSize = 11.sp,
                                        color = ActiveMintAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Slider(
                                    value = extensionDays,
                                    onValueChange = { extensionDays = it },
                                    valueRange = 1f..30f,
                                    steps = 29,
                                    colors = SliderDefaults.colors(
                                        thumbColor = SoftForestGreen,
                                        activeTrackColor = ActiveMintAccent
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("lease_extension_slider")
                                )
                                
                                Text(
                                    text = "💡 续租将从原定计划终点直接顺延，自动向后安全展期天数。",
                                    fontSize = 10.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (isPerformingExtendInput) {
                        Button(
                            onClick = {
                                viewModel.extendLease(equip.id, extensionDays.toInt())
                                selectedEquipmentIdForLeaseAction = null
                                isPerformingExtendInput = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                            modifier = Modifier.testTag("confirm_lease_extension_btn")
                        ) {
                            Text("确认续租", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { isPerformingExtendInput = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = PaleForestBg,
                                    contentColor = SoftForestGreen
                                ),
                                modifier = Modifier.testTag("trigger_extend_lease_action_btn")
                            ) {
                                Text("办理续租")
                            }

                            Button(
                                onClick = {
                                    viewModel.returnEquipment(equip.id)
                                    selectedEquipmentIdForLeaseAction = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralOrangeAlert),
                                modifier = Modifier.testTag("confirm_lease_return_action_btn")
                            ) {
                                Text("一键退库归还")
                            }
                        }
                    }
                },
                dismissButton = {
                    if (isPerformingExtendInput) {
                        TextButton(onClick = { isPerformingExtendInput = false }) {
                            Text("返回详情", color = TextSecondaryMuted)
                        }
                    } else {
                        TextButton(onClick = { selectedEquipmentIdForLeaseAction = null }) {
                            Text("关闭", color = TextSecondaryMuted)
                        }
                    }
                }
            )
        }
    }
}

/**
 * 1. 日程全览月历视图
 */
@Composable
fun ColumnScope.FullScheduleCalendarView(
    allEquipment: List<Equipment>,
    allRecords: List<RentalRecord>,
    onManageEquipment: (Int) -> Unit
) {
    // Current display Calendar states
    var calendarYearMonth by remember {
        mutableStateOf(GregorianCalendar())
    }
    
    val currentMonthYearString = remember(calendarYearMonth) {
        val monthLabel = calendarYearMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.SIMPLIFIED_CHINESE) ?: ""
        "${calendarYearMonth.get(Calendar.YEAR)}年 $monthLabel"
    }

    // Selected Day inside the display Month
    var selectedDayCalendar by remember {
        val cal = GregorianCalendar()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        mutableStateOf(cal)
    }

    // Days list helper generator
    val daysInMonthList = remember(calendarYearMonth) {
        generateMonthGrid(calendarYearMonth)
    }

    // Header Controls
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                val newCal = calendarYearMonth.clone() as GregorianCalendar
                newCal.add(Calendar.MONTH, -1)
                calendarYearMonth = newCal
            }
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = SoftForestGreen)
        }

        Text(
            text = currentMonthYearString,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = DeepForestGreen
        )

        IconButton(
            onClick = {
                val newCal = calendarYearMonth.clone() as GregorianCalendar
                newCal.add(Calendar.MONTH, 1)
                calendarYearMonth = newCal
            }
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = SoftForestGreen)
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Weekday labels row
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf("日", "一", "二", "三", "四", "五", "六")
        for (dayName in daysOfWeek) {
            Text(
                text = dayName,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryMuted
            )
        }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 7-Column Calendar Days Grid
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val totalWeeks = daysInMonthList.size / 7
            for (w in 0 until totalWeeks) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (d in 0..6) {
                        val dayIndex = w * 7 + d
                        val dayCalObj = daysInMonthList[dayIndex]
                        
                        val isDayInCurrentMonth = dayCalObj.get(Calendar.MONTH) == calendarYearMonth.get(Calendar.MONTH)
                        
                        // Check if selectedDay matches this dayCalObj
                        val isSelected = isSameDay(dayCalObj, selectedDayCalendar)
                        
                        // Check if we have active borrowings on this day!
                        val dayTimeStart = dayCalObj.timeInMillis
                        val dayTimeEnd = dayTimeStart + 24L * 60 * 60 * 1000 - 1
                        
                        val activeRentalCount = allRecords.count { record ->
                            // Record spans across this day:
                            val recordStart = record.startDate
                            val recordEnd = record.returnDate ?: System.currentTimeMillis()
                            
                            (recordStart <= dayTimeEnd) && (recordEnd >= dayTimeStart) && record.status == RentalRecord.STATUS_RENTED
                        }

                        // Render single day node
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> SoftForestGreen
                                        activeRentalCount > 0 && isDayInCurrentMonth -> PaleForestBg
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    val newSel = dayCalObj.clone() as GregorianCalendar
                                    selectedDayCalendar = newSel
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayCalObj.get(Calendar.DAY_OF_MONTH).toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> PureCleanWhite
                                        !isDayInCurrentMonth -> TextSecondaryMuted.copy(alpha = 0.3f)
                                        activeRentalCount > 0 -> ActiveMintAccent
                                        else -> TextPrimaryDark
                                    }
                                )
                                
                                // Little dot indicating rental records count
                                if (activeRentalCount > 0 && isDayInCurrentMonth) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PureCleanWhite else ActiveMintAccent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Selected Day List Header
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.DateRange, contentDescription = "calendar schedule indicator", tint = SoftForestGreen)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${TimeFormatter.formatDay(selectedDayCalendar.timeInMillis)} 借出明细",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DeepForestGreen
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // List of active borrowing details for selected day
    val selectedDayTimeStart = selectedDayCalendar.timeInMillis
    val selectedDayTimeEnd = selectedDayTimeStart + 24L * 60 * 60 * 1000 - 1
    
    val rentsOnThisDay = remember(selectedDayCalendar, allRecords) {
        allRecords.filter { record ->
            val recordStart = record.startDate
            val recordEnd = record.returnDate ?: System.currentTimeMillis()
            (recordStart <= selectedDayTimeEnd) && (recordEnd >= selectedDayTimeStart) && record.status == RentalRecord.STATUS_RENTED
        }
    }

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
            .background(PureCleanWhite)
    ) {
        if (rentsOnThisDay.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.InsertInvitation,
                        contentDescription = "empty schedule",
                        tint = TextSecondaryMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "是日无在租设备（仓库全部在位哦!）",
                        color = TextSecondaryMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(rentsOnThisDay) { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onManageEquipment(record.equipmentId) }
                        .padding(14.dp)
                        .testTag("on_rent_lease_row_${record.equipmentId}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PaleForestBg)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(record.equipmentCategory),
                                contentDescription = null,
                                tint = SoftForestGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(record.equipmentName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                            Text(
                                "借用人: ${record.renter}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ActiveMintAccent
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PaleOrangeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "预计还期: ${TimeFormatter.formatShortDate(record.plannedEndDate)}",
                                fontSize = 10.sp,
                                color = CoralOrangeAlert,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "借期: ${TimeFormatter.formatShortDate(record.startDate)} ~ 还期",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }
                Divider(color = BorderSlateGrey, thickness = 0.5.dp)
            }
        }
    }
}


/**
 * 2. 单个设备日程视图
 */
@Composable
fun SingleEquipmentTimelineView(
    allEquipment: List<Equipment>,
    allRecords: List<RentalRecord>,
    onManageEquipment: (Int) -> Unit
) {
    var selectedEquipmentId by remember {
        mutableStateOf(allEquipment.firstOrNull()?.id ?: -1)
    }

    val selectedEquipment = remember(selectedEquipmentId, allEquipment) {
        allEquipment.find { it.id == selectedEquipmentId }
    }

    val itemTimelineRecords = remember(selectedEquipmentId, allRecords) {
        allRecords.filter { it.equipmentId == selectedEquipmentId }
    }

    if (allEquipment.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("无设备可显示，请先在“设备仓库”添加一笔影视器材", color = TextSecondaryMuted)
        }
        return
    }

    // Layout splits: Left mini-list lookup, Right timeline
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left column: Device selecting list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                .background(PureCleanWhite)
        ) {
            items(allEquipment) { item ->
                val isSelected = item.id == selectedEquipmentId
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) PaleForestBg else Color.Transparent)
                        .clickable { selectedEquipmentId = item.id }
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .testTag("select_calendar_device_${item.id}")
                ) {
                    Text(
                        item.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) DeepForestGreen else TextPrimaryDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.model, fontSize = 11.sp, color = TextSecondaryMuted)
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (item.status == Equipment.STATUS_AVAILABLE) ActiveMintAccent else CoralOrangeAlert)
                        )
                    }
                }
                Divider(color = BorderSlateGrey, thickness = 0.5.dp)
            }
        }

        // Right column: Detailed timeline schedules of selected device
        Card(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (selectedEquipment != null) {
                    Text(
                        selectedEquipment.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepForestGreen
                    )
                    Text("型号：${selectedEquipment.model}", fontSize = 12.sp, color = TextSecondaryMuted)
                    CategoryBadge(selectedEquipment.category, modifier = Modifier.padding(top = 4.dp))

                    Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    // Current Active Lease details
                    Text("📍 当前租约", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepForestGreen)
                    
                    if (selectedEquipment.status == Equipment.STATUS_RENTED) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = PaleForestBg)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "借出人: ${selectedEquipment.currentRenter ?: "-"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ActiveMintAccent
                                )
                                Text(
                                    "起租: ${selectedEquipment.rentStartDate?.let { TimeFormatter.formatShortDate(it) }}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                                Text(
                                    "截止: ${selectedEquipment.rentEndDate?.let { TimeFormatter.formatShortDate(it) }}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                                val daysLeft = selectedEquipment.rentEndDate?.let {
                                    val diff = it - System.currentTimeMillis()
                                    (diff / (24L * 60 * 60 * 1000)).toInt()
                                } ?: 0
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (daysLeft >= 0) PaleOrangeBg else PaleRedBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (daysLeft >= 0) "剩余还租期限: $daysLeft 天" else "警告: 逾期租客 $daysLeft 天",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (daysLeft >= 0) CoralOrangeAlert else CoralRedAlert
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onManageEquipment(selectedEquipment.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                                        modifier = Modifier.fillMaxWidth().testTag("single_device_timeline_manage_btn")
                                    ) {
                                        Text("管理该租约计划 (归还或续租)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "该设备目前处于空闲在库状态；可借出。",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    Text("📝 日程史/租赁时间轴", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepForestGreen)

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        if (itemTimelineRecords.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("此设备目前无近期日程借还记录", color = TextSecondaryMuted, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(itemTimelineRecords) { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Bullet line timeline graphic
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (record.returnDate == null) CoralOrangeAlert else ActiveMintAccent)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(40.dp)
                                                .background(BorderSlateGrey)
                                        )
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = record.renter,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = TextPrimaryDark
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(if (record.returnDate == null) PaleOrangeBg else PaleForestBg)
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    if (record.returnDate == null) "租赁中" else "已归还",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (record.returnDate == null) CoralOrangeAlert else SoftForestGreen
                                                )
                                            }
                                        }
                                        
                                        Text(
                                            "借: ${TimeFormatter.formatShortDate(record.startDate)} ${if (record.returnDate != null) " • 还: " + TimeFormatter.formatShortDate(record.returnDate) else ""}",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("请选择要查询的设备档案")
                    }
                }
            }
        }
    }
}


// Generates standard calendar month coordinates
fun generateMonthGrid(calendar: GregorianCalendar): List<GregorianCalendar> {
    val list = mutableListOf<GregorianCalendar>()
    
    val worker = calendar.clone() as GregorianCalendar
    worker.set(Calendar.DAY_OF_MONTH, 1)
    
    // Find previous padding days
    val firstDayOfWeek = worker.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, etc.
    val paddingDays = firstDayOfWeek - 1 // Sunday starts at 1, so offset
    
    worker.add(Calendar.DAY_OF_MONTH, -paddingDays)
    
    // Generate exactly 6 rows representing standard grid (42 nodes)
    for (i in 0 until 42) {
        list.add(worker.clone() as GregorianCalendar)
        worker.add(Calendar.DAY_OF_MONTH, 1)
    }
    return list
}

fun isSameDay(c1: GregorianCalendar, c2: GregorianCalendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
            c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Equipment
import com.example.ui.WarehouseViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen(
    viewModel: WarehouseViewModel,
    modifier: Modifier = Modifier,
    onNavigateToScan: () -> Unit
) {
    val itemsList by viewModel.filteredEquipment.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRentalRecords.collectAsStateWithLifecycle()
    
    val usageCountMap by viewModel.equipmentUsageCount.collectAsStateWithLifecycle()
    val usageDurationMap by viewModel.equipmentUsageDuration.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.filterStatus.collectAsStateWithLifecycle()

    // Dialog state controllers
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<Equipment?>(null) }

    val categories = listOf("全部", "相机/摄影机", "大镜头", "监视器", "灯光设备", "音频组件", "脚架/航拍")
    val statusFilters = listOf("全部", "在库", "已借出")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSandBackground)
            .padding(16.dp)
    ) {
        // App Core Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "仓储资产目录",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "影视基地设备仓库流转看板",
                    fontSize = 11.sp,
                    color = TextSecondaryMuted
                )
            }

            // Quick Tap Barcode Action Group
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToScan,
                    modifier = Modifier
                        .background(PaleForestBg, RoundedCornerShape(10.dp))
                        .size(44.dp)
                        .testTag("app_bar_scanner_btn")
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "扫码借还",
                        tint = SoftForestGreen
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("add_item_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("新增设备", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

        // Search Bar (Notion Style)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("搜索设备名称、型号或条目条码...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondaryMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondaryMuted)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PureCleanWhite,
                unfocusedContainerColor = PureCleanWhite,
                focusedBorderColor = SoftForestGreen,
                unfocusedBorderColor = BorderSlateGrey
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("warehouse_search_bar")
        )

        // Filters scroll list (Categories)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else BorderSlateGrey,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .background(
                            if (isSelected) SoftForestGreen else PureCleanWhite
                        )
                        .clickable { viewModel.filterCategory.value = cat }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("filter_category_$cat")
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureCleanWhite else TextSecondaryMuted
                    )
                }
            }
        }

        // Sub Filters (Status)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "状态过滤：",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryMuted
            )

            for (statusOpt in statusFilters) {
                val isSelected = statusOpt == selectedStatusFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) PaleForestBg else Color.Transparent)
                        .clickable { viewModel.filterStatus.value = statusOpt }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("filter_status_$statusOpt")
                ) {
                    Text(
                        text = statusOpt,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) SoftForestGreen else TextSecondaryMuted
                    )
                }
            }
        }

        // Equipment Lists Layout
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                .background(PureCleanWhite)
        ) {
            if (itemsList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.FilterListOff,
                            contentDescription = "empty filter",
                            tint = BorderSlateGrey,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "未搜索到匹配项",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryMuted,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "请更改标签分类过滤或搜索词，或新增影视设备。",
                            color = TextSecondaryMuted.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            } else {
                items(itemsList) { item ->
                    val usageCount = usageCountMap[item.id] ?: 0
                    val durationVal = usageDurationMap[item.id] ?: 0L

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedItemForDetail = item }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("equipment_item_row_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(getCategoryColor(item.category))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        getCategoryIcon(item.category),
                                        contentDescription = null,
                                        tint = getCategoryOnColor(item.category)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = "型号: ${item.model}",
                                        fontSize = 11.sp,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                StatusBadge(item.status)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = "barcode",
                                        tint = TextSecondaryMuted,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        item.barcode,
                                        fontSize = 10.sp,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                        }

                        // Bottom dynamic analytics tag ("时间和次数" for each item)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF1F3F5))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "频次: ${usageCount}次",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryMuted
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF1F3F5))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "时长: ${TimeFormatter.formatDurationDays(durationVal)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }

                            // If rented, display quick active renter label
                            if (item.status == Equipment.STATUS_RENTED) {
                                Text(
                                    text = "借给: ${item.currentRenter ?: "未知"}",
                                    fontSize = 11.sp,
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

        // 1. ADD EQUIPMENT FORM DIALOG
        if (showAddDialog) {
            var newName by remember { mutableStateOf("") }
            var newModel by remember { mutableStateOf("") }
            var newCat by remember { mutableStateOf("相机/摄影机") }
            var newBarcode by remember { mutableStateOf("") }
            var newNotes by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        "📄 登记入库新设备",
                        color = DeepForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("设备资产名称 (必填)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftForestGreen),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_field_name")
                        )

                        OutlinedTextField(
                            value = newModel,
                            onValueChange = { newModel = it },
                            label = { Text("设备型号编号") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftForestGreen),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_field_model")
                        )

                        // Custom dropdown mock
                        Text("所属仓库品类", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryMuted)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Skip "全部" category
                            items(categories.filter { it != "全部" }) { categoryOption ->
                                val active = newCat == categoryOption
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) ActiveMintAccent else PaleForestBg)
                                        .clickable { newCat = categoryOption }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        categoryOption,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) PureCleanWhite else TextSecondaryMuted
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = newBarcode,
                            onValueChange = { newBarcode = it },
                            label = { Text("出库条码/SN码 (留空自动生成)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftForestGreen),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_field_barcode")
                        )

                        OutlinedTextField(
                            value = newNotes,
                            onValueChange = { newNotes = it },
                            label = { Text("保管注意事项 / 套件内容备忘") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftForestGreen),
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("add_field_notes")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank()) {
                                viewModel.addEquipment(newName, newModel, newCat, newBarcode, newNotes)
                                showAddDialog = false
                            }
                        },
                        enabled = newName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                        modifier = Modifier.testTag("save_equipment_btn")
                    ) {
                        Text("录 入 库 存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("取消", color = TextSecondaryMuted)
                    }
                }
            )
        }

        // 2. DETAILED DRAWER MODAL SHEET FOR SELECTING EQUIPMENT
        if (selectedItemForDetail != null) {
            val item = selectedItemForDetail!!
            val itemRecords = allRecords.filter { it.equipmentId == item.id }

            var detailedNotesEdit by remember(item) { mutableStateOf(item.notes) }
            var isEditingNotes by remember { mutableStateOf(false) }
            
            // Manual loan/borrow states
            var isPerformingManualBorrow by remember(item) { mutableStateOf(false) }
            var manualRenterName by remember(item) { mutableStateOf("") }
            var manualLeaseDays by remember(item) { mutableFloatStateOf(3f) }

            AlertDialog(
                onDismissRequest = {
                    selectedItemForDetail = null
                    isEditingNotes = false
                    isPerformingManualBorrow = false
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPerformingManualBorrow) "👤 设备手动借出登记" else "🔎 器材档案详情",
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen,
                            fontSize = 18.sp
                        )
                        if (!isPerformingManualBorrow) {
                            IconButton(onClick = { viewModel.deleteEquipment(item); selectedItemForDetail = null }) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "删除该卡片设备", tint = Color.Red)
                            }
                        }
                    }
                },
                text = {
                    if (isPerformingManualBorrow) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "正在为以下器材办理手动出库：",
                                fontSize = 12.sp,
                                color = TextSecondaryMuted
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PaleForestBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepForestGreen)
                                    Text("型号: ${item.model}  •  条码: ${item.barcode}", fontSize = 11.sp, color = TextSecondaryMuted)
                                }
                            }

                            Divider(color = BorderSlateGrey, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

                            OutlinedTextField(
                                value = manualRenterName,
                                onValueChange = { manualRenterName = it },
                                label = { Text("借用/领用人姓名 (必填)") },
                                placeholder = { Text("例如：张摄像 / 灯光二组") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SoftForestGreen,
                                    unfocusedBorderColor = BorderSlateGrey
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("manual_borrow_renter_field")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "预设租期天数: ${manualLeaseDays.toInt()} 天",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "截至 ${(System.currentTimeMillis() + (manualLeaseDays.toInt() * 24L * 60 * 60 * 1000)).let { TimeFormatter.formatShortDate(it) }}",
                                    fontSize = 11.sp,
                                    color = ActiveMintAccent
                                )
                            }

                            Slider(
                                value = manualLeaseDays,
                                onValueChange = { manualLeaseDays = it },
                                valueRange = 1f..30f,
                                steps = 29,
                                colors = SliderDefaults.colors(
                                    thumbColor = SoftForestGreen,
                                    activeTrackColor = ActiveMintAccent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Specs
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PaleForestBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
                                    Text("型号: ${item.model}", fontSize = 12.sp, color = TextSecondaryMuted)
                                    Text("条码: ${item.barcode}", fontSize = 12.sp, color = TextSecondaryMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CategoryBadge(item.category)
                                }
                            }

                            // Renting Status Info Block
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                                border = BorderStroke(1.dp, BorderSlateGrey),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("💡 流转状态标志", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepForestGreen)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (item.status == Equipment.STATUS_RENTED) {
                                        Text("借用人: ${item.currentRenter ?: "-"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = CoralOrangeAlert)
                                        Text("借出时间: ${item.rentStartDate?.let { TimeFormatter.formatDateTime(it) }}", fontSize = 11.sp, color = TextSecondaryMuted)
                                        Text("到期期限: ${item.rentEndDate?.let { TimeFormatter.formatDateTime(it) }}", fontSize = 11.sp, color = TextSecondaryMuted)
                                    } else {
                                        Text("闲置中 (可在库极速买入/出库调配)", color = ActiveMintAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Notes segment
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📝 保管与配置备注", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepForestGreen)
                                    Text(
                                        text = if (isEditingNotes) " [保存]" else " [编辑]",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftForestGreen,
                                        modifier = Modifier.clickable {
                                            if (isEditingNotes) {
                                                viewModel.updateEquipmentNotes(item, detailedNotesEdit)
                                            }
                                            isEditingNotes = !isEditingNotes
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isEditingNotes) {
                                    OutlinedTextField(
                                        value = detailedNotesEdit,
                                        onValueChange = { detailedNotesEdit = it },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftForestGreen),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(WarmSandBackground)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = item.notes.ifBlank { "无说明注意事项。" },
                                            color = TextPrimaryDark,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Quick stats
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("历史周转频次", fontSize = 10.sp, color = TextSecondaryMuted)
                                    Text("${usageCountMap[item.id] ?: 0} 次", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepForestGreen)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("累计流转天数", fontSize = 10.sp, color = TextSecondaryMuted)
                                    Text(TimeFormatter.formatDurationDays(usageDurationMap[item.id] ?: 0L), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepForestGreen)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (isPerformingManualBorrow) {
                        Button(
                            onClick = {
                                if (manualRenterName.isNotBlank()) {
                                    viewModel.checkoutEquipment(item.id, manualRenterName.trim(), manualLeaseDays.toInt())
                                    selectedItemForDetail = null
                                    isPerformingManualBorrow = false
                                }
                            },
                            enabled = manualRenterName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                            modifier = Modifier.testTag("confirm_manual_borrow_btn")
                        ) {
                            Text("确认借出登记", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        if (item.status == Equipment.STATUS_AVAILABLE) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = { isPerformingManualBorrow = true },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = PaleForestBg,
                                        contentColor = SoftForestGreen
                                    ),
                                    modifier = Modifier.testTag("manual_borrow_trigger_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "manual borrow icon", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("手动借出")
                                }

                                Button(
                                    onClick = {
                                        selectedItemForDetail = null
                                        onNavigateToScan()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                                    modifier = Modifier.testTag("scan_borrow_trigger_btn")
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = "borrow icon", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("扫码出库")
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.returnEquipment(item.id)
                                    selectedItemForDetail = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralOrangeAlert),
                                modifier = Modifier.testTag("direct_manual_return_btn")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "return icon", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("一键还库入仓")
                            }
                        }
                    }
                },
                dismissButton = {
                    if (isPerformingManualBorrow) {
                        TextButton(onClick = { isPerformingManualBorrow = false }) {
                            Text("返回详情", color = TextSecondaryMuted)
                        }
                    } else {
                        TextButton(onClick = { selectedItemForDetail = null; isEditingNotes = false }) {
                            Text("关闭", color = TextSecondaryMuted)
                        }
                    }
                }
            )
        }
    }
}

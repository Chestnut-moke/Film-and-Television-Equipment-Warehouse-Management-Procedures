package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Equipment
import com.example.ui.WarehouseViewModel
import com.example.ui.components.CategoryBadge
import com.example.ui.components.TimeFormatter
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: WarehouseViewModel,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    val scanState by viewModel.scanResult.collectAsStateWithLifecycle()
    val allEquipment by viewModel.allEquipment.collectAsStateWithLifecycle()
    
    var manualBarcode by remember { mutableStateOf("") }
    
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var useRealCamera by remember { mutableStateOf(false) }

    LaunchedEffect(useRealCamera) {
        if (useRealCamera && !cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    // Scanner light laser beam vertical position animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserYOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSandBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
                Text(
                    text = "快速扫码借还",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepForestGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = "Scan icon",
                tint = ActiveMintAccent,
                modifier = Modifier.size(28.dp)
            )
        }

        Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        // Large animated Scanner viewbox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .border(2.dp, ActiveMintAccent, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (useRealCamera && cameraPermissionState.status.isGranted) {
                // Real Live CameraX Viewfinder
                CameraPreview(modifier = Modifier.fillMaxSize())
                
                // Sweep Laser overlays on top of Camera
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Draw neon sweeping laser line
                        val laserLineY = h * laserYOffset
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF52B788), Color(0xFFD8F3DC), Color(0xFF52B788), Color.Transparent)
                            ),
                            start = Offset(20f, laserLineY),
                            end = Offset(w - 20f, laserLineY),
                            strokeWidth = 6f
                        )
                    }
                }

                // Decorative Corner overlays
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "📷 实景相机已开启",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            "请对准条形码",
                            color = ActiveMintAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            } else if (useRealCamera && !cameraPermissionState.status.isGranted) {
                // Camera requested but permission is not yet allowed/granted
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "No Permission",
                        tint = CoralOrangeAlert,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "未获得相机权限，无法开启实景摄像头",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("点击授权", fontSize = 11.sp)
                    }
                }
            } else {
                // Simulation Preview Mode (Laser sweep animation)
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Draw neon sweeping laser line
                        val laserLineY = h * laserYOffset
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF52B788), Color(0xFFD8F3DC), Color(0xFF52B788), Color.Transparent)
                            ),
                            start = Offset(20f, laserLineY),
                            end = Offset(w - 20f, laserLineY),
                            strokeWidth = 6f
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "Scanner Center Icon",
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "虚拟扫码模式运行中",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "您可以使用下方设备卡片模拟触碰扫码",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Camera option switcher row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "扫码模式: " + if (useRealCamera) "📷 实景摄像" else "🤖 智能模拟",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = DeepForestGreen
            )
            FilledTonalButton(
                onClick = { useRealCamera = !useRealCamera },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (useRealCamera) SoftForestGreen else BorderSlateGrey.copy(alpha = 0.5f),
                    contentColor = if (useRealCamera) PureCleanWhite else DeepForestGreen
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("toggle_camera_mode_btn")
            ) {
                Icon(
                    imageVector = if (useRealCamera) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    contentDescription = "camera toggle icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (useRealCamera) "切换为虚拟模式" else "调用实景摄像头",
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scanner action modal / state panel
        AnimatedContent(
            targetState = scanState,
            transitionSpec = {
                slideInVertically { height -> height } + fadeIn() with
                slideOutVertically { height -> -height } + fadeOut()
            },
            label = "scan_result_content"
        ) { resultState ->
            when (resultState) {
                is WarehouseViewModel.ScanResultState.Idle -> {
                    var isTipExpanded by remember { mutableStateOf(true) }

                    // Manual entry or helper instructions card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PureCleanWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isTipExpanded = !isTipExpanded }
                                    .testTag("tip_header_toggle"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "💡 小贴士：模拟扫码快速借还与实景扫码",
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = if (isTipExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isTipExpanded) "收起小贴士" else "展开小贴士",
                                    tint = DeepForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            if (isTipExpanded) {
                                Text(
                                    text = "由于此页面在模拟环境中运行，您可以通过下方设备快捷卡片触发“模拟扫码”流程；也可以点击上方“调用实景摄像头”开启真实扫码预览，或直接输入设备条码编号直接检测进行极速借还。",
                                    color = TextSecondaryMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = manualBarcode,
                                    onValueChange = { manualBarcode = it },
                                    label = { Text("输入设备条码编号") },
                                    isError = false,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SoftForestGreen,
                                        unfocusedBorderColor = BorderSlateGrey
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("manual_barcode_input"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { 
                                        if (manualBarcode.isNotBlank()) {
                                            viewModel.processBarcodeScan(manualBarcode.trim())
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .testTag("submit_manual_barcode")
                                ) {
                                    Text("检 索", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                is WarehouseViewModel.ScanResultState.Success -> {
                    val equipment = resultState.equipment
                    val checkoutFlow = resultState.type == "BORROW"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, ActiveMintAccent, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PaleForestBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Scanned equipment primary details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ActiveMintAccent)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        getCategoryIcon(equipment.category),
                                        contentDescription = "category icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = equipment.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = DeepForestGreen
                                    )
                                    Text(
                                        text = "型号：${equipment.model}  •  条码：${equipment.barcode}",
                                        fontSize = 12.sp,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            CategoryBadge(equipment.category)
                            Divider(color = BorderSlateGrey, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                            if (checkoutFlow) {
                                // RENT OUT WORKFLOW
                                Text(
                                    text = "📥 设备借出登记",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                var renterName by remember { mutableStateOf("") }
                                var leaseDays by remember { mutableFloatStateOf(3f) }

                                OutlinedTextField(
                                    value = renterName,
                                    onValueChange = { renterName = it },
                                    label = { Text("租赁借用人姓名") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    placeholder = { Text("例如：张摄影 / 李灯光组") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SoftForestGreen,
                                        unfocusedBorderColor = BorderSlateGrey
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("renter_name_input"),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Days Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "预设租期期限: ${leaseDays.toInt()} 天",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = "预计 ${(System.currentTimeMillis() + (leaseDays.toInt() * 24L * 60 * 60 * 1000)).let { TimeFormatter.formatShortDate(it) }} 前归还",
                                        fontSize = 11.sp,
                                        color = ActiveMintAccent
                                    )
                                }
                                Slider(
                                    value = leaseDays,
                                    onValueChange = { leaseDays = it },
                                    valueRange = 1f..30f,
                                    steps = 29,
                                    colors = SliderDefaults.colors(
                                        thumbColor = SoftForestGreen,
                                        activeTrackColor = ActiveMintAccent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetScanResult() },
                                        border = BorderStroke(1.dp, SoftForestGreen),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftForestGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("取 消")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            if (renterName.isNotBlank()) {
                                                viewModel.checkoutEquipment(equipment.id, renterName, leaseDays.toInt())
                                                viewModel.resetScanResult()
                                                manualBarcode = ""
                                            }
                                        },
                                        enabled = renterName.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftForestGreen),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .testTag("confirm_checkout_btn")
                                    ) {
                                        Text("确认借出", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // RETURN CHECK-IN WORKFLOW
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "rented warning",
                                        tint = CoralOrangeAlert,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📤 设备已在借出状态",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralOrangeAlert
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PureCleanWhite)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "租赁借用人: ${equipment.currentRenter ?: "未知"}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "借出时间: ${equipment.rentStartDate?.let { TimeFormatter.formatDateTime(it) } ?: "-"}",
                                        fontSize = 12.sp,
                                        color = TextSecondaryMuted
                                    )
                                    Text(
                                        text = "预计还期: ${equipment.rentEndDate?.let { TimeFormatter.formatDateTime(it) } ?: "-"}",
                                        fontSize = 12.sp,
                                        color = TextSecondaryMuted
                                    )
                                    
                                    val isOverdue = equipment.rentEndDate != null && System.currentTimeMillis() > equipment.rentEndDate
                                    if (isOverdue) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PaleRedBg)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("⚠️ 该设备租约已逾期！请尽快归还", color = CoralRedAlert, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetScanResult() },
                                        border = BorderStroke(1.dp, SoftForestGreen),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftForestGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("取 消")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            viewModel.returnEquipment(equipment.id)
                                            viewModel.resetScanResult()
                                            manualBarcode = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6A4F)),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .testTag("confirm_return_btn")
                                    ) {
                                        Text("确认归还入库", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                is WarehouseViewModel.ScanResultState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PaleRedBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "error icon", tint = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("扫码错误", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = resultState.message, color = TextPrimaryDark, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.resetScanResult() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("重 新 扫 描", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated quick scans list header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "点击模拟扫码 (备选影视器材条码 🏷️)",
                style = MaterialTheme.typography.titleMedium,
                color = DeepForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Text(
            text = "点击以下设备名单，系统将模拟摄像头物理扫码该设备的流程。可在“在库”或“借出中”之间流畅模拟。",
            color = TextSecondaryMuted,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Lazy column displaying all equipment for quick mock selection
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderSlateGrey, RoundedCornerShape(12.dp))
                .background(PureCleanWhite)
                .padding(horizontal = 4.dp)
        ) {
            if (allEquipment.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("仓库为空，请先录入产品设备", color = TextSecondaryMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(allEquipment) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.processBarcodeScan(item.barcode)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("simulate_scan_item_${item.id}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = "mock qrcode",
                                    tint = TextSecondaryMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(item.barcode, fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                        }

                        // State label
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (item.status == Equipment.STATUS_AVAILABLE) Color(0xFFEAF5EE)
                                    else Color(0xFFFEF2F2)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (item.status == Equipment.STATUS_AVAILABLE) "在库" else "借用:${item.currentRenter ?: "未知"}",
                                fontSize = 11.sp,
                                color = if (item.status == Equipment.STATUS_AVAILABLE) Color(0xFF2D6A4F) else Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Divider(color = BorderSlateGrey, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val executor = ContextCompat.getMainExecutor(ctx)
                try {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, executor)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

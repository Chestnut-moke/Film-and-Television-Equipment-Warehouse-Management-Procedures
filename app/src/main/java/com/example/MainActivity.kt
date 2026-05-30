package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.WarehouseViewModel
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.WarehouseScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureCleanWhite
import com.example.ui.theme.SoftForestGreen

class MainActivity : ComponentActivity() {
    private val viewModel: WarehouseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable fullscreen edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                var currentScreenIndex by remember { mutableIntStateOf(0) }
                
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = PureCleanWhite,
                            tonalElevation = NavigationBarDefaults.Elevation,
                            modifier = Modifier.testTag("app_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentScreenIndex == 0,
                                onClick = { currentScreenIndex = 0 },
                                icon = { Icon(Icons.Default.Storage, contentDescription = "Warehouse Navigation Icon") },
                                label = { Text("设备仓库", style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PureCleanWhite,
                                    selectedTextColor = SoftForestGreen,
                                    indicatorColor = SoftForestGreen
                                ),
                                modifier = Modifier.testTag("nav_tab_warehouse")
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 1,
                                onClick = { currentScreenIndex = 1 },
                                icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar Navigation Icon") },
                                label = { Text("租期日程", style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PureCleanWhite,
                                    selectedTextColor = SoftForestGreen,
                                    indicatorColor = SoftForestGreen
                                ),
                                modifier = Modifier.testTag("nav_tab_calendar")
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 2,
                                onClick = { currentScreenIndex = 2 },
                                icon = { Icon(Icons.Default.Assignment, contentDescription = "History Log Navigation Icon") },
                                label = { Text("借还历史", style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PureCleanWhite,
                                    selectedTextColor = SoftForestGreen,
                                    indicatorColor = SoftForestGreen
                                ),
                                modifier = Modifier.testTag("nav_tab_history")
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 3,
                                onClick = { currentScreenIndex = 3 },
                                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner Navigation Icon") },
                                label = { Text("扫码快速通道", style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PureCleanWhite,
                                    selectedTextColor = SoftForestGreen,
                                    indicatorColor = SoftForestGreen
                                ),
                                modifier = Modifier.testTag("nav_tab_scanner")
                            )
                        }
                    }
                ) { innerPadding ->
                    // Animated transition switching panels
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreenIndex) {
                            0 -> WarehouseScreen(
                                viewModel = viewModel,
                                onNavigateToScan = { currentScreenIndex = 3 }
                            )
                            1 -> CalendarScreen(
                                viewModel = viewModel
                            )
                            2 -> HistoryScreen(
                                viewModel = viewModel
                            )
                            3 -> ScannerScreen(
                                viewModel = viewModel,
                                onBack = { currentScreenIndex = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}


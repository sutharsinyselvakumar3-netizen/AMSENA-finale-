package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ConnectionStatusBadge
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RobotScreen
import com.example.ui.screens.SensorsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.SurfaceWhite
import com.example.viewmodel.RobotViewModel

enum class AppTab(val label: String, val icon: ImageVector) {
    HOME("HOME", Icons.Default.Home),
    ROBOT("ROBOT", Icons.Default.PrecisionManufacturing),
    SENSORS("SENSORS", Icons.Default.Sensors),
    SETTINGS("SETTINGS", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: RobotViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val mqttState by viewModel.mqttState.collectAsStateWithLifecycle()
                val robotStatus by viewModel.robotStatus.collectAsStateWithLifecycle()
                val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
                val cameraStatus by viewModel.cameraStatus.collectAsStateWithLifecycle()
                val currentCameraFrame by viewModel.cameraService.currentFrame.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()
                val commandUiState by viewModel.commandUiState.collectAsStateWithLifecycle()
                val safetyEvent by viewModel.safetyEvent.collectAsStateWithLifecycle()
                val isEmergencyActive by viewModel.isEmergencyActive.collectAsStateWithLifecycle()
                val isRobotConnectionLost by viewModel.isRobotConnectionLost.collectAsStateWithLifecycle()
                val testResult by viewModel.testConnectionResult.collectAsStateWithLifecycle()

                var currentTab by remember { mutableStateOf(AppTab.HOME) }

                // BackHandler returns to Home if on another tab
                if (currentTab != AppTab.HOME) {
                    BackHandler {
                        currentTab = AppTab.HOME
                    }
                }

                val isRobotOnline = (robotStatus.isOnline || telemetry.online) && !isRobotConnectionLost

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RoyalWhite),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "AI COMPANION",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = NavyBlue,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Smart Onion Garden Robot",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = NavyBlue.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            actions = {
                                ConnectionStatusBadge(
                                    mqttState = mqttState,
                                    isRobotOnline = isRobotOnline,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = RoyalWhite,
                                titleContentColor = NavyBlue,
                                actionIconContentColor = NavyBlue
                            ),
                            modifier = Modifier.border(0.dp, CardBorder)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = RoyalWhite,
                            contentColor = NavyBlue,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .border(1.dp, CardBorder)
                                .testTag("bottom_nav_bar")
                        ) {
                            AppTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RoyalWhite,
                                        selectedTextColor = NavyBlue,
                                        indicatorColor = NavyBlue,
                                        unselectedIconColor = NavyBlue.copy(alpha = 0.6f),
                                        unselectedTextColor = NavyBlue.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = RoyalWhite
                    ) {
                        when (currentTab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    mqttState = mqttState,
                                    robotStatus = robotStatus,
                                    telemetry = telemetry,
                                    cameraStatus = cameraStatus,
                                    settings = settings,
                                    safetyEvent = safetyEvent,
                                    isEmergencyActive = isEmergencyActive,
                                    onNavigateToRobot = { currentTab = AppTab.ROBOT },
                                    onNavigateToSensors = { currentTab = AppTab.SENSORS },
                                    onNavigateToSettings = { currentTab = AppTab.SETTINGS },
                                    onEmergencyStop = { viewModel.emergencyStop() }
                                )
                            }

                            AppTab.ROBOT -> {
                                RobotScreen(
                                    mqttState = mqttState,
                                    isRobotOnline = isRobotOnline,
                                    isRobotConnectionLost = isRobotConnectionLost,
                                    isEmergencyActive = isEmergencyActive,
                                    telemetry = telemetry,
                                    cameraStatus = cameraStatus,
                                    currentCameraFrame = currentCameraFrame,
                                    commandUiState = commandUiState,
                                    safetyEvent = safetyEvent,
                                    onForward = { viewModel.moveForward() },
                                    onBackward = { viewModel.moveBackward() },
                                    onLeft = { viewModel.turnLeft() },
                                    onRight = { viewModel.turnRight() },
                                    onStop = { viewModel.stopRobot() },
                                    onSpeedChange = { viewModel.setSpeed(it) },
                                    onCameraServoChange = { viewModel.setCameraServo(it) },
                                    onSoilServoChange = { viewModel.setSoilServo(it) },
                                    onToggleSoilRelay = { viewModel.toggleSoilRelay(it) },
                                    onToggleWaterPump = { viewModel.toggleWaterPump(it) },
                                    onEmergencyStop = { viewModel.emergencyStop() },
                                    onResetEmergency = { viewModel.resetEmergency() },
                                    onRetryCommand = { viewModel.retryLastCommand() },
                                    onDismissAlert = { viewModel.dismissAlert() },
                                    onRetryCamera = { viewModel.retryCamera() },
                                    onOpenSettings = { currentTab = AppTab.SETTINGS }
                                )
                            }

                            AppTab.SENSORS -> {
                                SensorsScreen(
                                    robotStatus = robotStatus,
                                    telemetry = telemetry
                                )
                            }

                            AppTab.SETTINGS -> {
                                SettingsScreen(
                                    currentSettings = settings,
                                    mqttState = mqttState,
                                    testResult = testResult,
                                    onSaveAndConnect = { viewModel.saveAndConnect(it) },
                                    onDisconnect = { viewModel.disconnect() },
                                    onTestConnection = { viewModel.testConnection() },
                                    onClearTestResult = { viewModel.clearTestResult() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

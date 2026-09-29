package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraStatus
import com.example.model.RobotTelemetry
import com.example.model.SafetyEvent
import com.example.service.CommandUiState
import com.example.service.MqttConnectionState
import com.example.ui.components.CameraCard
import com.example.ui.components.CameraServoCard
import com.example.ui.components.MotorSpeedCard
import com.example.ui.components.MovementControl
import com.example.ui.components.RelaySwitchCard
import com.example.ui.components.SafetyBanner
import com.example.ui.components.SafetyStatusCard
import com.example.ui.components.SoilSensorCard
import com.example.ui.components.SoilServoCard
import com.example.ui.components.WaterLevelCard
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite

@Composable
fun RobotScreen(
    mqttState: MqttConnectionState,
    isRobotOnline: Boolean,
    isRobotConnectionLost: Boolean,
    isEmergencyActive: Boolean,
    telemetry: RobotTelemetry,
    cameraStatus: CameraStatus,
    currentCameraFrame: Bitmap?,
    commandUiState: CommandUiState,
    safetyEvent: SafetyEvent?,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (Int) -> Unit,
    onCameraServoChange: (Int) -> Unit,
    onSoilServoChange: (Int) -> Unit,
    onToggleSoilRelay: (Boolean) -> Unit,
    onToggleWaterPump: (Boolean) -> Unit,
    onEmergencyStop: () -> Unit,
    onResetEmergency: () -> Unit,
    onRetryCommand: () -> Unit,
    onDismissAlert: () -> Unit,
    onRetryCamera: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canMove = isRobotOnline && !isRobotConnectionLost && !isEmergencyActive &&
            mqttState is MqttConnectionState.Connected

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RoyalWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("robot_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tagline / Subtitle
        Column {
            Text(
                text = "ROBOT CONTROL PANEL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Text(
                text = "“See the Weed. Protect the Onion. Work Smart.”",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue
            )
        }

        // Safety & Timeout Alerts (if active)
        SafetyBanner(
            safetyEvent = safetyEvent,
            isEmergencyActive = isEmergencyActive,
            commandUiState = commandUiState,
            isRobotConnectionLost = isRobotConnectionLost,
            onRetryCommand = onRetryCommand,
            onStopRobot = onStop,
            onResetEmergency = onResetEmergency,
            onDismissAlert = onDismissAlert
        )

        // 1. CAMERA (Large live preview, online/offline, retry, settings)
        CameraCard(
            cameraStatus = cameraStatus,
            currentFrame = currentCameraFrame,
            onRetry = onRetryCamera,
            onOpenSettings = onOpenSettings
        )

        // 2. ROBOT MOVEMENT (▲ FORWARD, ◀ LEFT, ■ STOP, RIGHT ▶, ▼ REVERSE)
        MovementControl(
            isEnabled = canMove,
            isEmergency = isEmergencyActive,
            onForward = onForward,
            onBackward = onBackward,
            onLeft = onLeft,
            onRight = onRight,
            onStop = onStop
        )

        // 3. MOTOR SPEED (0-100%, Navy slider, presets)
        MotorSpeedCard(
            currentSpeed = telemetry.motorSpeed,
            onSpeedChange = onSpeedChange
        )

        // 4. CAMERA SERVO (0°-180°)
        CameraServoCard(
            currentAngle = telemetry.cameraServo,
            onAngleChange = onCameraServoChange
        )

        // 5. SOIL SERVO (0°-45°)
        SoilServoCard(
            currentAngle = telemetry.soilServo,
            onAngleChange = onSoilServoChange
        )

        // 6. SOIL SENSOR (Moisture percentage)
        SoilSensorCard(
            soilPercent = telemetry.soilPercent
        )

        // 7. SOIL RELAY (Switch OFF / ON)
        RelaySwitchCard(
            title = "SOIL RELAY",
            subtitle = "Subsurface soil actuator",
            isOn = telemetry.soilRelay,
            onToggle = onToggleSoilRelay,
            testTag = "soil_relay_card"
        )

        // 8. WATER LEVEL (Percentage, tank-style indicator)
        WaterLevelCard(
            waterPercent = telemetry.waterPercent,
            lowLimit = 20
        )

        // 9. WATER PUMP (Switch OFF / ON)
        RelaySwitchCard(
            title = "WATER PUMP",
            subtitle = "Targeted irrigation pump",
            isOn = telemetry.waterPump,
            onToggle = onToggleWaterPump,
            testTag = "water_pump_card"
        )

        // 10. SAFETY STATUS (SAFE / WARNING / EMERGENCY, Emergency stop button)
        SafetyStatusCard(
            isEmergencyActive = isEmergencyActive,
            safetyEvent = safetyEvent,
            onEmergencyStop = onEmergencyStop,
            onResetEmergency = onResetEmergency
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

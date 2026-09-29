package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraStatus
import com.example.model.MqttSettings
import com.example.model.RobotStatus
import com.example.model.RobotTelemetry
import com.example.model.SafetyEvent
import com.example.service.MqttConnectionState
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceWhite

@Composable
fun HomeScreen(
    mqttState: MqttConnectionState,
    robotStatus: RobotStatus,
    telemetry: RobotTelemetry,
    cameraStatus: CameraStatus,
    settings: MqttSettings,
    safetyEvent: SafetyEvent?,
    isEmergencyActive: Boolean,
    onNavigateToRobot: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onEmergencyStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RoyalWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tagline Header Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavyBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = null,
                        tint = RoyalWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = settings.robotName.ifBlank { "Smart Onion Robot" },
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = NavyBlue
                    )
                    Text(
                        text = "ID: ${settings.robotId}",
                        fontSize = 12.sp,
                        color = NavyBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "“See the Weed. Protect the Onion. Work Smart.”",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue
            )
        }

        // Section Title
        Text(
            text = "SYSTEM TELEMETRY OVERVIEW",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )

        // 7 Compact Cards: MQTT, ROBOT, CAMERA, SOIL, WATER, BATTERY, SAFETY
        // Row 1: MQTT & ROBOT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val (mqttColor, mqttLabel) = when (mqttState) {
                is MqttConnectionState.Connected -> Pair(StatusGreen, "CONNECTED")
                is MqttConnectionState.Connecting -> Pair(StatusAmber, "CONNECTING")
                is MqttConnectionState.Disconnected -> Pair(LightGrey, "DISCONNECTED")
                is MqttConnectionState.Error -> Pair(StatusRed, "ERROR")
            }
            CompactStatusCard(
                title = "MQTT",
                value = mqttLabel,
                dotColor = mqttColor,
                icon = Icons.Default.Wifi,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSettings
            )

            val isOnline = robotStatus.isOnline || telemetry.online
            CompactStatusCard(
                title = "ROBOT",
                value = if (isOnline) "ONLINE" else "OFFLINE",
                dotColor = if (isOnline) StatusGreen else LightGrey,
                icon = Icons.Default.PrecisionManufacturing,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToRobot
            )
        }

        // Row 2: CAMERA & BATTERY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactStatusCard(
                title = "CAMERA",
                value = if (cameraStatus.isOnline) "ONLINE" else "OFFLINE",
                dotColor = if (cameraStatus.isOnline) StatusGreen else LightGrey,
                icon = Icons.Default.Videocam,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToRobot
            )

            val batPercent = telemetry.batteryPercent
            CompactStatusCard(
                title = "BATTERY",
                value = "$batPercent%",
                dotColor = if (batPercent > 20) StatusGreen else StatusAmber,
                icon = Icons.Default.BatteryFull,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSensors
            )
        }

        // Row 3: SOIL & WATER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactStatusCard(
                title = "SOIL MOISTURE",
                value = "${telemetry.soilPercent}%",
                dotColor = NavyBlue,
                icon = Icons.Default.Sensors,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSensors
            )

            val waterColor = when {
                telemetry.waterPercent <= 10 -> StatusRed
                telemetry.waterPercent <= 20 -> StatusAmber
                else -> NavyBlue
            }
            CompactStatusCard(
                title = "WATER LEVEL",
                value = "${telemetry.waterPercent}%",
                dotColor = waterColor,
                icon = Icons.Default.Opacity,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSensors
            )
        }

        // Card 7: SAFETY
        val isEmergency = isEmergencyActive || (safetyEvent?.event == "flame_detected" && safetyEvent.active)
        val safetyText = when {
            isEmergency -> "EMERGENCY STOP ACTIVE"
            safetyEvent?.event == "low_water" -> "LOW WATER WARNING"
            else -> "ALL SYSTEMS SAFE"
        }
        val safetyColor = when {
            isEmergency -> StatusRed
            safetyEvent?.event == "low_water" -> StatusAmber
            else -> StatusGreen
        }
        CompactStatusCard(
            title = "SAFETY WATCHDOG",
            value = safetyText,
            dotColor = safetyColor,
            icon = if (isEmergency) Icons.Default.Dangerous else Icons.Default.Security,
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToRobot
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Action Buttons
        Button(
            onClick = onNavigateToRobot,
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyBlue,
                contentColor = RoyalWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("home_go_robot_btn")
        ) {
            Text(
                text = "OPEN ROBOT CONTROLLER",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        // Quick Emergency Stop button
        Button(
            onClick = onEmergencyStop,
            colors = ButtonDefaults.buttonColors(
                containerColor = StatusRed,
                contentColor = RoyalWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("home_emergency_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Dangerous,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "EMERGENCY STOP",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CompactStatusCard(
    title: String,
    value: String,
    dotColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyBlue,
                modifier = Modifier.size(18.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyBlue.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = NavyBlue
        )
    }
}

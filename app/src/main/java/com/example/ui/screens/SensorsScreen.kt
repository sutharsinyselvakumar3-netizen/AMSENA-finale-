package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDamage
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
import com.example.model.RobotStatus
import com.example.model.RobotTelemetry
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun SensorsScreen(
    robotStatus: RobotStatus,
    telemetry: RobotTelemetry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RoyalWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("sensors_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "REAL-TIME TELEMETRY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Text(
                text = "Live ESP32 Sensor & Actuator Telemetry",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        }

        // 1. Robot Online Status & Battery Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isOnline = robotStatus.isOnline || telemetry.online
            SensorMetricCard(
                title = "ROBOT ONLINE",
                value = if (isOnline) "ONLINE" else "OFFLINE",
                indicatorColor = if (isOnline) StatusGreen else LightGrey,
                icon = Icons.Default.PrecisionManufacturing,
                modifier = Modifier.weight(1f)
            )

            SensorMetricCard(
                title = "BATTERY",
                value = "${telemetry.batteryPercent}%",
                indicatorColor = if (telemetry.batteryPercent > 20) StatusGreen else StatusAmber,
                icon = Icons.Default.BatteryChargingFull,
                modifier = Modifier.weight(1f)
            )
        }

        // 2. Soil Moisture Gauge Card
        SensorBarCard(
            title = "SOIL MOISTURE",
            subtitle = "Subsurface volumetric water content",
            valueText = "${telemetry.soilPercent}%",
            progress = (telemetry.soilPercent.coerceIn(0, 100)) / 100f,
            barColor = NavyBlue,
            icon = Icons.Default.Sensors
        )

        // 3. Water Level Tank Card
        val waterColor = when {
            telemetry.waterPercent <= 10 -> StatusRed
            telemetry.waterPercent <= 20 -> StatusAmber
            else -> NavyBlue
        }
        SensorBarCard(
            title = "WATER LEVEL",
            subtitle = "Reservoir capacity",
            valueText = "${telemetry.waterPercent}%",
            progress = (telemetry.waterPercent.coerceIn(0, 100)) / 100f,
            barColor = waterColor,
            icon = Icons.Default.Opacity
        )

        // 4. Flame Sensor Status
        val flameActive = telemetry.flame
        SensorMetricCard(
            title = "FLAME SENSOR",
            value = if (flameActive) "FLAME DETECTED!" else "NORMAL / NO FIRE",
            indicatorColor = if (flameActive) StatusRed else StatusGreen,
            icon = Icons.Default.LocalFireDepartment,
            modifier = Modifier.fillMaxWidth(),
            isHighlight = flameActive
        )

        // 5. Motor Status & Speed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SensorMetricCard(
                title = "MOTOR STATUS",
                value = telemetry.motorState.uppercase(),
                indicatorColor = if (telemetry.motorState == "stop") LightGrey else StatusGreen,
                icon = Icons.Default.Power,
                modifier = Modifier.weight(1f)
            )

            SensorMetricCard(
                title = "MOTOR SPEED",
                value = "${telemetry.motorSpeed}%",
                indicatorColor = NavyBlue,
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }

        // 6. Camera Servo & Soil Servo Angles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SensorMetricCard(
                title = "CAMERA SERVO",
                value = "${telemetry.cameraServo}°",
                indicatorColor = NavyBlue,
                icon = Icons.Default.CameraAlt,
                modifier = Modifier.weight(1f)
            )

            SensorMetricCard(
                title = "SOIL SERVO",
                value = "${telemetry.soilServo}°",
                indicatorColor = NavyBlue,
                icon = Icons.Default.WaterDamage,
                modifier = Modifier.weight(1f)
            )
        }

        // 7. Relays: Soil Relay & Water Pump
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SensorMetricCard(
                title = "SOIL RELAY",
                value = if (telemetry.soilRelay) "ON" else "OFF",
                indicatorColor = if (telemetry.soilRelay) NavyBlue else LightGrey,
                icon = Icons.Default.ElectricBolt,
                modifier = Modifier.weight(1f)
            )

            SensorMetricCard(
                title = "WATER PUMP",
                value = if (telemetry.waterPump) "ON" else "OFF",
                indicatorColor = if (telemetry.waterPump) NavyBlue else LightGrey,
                icon = Icons.Default.Opacity,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SensorMetricCard(
    title: String,
    value: String,
    indicatorColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(
                if (isHighlight) 2.dp else 1.dp,
                if (isHighlight) StatusRed else CardBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
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
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyBlue.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = if (isHighlight) StatusRed else NavyBlue
        )
    }
}

@Composable
private fun SensorBarCard(
    title: String,
    subtitle: String,
    valueText: String,
    progress: Float,
    barColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NavyBlue,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = NavyBlue.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = valueText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LightGrey)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(barColor)
            )
        }
    }
}

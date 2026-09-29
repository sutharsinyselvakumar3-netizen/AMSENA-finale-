package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.InactiveTrack
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite

@Composable
fun CameraServoCard(
    currentAngle: Int,
    onAngleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("camera_servo_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CAMERA SERVO",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$currentAngle°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NavyBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0°", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
            Text("90° (CENTER)", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
            Text("180°", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
        }

        Slider(
            value = currentAngle.coerceIn(0, 180).toFloat(),
            onValueChange = { onAngleChange(it.toInt()) },
            valueRange = 0f..180f,
            colors = SliderDefaults.colors(
                thumbColor = NavyBlue,
                activeTrackColor = NavyBlue,
                inactiveTrackColor = InactiveTrack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("camera_servo_slider")
        )
    }
}

@Composable
fun SoilServoCard(
    currentAngle: Int,
    onAngleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Strictly clamp <= 45°
    val safeAngle = currentAngle.coerceIn(0, 45)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("soil_servo_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOIL SERVO",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$safeAngle°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NavyBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0° (HOME)", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
            Text("45° (MAX)", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
        }

        Slider(
            value = safeAngle.toFloat(),
            onValueChange = { onAngleChange(it.toInt().coerceIn(0, 45)) },
            valueRange = 0f..45f,
            colors = SliderDefaults.colors(
                thumbColor = NavyBlue,
                activeTrackColor = NavyBlue,
                inactiveTrackColor = InactiveTrack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("soil_servo_slider")
        )
    }
}

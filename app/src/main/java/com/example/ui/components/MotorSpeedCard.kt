package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
fun MotorSpeedCard(
    currentSpeed: Int,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("motor_speed_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MOTOR SPEED",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$currentSpeed%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NavyBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Presets: SLOW (30%), MEDIUM (60%), FAST (90%)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetButton(
                label = "SLOW",
                presetValue = 30,
                currentValue = currentSpeed,
                onSelect = { onSpeedChange(30) },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                label = "MEDIUM",
                presetValue = 60,
                currentValue = currentSpeed,
                onSelect = { onSpeedChange(60) },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                label = "FAST",
                presetValue = 90,
                currentValue = currentSpeed,
                onSelect = { onSpeedChange(90) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navy Blue Slider
        Slider(
            value = currentSpeed.toFloat(),
            onValueChange = { onSpeedChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = NavyBlue,
                activeTrackColor = NavyBlue,
                inactiveTrackColor = InactiveTrack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("motor_speed_slider")
        )
    }
}

@Composable
private fun PresetButton(
    label: String,
    presetValue: Int,
    currentValue: Int,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = (currentValue in (presetValue - 10)..(presetValue + 10))
    if (isSelected) {
        Button(
            onClick = onSelect,
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyBlue,
                contentColor = RoyalWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.height(40.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        OutlinedButton(
            onClick = onSelect,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NavyBlue
            ),
            border = BorderStroke(1.dp, NavyBlue),
            modifier = modifier.height(40.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SafetyEvent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun SafetyStatusCard(
    isEmergencyActive: Boolean,
    safetyEvent: SafetyEvent?,
    onEmergencyStop: () -> Unit,
    onResetEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmergency = isEmergencyActive || (safetyEvent?.event == "flame_detected" && safetyEvent.active)
    val isWarning = safetyEvent?.event == "low_water" && safetyEvent.active

    val (statusLabel, statusColor, statusIcon) = when {
        isEmergency -> Triple("EMERGENCY", StatusRed, Icons.Default.Dangerous)
        isWarning -> Triple("WARNING", StatusAmber, Icons.Default.Warning)
        else -> Triple("SAFE", StatusGreen, Icons.Default.Shield)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(
                if (isEmergency) 2.dp else 1.dp,
                if (isEmergency) StatusRed else CardBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
            .testTag("safety_status_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SAFETY STATUS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isEmergency) "HALTED: All systems safe" else "Hardware Watchdog Active",
                    fontSize = 12.sp,
                    color = NavyBlue.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = statusLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Emergency Stop Control
        if (!isEmergency) {
            Button(
                onClick = onEmergencyStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusRed,
                    contentColor = RoyalWhite
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("emergency_stop_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Dangerous,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EMERGENCY STOP",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 0.8.sp
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onEmergencyStop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRed,
                        contentColor = RoyalWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        text = "FORCE STOP",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = onResetEmergency,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("reset_emergency_button")
                ) {
                    Text(
                        text = "RESET SAFETY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SafetyEvent
import com.example.service.CommandUiState
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusRed

@Composable
fun SafetyBanner(
    safetyEvent: SafetyEvent?,
    isEmergencyActive: Boolean,
    commandUiState: CommandUiState,
    isRobotConnectionLost: Boolean,
    onRetryCommand: () -> Unit,
    onStopRobot: () -> Unit,
    onResetEmergency: () -> Unit,
    onDismissAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Flame Detected Alert
        if (safetyEvent?.event == "flame_detected" && safetyEvent.active) {
            AlertCard(
                title = "FLAME DETECTED",
                subtitle = "ROBOT SAFETY STOP ACTIVE",
                accentColor = StatusRed,
                onPrimaryAction = onStopRobot,
                primaryActionText = "CONFIRM STOP",
                onSecondaryAction = onResetEmergency,
                secondaryActionText = "RESET SAFETY"
            )
        }

        // 2. Emergency Stop Active
        if (isEmergencyActive && safetyEvent?.event != "flame_detected") {
            AlertCard(
                title = "EMERGENCY STOP ACTIVE",
                subtitle = "All motors and pumps halted",
                accentColor = StatusRed,
                onPrimaryAction = onResetEmergency,
                primaryActionText = "RESUME OPERATION",
                onSecondaryAction = null,
                secondaryActionText = null
            )
        }

        // 3. Robot Connection Lost (Watchdog)
        if (isRobotConnectionLost && !isEmergencyActive) {
            AlertCard(
                title = "ROBOT CONNECTION LOST",
                subtitle = "Heartbeat timeout. Controls disabled for safety.",
                accentColor = StatusAmber,
                onPrimaryAction = onStopRobot,
                primaryActionText = "STOP ROBOT",
                onSecondaryAction = null,
                secondaryActionText = null
            )
        }

        // 4. Command Status: Timeout ("COMMAND NOT CONFIRMED")
        if (commandUiState is CommandUiState.Timeout) {
            AlertCard(
                title = "COMMAND NOT CONFIRMED",
                subtitle = "No ACK received from robot within 3s for '${commandUiState.description}'",
                accentColor = StatusAmber,
                onPrimaryAction = onRetryCommand,
                primaryActionText = "RETRY",
                onSecondaryAction = onStopRobot,
                secondaryActionText = "STOP ROBOT",
                onClose = onDismissAlert
            )
        }

        // 5. Command Status: Rejected
        if (commandUiState is CommandUiState.Rejected) {
            AlertCard(
                title = "ROBOT REJECTED COMMAND",
                subtitle = "Reason: ${commandUiState.error}",
                accentColor = StatusRed,
                onPrimaryAction = onStopRobot,
                primaryActionText = "STOP ROBOT",
                onSecondaryAction = null,
                secondaryActionText = null,
                onClose = onDismissAlert
            )
        }
    }
}

@Composable
private fun AlertCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    onPrimaryAction: (() -> Unit)?,
    primaryActionText: String?,
    onSecondaryAction: (() -> Unit)?,
    secondaryActionText: String?,
    onClose: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(2.dp, accentColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("safety_alert_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = NavyBlue
                )
            }
            if (onClose != null) {
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NavyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (onPrimaryAction != null || onSecondaryAction != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onPrimaryAction != null && primaryActionText != null) {
                    Button(
                        onClick = onPrimaryAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = RoyalWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("alert_primary_button")
                    ) {
                        Text(
                            text = primaryActionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                if (onSecondaryAction != null && secondaryActionText != null) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("alert_secondary_button")
                    ) {
                        Text(
                            text = secondaryActionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

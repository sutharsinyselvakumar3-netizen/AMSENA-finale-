package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.MqttConnectionState
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun ConnectionStatusBadge(
    mqttState: MqttConnectionState,
    isRobotOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // MQTT status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val (mqttColor, mqttText) = when (mqttState) {
                is MqttConnectionState.Connected -> Pair(StatusGreen, "MQTT")
                is MqttConnectionState.Connecting -> Pair(StatusAmber, "CONNECTING")
                is MqttConnectionState.Disconnected -> Pair(LightGrey, "MQTT OFF")
                is MqttConnectionState.Error -> Pair(StatusRed, "MQTT ERR")
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(mqttColor)
            )
            Text(
                text = mqttText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        }

        // Separator
        Box(
            modifier = Modifier
                .size(width = 1.dp, height = 12.dp)
                .background(CardBorder)
        )

        // Robot status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val robotColor = if (isRobotOnline) StatusGreen else LightGrey
            val robotText = if (isRobotOnline) "ROBOT ON" else "ROBOT OFF"
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(robotColor)
            )
            Text(
                text = robotText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        }
    }
}

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Stop
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
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusRed

@Composable
fun MovementControl(
    isEnabled: Boolean,
    isEmergency: Boolean,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("movement_control_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ROBOT MOVEMENT",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
            if (!isEnabled) {
                Text(
                    text = "CONTROLS DISABLED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FORWARD Button
        PhysicalButton(
            text = "FORWARD",
            icon = Icons.Default.ArrowUpward,
            enabled = isEnabled,
            color = NavyBlue,
            onClick = onForward,
            modifier = Modifier
                .width(160.dp)
                .height(60.dp)
                .testTag("btn_forward")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LEFT, STOP, RIGHT Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PhysicalButton(
                text = "LEFT",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                enabled = isEnabled,
                color = NavyBlue,
                onClick = onLeft,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("btn_left")
            )

            Spacer(modifier = Modifier.width(10.dp))

            PhysicalButton(
                text = "STOP",
                icon = Icons.Default.Stop,
                enabled = true, // STOP can always be pressed
                color = if (isEmergency) StatusRed else NavyBlue,
                onClick = onStop,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("btn_stop")
            )

            Spacer(modifier = Modifier.width(10.dp))

            PhysicalButton(
                text = "RIGHT",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                enabled = isEnabled,
                color = NavyBlue,
                onClick = onRight,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("btn_right")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // REVERSE Button
        PhysicalButton(
            text = "REVERSE",
            icon = Icons.Default.ArrowDownward,
            enabled = isEnabled,
            color = NavyBlue,
            onClick = onBackward,
            modifier = Modifier
                .width(160.dp)
                .height(60.dp)
                .testTag("btn_reverse")
        )
    }
}

@Composable
private fun PhysicalButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = RoyalWhite,
            disabledContainerColor = LightGrey,
            disabledContentColor = RoyalWhite.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

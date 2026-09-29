package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusRed

@Composable
fun WaterLevelCard(
    waterPercent: Int,
    lowLimit: Int = 20,
    modifier: Modifier = Modifier
) {
    val clamped = waterPercent.coerceIn(0, 100)
    val fraction = clamped / 100f

    // Normal: Navy Blue, Low: Amber, Critical: Red (<10%)
    val statusColor = when {
        clamped <= 10 -> StatusRed
        clamped <= lowLimit -> StatusAmber
        else -> NavyBlue
    }

    val statusLabel = when {
        clamped <= 10 -> "CRITICAL LEVEL"
        clamped <= lowLimit -> "LOW LEVEL"
        else -> "NORMAL"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("water_level_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WATER LEVEL",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = statusLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            Text(
                text = "$clamped%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tank-style clean indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(InactiveTrack)
                .border(1.dp, CardBorder, RoundedCornerShape(9.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(9.dp))
                    .background(statusColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Segment markings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%", fontSize = 10.sp, color = NavyBlue.copy(alpha = 0.5f))
            Text("25%", fontSize = 10.sp, color = NavyBlue.copy(alpha = 0.5f))
            Text("50%", fontSize = 10.sp, color = NavyBlue.copy(alpha = 0.5f))
            Text("75%", fontSize = 10.sp, color = NavyBlue.copy(alpha = 0.5f))
            Text("100%", fontSize = 10.sp, color = NavyBlue.copy(alpha = 0.5f))
        }
    }
}

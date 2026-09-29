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
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite

@Composable
fun SoilSensorCard(
    soilPercent: Int,
    modifier: Modifier = Modifier
) {
    val progress = (soilPercent.coerceIn(0, 100)) / 100f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("soil_sensor_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SOIL SENSOR",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Soil Moisture",
                    fontSize = 12.sp,
                    color = NavyBlue.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "$soilPercent%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = NavyBlue
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navy Blue Progress Bar with Light Grey track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LightGrey)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavyBlue)
            )
        }
    }
}

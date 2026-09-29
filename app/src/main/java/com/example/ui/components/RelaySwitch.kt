package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun RelaySwitchCard(
    title: String,
    subtitle: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = NavyBlue.copy(alpha = 0.7f)
                )
            }

            // Custom Physical Switch: OFF: Light Grey, ON: Navy Blue. (NEVER Green)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isOn) "ON" else "OFF",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isOn) NavyBlue else LightGrey
                )

                CustomNavySwitch(
                    checked = isOn,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}

@Composable
fun CustomNavySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackBg = if (checked) NavyBlue else LightGrey
    val thumbAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = modifier
            .width(56.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(trackBg)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = thumbAlignment
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(RoyalWhite)
        )
    }
}

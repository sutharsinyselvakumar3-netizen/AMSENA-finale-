package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraStatus
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceWhite

@Composable
fun CameraCard(
    cameraStatus: CameraStatus,
    currentFrame: Bitmap?,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("camera_card")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CAMERA",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (cameraStatus.isOnline) StatusGreen else LightGrey)
                )
                Text(
                    text = if (cameraStatus.isOnline) "ONLINE" else "OFFLINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 16:9 Live Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (cameraStatus.isOnline && currentFrame != null) {
                Image(
                    bitmap = currentFrame.asImageBitmap(),
                    contentDescription = "Real live camera feed",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Offline Placeholder
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = "Camera Offline",
                        tint = LightGrey,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CAMERA OFFLINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CHECK CAMERA CONNECTION",
                        fontSize = 11.sp,
                        color = NavyBlue.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons: RETRY and CAMERA SETTINGS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyBlue,
                    contentColor = RoyalWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("camera_retry_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RETRY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NavyBlue
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("camera_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = NavyBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SETTINGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

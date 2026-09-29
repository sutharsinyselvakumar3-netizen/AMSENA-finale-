package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MqttSettings
import com.example.service.MqttConnectionState
import com.example.ui.components.CustomNavySwitch
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LightGrey
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalWhite
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun SettingsScreen(
    currentSettings: MqttSettings,
    mqttState: MqttConnectionState,
    testResult: String?,
    onSaveAndConnect: (MqttSettings) -> Unit,
    onDisconnect: () -> Unit,
    onTestConnection: () -> Unit,
    onClearTestResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brokerHost by remember(currentSettings) { mutableStateOf(currentSettings.brokerHost) }
    var brokerPort by remember(currentSettings) { mutableStateOf(currentSettings.port.toString()) }
    var username by remember(currentSettings) { mutableStateOf(currentSettings.username) }
    var password by remember(currentSettings) { mutableStateOf(currentSettings.password) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var clientId by remember(currentSettings) { mutableStateOf(currentSettings.clientId) }
    var robotId by remember(currentSettings) { mutableStateOf(currentSettings.robotId) }
    var robotName by remember(currentSettings) { mutableStateOf(currentSettings.robotName) }
    var useTls by remember(currentSettings) { mutableStateOf(currentSettings.useTls) }
    var keepAlive by remember(currentSettings) { mutableStateOf(currentSettings.keepAlive.toString()) }
    var qos by remember(currentSettings) { mutableStateOf(currentSettings.qos.toString()) }

    var cameraHost by remember(currentSettings) { mutableStateOf(currentSettings.cameraHost) }
    var cameraPort by remember(currentSettings) { mutableStateOf(currentSettings.cameraPort.toString()) }
    var streamPath by remember(currentSettings) { mutableStateOf(currentSettings.cameraStreamPath) }
    var capturePath by remember(currentSettings) { mutableStateOf(currentSettings.cameraCapturePath) }

    var lowWaterLimit by remember(currentSettings) { mutableStateOf(currentSettings.lowWaterLimit.toString()) }
    var commandTimeout by remember(currentSettings) { mutableStateOf(currentSettings.commandTimeoutSec.toString()) }
    var heartbeatTimeout by remember(currentSettings) { mutableStateOf(currentSettings.heartbeatTimeoutSec.toString()) }

    var saveConfirmation by remember { mutableStateOf(false) }

    val buildSettings = {
        MqttSettings(
            brokerHost = brokerHost.trim(),
            port = brokerPort.toIntOrNull() ?: 1883,
            username = username.trim(),
            password = password,
            clientId = clientId.trim(),
            robotId = robotId.trim(),
            robotName = robotName.trim(),
            useTls = useTls,
            keepAlive = keepAlive.toIntOrNull() ?: 60,
            qos = qos.toIntOrNull() ?: 1,
            cameraHost = cameraHost.trim(),
            cameraPort = cameraPort.toIntOrNull() ?: 81,
            cameraStreamPath = streamPath.trim(),
            cameraCapturePath = capturePath.trim(),
            lowWaterLimit = lowWaterLimit.toIntOrNull() ?: 20,
            commandTimeoutSec = commandTimeout.toIntOrNull() ?: 3,
            heartbeatTimeoutSec = heartbeatTimeout.toIntOrNull() ?: 10
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RoyalWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "COMMUNICATION & ROBOT CONFIGURATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Text(
                text = "MQTT, Camera & Safety Watchdog",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        }

        // Test Connection Notification Banner
        if (testResult != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoyalWhite)
                    .border(1.dp, NavyBlue, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = testResult,
                    fontSize = 12.sp,
                    color = NavyBlue,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClearTestResult, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NavyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // SECTION 1: MQTT CONNECTION
        SettingsSectionCard(title = "MQTT CONNECTION") {
            SettingsInputField(
                label = "BROKER HOST",
                value = brokerHost,
                onValueChange = { brokerHost = it },
                placeholder = "e.g. broker.hivemq.com"
            )
            SettingsInputField(
                label = "PORT",
                value = brokerPort,
                onValueChange = { brokerPort = it },
                keyboardType = KeyboardType.Number,
                placeholder = "1883 or 8883"
            )
            SettingsInputField(
                label = "USERNAME",
                value = username,
                onValueChange = { username = it },
                placeholder = "robot_user (optional)"
            )
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("PASSWORD", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = NavyBlue
                        )
                    }
                },
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            SettingsInputField(
                label = "CLIENT ID",
                value = clientId,
                onValueChange = { clientId = it },
                placeholder = "AI-COMPANION-APP"
            )

            SettingsInputField(
                label = "ROBOT ID",
                value = robotId,
                onValueChange = { robotId = it },
                placeholder = "AI-COMPANION-001"
            )

            // TLS Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("USE TLS / SSL", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                    Text("Secure TLS port 8883", fontSize = 11.sp, color = NavyBlue.copy(alpha = 0.6f))
                }
                CustomNavySwitch(
                    checked = useTls,
                    onCheckedChange = { useTls = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsInputField(
                    label = "KEEP ALIVE (s)",
                    value = keepAlive,
                    onValueChange = { keepAlive = it },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                SettingsInputField(
                    label = "QOS (0 or 1)",
                    value = qos,
                    onValueChange = { qos = it },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons: SAVE, CONNECT, DISCONNECT, TEST CONNECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val s = buildSettings()
                        onSaveAndConnect(s)
                        saveConfirmation = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyBlue,
                        contentColor = RoyalWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("SAVE & CONNECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDisconnect,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("DISCONNECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = onTestConnection,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TEST CONNECTION", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // SECTION 2: ROBOT IDENTITY
        SettingsSectionCard(title = "ROBOT IDENTITY") {
            SettingsInputField(
                label = "ROBOT ID",
                value = robotId,
                onValueChange = { robotId = it },
                placeholder = "AI-COMPANION-001"
            )
            SettingsInputField(
                label = "ROBOT NAME",
                value = robotName,
                onValueChange = { robotName = it },
                placeholder = "Smart Onion Robot"
            )
        }

        // SECTION 3: CAMERA
        SettingsSectionCard(title = "CAMERA STREAM") {
            SettingsInputField(
                label = "CAMERA HOST",
                value = cameraHost,
                onValueChange = { cameraHost = it },
                placeholder = "camera.example.com or 192.168.x.x"
            )
            SettingsInputField(
                label = "CAMERA PORT",
                value = cameraPort,
                onValueChange = { cameraPort = it },
                keyboardType = KeyboardType.Number,
                placeholder = "81"
            )
            SettingsInputField(
                label = "STREAM PATH",
                value = streamPath,
                onValueChange = { streamPath = it },
                placeholder = "stream"
            )
            SettingsInputField(
                label = "CAPTURE PATH",
                value = capturePath,
                onValueChange = { capturePath = it },
                placeholder = "capture"
            )
        }

        // SECTION 4: SAFETY THRESHOLDS
        SettingsSectionCard(title = "SAFETY WATCHDOG") {
            SettingsInputField(
                label = "LOW WATER LIMIT (%)",
                value = lowWaterLimit,
                onValueChange = { lowWaterLimit = it },
                keyboardType = KeyboardType.Number,
                placeholder = "20"
            )
            SettingsInputField(
                label = "COMMAND TIMEOUT (s)",
                value = commandTimeout,
                onValueChange = { commandTimeout = it },
                keyboardType = KeyboardType.Number,
                placeholder = "3"
            )
            SettingsInputField(
                label = "HEARTBEAT TIMEOUT (s)",
                value = heartbeatTimeout,
                onValueChange = { heartbeatTimeout = it },
                keyboardType = KeyboardType.Number,
                placeholder = "10"
            )
        }

        // Final Save All Button
        Button(
            onClick = {
                val s = buildSettings()
                onSaveAndConnect(s)
                saveConfirmation = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyBlue,
                contentColor = RoyalWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_settings_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE ALL CONFIGURATIONS",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue,
            letterSpacing = 0.5.sp
        )
        content()
    }
}

@Composable
private fun SettingsInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        placeholder = { Text(placeholder, fontSize = 12.sp, color = LightGrey) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = textFieldColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NavyBlue,
    unfocusedBorderColor = CardBorder,
    focusedLabelColor = NavyBlue,
    unfocusedLabelColor = NavyBlue.copy(alpha = 0.6f),
    cursorColor = NavyBlue,
    focusedTextColor = NavyBlue,
    unfocusedTextColor = NavyBlue
)

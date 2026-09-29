package com.example.service

import android.util.Log
import com.example.model.CommandAck
import com.example.model.CommandMessage
import com.example.model.MqttSettings
import com.example.model.RobotStatus
import com.example.model.RobotTelemetry
import com.example.model.SafetyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger

sealed class CommandUiState {
    object Idle : CommandUiState()
    data class Pending(val commandId: String, val description: String, val sentAt: Long) : CommandUiState()
    data class Confirmed(val commandId: String, val description: String) : CommandUiState()
    data class Timeout(val commandId: String, val description: String, val lastAction: () -> Unit) : CommandUiState()
    data class Rejected(val commandId: String, val error: String) : CommandUiState()
}

class RobotService(
    private val mqttService: MqttService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val TAG = "RobotService"
    }

    private val cmdCounter = AtomicInteger(100)

    private val _telemetry = MutableStateFlow(RobotTelemetry())
    val telemetry: StateFlow<RobotTelemetry> = _telemetry.asStateFlow()

    private val _robotStatus = MutableStateFlow(RobotStatus())
    val robotStatus: StateFlow<RobotStatus> = _robotStatus.asStateFlow()

    private val _commandUiState = MutableStateFlow<CommandUiState>(CommandUiState.Idle)
    val commandUiState: StateFlow<CommandUiState> = _commandUiState.asStateFlow()

    private val _safetyEvent = MutableStateFlow<SafetyEvent?>(null)
    val safetyEvent: StateFlow<SafetyEvent?> = _safetyEvent.asStateFlow()

    private val _isEmergencyStopActive = MutableStateFlow(false)
    val isEmergencyStopActive: StateFlow<Boolean> = _isEmergencyStopActive.asStateFlow()

    private val _isRobotConnectionLost = MutableStateFlow(false)
    val isRobotConnectionLost: StateFlow<Boolean> = _isRobotConnectionLost.asStateFlow()

    private var currentSettings: MqttSettings = MqttSettings()
    private var pendingTimeoutJob: Job? = null
    private var watchdogJob: Job? = null

    init {
        // Collect incoming messages from MQTT
        scope.launch {
            mqttService.incomingMessages.collect { (topic, payload) ->
                handleIncomingMessage(topic, payload)
            }
        }

        // Start heartbeat watchdog
        startWatchdog()
    }

    fun updateSettings(settings: MqttSettings) {
        currentSettings = settings
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = scope.launch {
            while (isActive) {
                delay(2000)
                val now = System.currentTimeMillis()
                val lastReceived = maxOf(_robotStatus.value.lastReceivedAt, _telemetry.value.lastReceivedAt)
                val timeoutMs = currentSettings.heartbeatTimeoutSec * 1000L

                if (lastReceived > 0 && (now - lastReceived > timeoutMs)) {
                    if (!_isRobotConnectionLost.value) {
                        _isRobotConnectionLost.value = true
                        Log.w(TAG, "Heartbeat watchdog: Robot connection lost (no message for ${timeoutMs}ms)")
                    }
                } else if (lastReceived > 0 && (now - lastReceived <= timeoutMs)) {
                    if (_isRobotConnectionLost.value) {
                        _isRobotConnectionLost.value = false
                    }
                }
            }
        }
    }

    private fun handleIncomingMessage(topic: String, payload: String) {
        val robotId = currentSettings.robotId
        val now = System.currentTimeMillis()

        try {
            when {
                topic == "ai_companion/$robotId/ack" -> {
                    val json = JSONObject(payload)
                    val ack = CommandAck(
                        commandId = json.optString("command_id", ""),
                        event = json.optString("event", null),
                        success = json.optBoolean("success", false),
                        motorState = json.optString("motor_state", null),
                        speed = if (json.has("speed")) json.optInt("speed") else null,
                        error = json.optString("error", null),
                        timestamp = json.optLong("timestamp", now)
                    )
                    handleCommandAck(ack)
                }

                topic == "ai_companion/$robotId/status" -> {
                    val json = JSONObject(payload)
                    val status = RobotStatus(
                        robotId = json.optString("robot_id", robotId),
                        status = json.optString("status", "offline"),
                        timestamp = json.optLong("timestamp", now),
                        lastReceivedAt = now
                    )
                    _robotStatus.value = status
                    _isRobotConnectionLost.value = false
                }

                topic == "ai_companion/$robotId/telemetry" -> {
                    val json = JSONObject(payload)
                    val telemetry = RobotTelemetry(
                        online = json.optBoolean("online", true),
                        motorState = json.optString("motor_state", "stop"),
                        motorSpeed = json.optInt("motor_speed", 50),
                        cameraServo = json.optInt("camera_servo", 90),
                        soilServo = json.optInt("soil_servo", 0),
                        soilPercent = json.optInt("soil_percent", 0),
                        waterPercent = json.optInt("water_percent", 0),
                        soilRelay = json.optBoolean("soil_relay", false),
                        waterPump = json.optBoolean("water_pump", false),
                        flame = json.optBoolean("flame", false),
                        batteryPercent = json.optInt("battery_percent", 0),
                        timestamp = json.optLong("timestamp", now),
                        lastReceivedAt = now
                    )
                    _telemetry.value = telemetry
                    _isRobotConnectionLost.value = false

                    if (telemetry.flame) {
                        _safetyEvent.value = SafetyEvent(
                            event = "flame_detected",
                            active = true,
                            message = "FLAME DETECTED - ROBOT SAFETY STOP",
                            timestamp = now
                        )
                        _isEmergencyStopActive.value = true
                    }
                }

                topic == "ai_companion/$robotId/event" -> {
                    val json = JSONObject(payload)
                    val event = json.optString("event", "")
                    val active = json.optBoolean("active", false)
                    val eventObj = SafetyEvent(
                        event = event,
                        active = active,
                        message = when (event) {
                            "flame_detected" -> "FLAME DETECTED - ROBOT SAFETY STOP"
                            "emergency_stop" -> "EMERGENCY STOP ACTIVE"
                            "low_water" -> "LOW WATER - PUMP BLOCKED"
                            else -> "SAFETY EVENT: $event"
                        },
                        timestamp = now
                    )
                    _safetyEvent.value = eventObj

                    if (event == "flame_detected" && active) {
                        _isEmergencyStopActive.value = true
                    } else if (event == "emergency_stop") {
                        _isEmergencyStopActive.value = active
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message on $topic: ${e.message}")
        }
    }

    private fun handleCommandAck(ack: CommandAck) {
        val currentUiState = _commandUiState.value
        val pendingId = when (currentUiState) {
            is CommandUiState.Pending -> currentUiState.commandId
            is CommandUiState.Timeout -> currentUiState.commandId
            else -> null
        }

        if (pendingId == ack.commandId || pendingId == null) {
            pendingTimeoutJob?.cancel()
            pendingTimeoutJob = null

            if (ack.success) {
                val desc = (currentUiState as? CommandUiState.Pending)?.description ?: "Command"
                _commandUiState.value = CommandUiState.Confirmed(ack.commandId, desc)

                // Clear confirmed state after brief moment
                scope.launch {
                    delay(2000)
                    if (_commandUiState.value is CommandUiState.Confirmed) {
                        _commandUiState.value = CommandUiState.Idle
                    }
                }
            } else {
                val errorMsg = ack.error ?: "REJECTED_BY_ROBOT"
                _commandUiState.value = CommandUiState.Rejected(ack.commandId, errorMsg)

                if (errorMsg.equals("LOW_WATER", ignoreCase = true)) {
                    _safetyEvent.value = SafetyEvent("low_water", true, "LOW WATER - PUMP BLOCKED")
                } else if (errorMsg.equals("SAFETY_STOP", ignoreCase = true)) {
                    _safetyEvent.value = SafetyEvent("safety_stop", true, "ROBOT SAFETY STOP ACTIVE")
                }
            }
        }
    }

    private fun generateCommandId(): String {
        return "CMD-${cmdCounter.incrementAndGet()}"
    }

    private fun sendCommand(
        cmd: CommandMessage,
        description: String,
        actionRetry: () -> Unit
    ) {
        val robotId = currentSettings.robotId
        val topic = "ai_companion/$robotId/command"

        val json = JSONObject().apply {
            put("command_id", cmd.commandId)
            put("command", cmd.command)
            cmd.action?.let { put("action", it) }
            cmd.speed?.let { put("speed", it) }
            cmd.value?.let { put("value", it) }
            cmd.angle?.let { put("angle", it) }
            cmd.state?.let { put("state", it) }
            put("timestamp", cmd.timestamp)
        }

        _commandUiState.value = CommandUiState.Pending(
            commandId = cmd.commandId,
            description = description,
            sentAt = System.currentTimeMillis()
        )

        // Set ACK timeout (default 3s)
        pendingTimeoutJob?.cancel()
        pendingTimeoutJob = scope.launch {
            delay(currentSettings.commandTimeoutSec * 1000L)
            if (_commandUiState.value is CommandUiState.Pending) {
                _commandUiState.value = CommandUiState.Timeout(
                    commandId = cmd.commandId,
                    description = description,
                    lastAction = actionRetry
                )
            }
        }

        mqttService.publish(
            topic = topic,
            payload = json.toString(),
            qos = 1,
            retained = false,
            onFailure = { err ->
                pendingTimeoutJob?.cancel()
                _commandUiState.value = CommandUiState.Rejected(
                    cmd.commandId,
                    err?.localizedMessage ?: "PUBLISH_FAILED"
                )
            }
        )
    }

    fun sendMotorCommand(action: String, speed: Int) {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "motor",
            action = action.lowercase(),
            speed = speed
        )
        sendCommand(cmd, "Motor ${action.uppercase()}") {
            sendMotorCommand(action, speed)
        }
    }

    fun sendStop() {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "motor",
            action = "stop",
            speed = 0
        )
        sendCommand(cmd, "Motor STOP") {
            sendStop()
        }
    }

    fun sendSpeed(speed: Int) {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "motor_speed",
            value = speed
        )
        sendCommand(cmd, "Speed $speed%") {
            sendSpeed(speed)
        }
    }

    fun sendCameraServo(angle: Int) {
        val clamped = angle.coerceIn(0, 180)
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "camera_servo",
            angle = clamped
        )
        sendCommand(cmd, "Camera Servo $clamped°") {
            sendCameraServo(clamped)
        }
    }

    fun sendSoilServo(angle: Int) {
        val clamped = angle.coerceIn(0, 45) // Never allow > 45°
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "soil_servo",
            angle = clamped
        )
        sendCommand(cmd, "Soil Servo $clamped°") {
            sendSoilServo(clamped)
        }
    }

    fun sendSoilRelay(state: Boolean) {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "soil_relay",
            state = state
        )
        sendCommand(cmd, "Soil Relay ${if (state) "ON" else "OFF"}") {
            sendSoilRelay(state)
        }
    }

    fun sendWaterPump(state: Boolean) {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "water_pump",
            state = state
        )
        sendCommand(cmd, "Water Pump ${if (state) "ON" else "OFF"}") {
            sendWaterPump(state)
        }
    }

    fun sendEmergencyStop() {
        val cmdId = generateCommandId()
        val cmd = CommandMessage(
            commandId = cmdId,
            command = "emergency_stop"
        )
        _isEmergencyStopActive.value = true
        sendCommand(cmd, "EMERGENCY STOP") {
            sendEmergencyStop()
        }
    }

    fun resetEmergencyStop() {
        _isEmergencyStopActive.value = false
        _safetyEvent.value = null
        _commandUiState.value = CommandUiState.Idle
    }

    fun dismissCommandAlert() {
        _commandUiState.value = CommandUiState.Idle
    }
}

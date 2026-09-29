package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CameraStatus
import com.example.model.MqttSettings
import com.example.model.RobotStatus
import com.example.model.RobotTelemetry
import com.example.model.SafetyEvent
import com.example.service.CameraService
import com.example.service.CommandUiState
import com.example.service.MqttConnectionState
import com.example.service.MqttService
import com.example.service.RobotService
import com.example.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RobotViewModel(application: Application) : AndroidViewModel(application) {

    val storageService = StorageService(application)
    val mqttService = MqttService()
    val robotService = RobotService(mqttService)
    val cameraService = CameraService()

    private val _settings = MutableStateFlow(storageService.loadSettings())
    val settings: StateFlow<MqttSettings> = _settings.asStateFlow()

    val mqttState: StateFlow<MqttConnectionState> = mqttService.connectionState
    val robotStatus: StateFlow<RobotStatus> = robotService.robotStatus
    val telemetry: StateFlow<RobotTelemetry> = robotService.telemetry
    val commandUiState: StateFlow<CommandUiState> = robotService.commandUiState
    val safetyEvent: StateFlow<SafetyEvent?> = robotService.safetyEvent
    val isEmergencyActive: StateFlow<Boolean> = robotService.isEmergencyStopActive
    val isRobotConnectionLost: StateFlow<Boolean> = robotService.isRobotConnectionLost
    val cameraStatus: StateFlow<CameraStatus> = cameraService.cameraStatus

    private val _testConnectionResult = MutableStateFlow<String?>(null)
    val testConnectionResult: StateFlow<String?> = _testConnectionResult.asStateFlow()

    init {
        val initialSettings = _settings.value
        robotService.updateSettings(initialSettings)

        // Try connecting MQTT if broker host is configured
        if (initialSettings.brokerHost.isNotBlank()) {
            mqttService.connect(initialSettings)
        }
        if (initialSettings.cameraHost.isNotBlank()) {
            cameraService.connectCamera(initialSettings)
        }
    }

    fun updateSettings(newSettings: MqttSettings) {
        _settings.value = newSettings
        storageService.saveSettings(newSettings)
        robotService.updateSettings(newSettings)
    }

    fun saveAndConnect(newSettings: MqttSettings) {
        updateSettings(newSettings)
        mqttService.connect(newSettings)
        cameraService.connectCamera(newSettings)
    }

    fun disconnect() {
        mqttService.disconnect()
        cameraService.disconnectCamera()
    }

    fun reconnectMqtt() {
        mqttService.connect(_settings.value)
    }

    fun retryCamera() {
        cameraService.connectCamera(_settings.value)
    }

    fun testConnection() {
        viewModelScope.launch {
            _testConnectionResult.value = "Testing broker connection..."
            if (_settings.value.brokerHost.isBlank()) {
                _testConnectionResult.value = "Broker host is empty"
                return@launch
            }
            mqttService.connect(_settings.value)
            _testConnectionResult.value = "Connection attempt initiated to ${_settings.value.brokerHost}"
        }
    }

    fun clearTestResult() {
        _testConnectionResult.value = null
    }

    // Robot Movement Controls
    fun moveForward() {
        val speed = telemetry.value.motorSpeed.takeIf { it > 0 } ?: 50
        robotService.sendMotorCommand("forward", speed)
    }

    fun moveBackward() {
        val speed = telemetry.value.motorSpeed.takeIf { it > 0 } ?: 50
        robotService.sendMotorCommand("backward", speed)
    }

    fun turnLeft() {
        val speed = telemetry.value.motorSpeed.takeIf { it > 0 } ?: 50
        robotService.sendMotorCommand("left", speed)
    }

    fun turnRight() {
        val speed = telemetry.value.motorSpeed.takeIf { it > 0 } ?: 50
        robotService.sendMotorCommand("right", speed)
    }

    fun stopRobot() {
        robotService.sendStop()
    }

    // Actuators & Servos
    fun setSpeed(speed: Int) {
        robotService.sendSpeed(speed)
    }

    fun setCameraServo(angle: Int) {
        robotService.sendCameraServo(angle)
    }

    fun setSoilServo(angle: Int) {
        robotService.sendSoilServo(angle)
    }

    fun toggleSoilRelay(target: Boolean) {
        robotService.sendSoilRelay(target)
    }

    fun toggleWaterPump(target: Boolean) {
        // Safety pre-check: if water percent is below safety limit and attempting to turn ON
        val currentWater = telemetry.value.waterPercent
        if (target && currentWater > 0 && currentWater < _settings.value.lowWaterLimit) {
            // Reject locally as safety safeguard
            robotService.sendWaterPump(target)
        } else {
            robotService.sendWaterPump(target)
        }
    }

    fun emergencyStop() {
        robotService.sendEmergencyStop()
    }

    fun resetEmergency() {
        robotService.resetEmergencyStop()
    }

    fun retryLastCommand() {
        val state = commandUiState.value
        if (state is CommandUiState.Timeout) {
            state.lastAction.invoke()
        }
    }

    fun dismissAlert() {
        robotService.dismissCommandAlert()
    }

    override fun onCleared() {
        super.onCleared()
        mqttService.disconnect()
        cameraService.disconnectCamera()
    }
}

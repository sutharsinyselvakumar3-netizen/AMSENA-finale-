package com.example.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.model.MqttSettings

class StorageService(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_companion_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BROKER_HOST = "broker_host"
        private const val KEY_PORT = "broker_port"
        private const val KEY_USERNAME = "username"
        private const val KEY_ENC_PASSWORD = "enc_password"
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_ROBOT_ID = "robot_id"
        private const val KEY_ROBOT_NAME = "robot_name"
        private const val KEY_USE_TLS = "use_tls"
        private const val KEY_KEEP_ALIVE = "keep_alive"
        private const val KEY_QOS = "qos"

        private const val KEY_CAMERA_HOST = "camera_host"
        private const val KEY_CAMERA_PORT = "camera_port"
        private const val KEY_CAMERA_STREAM_PATH = "camera_stream_path"
        private const val KEY_CAMERA_CAPTURE_PATH = "camera_capture_path"

        private const val KEY_LOW_WATER_LIMIT = "low_water_limit"
        private const val KEY_COMMAND_TIMEOUT = "command_timeout"
        private const val KEY_HEARTBEAT_TIMEOUT = "heartbeat_timeout"
    }

    fun loadSettings(): MqttSettings {
        val rawPass = prefs.getString(KEY_ENC_PASSWORD, "") ?: ""
        val decodedPassword = try {
            if (rawPass.isNotEmpty()) String(Base64.decode(rawPass, Base64.NO_WRAP)) else ""
        } catch (e: Exception) {
            ""
        }

        return MqttSettings(
            brokerHost = prefs.getString(KEY_BROKER_HOST, "broker.hivemq.com") ?: "broker.hivemq.com",
            port = prefs.getInt(KEY_PORT, 1883),
            username = prefs.getString(KEY_USERNAME, "") ?: "",
            password = decodedPassword,
            clientId = prefs.getString(KEY_CLIENT_ID, "AI-COMPANION-APP") ?: "AI-COMPANION-APP",
            robotId = prefs.getString(KEY_ROBOT_ID, "AI-COMPANION-001") ?: "AI-COMPANION-001",
            robotName = prefs.getString(KEY_ROBOT_NAME, "Smart Onion Robot") ?: "Smart Onion Robot",
            useTls = prefs.getBoolean(KEY_USE_TLS, false),
            keepAlive = prefs.getInt(KEY_KEEP_ALIVE, 60),
            qos = prefs.getInt(KEY_QOS, 1),
            cameraHost = prefs.getString(KEY_CAMERA_HOST, "") ?: "",
            cameraPort = prefs.getInt(KEY_CAMERA_PORT, 81),
            cameraStreamPath = prefs.getString(KEY_CAMERA_STREAM_PATH, "stream") ?: "stream",
            cameraCapturePath = prefs.getString(KEY_CAMERA_CAPTURE_PATH, "capture") ?: "capture",
            lowWaterLimit = prefs.getInt(KEY_LOW_WATER_LIMIT, 20),
            commandTimeoutSec = prefs.getInt(KEY_COMMAND_TIMEOUT, 3),
            heartbeatTimeoutSec = prefs.getInt(KEY_HEARTBEAT_TIMEOUT, 10)
        )
    }

    fun saveSettings(settings: MqttSettings) {
        val encPassword = if (settings.password.isNotEmpty()) {
            Base64.encodeToString(settings.password.toByteArray(), Base64.NO_WRAP)
        } else {
            ""
        }

        prefs.edit()
            .putString(KEY_BROKER_HOST, settings.brokerHost)
            .putInt(KEY_PORT, settings.port)
            .putString(KEY_USERNAME, settings.username)
            .putString(KEY_ENC_PASSWORD, encPassword)
            .putString(KEY_CLIENT_ID, settings.clientId)
            .putString(KEY_ROBOT_ID, settings.robotId)
            .putString(KEY_ROBOT_NAME, settings.robotName)
            .putBoolean(KEY_USE_TLS, settings.useTls)
            .putInt(KEY_KEEP_ALIVE, settings.keepAlive)
            .putInt(KEY_QOS, settings.qos)
            .putString(KEY_CAMERA_HOST, settings.cameraHost)
            .putInt(KEY_CAMERA_PORT, settings.cameraPort)
            .putString(KEY_CAMERA_STREAM_PATH, settings.cameraStreamPath)
            .putString(KEY_CAMERA_CAPTURE_PATH, settings.cameraCapturePath)
            .putInt(KEY_LOW_WATER_LIMIT, settings.lowWaterLimit)
            .putInt(KEY_COMMAND_TIMEOUT, settings.commandTimeoutSec)
            .putInt(KEY_HEARTBEAT_TIMEOUT, settings.heartbeatTimeoutSec)
            .apply()
    }
}

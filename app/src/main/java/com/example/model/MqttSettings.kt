package com.example.model

data class MqttSettings(
    val brokerHost: String = "broker.hivemq.com",
    val port: Int = 1883,
    val username: String = "",
    val password: String = "",
    val clientId: String = "AI-COMPANION-APP",
    val robotId: String = "AI-COMPANION-001",
    val robotName: String = "Smart Onion Robot",
    val useTls: Boolean = false,
    val keepAlive: Int = 60,
    val qos: Int = 1,
    // Camera
    val cameraHost: String = "",
    val cameraPort: Int = 81,
    val cameraStreamPath: String = "stream",
    val cameraCapturePath: String = "capture",
    // Safety
    val lowWaterLimit: Int = 20,
    val commandTimeoutSec: Int = 3,
    val heartbeatTimeoutSec: Int = 10
) {
    fun getStreamUrl(): String {
        if (cameraHost.isBlank()) return ""
        val cleanPath = cameraStreamPath.trimStart('/')
        val proto = if (useTls) "https" else "http"
        return "$proto://$cameraHost:$cameraPort/$cleanPath"
    }

    fun getCaptureUrl(): String {
        if (cameraHost.isBlank()) return ""
        val cleanPath = cameraCapturePath.trimStart('/')
        val proto = if (useTls) "https" else "http"
        return "$proto://$cameraHost:$cameraPort/$cleanPath"
    }
}

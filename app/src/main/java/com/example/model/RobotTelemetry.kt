package com.example.model

data class RobotTelemetry(
    val online: Boolean = false,
    val motorState: String = "stop", // forward, backward, left, right, stop
    val motorSpeed: Int = 50,
    val cameraServo: Int = 90,
    val soilServo: Int = 0,
    val soilPercent: Int = 0,
    val waterPercent: Int = 0,
    val soilRelay: Boolean = false,
    val waterPump: Boolean = false,
    val flame: Boolean = false,
    val batteryPercent: Int = 0,
    val timestamp: Long = 0L,
    val lastReceivedAt: Long = 0L
)

package com.example.model

data class RobotStatus(
    val robotId: String = "",
    val status: String = "offline",
    val timestamp: Long = 0L,
    val lastReceivedAt: Long = 0L
) {
    val isOnline: Boolean get() = status.equals("online", ignoreCase = true)
}

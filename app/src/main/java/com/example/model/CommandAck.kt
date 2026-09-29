package com.example.model

data class CommandAck(
    val commandId: String,
    val event: String? = null,
    val success: Boolean = false,
    val motorState: String? = null,
    val speed: Int? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

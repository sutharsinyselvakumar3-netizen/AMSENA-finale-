package com.example.model

data class CommandMessage(
    val commandId: String,
    val command: String,
    val action: String? = null,
    val speed: Int? = null,
    val value: Int? = null,
    val angle: Int? = null,
    val state: Boolean? = null,
    val timestamp: Long = System.currentTimeMillis()
)

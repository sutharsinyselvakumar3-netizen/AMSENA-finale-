package com.example.model

data class SafetyEvent(
    val event: String,
    val active: Boolean,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

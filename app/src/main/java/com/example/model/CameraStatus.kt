package com.example.model

data class CameraStatus(
    val isOnline: Boolean = false,
    val streamUrl: String = "",
    val lastUpdated: Long = 0L,
    val error: String? = null
)

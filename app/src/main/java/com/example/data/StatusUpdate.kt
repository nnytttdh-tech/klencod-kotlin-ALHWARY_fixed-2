package com.example.data

data class StatusUpdate(
    val id: String,
    val userId: String,
    val userName: String,
    val timestamp: String,
    val statusText: String,
    val gradientStart: Long = 0xFF008069,
    val gradientEnd: Long = 0xFF25D366,
    val isViewed: Boolean = false
)

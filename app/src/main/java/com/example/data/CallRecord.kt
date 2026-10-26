package com.example.data

enum class CallType {
    VOICE, VIDEO
}

enum class CallDirection {
    INCOMING, OUTGOING, MISSED
}

data class CallRecord(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val type: CallType,
    val direction: CallDirection,
    val timestamp: String,
    val duration: String? = null
)

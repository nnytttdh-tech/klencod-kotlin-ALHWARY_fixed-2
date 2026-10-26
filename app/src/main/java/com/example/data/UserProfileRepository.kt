package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Local profile used until a real authentication/backend is connected. */
object UserProfileRepository {
    private const val PREFS = "alhwary_profile"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"

    private val _username = MutableStateFlow("user")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    fun init(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _username.value = prefs.getString(KEY_USERNAME, "user")?.trim().orEmpty().ifBlank { "user" }
        _email.value = prefs.getString(KEY_EMAIL, "")?.trim().orEmpty()
    }

    fun save(username: String, email: String = _email.value, context: Context) {
        val clean = username.trim().removePrefix("@").replace(" ", "_")
            .ifBlank { "user" }
        val cleanEmail = email.trim()
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USERNAME, clean)
            .putString(KEY_EMAIL, cleanEmail)
            .apply()
        _username.value = clean
        _email.value = cleanEmail
    }
}

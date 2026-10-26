package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.ChatRepository
import com.example.data.User
import com.example.data.UserProfileRepository
import com.example.ui.screens.ActiveCallDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.UsersScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UserProfileRepository.init(applicationContext)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedUser by remember { mutableStateOf<User?>(null) }
                    val allUsers by ChatRepository.users.collectAsState()

                    // Keep selectedUser in sync with state updates if any
                    val currentChatUser = selectedUser?.let { sel ->
                        allUsers.find { it.id == sel.id } ?: sel
                    }

                    Crossfade(
                        targetState = currentChatUser,
                        label = "screen_transition"
                    ) { targetUser ->
                        if (targetUser != null) {
                            ChatScreen(
                                user = targetUser,
                                onBack = { selectedUser = null }
                            )
                        } else {
                            UsersScreen(
                                onUserClick = { user ->
                                    selectedUser = user
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleDarkTheme = { isDarkTheme = it }
                            )
                        }
                    }

                    // Active voice/video call overlay
                    ActiveCallDialog()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MyApplicationTheme {
        UsersScreen()
    }
}
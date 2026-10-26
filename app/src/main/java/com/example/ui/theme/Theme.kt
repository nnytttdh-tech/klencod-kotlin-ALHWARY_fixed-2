package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AlhwaryPrimaryLight,
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005244),
    onPrimaryContainer = Color(0xFF86F8DE),
    secondary = AlhwaryAccentGreen,
    onSecondary = Color(0xFF003912),
    background = AlhwaryDarkBg,
    onBackground = Color(0xFFE9EDEF),
    surface = AlhwaryDarkSurface,
    onSurface = Color(0xFFE9EDEF),
    surfaceVariant = AlhwaryDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF8696A0)
)

private val LightColorScheme = lightColorScheme(
    primary = AlhwaryPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F5E9),
    onPrimaryContainer = Color(0xFF002019),
    secondary = AlhwaryAccentGreen,
    onSecondary = Color.White,
    background = AlhwaryLightBg,
    onBackground = Color(0xFF111B21),
    surface = AlhwaryLightSurface,
    onSurface = Color(0xFF111B21),
    surfaceVariant = AlhwaryLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF667781)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinct ALHWARY emerald brand theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

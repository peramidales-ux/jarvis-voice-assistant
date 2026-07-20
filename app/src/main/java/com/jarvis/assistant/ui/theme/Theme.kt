package com.jarvis.assistant.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberPurple,
    secondary = CyberCyan,
    tertiary = CyberPink,
    background = CyberBackgroundDark,
    surface = CyberSurface,
    onPrimary = CyberBackgroundDark,
    onSecondary = CyberBackgroundDark,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography(),
        content = content
    )
}

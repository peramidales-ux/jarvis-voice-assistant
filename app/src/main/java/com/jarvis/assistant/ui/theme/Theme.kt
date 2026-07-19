package com.jarvis.assistant.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisBlue,
    secondary = JarvisAccent,
    background = JarvisBackground,
    surface = JarvisSurface,
    onPrimary = JarvisBackground,
    onSecondary = JarvisBackground,
    onBackground = JarvisText,
    onSurface = JarvisText
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography(),
        content = content
    )
}

package com.jarvis.assistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.assistant.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUser) JarvisBlueDark.copy(alpha = 0.3f) else JarvisSurface
    val textColor = if (isUser) JarvisText else JarvisBlue
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        // Typewriter effect for Jarvis messages
        var displayedText by remember { mutableStateOf("") }
        LaunchedEffect(message) {
            if (!isUser) {
                message.forEachIndexed { index, _ ->
                    displayedText = message.substring(0, index + 1)
                    delay(30)
                }
            } else {
                displayedText = message
            }
        }

        Text(
            text = if (isUser) message else displayedText,
            color = textColor,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(shape)
                .background(backgroundColor)
                .padding(12.dp)
        )
    }
}

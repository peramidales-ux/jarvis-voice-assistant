package com.jarvis.assistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.assistant.ui.theme.*

@Composable
fun JarvisScreen(viewModel: JarvisViewModel = viewModel()) {
    val voiceState by viewModel.voiceEngine.state.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val transcription by viewModel.currentTranscription.collectAsState()

    var showSettings by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(JarvisBackground, JarvisSurface)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "J.A.R.V.I.S.",
                color = JarvisBlue,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 40.dp)
            )

            Text(
                text = "Just A Rather Very Intelligent System",
                color = JarvisBlue.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Holographic Ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clickable { viewModel.toggleListening() },
                contentAlignment = Alignment.Center
            ) {
                HolographicRing(isListening = isListening)

                // Center text
                Text(
                    text = when (voiceState) {
                        VoiceEngine.State.LISTENING -> "Слушаю..."
                        VoiceEngine.State.PROCESSING -> "Обрабатываю..."
                        VoiceEngine.State.SPEAKING -> "Говорю..."
                        else -> "Нажмите"
                    },
                    color = JarvisBlue,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            // Transcription display
            if (transcription.isNotBlank()) {
                Text(
                    text = transcription,
                    color = JarvisText.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chat history
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message.content,
                        isUser = message.role == "user"
                    )
                }
            }

            // Text input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = {
                        Text(
                            "Введите команду...",
                            color = JarvisText.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisText,
                        unfocusedTextColor = JarvisText,
                        focusedBorderColor = JarvisBlue,
                        unfocusedBorderColor = JarvisBlue.copy(alpha = 0.5f),
                        cursorColor = JarvisBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendTextMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(JarvisBlue)
                ) {
                    Text(
                        text = "▶",
                        color = JarvisBackground,
                        fontSize = 20.sp
                    )
                }
            }

            // Settings button
            TextButton(
                onClick = { showSettings = true },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "⚙ Настройки AI",
                    color = JarvisBlue.copy(alpha = 0.7f)
                )
            }
        }

        // Settings dialog
        if (showSettings) {
            SettingsDialog(
                onDismiss = { showSettings = false },
                onSave = { apiKey ->
                    viewModel.configureAi(apiKey)
                    showSettings = false
                }
            )
        }
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var apiKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JarvisSurface,
        title = {
            Text(
                "Настройки AI",
                color = JarvisBlue
            )
        },
        text = {
            Column {
                Text(
                    "Введите API ключ OpenAI:",
                    color = JarvisText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("sk-...", color = JarvisText.copy(alpha = 0.5f))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisText,
                        unfocusedTextColor = JarvisText,
                        focusedBorderColor = JarvisBlue,
                        unfocusedBorderColor = JarvisBlue.copy(alpha = 0.5f),
                        cursorColor = JarvisBlue
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(apiKey) }) {
                Text("Сохранить", color = JarvisBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = JarvisBlue.copy(alpha = 0.7f))
            }
        }
    )
}

package com.jarvis.assistant.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.assistant.R
import com.jarvis.assistant.ui.theme.*
import com.jarvis.assistant.voice.VoiceEngine

@Composable
fun JarvisScreen(viewModel: JarvisViewModel = viewModel()) {
    val voiceState by viewModel.voiceEngine.state.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val transcription by viewModel.currentTranscription.collectAsState()

    var showSettings by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBackgroundDark, CyberBackgroundLight)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with J icon
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_jarvis_logo),
                    contentDescription = "JARVIS",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("J.A.R.V.I.S.", color = CyberCyan, fontSize = 28.sp, fontFamily = FontFamily.Monospace)
                    Text("v2.0 — Cyberpunk Edition", color = CyberPurple, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cyberpunk Ring
            Box(
                modifier = Modifier.size(200.dp).clickable { viewModel.toggleListening() },
                contentAlignment = Alignment.Center
            ) {
                CyberpunkRing(isListening = isListening)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (voiceState) {
                            VoiceEngine.State.LISTENING -> "СЛУШАЮ"
                            VoiceEngine.State.PROCESSING -> "ОБРАБАТЫВАЮ"
                            VoiceEngine.State.SPEAKING -> "ГОВОРЮ"
                            else -> "ГОТОВ"
                        },
                        color = CyberCyan, fontSize = 14.sp, fontFamily = FontFamily.Monospace
                    )
                    if (transcription.isNotBlank()) {
                        Text(transcription, color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chat history
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(messages) { message ->
                    CyberpunkChatBubble(message.content, message.role == "user")
                }
            }

            // Text input
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text("Введите команду...", color = CyberTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = CyberPurple.copy(alpha = 0.5f),
                        cursorColor = CyberCyan
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
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(CyberPurple, CyberCyan)))
                ) {
                    Text("▶", color = CyberBackgroundDark, fontSize = 20.sp)
                }
            }

            // Settings button
            TextButton(onClick = { showSettings = true }, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("⚙ НАСТРОЙКИ AI", color = CyberPurple)
            }
        }

        if (showSettings) {
            CyberpunkSettingsDialog(
                onDismiss = { showSettings = false },
                onSave = { config -> viewModel.updateAiConfig(config); showSettings = false }
            )
        }
    }
}

@Composable
fun CyberpunkChatBubble(message: String, isUser: Boolean, modifier: Modifier = Modifier) {
    val bgColor = if (isUser) CyberPurple.copy(alpha = 0.2f) else CyberSurface
    val textColor = if (isUser) CyberTextPrimary else CyberCyan
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), contentAlignment = alignment) {
        var displayedText by remember { mutableStateOf("") }
        LaunchedEffect(message) {
            if (!isUser) {
                message.forEachIndexed { index, _ ->
                    displayedText = message.substring(0, index + 1)
                    kotlinx.coroutines.delay(30)
                }
            } else {
                displayedText = message
            }
        }
        Text(
            text = if (isUser) message else displayedText,
            color = textColor, fontSize = 14.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.widthIn(max = 280.dp).clip(shape).background(bgColor).padding(12.dp)
        )
    }
}

@Composable
fun CyberpunkSettingsDialog(onDismiss: () -> Unit, onSave: (com.jarvis.assistant.ai.AiConfig) -> Unit) {
    var selectedProvider by remember { mutableStateOf(com.jarvis.assistant.ai.AiProvider.OPENAI) }
    var apiKey by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = { Text("Настройки AI", color = CyberCyan) },
        text = {
            Column {
                Text("Провайдер:", color = CyberTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                com.jarvis.assistant.ai.AiProvider.values().forEach { provider ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedProvider == provider, onClick = { selectedProvider = provider; baseUrl = provider.defaultBaseUrl }, colors = RadioButtonDefaults.colors(selectedColor = CyberPurple, unselectedColor = CyberTextMuted))
                        Text(provider.displayName, color = CyberTextPrimary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, modifier = Modifier.fillMaxWidth(), label = { Text("API Key", color = CyberTextSecondary) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CyberTextPrimary, unfocusedTextColor = CyberTextPrimary, focusedBorderColor = CyberPurple, unfocusedBorderColor = CyberTextMuted, cursorColor = CyberCyan), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Base URL", color = CyberTextSecondary) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CyberTextPrimary, unfocusedTextColor = CyberTextPrimary, focusedBorderColor = CyberPurple, unfocusedBorderColor = CyberTextMuted, cursorColor = CyberCyan), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = model, onValueChange = { model = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Модель", color = CyberTextSecondary) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CyberTextPrimary, unfocusedTextColor = CyberTextPrimary, focusedBorderColor = CyberPurple, unfocusedBorderColor = CyberTextMuted, cursorColor = CyberCyan), singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(com.jarvis.assistant.ai.AiConfig.forProvider(selectedProvider, apiKey, model).copy(baseUrl = baseUrl)) }) {
                Text("Сохранить", color = CyberCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = CyberTextMuted) }
        }
    )
}

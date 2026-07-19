package com.jarvis.assistant.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.ai.AiBackend
import com.jarvis.assistant.ai.AiConfig
import com.jarvis.assistant.commands.Command
import com.jarvis.assistant.commands.CommandHandler
import com.jarvis.assistant.commands.CommandParser
import com.jarvis.assistant.data.ChatDatabase
import com.jarvis.assistant.data.ChatMessage
import com.jarvis.assistant.voice.VoiceEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    val voiceEngine = VoiceEngine(application)
    private val commandParser = CommandParser()
    private val commandHandler = CommandHandler(application)
    private val chatDao = ChatDatabase.getDatabase(application).chatDao()
    private var aiBackend: AiBackend? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _currentTranscription = MutableStateFlow("")
    val currentTranscription: StateFlow<String> = _currentTranscription

    init {
        voiceEngine.initialize()
        loadHistory()
    }

    fun configureAi(apiKey: String) {
        aiBackend = AiBackend(AiConfig(apiKey = apiKey))
    }

    fun toggleListening() {
        if (voiceEngine.isListening()) {
            voiceEngine.stopListening()
            _isListening.value = false
        } else {
            voiceEngine.startListening()
            _isListening.value = true
        }
    }

    fun processVoiceInput(text: String) {
        viewModelScope.launch {
            _isListening.value = false
            voiceEngine.stopListening()

            // Add user message
            val userMessage = ChatMessage(role = "user", content = text)
            chatDao.insert(userMessage)
            _messages.value = _messages.value + userMessage

            // Parse command
            val command = commandParser.parse(text)

            // Handle command or AI
            val response = when (command) {
                is Command.AskAI -> {
                    aiBackend?.ask(text) ?: "AI не настроен. Скажите 'помощь' для списка команд."
                }
                else -> commandHandler.execute(command)
            }

            if (response.isNotBlank()) {
                // Add Jarvis response
                val jarvisMessage = ChatMessage(role = "jarvis", content = response)
                chatDao.insert(jarvisMessage)
                _messages.value = _messages.value + jarvisMessage

                // Speak response
                voiceEngine.speak(response)
            }
        }
    }

    fun sendTextMessage(text: String) {
        processVoiceInput(text)
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = chatDao.getRecentMessages().reversed()
            _messages.value = history
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatDao.clearAll()
            _messages.value = emptyList()
            aiBackend?.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.shutdown()
    }
}

package com.jarvis.assistant.voice

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VoiceEngine(context: Context) {
    enum class State {
        IDLE, LISTENING, PROCESSING, SPEAKING
    }

    private val _state = MutableStateFlow(State.IDLE)
    val state: StateFlow<State> = _state

    private val _lastResult = MutableStateFlow("")
    val lastResult: StateFlow<String> = _lastResult

    private val _lastResponse = MutableStateFlow("")
    val lastResponse: StateFlow<String> = _lastResponse

    private val recognizer = VoiceRecognizer(
        context = context,
        onResult = { text ->
            _lastResult.value = text
            _state.value = State.PROCESSING
        },
        onPartialResult = { text ->
            _lastResult.value = text
        },
        onError = { error ->
            _state.value = State.IDLE
        }
    )

    private val speaker = SpeechSpeaker(
        context = context,
        onReady = {},
        onDone = { _state.value = State.IDLE },
        onError = { _state.value = State.IDLE }
    )

    fun initialize() {
        speaker.initialize()
    }

    fun startListening() {
        _state.value = State.LISTENING
        recognizer.startListening()
    }

    fun stopListening() {
        recognizer.stopListening()
    }

    fun speak(text: String) {
        _lastResponse.value = text
        _state.value = State.SPEAKING
        speaker.speak(text)
    }

    fun stopSpeaking() {
        speaker.stop()
        _state.value = State.IDLE
    }

    fun shutdown() {
        recognizer.stopListening()
        speaker.shutdown()
    }

    fun isListening() = recognizer.isCurrentlyListening()
    fun isSpeaking() = speaker.isSpeaking()
    fun isReady() = speaker.isInitialized()
}

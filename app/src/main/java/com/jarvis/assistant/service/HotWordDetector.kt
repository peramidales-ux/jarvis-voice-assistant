package com.jarvis.assistant.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class HotWordDetector(
    private val context: Context,
    private val onHotWordDetected: () -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private val hotWords = listOf("hey jarvis", "джарвис", "jarvis", "привет jarvis", "эй jarvis")
    private val handler = Handler(Looper.getMainLooper())

    fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) return
        startListening()
    }

    fun stop() {
        stopListening()
    }

    private fun startListening() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
                setRecognitionListener(createListener())
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            handler.postDelayed({ startListening() }, 5000)
        }
    }

    private fun stopListening() {
        try { speechRecognizer?.stopListening(); speechRecognizer?.destroy() } catch (e: Exception) {}
        speechRecognizer = null
        isListening = false
    }

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            isListening = false
            handler.postDelayed({ startListening() }, 1000)
        }
        override fun onError(error: Int) {
            isListening = false
            if (error != SpeechRecognizer.ERROR_CLIENT) {
                handler.postDelayed({ startListening() }, 2000)
            }
        }
        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.lowercase() ?: ""
            if (hotWords.any { text.contains(it) }) {
                onHotWordDetected()
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}

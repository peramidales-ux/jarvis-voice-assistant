package com.jarvis.assistant.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class SpeechSpeaker(
    private val context: Context,
    private val onReady: () -> Unit = {},
    private val onDone: () -> Unit = {},
    private val onError: (String) -> Unit = {}
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    val result = engine.setLanguage(Locale("ru", "RU"))
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        engine.language = Locale.US
                    }
                    engine.setSpeechRate(1.0f)
                    engine.setPitch(0.9f)
                    isReady = true
                    onReady()
                }
            } else {
                onError("TTS инициализация не удалась")
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onDone() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { onError("Ошибка воспроизведения") }
        })
    }

    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun speakSequential(texts: List<String>) {
        tts?.let { engine ->
            texts.forEachIndexed { index, text ->
                val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                engine.speak(text, queueMode, null, "seq_$index")
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }

    fun isSpeaking() = tts?.isSpeaking == true
    fun isInitialized() = isReady
}

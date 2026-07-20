package com.jarvis.assistant.ai

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AiBackend(private var config: AiConfig) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val conversationHistory = mutableListOf<AiMessage>()

    init {
        conversationHistory.add(AiMessage(role = "system", content = config.systemPrompt))
    }

    fun updateConfig(newConfig: AiConfig) {
        config = newConfig
    }

    suspend fun ask(question: String): String = withContext(Dispatchers.IO) {
        if (config.apiKey.isBlank()) {
            return@withContext "AI не настроен. Скажите 'настрой AI' для настройки."
        }
        try {
            conversationHistory.add(AiMessage(role = "user", content = question))
            val requestBody = when (config.provider) {
                AiProvider.ANTHROPIC -> formatAnthropicRequest()
                AiProvider.GOOGLE -> formatGoogleRequest()
                else -> formatOpenAIRequest()
            }
            val httpRequest = buildRequest(requestBody)
            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val answer = parseResponse(responseBody)
                conversationHistory.add(AiMessage(role = "assistant", content = answer))
                if (conversationHistory.size > 20) {
                    val sys = conversationHistory.first()
                    conversationHistory.clear()
                    conversationHistory.add(sys)
                }
                answer
            } else {
                "Ошибка AI (${response.code})"
            }
        } catch (e: Exception) {
            "Ошибка подключения: ${e.message}"
        }
    }

    private fun formatOpenAIRequest(): String = gson.toJson(mapOf(
        "model" to config.model,
        "messages" to conversationHistory.map { mapOf("role" to it.role, "content" to it.content) },
        "max_tokens" to config.maxTokens,
        "temperature" to config.temperature
    ))

    private fun formatAnthropicRequest(): String {
        val sys = conversationHistory.firstOrNull { it.role == "system" }?.content ?: ""
        val msgs = conversationHistory.filter { it.role != "system" }
        return gson.toJson(mapOf(
            "model" to config.model,
            "max_tokens" to config.maxTokens,
            "system" to sys,
            "messages" to msgs.map { mapOf("role" to it.role, "content" to it.content) }
        ))
    }

    private fun formatGoogleRequest(): String {
        val contents = conversationHistory.filter { it.role != "system" }.map { msg ->
            mapOf(
                "role" to if (msg.role == "assistant") "model" else "user",
                "parts" to listOf(mapOf("text" to msg.content))
            )
        }
        return gson.toJson(mapOf(
            "contents" to contents,
            "generationConfig" to mapOf(
                "maxOutputTokens" to config.maxTokens,
                "temperature" to config.temperature
            )
        ))
    }

    private fun buildRequest(body: String): Request {
        val url = when (config.provider) {
            AiProvider.GOOGLE -> "${config.baseUrl}/models/${config.model}:generateContent?key=${config.apiKey}"
            AiProvider.ANTHROPIC -> "${config.baseUrl}/messages"
            else -> "${config.baseUrl}/chat/completions"
        }
        val builder = Request.Builder().url(url)
        when (config.provider) {
            AiProvider.ANTHROPIC -> {
                builder.addHeader("x-api-key", config.apiKey)
                builder.addHeader("anthropic-version", "2023-06-01")
            }
            AiProvider.GOOGLE -> { /* API key in URL */ }
            else -> builder.addHeader("Authorization", "Bearer ${config.apiKey}")
        }
        return builder.addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType())).build()
    }

    private fun parseResponse(body: String): String = when (config.provider) {
        AiProvider.ANTHROPIC -> {
            val r = gson.fromJson(body, AnthropicResponse::class.java)
            r.content.firstOrNull()?.text ?: "Нет ответа"
        }
        AiProvider.GOOGLE -> {
            val r = gson.fromJson(body, GoogleResponse::class.java)
            r.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Нет ответа"
        }
        else -> {
            val r = gson.fromJson(body, AiResponse::class.java)
            r.choices.firstOrNull()?.message?.content ?: "Нет ответа"
        }
    }

    fun clearHistory() {
        val sys = conversationHistory.first()
        conversationHistory.clear()
        conversationHistory.add(sys)
    }
}

data class AnthropicResponse(val content: List<AnthropicContent>)
data class AnthropicContent(val text: String)
data class GoogleResponse(val candidates: List<GoogleCandidate>)
data class GoogleCandidate(val content: GoogleContent)
data class GoogleContent(val parts: List<GooglePart>)
data class GooglePart(val text: String)

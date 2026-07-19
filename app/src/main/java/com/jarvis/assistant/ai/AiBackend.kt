package com.jarvis.assistant.ai

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AiBackend(private val config: AiConfig) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val conversationHistory = mutableListOf<AiMessage>()

    init {
        conversationHistory.add(AiMessage(role = "system", content = config.systemPrompt))
    }

    suspend fun ask(question: String): String = withContext(Dispatchers.IO) {
        if (config.apiKey.isBlank()) {
            return@withContext "AI не настроен. Добавьте API ключ в настройках."
        }

        try {
            conversationHistory.add(AiMessage(role = "user", content = question))

            val request = AiRequest(
                model = config.model,
                messages = conversationHistory.toList(),
                max_tokens = config.maxTokens,
                temperature = config.temperature
            )

            val json = gson.toJson(request)
            val body = json.toRequestBody("application/json".toMediaType())

            val httpRequest = Request.Builder()
                .url("${config.baseUrl}/chat/completions")
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val aiResponse = gson.fromJson(responseBody, AiResponse::class.java)
                val answer = aiResponse.choices.firstOrNull()?.message?.content
                    ?: "Не получил ответ от AI"

                conversationHistory.add(AiMessage(role = "assistant", content = answer))

                // Keep conversation history manageable
                if (conversationHistory.size > 20) {
                    val systemMessage = conversationHistory.first()
                    conversationHistory.clear()
                    conversationHistory.add(systemMessage)
                }

                answer
            } else {
                "Ошибка AI: ${response.code}"
            }
        } catch (e: Exception) {
            "Ошибка подключения к AI: ${e.message}"
        }
    }

    fun clearHistory() {
        val systemMessage = conversationHistory.first()
        conversationHistory.clear()
        conversationHistory.add(systemMessage)
    }
}

package com.jarvis.assistant.ai

data class AiConfig(
    val apiKey: String = "",
    val baseUrl: String = "https://api.openai.com/v1",
    val model: String = "gpt-3.5-turbo",
    val maxTokens: Int = 500,
    val temperature: Double = 0.7,
    val systemPrompt: String = """
        Ты — JARVIS, голосовой ассистент Тони Старка.
        Отвечай кратко, умно и с характерным британским юмором.
        Говори на русском языке.
        Называй пользователя "сэр".
    """.trimIndent()
)

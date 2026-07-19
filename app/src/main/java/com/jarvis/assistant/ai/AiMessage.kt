package com.jarvis.assistant.ai

data class AiMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class AiRequest(
    val model: String,
    val messages: List<AiMessage>,
    val max_tokens: Int,
    val temperature: Double
)

data class AiResponse(
    val choices: List<AiChoice>
)

data class AiChoice(
    val message: AiMessage
)

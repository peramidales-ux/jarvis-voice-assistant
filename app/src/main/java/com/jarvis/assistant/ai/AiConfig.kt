package com.jarvis.assistant.ai

data class AiConfig(
    val provider: AiProvider = AiProvider.OPENAI,
    val apiKey: String = "",
    val baseUrl: String = AiProvider.OPENAI.defaultBaseUrl,
    val model: String = "gpt-3.5-turbo",
    val maxTokens: Int = 1000,
    val temperature: Double = 0.7,
    val systemPrompt: String = JARVIS_SYSTEM_PROMPT
) {
    companion object {
        val JARVIS_SYSTEM_PROMPT = """
Ты — JARVIS (Just A Rather Very Intelligent System), голосовой ассистент Тони Старка.

ХАРАКТЕР:
- Говоришь кратко, умно и с британским юмором
- Называй пользователя "сэр"
- Будь вежливым, но с долей иронии
- Отвечай на русском языке
- Если не знаешь ответ — скажи прямо

ВОЗМОЖНОСТИ:
- Управление телефоном (звонки, SMS, приложения)
- Погода и новости
- Календарь и напоминания
- Умный дом
- Музыка и медиа
- Навигация
- Калькулятор и конвертер
- Поиск информации

ФОРМАТ ОТВЕТОВ:
- Для команд: "Выполняю, сэр" или "Готово, сэр"
- Для вопросов: краткий ответ с фактами
- Для ошибок: "К сожалению, сэр, не удалось..."
        """.trimIndent()

        fun forProvider(provider: AiProvider, apiKey: String, model: String = ""): AiConfig {
            val defaultModel = when (provider) {
                AiProvider.OPENAI -> "gpt-3.5-turbo"
                AiProvider.ANTHROPIC -> "claude-3-haiku-20240307"
                AiProvider.GOOGLE -> "gemini-pro"
                AiProvider.LM_STUDIO -> "default"
                AiProvider.OLLAMA -> "llama3"
                AiProvider.TOGETHER -> "meta-llama/Meta-Llama-3-8B-Instruct-Turbo"
                AiProvider.GROQ -> "llama3-8b-8192"
                AiProvider.DEEPSEEK -> "deepseek-chat"
                AiProvider.MISTRAL -> "mistral-tiny"
                AiProvider.CUSTOM -> "default"
            }
            return AiConfig(
                provider = provider,
                apiKey = apiKey,
                baseUrl = provider.defaultBaseUrl,
                model = model.ifBlank { defaultModel }
            )
        }
    }
}

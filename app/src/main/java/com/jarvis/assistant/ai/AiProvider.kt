package com.jarvis.assistant.ai

enum class AiProvider(val displayName: String, val defaultBaseUrl: String) {
    OPENAI("OpenAI", "https://api.openai.com/v1"),
    ANTHROPIC("Anthropic", "https://api.anthropic.com/v1"),
    GOOGLE("Google Gemini", "https://generativelanguage.googleapis.com/v1beta"),
    LM_STUDIO("LM Studio", "http://localhost:1234/v1"),
    OLLAMA("Ollama", "http://localhost:11434/v1"),
    TOGETHER("Together AI", "https://api.together.xyz/v1"),
    GROQ("Groq", "https://api.groq.com/openai/v1"),
    DEEPSEEK("DeepSeek", "https://api.deepseek.com/v1"),
    MISTRAL("Mistral", "https://api.mistral.ai/v1"),
    CUSTOM("Custom (OpenAI-compatible)", "")
}

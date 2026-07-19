package com.jarvis.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "jarvis"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

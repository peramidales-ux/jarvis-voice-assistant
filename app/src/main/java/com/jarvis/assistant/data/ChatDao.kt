package com.jarvis.assistant.data

import androidx.room.*

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentMessages(): List<ChatMessage>

    @Insert
    suspend fun insert(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()

    @Query("SELECT * FROM chat_messages WHERE content LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ChatMessage>
}

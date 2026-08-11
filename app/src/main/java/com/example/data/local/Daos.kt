package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM assistant_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<AssistantMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AssistantMemoryEntity): Long

    @Query("DELETE FROM assistant_memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM assistant_memories")
    suspend fun clearAllMemories()
}

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automation_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<AutomationRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutomationRuleEntity): Long

    @Query("UPDATE automation_rules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateRuleStatus(id: Long, isEnabled: Boolean)

    @Query("DELETE FROM automation_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)
}

@Dao
interface NotificationLogDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentNotifications(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: NotificationLogEntity): Long

    @Query("DELETE FROM notification_logs")
    suspend fun clearAllLogs()
}

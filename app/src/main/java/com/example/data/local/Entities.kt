package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "JARVIS"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val commandExecuted: String? = null,
    val isSystemEvent: Boolean = false
)

@Entity(tableName = "assistant_memories")
data class AssistantMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val value: String,
    val category: String = "GENERAL",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleName: String,
    val triggerType: String, // "TIME", "BATTERY_LOW", "NOTIFICATION"
    val triggerValue: String,
    val actionType: String, // "TTS", "TOGGLE_WIFI", "OPEN_APP", "READ_ALOUD"
    val actionValue: String,
    val isEnabled: Boolean = true
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedReplies: String? = null
)

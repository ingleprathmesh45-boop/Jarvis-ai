package com.example.data.repository

import com.example.ai.CommandResult
import com.example.ai.CommandRouter
import com.example.ai.GeminiClient
import com.example.bridge.AndroidSystemBridge
import com.example.data.local.*
import com.example.data.preferences.AssistantPreferences
import kotlinx.coroutines.flow.Flow

class JarvisRepository(
    private val db: AppDatabase,
    val prefs: AssistantPreferences,
    val bridge: AndroidSystemBridge
) {
    val chatDao = db.chatDao()
    val memoryDao = db.memoryDao()
    val automationDao = db.automationDao()
    val notificationDao = db.notificationLogDao()

    private val geminiClient = GeminiClient()
    private val commandRouter = CommandRouter(bridge)

    val chatMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    val memories: Flow<List<AssistantMemoryEntity>> = memoryDao.getAllMemories()
    val automations: Flow<List<AutomationRuleEntity>> = automationDao.getAllRules()
    val notifications: Flow<List<NotificationLogEntity>> = notificationDao.getRecentNotifications()

    suspend fun processUserMessage(input: String): String {
        // Save user message
        chatDao.insertMessage(
            ChatMessageEntity(sender = "USER", content = input)
        )

        // Try local router first
        val routerResult = commandRouter.processInputLocally(input)
        val responseText = when (routerResult) {
            is CommandResult.Executed -> routerResult.responseText
            else -> {
                // Construct system instruction with preferences & personality
                val assistantName = prefs.assistantName.value
                val language = prefs.voiceLanguage.value
                val style = prefs.personalityStyle.value
                val customPrompt = prefs.customPersonalityPrompt.value
                val customApiKey = prefs.customApiKey.value

                val systemPrompt = """
                    You are $assistantName, an AI assistant inspired by JARVIS.
                    Primary Language: $language (Understand English, Hindi, and Hinglish natively).
                    Personality Tone: $style. $customPrompt
                    Provide concise, helpful, futuristic response without unnecessary fluff.
                """.trimIndent()

                geminiClient.generateResponse(
                    prompt = input,
                    systemInstruction = systemPrompt,
                    customApiKey = customApiKey
                )
            }
        }

        // Save JARVIS response
        chatDao.insertMessage(
            ChatMessageEntity(sender = "JARVIS", content = responseText)
        )

        // Speak response via TTS
        bridge.speak(responseText, prefs.speakingSpeed.value)

        return responseText
    }

    suspend fun clearChatHistory() {
        chatDao.clearAllMessages()
    }

    suspend fun saveMemory(key: String, value: String, category: String) {
        memoryDao.insertMemory(
            AssistantMemoryEntity(key = key, value = value, category = category)
        )
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun clearMemory() {
        memoryDao.clearAllMemories()
    }

    suspend fun addAutomationRule(name: String, triggerType: String, triggerValue: String, actionType: String, actionValue: String) {
        automationDao.insertRule(
            AutomationRuleEntity(
                ruleName = name,
                triggerType = triggerType,
                triggerValue = triggerValue,
                actionType = actionType,
                actionValue = actionValue
            )
        )
    }

    suspend fun toggleAutomationRule(id: Long, isEnabled: Boolean) {
        automationDao.updateRuleStatus(id, isEnabled)
    }

    suspend fun deleteAutomationRule(id: Long) {
        automationDao.deleteRuleById(id)
    }

    suspend fun generateSmartReplies(notificationText: String): List<String> {
        val apiKey = prefs.customApiKey.value
        val prompt = "Generate 3 short, realistic smart replies for this notification message: '$notificationText'. Return only 3 replies separated by '|'."
        val result = geminiClient.generateResponse(prompt = prompt, customApiKey = apiKey)
        return result.split("|").map { it.trim() }.filter { it.isNotEmpty() }.take(3)
    }

    suspend fun executePromptSelfUpdate(prompt: String): String {
        val apiKey = prefs.customApiKey.value
        val systemInstruction = """
            You are the System Reconfiguration Engine for JARVIS AI Assistant on Android.
            The user provides a natural language prompt instructing how they want to reconfigure settings, change name/personality/voice/greeting/theme, or add context memories or automation rules.

            Analyze the user prompt and respond ONLY with a raw JSON object (no markdown, no ```json wrapper):
            {
              "assistantName": "optional string (e.g. JARVIS, FRIDAY, EDITH, ULTRA)",
              "personalityStyle": "optional string (Professional | Friendly | Funny | Calm | Minimal | Technical | Custom)",
              "customPersonalityPrompt": "optional string (e.g. 'Always answer in concise points with British accent.')",
              "greetingMessage": "optional string (e.g. 'Systems fully operational, master.')",
              "voiceLanguage": "optional string (English | Hindi | Hinglish)",
              "speakingSpeed": 1.0,
              "themeStyle": "optional string (Futuristic | Cyberpunk Gold | Emerald | Light Clean)",
              "memories": [
                {"key": "string", "value": "string", "category": "GENERAL"}
              ],
              "automations": [
                {"name": "string", "triggerType": "BATTERY_LOW|TIME|NOTIFICATION", "triggerValue": "15%", "actionType": "TTS", "actionValue": "string"}
              ],
              "summary": "Short 1-2 sentence confirmation of what settings, memories, or rules were created/updated."
            }
        """.trimIndent()

        val rawResponse = geminiClient.generateResponse(prompt = prompt, systemInstruction = systemInstruction, customApiKey = apiKey)

        val cleanJson = rawResponse.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        var summaryMsg = "System configuration updated successfully."

        try {
            val json = org.json.JSONObject(cleanJson)

            if (json.has("assistantName") && !json.isNull("assistantName")) {
                val name = json.getString("assistantName")
                if (name.isNotBlank()) prefs.setAssistantName(name)
            }
            if (json.has("personalityStyle") && !json.isNull("personalityStyle")) {
                val style = json.getString("personalityStyle")
                if (style.isNotBlank()) prefs.setPersonalityStyle(style)
            }
            if (json.has("customPersonalityPrompt") && !json.isNull("customPersonalityPrompt")) {
                val custom = json.getString("customPersonalityPrompt")
                if (custom.isNotBlank()) prefs.setCustomPersonalityPrompt(custom)
            }
            if (json.has("greetingMessage") && !json.isNull("greetingMessage")) {
                val greeting = json.getString("greetingMessage")
                if (greeting.isNotBlank()) prefs.setGreetingMessage(greeting)
            }
            if (json.has("voiceLanguage") && !json.isNull("voiceLanguage")) {
                val lang = json.getString("voiceLanguage")
                if (lang.isNotBlank()) prefs.setVoiceLanguage(lang)
            }
            if (json.has("speakingSpeed") && !json.isNull("speakingSpeed")) {
                val speed = json.getDouble("speakingSpeed").toFloat()
                if (speed in 0.5f..2.0f) prefs.setSpeakingSpeed(speed)
            }
            if (json.has("themeStyle") && !json.isNull("themeStyle")) {
                val theme = json.getString("themeStyle")
                if (theme.isNotBlank()) prefs.setThemeStyle(theme)
            }

            if (json.has("memories") && !json.isNull("memories")) {
                val memArr = json.getJSONArray("memories")
                for (i in 0 until memArr.length()) {
                    val mem = memArr.getJSONObject(i)
                    val key = mem.optString("key", "Fact")
                    val value = mem.optString("value", "")
                    val category = mem.optString("category", "GENERAL")
                    if (key.isNotBlank() && value.isNotBlank()) {
                        memoryDao.insertMemory(AssistantMemoryEntity(key = key, value = value, category = category))
                    }
                }
            }

            if (json.has("automations") && !json.isNull("automations")) {
                val autoArr = json.getJSONArray("automations")
                for (i in 0 until autoArr.length()) {
                    val rule = autoArr.getJSONObject(i)
                    val name = rule.optString("name", "Auto Rule")
                    val triggerType = rule.optString("triggerType", "BATTERY_LOW")
                    val triggerValue = rule.optString("triggerValue", "20%")
                    val actionType = rule.optString("actionType", "TTS")
                    val actionValue = rule.optString("actionValue", "Alert triggered.")
                    automationDao.insertRule(
                        AutomationRuleEntity(
                            ruleName = name,
                            triggerType = triggerType,
                            triggerValue = triggerValue,
                            actionType = actionType,
                            actionValue = actionValue
                        )
                    )
                }
            }

            if (json.has("summary") && !json.isNull("summary")) {
                summaryMsg = json.getString("summary")
            }
        } catch (e: Exception) {
            prefs.setCustomPersonalityPrompt(prompt)
            summaryMsg = "Applied prompt directly to system custom prompt."
        }

        bridge.speak(summaryMsg, prefs.speakingSpeed.value)
        return summaryMsg
    }

    suspend fun analyzeScreenText(screenText: String): String {
        val apiKey = prefs.customApiKey.value
        val prompt = "Analyze this screen content and provide a 2-sentence executive summary with actionable takeaways:\n\n$screenText"
        return geminiClient.generateResponse(prompt = prompt, customApiKey = apiKey)
    }
}

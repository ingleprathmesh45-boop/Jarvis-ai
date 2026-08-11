package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridge.AndroidSystemBridge
import com.example.data.local.*
import com.example.data.preferences.AssistantPreferences
import com.example.data.repository.JarvisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val prefs = AssistantPreferences(application)
    val bridge = AndroidSystemBridge(application)
    val repository = JarvisRepository(db, prefs, bridge)

    // Preference Flows
    val assistantName = prefs.assistantName
    val assistantAvatar = prefs.assistantAvatar
    val voiceLanguage = prefs.voiceLanguage
    val speakingSpeed = prefs.speakingSpeed
    val personalityStyle = prefs.personalityStyle
    val customPersonalityPrompt = prefs.customPersonalityPrompt
    val greetingMessage = prefs.greetingMessage
    val themeStyle = prefs.themeStyle
    val customApiKey = prefs.customApiKey

    // Room Database Flows
    val chatMessages = repository.chatMessages
    val memories = repository.memories
    val automations = repository.automations
    val notifications = repository.notifications

    // Bridge States
    val batteryStatus = bridge.batteryStatus
    val isSpeaking = bridge.isSpeaking

    // Vision & Smart Reply States
    private val _screenVisionText = MutableStateFlow("")
    val screenVisionText: StateFlow<String> = _screenVisionText

    private val _screenVisionAnalysis = MutableStateFlow("")
    val screenVisionAnalysis: StateFlow<String> = _screenVisionAnalysis

    private val _suggestedReplies = MutableStateFlow<List<String>>(emptyList())
    val suggestedReplies: StateFlow<List<String>> = _suggestedReplies

    private val _selfUpdateResult = MutableStateFlow("")
    val selfUpdateResult: StateFlow<String> = _selfUpdateResult

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun updateAssistantName(name: String) = prefs.setAssistantName(name)
    fun updateAssistantAvatar(avatar: String) = prefs.setAssistantAvatar(avatar)
    fun updateVoiceLanguage(language: String) = prefs.setVoiceLanguage(language)
    fun updateSpeakingSpeed(speed: Float) = prefs.setSpeakingSpeed(speed)
    fun updatePersonalityStyle(style: String) = prefs.setPersonalityStyle(style)
    fun updateCustomPersonalityPrompt(prompt: String) = prefs.setCustomPersonalityPrompt(prompt)
    fun updateGreetingMessage(greeting: String) = prefs.setGreetingMessage(greeting)
    fun updateThemeStyle(theme: String) = prefs.setThemeStyle(theme)
    fun updateCustomApiKey(key: String) = prefs.setCustomApiKey(key)

    fun sendChatMessage(input: String) {
        if (input.isBlank()) return
        viewModelScope.launch {
            _isProcessing.value = true
            repository.processUserMessage(input)
            _isProcessing.value = false
        }
    }

    fun speakText(text: String) {
        bridge.speak(text, speakingSpeed.value)
    }

    fun stopSpeaking() {
        bridge.stopSpeaking()
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    fun saveMemory(key: String, value: String, category: String) {
        viewModelScope.launch {
            repository.saveMemory(key, value, category)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearMemory() {
        viewModelScope.launch {
            repository.clearMemory()
        }
    }

    fun addAutomationRule(name: String, triggerType: String, triggerValue: String, actionType: String, actionValue: String) {
        viewModelScope.launch {
            repository.addAutomationRule(name, triggerType, triggerValue, actionType, actionValue)
        }
    }

    fun toggleAutomationRule(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutomationRule(id, isEnabled)
        }
    }

    fun deleteAutomationRule(id: Long) {
        viewModelScope.launch {
            repository.deleteAutomationRule(id)
        }
    }

    fun setScreenVisionText(text: String) {
        _screenVisionText.value = text
    }

    fun analyzeScreenVision(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isProcessing.value = true
            val analysis = repository.analyzeScreenText(text)
            _screenVisionAnalysis.value = analysis
            _isProcessing.value = false
        }
    }

    fun generateSmartReplies(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isProcessing.value = true
            val replies = repository.generateSmartReplies(text)
            _suggestedReplies.value = replies
            _isProcessing.value = false
        }
    }

    fun executePromptSelfUpdate(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isProcessing.value = true
            val result = repository.executePromptSelfUpdate(prompt)
            _selfUpdateResult.value = result
            _isProcessing.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        bridge.shutdown()
    }
}

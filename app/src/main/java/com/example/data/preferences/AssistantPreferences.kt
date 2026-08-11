package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AssistantPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_prefs", Context.MODE_PRIVATE)

    private val _assistantName = MutableStateFlow(prefs.getString("assistant_name", "JARVIS") ?: "JARVIS")
    val assistantName: StateFlow<String> = _assistantName

    private val _assistantAvatar = MutableStateFlow(prefs.getString("assistant_avatar", "Cyan Arc") ?: "Cyan Arc")
    val assistantAvatar: StateFlow<String> = _assistantAvatar

    private val _voiceLanguage = MutableStateFlow(prefs.getString("voice_language", "English") ?: "English")
    val voiceLanguage: StateFlow<String> = _voiceLanguage

    private val _speakingSpeed = MutableStateFlow(prefs.getFloat("speaking_speed", 1.0f))
    val speakingSpeed: StateFlow<Float> = _speakingSpeed

    private val _personalityStyle = MutableStateFlow(prefs.getString("personality_style", "Friendly") ?: "Friendly")
    val personalityStyle: StateFlow<String> = _personalityStyle

    private val _customPersonalityPrompt = MutableStateFlow(prefs.getString("custom_prompt", "") ?: "")
    val customPersonalityPrompt: StateFlow<String> = _customPersonalityPrompt

    private val _greetingMessage = MutableStateFlow(prefs.getString("greeting_message", "Systems operational. At your service, sir.") ?: "Systems operational. At your service, sir.")
    val greetingMessage: StateFlow<String> = _greetingMessage

    private val _themeStyle = MutableStateFlow(prefs.getString("theme_style", "Futuristic") ?: "Futuristic")
    val themeStyle: StateFlow<String> = _themeStyle

    private val _customApiKey = MutableStateFlow(prefs.getString("custom_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey

    fun setAssistantName(name: String) {
        prefs.edit().putString("assistant_name", name).apply()
        _assistantName.value = name
    }

    fun setAssistantAvatar(avatar: String) {
        prefs.edit().putString("assistant_avatar", avatar).apply()
        _assistantAvatar.value = avatar
    }

    fun setVoiceLanguage(language: String) {
        prefs.edit().putString("voice_language", language).apply()
        _voiceLanguage.value = language
    }

    fun setSpeakingSpeed(speed: Float) {
        prefs.edit().putFloat("speaking_speed", speed).apply()
        _speakingSpeed.value = speed
    }

    fun setPersonalityStyle(style: String) {
        prefs.edit().putString("personality_style", style).apply()
        _personalityStyle.value = style
    }

    fun setCustomPersonalityPrompt(prompt: String) {
        prefs.edit().putString("custom_prompt", prompt).apply()
        _customPersonalityPrompt.value = prompt
    }

    fun setGreetingMessage(greeting: String) {
        prefs.edit().putString("greeting_message", greeting).apply()
        _greetingMessage.value = greeting
    }

    fun setThemeStyle(theme: String) {
        prefs.edit().putString("theme_style", theme).apply()
        _themeStyle.value = theme
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString("custom_api_key", key).apply()
        _customApiKey.value = key
    }
}

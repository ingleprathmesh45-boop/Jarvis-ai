package com.example.ai

import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        prompt: String,
        systemInstruction: String = "You are JARVIS, a highly intelligent and polite AI personal assistant.",
        customApiKey: String = ""
    ): String {
        val apiKey = customApiKey.ifEmpty { BuildConfig.GEMINI_API_KEY }
        if (apiKey.isEmpty()) {
            return "JARVIS System Alert: API Key is missing. Please configure it in Settings or AI Studio Secrets."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val rootJson = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", prompt)
                ))
            ))
            put("systemInstruction", JSONObject().put("parts", JSONArray().put(
                JSONObject().put("text", systemInstruction)
            )))
        }

        val body = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder().url(url).post(body).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return "JARVIS Network Error (${response.code}): ${response.message}"
                }
                val responseStr = response.body?.string() ?: ""
                val jsonObj = JSONObject(responseStr)
                val candidates = jsonObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return parts.getJSONObject(0).getString("text")
                    }
                }
                "JARVIS: No content generated."
            }
        } catch (e: Exception) {
            "JARVIS Connectivity Exception: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}

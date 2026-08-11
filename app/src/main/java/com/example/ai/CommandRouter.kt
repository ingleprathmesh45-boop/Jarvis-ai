package com.example.ai

import com.example.bridge.AndroidSystemBridge

sealed class CommandResult {
    data class Executed(val responseText: String) : CommandResult()
    object HandledLocally : CommandResult()
    object NeedsAI : CommandResult()
}

class CommandRouter(private val bridge: AndroidSystemBridge) {

    fun processInputLocally(input: String): CommandResult {
        val lower = input.lowercase().trim()

        // Battery check
        if (lower.contains("battery") || lower.contains("charging") || lower.contains("power level")) {
            val info = bridge.getBatteryInfo()
            val chargingText = if (info.isCharging) "and currently charging" else "not charging"
            return CommandResult.Executed("Battery status: ${info.level}% $chargingText.")
        }

        // Camera
        if (lower.contains("open camera") || lower.contains("take photo") || lower.contains("take picture")) {
            val res = bridge.openCamera()
            return CommandResult.Executed(res)
        }

        // Alarm: "set alarm for 7:30" or "set alarm at 8 15"
        if (lower.contains("set alarm") || lower.contains("alarm at") || lower.contains("alarm for")) {
            val timeRegex = Regex("(\\d{1,2})[:\\s](\\d{2})")
            val match = timeRegex.find(lower)
            if (match != null) {
                val hour = match.groupValues[1].toIntOrNull() ?: 7
                val min = match.groupValues[2].toIntOrNull() ?: 0
                val res = bridge.setAlarm(hour, min, "JARVIS Alarm")
                return CommandResult.Executed(res)
            }
        }

        // Timer: "set timer for 60 seconds" or "timer 5 minutes"
        if (lower.contains("set timer") || lower.contains("timer for")) {
            val secRegex = Regex("(\\d+)\\s*(second|sec|minute|min)")
            val match = secRegex.find(lower)
            if (match != null) {
                val valNum = match.groupValues[1].toIntOrNull() ?: 60
                val unit = match.groupValues[2]
                val totalSec = if (unit.startsWith("min")) valNum * 60 else valNum
                val res = bridge.setTimer(totalSec, "JARVIS Timer")
                return CommandResult.Executed(res)
            }
        }

        // Launch app: "open whatsapp", "launch youtube", "open chrome"
        if (lower.startsWith("open ") || lower.startsWith("launch ")) {
            val appName = lower.replace("open ", "").replace("launch ", "").trim()
            if (appName.isNotEmpty()) {
                val res = bridge.launchAppByName(appName)
                return CommandResult.Executed(res)
            }
        }

        // Make call: "call 9876543210" or "dial 12345"
        if (lower.startsWith("call ") || lower.startsWith("dial ")) {
            val number = lower.replace("call ", "").replace("dial ", "").replace("-", "").trim()
            if (number.isNotEmpty() && number.all { it.isDigit() || it == '+' }) {
                val res = bridge.makeCall(number)
                return CommandResult.Executed(res)
            }
        }

        return CommandResult.NeedsAI
    }
}

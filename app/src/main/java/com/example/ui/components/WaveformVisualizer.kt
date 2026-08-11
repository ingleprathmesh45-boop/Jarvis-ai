package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val color = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val barCount = 20
        val barWidth = width / (barCount * 2)

        for (i in 0 until barCount) {
            val x = i * (barWidth * 2) + barWidth
            val multiplier = if (isSpeaking) {
                Math.sin(Math.toRadians((phase + i * 20).toDouble())).toFloat() * 0.8f + 0.2f
            } else {
                0.1f
            }
            val barHeight = (height * 0.8f) * Math.abs(multiplier)
            val startY = centerY - (barHeight / 2)
            val endY = centerY + (barHeight / 2)

            drawLine(
                color = color,
                start = Offset(x, startY),
                end = Offset(x, endY),
                strokeWidth = barWidth
            )
        }
    }
}

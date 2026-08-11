package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun JarvisOrb(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    isSpeaking: Boolean = false,
    isProcessing: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 2000 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = if (isSpeaking) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 400 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier.size(size)) {
        val centerPoint = center
        val radius = (size.toPx() / 2) * pulseScale

        // Outer Arc Ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent),
                center = centerPoint,
                radius = radius * 1.2f
            ),
            radius = radius * 1.2f
        )

        // Inner Rotating Arc
        drawArc(
            color = primaryColor,
            startAngle = rotation,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 6.dp.toPx())
        )

        drawArc(
            color = secondaryColor,
            startAngle = rotation + 180f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx())
        )

        // Core Reactor Center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.2f)),
                center = centerPoint,
                radius = radius * 0.5f
            ),
            radius = radius * 0.4f
        )
    }
}

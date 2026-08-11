package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JarvisDarkColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = JarvisCyan,
    secondary = JarvisArcOrange,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF421E00),
    onSecondaryContainer = JarvisArcOrange,
    tertiary = JarvisEmerald,
    onTertiary = Color.Black,
    background = JarvisDarkBackground,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurfaceDark,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisGlassSurface,
    onSurfaceVariant = JarvisTextSecondary,
    outline = JarvisBorderGlow,
    error = Color(0xFFFF5252),
    onError = Color.White
)

private val CyberpunkGoldColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF332B00),
    secondary = Color(0xFFFF9100),
    background = Color(0xFF0F0E0B),
    surface = Color(0xFF191712),
    surfaceVariant = Color(0xFF26231B),
    onSurface = Color(0xFFF0EAE1),
    onSurfaceVariant = Color(0xFFB0A798),
    outline = Color(0xFFFFD700)
)

private val EmeraldColorScheme = darkColorScheme(
    primary = JarvisEmerald,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00381E),
    secondary = JarvisCyan,
    background = Color(0xFF06140D),
    surface = Color(0xFF0D2418),
    surfaceVariant = Color(0xFF143624),
    onSurface = Color(0xFFE0F7EB),
    onSurfaceVariant = Color(0xFF90C2A8),
    outline = JarvisEmerald
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006875),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFF984700),
    background = Color(0xFFF8FDFF),
    surface = Color(0xFFEDF8FA),
    surfaceVariant = Color(0xFFDBE4E6),
    onSurface = Color(0xFF191C1D),
    onSurfaceVariant = Color(0xFF3F484A),
    outline = Color(0xFF6F797B)
)

@Composable
fun JarvisTheme(
    themeStyle: String = "Futuristic",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeStyle) {
        "Cyberpunk Gold" -> CyberpunkGoldColorScheme
        "Emerald" -> EmeraldColorScheme
        "Light Clean" -> LightColorScheme
        else -> JarvisDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AnimeColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = AnimeDarkBg,
    primaryContainer = AnimeSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = NeonCrimson,
    onSecondary = TextPrimary,
    secondaryContainer = AnimeSurfaceVariant,
    onSecondaryContainer = NeonCrimson,
    tertiary = SolarGold,
    onTertiary = AnimeDarkBg,
    background = AnimeDarkBg,
    onBackground = TextPrimary,
    surface = AnimeSurface,
    onSurface = TextPrimary,
    surfaceVariant = AnimeSurfaceVariant,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AnimeColorScheme,
        typography = Typography,
        content = content
    )
}

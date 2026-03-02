package com.allterra.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2F7A8E),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = androidx.compose.ui.graphics.Color(0xFF4A8B7F),
    onSecondary = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFFF2F4F6),
    onSurface = androidx.compose.ui.graphics.Color(0xFF102129),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDCE4E8),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF314852),
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF78B6C7),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF0B2A33),
    secondary = androidx.compose.ui.graphics.Color(0xFF8AC8A2),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF113326),
    surface = androidx.compose.ui.graphics.Color(0xFF0F1C23),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6EEF2),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF1E2D36),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC8D5DB),
)

@Composable
fun AllterraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}

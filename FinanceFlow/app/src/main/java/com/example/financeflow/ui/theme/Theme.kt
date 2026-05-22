package com.example.financeflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7C4DFF),
    onPrimary = Color.White,
    secondary = Color(0xFFEDE7FF),
    onSecondary = Color(0xFF7C4DFF),
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF2D1B50),
    secondary = Color(0xFF3A2E52),
    onSecondary = Color(0xFFEDE2FF),
    background = Color(0xFF141218),
    surface = Color(0xFF1D1A24),
    onBackground = Color(0xFFE8E0F0),
    onSurface = Color(0xFFE8E0F0)
)

@Composable
fun FinanceFlowTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

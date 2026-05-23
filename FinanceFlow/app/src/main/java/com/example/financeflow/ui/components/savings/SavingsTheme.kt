package com.example.financeflow.ui.components.savings

import androidx.compose.ui.graphics.Color

data class SavingsColors(
    val background: Color,
    val cardBg: Color,
    val accent: Color,
    val success: Color,
    val formBg: Color,
    val fieldBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val muted: Color,
    val progressTrack: Color,
    val chipBg: Color
)

// Light tokens
private val LightBackground   = Color(0xFFEDE2FF)
private val LightCardBg       = Color(0xFFFFFFFF)
private val LightAccent       = Color(0xFFF5A623)
private val LightSuccess      = Color(0xFF3DBD7D)
private val LightFormBg       = Color(0xFFF7E4A7)
private val LightFieldBorder  = Color(0xFFD0C4E8)
private val LightTextPrimary  = Color(0xFF1A1A1A)
private val LightTextSecondary= Color(0xFF6B7280)
private val LightMuted        = Color(0xFF9CA3AF)
private val LightProgressTrack= Color(0xFFF0E6D0)
private val LightChipBg       = Color(0xFFF0E6D0)

// Dark tokens
private val DarkBackground    = Color(0xFF0F0F16)
private val DarkCardBg        = Color(0xFF1F1F2B)
private val DarkAccent        = Color(0xFFF5A623)
private val DarkSuccess       = Color(0xFF2DBD6E)
private val DarkFormBg        = Color(0xFF2A2A36)
private val DarkFieldBorder   = Color(0xFF3A3A4E)
private val DarkTextPrimary   = Color(0xFFE8E8E8)
private val DarkTextSecondary = Color(0xFFB0B0B0)
private val DarkMuted         = Color(0xFF7C7C88)
private val DarkProgressTrack = Color(0xFF3A3A4E)
private val DarkChipBg        = Color(0xFF3E3E2A)

fun getSavingsColors(isDarkTheme: Boolean): SavingsColors = if (isDarkTheme) {
    SavingsColors(
        background    = DarkBackground,
        cardBg        = DarkCardBg,
        accent        = DarkAccent,
        success       = DarkSuccess,
        formBg        = DarkFormBg,
        fieldBorder   = DarkFieldBorder,
        textPrimary   = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        muted         = DarkMuted,
        progressTrack = DarkProgressTrack,
        chipBg        = DarkChipBg
    )
} else {
    SavingsColors(
        background    = LightBackground,
        cardBg        = LightCardBg,
        accent        = LightAccent,
        success       = LightSuccess,
        formBg        = LightFormBg,
        fieldBorder   = LightFieldBorder,
        textPrimary   = LightTextPrimary,
        textSecondary = LightTextSecondary,
        muted         = LightMuted,
        progressTrack = LightProgressTrack,
        chipBg        = LightChipBg
    )
}

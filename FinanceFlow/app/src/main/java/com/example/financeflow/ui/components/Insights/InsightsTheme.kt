package com.example.financeflow.ui.components.insights

import androidx.compose.ui.graphics.Color

data class InsightsColors(
    val BgPurple: Color,
    val PrimaryPurple: Color,
    val CardWhite: Color,
    val TextDark: Color,
    val TextMuted: Color,
    val DayDetailBg: Color,
    val IncomeGreen: Color,
)

fun getInsightsColors(isDarkTheme: Boolean): InsightsColors {
    return if (isDarkTheme) {
        InsightsColors(
            BgPurple = Color(0xFF1E1B2E),
            PrimaryPurple = Color(0xFFB794F4),
            CardWhite = Color(0xFF0F1724),
            TextDark = Color(0xFFF3F4F6),
            TextMuted = Color(0xFF9CA3AF),
            DayDetailBg = Color(0xFF0B1220),
            IncomeGreen = Color(0xFF22C55E)
        )
    } else {
        InsightsColors(
            BgPurple = Color(0xFFF3ECFF),
            PrimaryPurple = Color(0xFF8B5CF6),
            CardWhite = Color(0xFFFFFFFF),
            TextDark = Color(0xFF1E1B2E),
            TextMuted = Color(0xFF9CA3AF),
            DayDetailBg = Color(0xFFE8F4FD),
            IncomeGreen = Color(0xFF22C55E)
        )
    }
}

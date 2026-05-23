package com.example.financeflow.ui.components.Expenses

import androidx.compose.ui.graphics.Color

data class ExpensesColors(
    val AppBg: Color,
    val CardBg: Color,
    val HeaderRed: Color,
    val Primary: Color,
    val PrimaryLight: Color,
    val PrimaryBorder: Color,
    val PrimaryText: Color,
    val Border: Color,
    val SurfaceGrey: Color,
    val TextPrimary: Color,
    val TextMuted: Color,
    val MustAmber: Color,
    val MustBg: Color,
    val MustBorder: Color,
    val MustText: Color,
    val ExpenseRed: Color,
    val ExpenseBg: Color,
    val SuccessGreen: Color,
    val BlueCard: Color,
    val TealBg: Color,
    val PurpleBg: Color,
    val AmberWarning: Color
)

fun getExpensesColors(isDarkTheme: Boolean): ExpensesColors {
    return if (isDarkTheme) {
        ExpensesColors(
            AppBg = Color(0xFF0B1220),
            CardBg = Color(0xFF0F1724),
            HeaderRed = Color(0xFF991B1B),
            Primary = Color(0xFF8B5CF6),
            PrimaryLight = Color(0xFF1F2A44),
            PrimaryBorder = Color(0xFF374151),
            PrimaryText = Color(0xFFB794F4),
            Border = Color(0xFF1F2937),
            SurfaceGrey = Color(0xFF0B1220),
            TextPrimary = Color(0xFFF3F4F6),
            TextMuted = Color(0xFF9CA3AF),
            MustAmber = Color(0xFFF59E0B),
            MustBg = Color(0xFF2B2B00),
            MustBorder = Color(0xFF3B3B00),
            MustText = Color(0xFFFDE68A),
            ExpenseRed = Color(0xFFEF4444),
            ExpenseBg = Color(0xFF2B0F0F),
            SuccessGreen = Color(0xFF22C55E),
            BlueCard = Color(0xFF0A1722),
            TealBg = Color(0xFF05201C),
            PurpleBg = Color(0xFF1B1222),
            AmberWarning = Color(0xFF332A00)
        )
    } else {
        ExpensesColors(
            AppBg = Color(0xFFF8FAFC),
            CardBg = Color(0xFFFFFFFF),
            HeaderRed = Color(0xFFDC2626),
            Primary = Color(0xFF8B5CF6),
            PrimaryLight = Color(0xFFEEF2FF),
            PrimaryBorder = Color(0xFFC7D2FE),
            PrimaryText = Color(0xFF4F46E5),
            Border = Color(0xFFE5E7EB),
            SurfaceGrey = Color(0xFFF3F4F6),
            TextPrimary = Color(0xFF1F2937),
            TextMuted = Color(0xFF6B7280),
            MustAmber = Color(0xFFF59E0B),
            MustBg = Color(0xFFFFFBEB),
            MustBorder = Color(0xFFFDE68A),
            MustText = Color(0xFF92400E),
            ExpenseRed = Color(0xFFEF4444),
            ExpenseBg = Color(0xFFFEF2F2),
            SuccessGreen = Color(0xFF22C55E),
            BlueCard = Color(0xFFEFF6FF),
            TealBg = Color(0xFFF0FDFA),
            PurpleBg = Color(0xFFF5F3FF),
            AmberWarning = Color(0xFFFFFBEB)
        )
    }
}

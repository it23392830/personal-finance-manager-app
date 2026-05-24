package com.example.financeflow.ui.components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  Design Tokens - Light Mode
// ─────────────────────────────────────────────
private val LightGradientStart   = Color(0xFFEDE7FF)   // soft lavender
private val LightGradientMid     = Color(0xFFE8F4FF)   // sky-tint
private val LightGradientEnd     = Color(0xFFF3EDFF)   // back to lavender
private val LightTextPrimary     = Color(0xFF1A1A2E)
private val LightTextSecondary   = Color(0xFF6B7280)

// ─────────────────────────────────────────────
//  Design Tokens - Dark Mode
// ─────────────────────────────────────────────
private val DarkGradientStart    = Color(0xFF2A2A3E)
private val DarkGradientMid      = Color(0xFF1F2A3E)
private val DarkGradientEnd      = Color(0xFF2A2A3E)
private val DarkTextPrimary      = Color(0xFFE8E8E8)
private val DarkTextSecondary    = Color(0xFFB0B0B0)

// ─────────────────────────────────────────────
//  Common Design Tokens
// ─────────────────────────────────────────────
private val PrimaryPurple   = Color(0xFF7C4DFF)
private val PositiveGreen   = Color(0xFF2DBD6E)
private val NegativeRed     = Color(0xFFFF5252)
private val StreakAmber     = Color(0xFFFFB800)

data class BalanceCardColors(
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

private fun getBalanceCardColors(isDarkTheme: Boolean): BalanceCardColors =
    if (isDarkTheme) {
        BalanceCardColors(
            gradientStart = DarkGradientStart,
            gradientMid = DarkGradientMid,
            gradientEnd = DarkGradientEnd,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary
        )
    } else {
        BalanceCardColors(
            gradientStart = LightGradientStart,
            gradientMid = LightGradientMid,
            gradientEnd = LightGradientEnd,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary
        )
    }

// ─────────────────────────────────────────────
//  Data model  (replace with domain model later)
// ─────────────────────────────────────────────
data class BalanceCardData(
    val userName: String       = "Kavindu",
    val availableBalance: Long = 35_000L,
    val totalIncome: Long      = 120_000L,
    val totalExpenses: Long    = 37_500L,
    val totalSaved: Long       = 53_200L,
    val streakDays: Int        = 3,
    val currencySymbol: String = "LKR"
)

// ─────────────────────────────────────────────
//  Public composable
// ─────────────────────────────────────────────
/**
 * BalanceCard
 *
 * Displays the user greeting, available balance, income/expense/saving
 * summary rows, and a streak chip — all in one pastel-gradient card.
 *
 * @param data          Content to display.  Swap for ViewModel state later.
 * @param modifier      External layout modifier.
 * @param cornerRadius  Card corner radius (default 24 dp).
 * @param elevation     Card shadow elevation (default 6 dp).
 */
@Composable
fun BalanceCard(
    isDarkTheme: Boolean = false,
    data: BalanceCardData = BalanceCardData(),
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 6.dp,
    onStreakClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val colors = getBalanceCardColors(isDarkTheme)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation  = elevation,
                shape      = RoundedCornerShape(cornerRadius),
                ambientColor = PrimaryPurple.copy(alpha = 0.12f),
                spotColor  = PrimaryPurple.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.gradientStart, colors.gradientMid, colors.gradientEnd)
                )
            )
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // ── Greeting row ────────────────────────────────
            GreetingRow(
                isDarkTheme = isDarkTheme,
                userName = data.userName,
                onThemeClick = onThemeClick,
                onProfileClick = onProfileClick
            )

            // ── Balance block ───────────────────────────────
            BalanceBlock(
                isDarkTheme    = isDarkTheme,
                balance        = data.availableBalance,
                currencySymbol = data.currencySymbol
            )

            // ── Summary rows ────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BalanceSummaryRow(
                    isDarkTheme    = isDarkTheme,
                    label          = "Total Income",
                    amount         = data.totalIncome,
                    currencySymbol = data.currencySymbol,
                    isPositive     = true
                )
                BalanceSummaryRow(
                    isDarkTheme    = isDarkTheme,
                    label          = "Total Expenses",
                    amount         = data.totalExpenses,
                    currencySymbol = data.currencySymbol,
                    isPositive     = false,
                    prefix         = "-( -)"
                )
                BalanceSummaryRow(
                    isDarkTheme    = isDarkTheme,
                    label          = "Saved",
                    amount         = data.totalSaved,
                    currencySymbol = data.currencySymbol,
                    isPositive     = true,
                    useStreakIcon   = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                StreakChip(
                    isDarkTheme = isDarkTheme,
                    days = data.streakDays,
                    onClick = onStreakClick
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Private sub-composables
// ─────────────────────────────────────────────

@Composable
private fun GreetingRow(
    isDarkTheme: Boolean = false,
    userName: String,
    onThemeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = getBalanceCardColors(isDarkTheme)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Hello, $userName!!",
                style      = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryPurple,
                    fontSize   = 22.sp
                )
            )
            Text(
                text  = "Welcome Back",
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = colors.textSecondary,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
private fun StreakChip(
    isDarkTheme: Boolean = false,
    days: Int,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (isDarkTheme) Color(0xFF3E3E2A) else Color(0xFFFFFDE7),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "⚡", fontSize = 14.sp)
            Text(
                text  = "$days Day Streak",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = StreakAmber,
                    fontSize   = 12.sp
                )
            )
        }
    }
}

@Composable
private fun BalanceBlock(
    isDarkTheme: Boolean = false,
    balance: Long,
    currencySymbol: String
) {
    val colors = getBalanceCardColors(isDarkTheme)
    Column(
        modifier              = Modifier.fillMaxWidth(),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text      = "$currencySymbol ${"%,.2f".format(balance.toDouble())}",
            style     = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = colors.textPrimary,
                fontSize   = 36.sp,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text  = "Available Balance",
            style = MaterialTheme.typography.bodySmall.copy(
                color    = colors.textSecondary,
                fontSize = 13.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BalanceSummaryRow(
    isDarkTheme: Boolean = false,
    label: String,
    amount: Long,
    currencySymbol: String,
    isPositive: Boolean,
    prefix: String = "+",
    useStreakIcon: Boolean = false
) {
    val colors = getBalanceCardColors(isDarkTheme)
    val tint       = if (isPositive) PositiveGreen else NegativeRed
    val amountText = if (isPositive)
        "$prefix$currencySymbol ${"%,d".format(amount)}"
    else
        "$prefix$currencySymbol ${"%,d".format(amount)}"

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when {
                useStreakIcon -> Text(text = "🪙", fontSize = 14.sp)
                isPositive   -> Icon(
                    imageVector        = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint               = tint,
                    modifier           = Modifier.size(18.dp)
                )
                else         -> Icon(
                    imageVector        = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint               = tint,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = colors.textSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 13.sp
                )
            )
        }

        Text(
            text  = amountText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color      = tint,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp
            )
        )
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun BalanceCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BalanceCard(
                data = BalanceCardData(
                    userName       = "Kavindu",
                    availableBalance = 35_000L,
                    totalIncome    = 120_000L,
                    totalExpenses  = 37_500L,
                    totalSaved     = 53_200L,
                    streakDays     = 3
                )
            )
        }
    }
}

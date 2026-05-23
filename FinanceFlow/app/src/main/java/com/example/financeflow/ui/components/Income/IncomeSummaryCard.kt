package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// ── Design tokens ─────────────────────────────────────────────────────────────
private val LightGreenCard   = Color(0xFFA8E6B0)
private val LightGreenButton = Color(0xFF22C55E)
private val LightTextDark    = Color(0xFF1F2937)
private val LightTextMuted   = Color(0xFF6B7280)

private val DarkGreenCard    = Color(0xFF214233)
private val DarkGreenButton  = Color(0xFF2DBD6E)
private val DarkTextDark     = Color(0xFFE8E8E8)
private val DarkTextMuted    = Color(0xFFB0B0B0)

private data class IncomeSummaryColors(
    val cardBg: Color,
    val buttonBg: Color,
    val textDark: Color,
    val textMuted: Color
)

private fun getIncomeSummaryColors(isDarkTheme: Boolean): IncomeSummaryColors =
    if (isDarkTheme) {
        IncomeSummaryColors(
            cardBg = DarkGreenCard,
            buttonBg = DarkGreenButton,
            textDark = DarkTextDark,
            textMuted = DarkTextMuted
        )
    } else {
        IncomeSummaryColors(
            cardBg = LightGreenCard,
            buttonBg = LightGreenButton,
            textDark = LightTextDark,
            textMuted = LightTextMuted
        )
    }

/**
 * Displays the total income for the selected month plus an "+ Add Income" call-to-action.
 *
 * @param totalAmount      Numeric total income (in [currencyCode]).
 * @param currencyCode     ISO-4217 currency code shown as a prefix (e.g. "LKR", "USD").
 * @param onAddIncomeClick Callback when the user taps "+ Add Income".
 * @param modifier         Optional [Modifier].
 */
@Composable
fun IncomeSummaryCard(
    isDarkTheme: Boolean = false,
    totalAmount: Double,
    currencyCode: String = "LKR",
    onAddIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getIncomeSummaryColors(isDarkTheme)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Label ─────────────────────────────────────────────────────────
            Text(
                text = "Total Income This Month",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textDark.copy(alpha = 0.75f)
            )

            // ── Amount ────────────────────────────────────────────────────────
            Text(
                text = formatAmount(currencyCode, totalAmount),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = colors.textDark
            )

            // ── CTA button ────────────────────────────────────────────────────
            Button(
                onClick = onAddIncomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.buttonBg)
            ) {
                Text(
                    text = "+ Add Income",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Formats a numeric [amount] as "LKR 215,500.00".
 */
fun formatAmount(currencyCode: String, amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$currencyCode ${formatter.format(amount)}"
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
private fun IncomeSummaryCardPreview() {
    IncomeSummaryCard(
        isDarkTheme = false,
        totalAmount = 215_500.0,
        currencyCode = "LKR",
        onAddIncomeClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
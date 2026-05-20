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
private val GreenCard   = Color(0xFFA8E6B0)  // Pastel green card background
private val GreenButton = Color(0xFF22C55E)  // Solid green CTA button
private val TextDark    = Color(0xFF1F2937)
private val TextMuted   = Color(0xFF6B7280)

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
    totalAmount: Double,
    currencyCode: String = "LKR",
    onAddIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenCard),
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
                color = TextDark.copy(alpha = 0.75f)
            )

            // ── Amount ────────────────────────────────────────────────────────
            Text(
                text = formatAmount(currencyCode, totalAmount),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = TextDark
            )

            // ── CTA button ────────────────────────────────────────────────────
            Button(
                onClick = onAddIncomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
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
        totalAmount = 215_500.0,
        currencyCode = "LKR",
        onAddIncomeClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
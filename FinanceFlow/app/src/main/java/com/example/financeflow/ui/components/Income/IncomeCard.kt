package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.model.IncomeBySource
import com.example.financeflow.model.IncomeSource

// ── Design tokens ─────────────────────────────────────────────────────────────
private val TextDark   = Color(0xFF1F2937)
private val TextMuted  = Color(0xFF6B7280)
private val GreenText  = Color(0xFF22C55E)

/**
 * A single row inside the "Income by Source" section.
 *
 * Shows the source icon, name, transaction count, total amount, and percentage.
 *
 * @param data     [IncomeBySource] aggregated data for this row.
 * @param modifier Optional [Modifier].
 */
@Composable
fun IncomeCard(
    data: IncomeBySource,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Source icon ───────────────────────────────────────────────────
            SourceIconBadge(source = data.source)

            Spacer(modifier = Modifier.width(12.dp))

            // ── Name + transaction count ──────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.source.label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = TextDark
                )
                Text(
                    text = "${data.transactionCount} Transaction${if (data.transactionCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // ── Amount + percentage ───────────────────────────────────────────
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "LKR ${formatCompact(data.totalAmount)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = TextDark
                )
                Text(
                    text = "${"%.1f".format(data.percentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Circular icon badge whose background colour is derived from the [source].
 */
@Composable
private fun SourceIconBadge(source: IncomeSource) {
    val (bgColor, emoji) = when (source) {
        IncomeSource.SALARY     -> Color(0xFFDBEAFE) to "💼"
        IncomeSource.FREELANCE  -> Color(0xFFE0E7FF) to "<>"
        IncomeSource.ADSENSE    -> Color(0xFFDCFCE7) to "$"
        IncomeSource.CRYPTO     -> Color(0xFFFFEDD5) to "₿"
        IncomeSource.INVESTMENT -> Color(0xFFFEF9C3) to "📈"
        IncomeSource.RENTAL     -> Color(0xFFFFE4E6) to "🏠"
        IncomeSource.OTHER      -> Color(0xFFF3F4F6) to "+"
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 18.sp
        )
    }
}

// ── Compact number formatter ──────────────────────────────────────────────────

/** Formats a number as "135,000" without decimal places. */
private fun formatCompact(amount: Double): String {
    return "%,.0f".format(amount)
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
private fun IncomeCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            IncomeBySource(IncomeSource.SALARY,    135_000.0, 1, 62.6),
            IncomeBySource(IncomeSource.FREELANCE,  73_500.0, 2, 33.9),
            IncomeBySource(IncomeSource.ADSENSE,     5_200.0, 1,  2.4),
            IncomeBySource(IncomeSource.CRYPTO,      2_300.0, 1,  1.1),
        ).forEach { IncomeCard(data = it) }
    }
}
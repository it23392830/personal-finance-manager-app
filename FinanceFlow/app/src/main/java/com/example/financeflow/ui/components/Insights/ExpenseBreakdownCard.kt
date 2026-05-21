package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

// ─── Colors ───────────────────────────────────────────────────────────────────
private val CardWhite   = Color(0xFFFFFFFF)
private val TextDark    = Color(0xFF1E1B2E)
private val TextMuted   = Color(0xFF9CA3AF)
private val MustRed     = Color(0xFFEF4444)
private val OptionalBlue = Color(0xFF60A5FA)
private val MustBg      = Color(0xFFFFEEEE)
private val OptionalBg  = Color(0xFFEFF6FF)
private val PrimaryPurple = Color(0xFF8B5CF6)
private val IncomeGreen = Color(0xFF22C55E)

/** Line item inside a breakdown list */
data class BreakdownItem(val label: String, val amount: String)

/**
 * ExpenseBreakdownCard
 *
 * Shows the Must vs Optional expense split with two header chips and
 * itemised lists below each heading.
 */
@Composable
fun ExpenseBreakdownCard(
    mustTotal: String = "Rs52,000",
    mustPct: String = "43% of total",
    optionalTotal: String = "Rs68,400",
    optionalPct: String = "57% of total",
    mustItems: List<BreakdownItem> = listOf(
        BreakdownItem("Rent",          "LKR 34,000"),
        BreakdownItem("Utilities",     "LKR 8,500"),
        BreakdownItem("Subscriptions", "LKR 5,200"),
        BreakdownItem("Internet",      "LKR 4,300")
    ),
    optionalItems: List<BreakdownItem> = listOf(
        BreakdownItem("Food & Dining",  "LKR 28,400"),
        BreakdownItem("Transport",      "LKR 18,600"),
        BreakdownItem("Entertainment",  "LKR 12,900"),
        BreakdownItem("Shopping",       "LKR 8,500")
    )
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Expense Breakdown",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark
            )

            Spacer(Modifier.height(14.dp))

            // Two header chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Must chip
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MustBg)
                        .padding(12.dp)
                ) {
                    Text("Must Expenses", fontSize = 11.sp, color = MustRed, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(mustTotal, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MustRed)
                    Text(mustPct, fontSize = 11.sp, color = TextMuted)
                }

                // Optional chip
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OptionalBg)
                        .padding(12.dp)
                ) {
                    Text("Optional Expenses", fontSize = 11.sp, color = OptionalBlue, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(optionalTotal, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = OptionalBlue)
                    Text(optionalPct, fontSize = 11.sp, color = TextMuted)
                }
            }

            Spacer(Modifier.height(18.dp))

            // Must items
            BreakdownSection(
                header = "Must Expenses",
                headerColor = MustRed,
                items = mustItems,
                amountColor = MustRed
            )

            Spacer(Modifier.height(14.dp))

            // Optional items
            BreakdownSection(
                header = "Optional Expenses",
                headerColor = OptionalBlue,
                items = optionalItems,
                amountColor = OptionalBlue
            )
        }
    }
}

/** Reusable section with a bold header and list of label-amount rows */
@Composable
private fun BreakdownSection(
    header: String,
    headerColor: Color,
    items: List<BreakdownItem>,
    amountColor: Color
) {
    Text(
        text = header,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = headerColor
    )
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.label, fontSize = 13.sp, color = Color(0xFF1E1B2E))
                Text(
                    text = item.amount,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun ExpenseBreakdownCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        ExpenseBreakdownCard()
    }
}
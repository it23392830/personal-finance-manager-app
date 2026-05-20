package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark  = Color(0xFF1E1B2E)
private val TextMuted = Color(0xFF9CA3AF)
private val GreenPos  = Color(0xFF22C55E)
private val RedNeg    = Color(0xFFEF4444)
private val GrayFlat  = Color(0xFF9CA3AF)

/** Direction of a month-over-month change */
enum class ChangeDirection { UP, DOWN, FLAT }

/** Data for a single row in the comparison card */
data class ComparisonRow(
    val label: String,
    val subtitle: String,
    val badge: String,         // e.g. "+8.8%"
    val direction: ChangeDirection
)

private fun ChangeDirection.color() = when (this) {
    ChangeDirection.UP   -> GreenPos
    ChangeDirection.DOWN -> RedNeg
    ChangeDirection.FLAT -> GrayFlat
}

private fun ChangeDirection.icon(): ImageVector = when (this) {
    ChangeDirection.UP   -> Icons.Default.TrendingUp
    ChangeDirection.DOWN -> Icons.Default.TrendingDown
    ChangeDirection.FLAT -> Icons.Default.TrendingFlat
}

/**
 * MonthlyComparisonCard
 *
 * "This Month vs Last Month" section with three coloured comparison rows.
 */
@Composable
fun MonthlyComparisonCard(
    rows: List<ComparisonRow> = listOf(
        ComparisonRow(
            label = "Savings Increased",
            subtitle = "LKR 4,300 more than March",
            badge = "+8.8%",
            direction = ChangeDirection.UP
        ),
        ComparisonRow(
            label = "Optional Expenses Reduce",
            subtitle = "LKR 7,800 less than March",
            badge = "-8.7%",
            direction = ChangeDirection.DOWN
        ),
        ComparisonRow(
            label = "Must Expenses Stable",
            subtitle = "No change from March",
            badge = "0%",
            direction = ChangeDirection.FLAT
        )
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
                text = "This Month vs Last Month",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark
            )

            Spacer(Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { row ->
                    ComparisonRowItem(row)
                }
            }
        }
    }
}

@Composable
private fun ComparisonRowItem(row: ComparisonRow) {
    val color = row.direction.color()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = row.direction.icon(),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(text = row.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(text = row.subtitle, fontSize = 11.sp, color = TextMuted)
            }
        }

        // Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = row.badge,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun MonthlyComparisonCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        MonthlyComparisonCard()
    }
}
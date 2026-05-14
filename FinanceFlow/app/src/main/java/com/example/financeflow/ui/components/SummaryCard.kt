package com.example.financeflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Savings

// ──────────────────────────────────────────────
// Data model
// ──────────────────────────────────────────────

data class SummaryCardData(
    val title: String,
    val value: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val trend: SummaryTrend? = null,
    val accentColor: Color = SummaryDefaults.Purple
)

data class SummaryTrend(
    val percent: Float,          // positive = up, negative = down
    val label: String = ""
)

object SummaryDefaults {
    val Purple  = Color(0xFF9B72E8)
    val Teal    = Color(0xFF5EC4C4)
    val Peach   = Color(0xFFFF9F7F)
    val Mint    = Color(0xFF6FCF97)
    val Rose    = Color(0xFFFF7E9D)
    val Indigo  = Color(0xFF7B8FE0)
}

// ──────────────────────────────────────────────
// Public composable
// ──────────────────────────────────────────────

@Composable
fun SummaryCard(
    data: SummaryCardData,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp
) {
    val cardShape = RoundedCornerShape(cornerRadius)
    val accent = data.accentColor

    Card(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                ambientColor = accent.copy(alpha = 0.25f),
                spotColor = accent.copy(alpha = 0.15f)
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Icon + title ─────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryIconBadge(icon = data.icon, accent = accent)
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6B6880),
                    fontWeight = FontWeight.Medium
                )
            }

            // ── Main value ───────────────────────────
            Text(
                text = data.value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1D1530)
            )

            // ── Subtitle + trend ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFADABBB)
                    )
                }
                data.trend?.let {
                    TrendBadge(trend = it, accent = accent)
                }
            }

            // ── Accent progress bar ──────────────────
            AccentBar(accent = accent)
        }
    }
}

// ──────────────────────────────────────────────
// Two-column grid helper (for LazyColumn use)
// ──────────────────────────────────────────────

@Composable
fun SummaryCardGrid(
    items: List<SummaryCardData>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 12.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                rowItems.forEach { item ->
                    SummaryCard(
                        data = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill gap if odd number
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// Sub-composables
// ──────────────────────────────────────────────

@Composable
private fun SummaryIconBadge(
    icon: ImageVector,
    accent: Color
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.18f),
                        accent.copy(alpha = 0.10f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TrendBadge(
    trend: SummaryTrend,
    accent: Color
) {
    val isUp = trend.percent >= 0
    val sign = if (isUp) "+" else ""
    val trendColor = if (isUp) Color(0xFF6FCF97) else Color(0xFFFF7E9D)
    val icon = if (isUp) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(trendColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = trendColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "$sign${"%.1f".format(trend.percent)}%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = trendColor
        )
    }
}

@Composable
private fun AccentBar(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(accent, accent.copy(alpha = 0.25f))
                )
            )
    )
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3EEFF)
@Composable
fun SummaryCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            SummaryCardGrid(
                items = listOf(
                    SummaryCardData(
                        title = "Net Worth",
                        value = "$124K",
                        subtitle = "Updated today",
                        icon = Icons.Rounded.AccountBalanceWallet,
                        trend = SummaryTrend(5.4f),
                        accentColor = SummaryDefaults.Purple
                    ),
                    SummaryCardData(
                        title = "Savings",
                        value = "$18,400",
                        subtitle = "3 accounts",
                        icon = Icons.Rounded.Savings,
                        trend = SummaryTrend(2.1f),
                        accentColor = SummaryDefaults.Mint
                    ),
                    SummaryCardData(
                        title = "Spending",
                        value = "$3,670",
                        subtitle = "This month",
                        icon = Icons.Rounded.TrendingDown,
                        trend = SummaryTrend(-8.3f),
                        accentColor = SummaryDefaults.Rose
                    ),
                    SummaryCardData(
                        title = "Investments",
                        value = "$82K",
                        subtitle = "Portfolio",
                        icon = Icons.Rounded.TrendingUp,
                        trend = SummaryTrend(12.7f),
                        accentColor = SummaryDefaults.Teal
                    )
                )
            )
        }
    }
}

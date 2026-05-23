package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.HARDCODED_PATTERN_STATS
import com.example.financeflow.ui.expenses.PatternStatUiItem
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpensePatternGrid(
    stats: List<PatternStatUiItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ExpenseColors.CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 Spending Patterns",
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = ExpenseColors.TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                stats.chunked(2).forEach { rowStats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowStats.forEach { stat ->
                            PatternCell(stat = stat, modifier = Modifier.weight(1f))
                        }
                        if (rowStats.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternCell(stat: PatternStatUiItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(ExpenseColors.SurfaceGrey, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(stat.emoji, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(stat.label, fontSize = 10.sp, color = ExpenseColors.TextMuted)
            Text(
                stat.value,
                fontSize = 13.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = ExpenseColors.TextPrimary,
                maxLines = 1
            )
            Text(stat.sub, fontSize = 10.sp, color = ExpenseColors.TextMuted, maxLines = 1)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensePatternGridPreview() {
    FinanceFlowTheme {
        ExpensePatternGrid(
            stats = HARDCODED_PATTERN_STATS,
            modifier = Modifier.padding(16.dp)
        )
    }
}

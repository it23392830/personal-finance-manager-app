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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.HARDCODED_INSIGHTS
import com.example.financeflow.ui.expenses.InsightUiItem
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseInsightsCard(
    insights: List<InsightUiItem>,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(false)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.TealBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.TealBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "💡 Smart Insights",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.SuccessGreen
            )

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                insights.forEach { insight ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.CardBg, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(insight.emoji, fontSize = 20.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = insight.text,
                            fontSize = 13.sp,
                            color = colors.TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseInsightsCardPreview() {
    FinanceFlowTheme {
        ExpenseInsightsCard(
            insights = HARDCODED_INSIGHTS,
            modifier = Modifier.padding(16.dp)
        )
    }
}

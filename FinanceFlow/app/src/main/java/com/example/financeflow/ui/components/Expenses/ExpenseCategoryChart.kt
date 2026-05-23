package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.CategoryBreakdownItem
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.HARDCODED_CATEGORY_BREAKDOWN
import com.example.financeflow.ui.expenses.fmtLKR
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseCategoryChart(
    breakdown: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(false)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Spending by Category",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            val maxAmount = breakdown.maxOfOrNull { it.amount } ?: 1

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                breakdown.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = colors.TextPrimary,
                            modifier = Modifier.width(80.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .background(colors.SurfaceGrey, RoundedCornerShape(99.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(item.amount.toFloat() / maxAmount)
                                    .fillMaxHeight()
                                    .background(item.color, RoundedCornerShape(99.dp))
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = fmtLKR(item.amount),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.TextPrimary,
                            modifier = Modifier.width(80.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseCategoryChartPreview() {
    FinanceFlowTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpenseCategoryChart(breakdown = HARDCODED_CATEGORY_BREAKDOWN)
        }
    }
}

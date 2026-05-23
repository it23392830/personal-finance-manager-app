package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.fmtLKR
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseBudgetCard(
    remaining: Int,
    budgetTotal: Int,
    budgetUsedPct: Float,
    todayTotal: Int,
    essentialTotal: Int,
    totalSpent: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEFF6FF), Color(0xFFECFEFF))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = ExpenseColors.PrimaryText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Discretionary Budget Left",
                    fontSize = 12.sp,
                    color = ExpenseColors.TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = fmtLKR(remaining),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ExpenseColors.PrimaryText
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { budgetUsedPct / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (budgetUsedPct > 90f) ExpenseColors.ExpenseRed else ExpenseColors.Primary,
                trackColor = Color.White.copy(alpha = 0.5f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget: ${fmtLKR(budgetTotal)}", fontSize = 11.sp, color = ExpenseColors.TextMuted)
                Text("${budgetUsedPct.toInt()}% used", fontSize = 11.sp, color = ExpenseColors.TextMuted)
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(thickness = 0.5.dp, color = ExpenseColors.Border.copy(alpha = 0.5f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BudgetStat(label = "Today", value = fmtLKR(todayTotal), modifier = Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(24.dp), thickness = 0.5.dp, color = ExpenseColors.Border)
                BudgetStat(label = "Essential", value = fmtLKR(essentialTotal), valueColor = ExpenseColors.MustAmber, modifier = Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(24.dp), thickness = 0.5.dp, color = ExpenseColors.Border)
                BudgetStat(label = "Total", value = fmtLKR(totalSpent), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BudgetStat(
    label: String,
    value: String,
    valueColor: Color = ExpenseColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = ExpenseColors.TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseBudgetCardPreview() {
    FinanceFlowTheme {
        ExpenseBudgetCard(
            remaining = 15000,
            budgetTotal = 50000,
            budgetUsedPct = 70f,
            todayTotal = 1200,
            essentialTotal = 25000,
            totalSpent = 35000,
            modifier = Modifier.padding(16.dp)
        )
    }
}

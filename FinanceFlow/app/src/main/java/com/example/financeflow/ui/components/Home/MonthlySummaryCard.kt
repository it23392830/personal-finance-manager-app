package com.example.financeflow.ui.components.Home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val TotalIncomeAmount = 120_000
private const val ExpensesAmount = 68_400
private const val SavingsAmount = 20_000
private const val RemainingAmount = 31_600

@Composable
fun MonthlySummaryCard(
    isDarkTheme: Boolean = false,
    onViewInsightsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 650),
        label = "monthly_summary_alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF8B5CF6).copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(if (isDarkTheme) Color(0xFF221E2D) else Color.White)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color(0xFFF7F2FF) else Color(0xFF1A1A1E),
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Income distribution for this month",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) Color(0xFFB9B0CA) else Color(0xFF7A7690),
                    fontSize = 13.sp
                )
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            PieChartComponent(
                isDarkTheme = isDarkTheme,
                totalIncome = TotalIncomeAmount.toFloat(),
                expenses = ExpensesAmount.toFloat(),
                savings = SavingsAmount.toFloat(),
                remaining = RemainingAmount.toFloat(),
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }

        LegendItem(
            isDarkTheme = isDarkTheme,
            color = Color(0xFFFF6B6B),
            label = "Expenses",
            amount = "LKR 68,400"
        )
        LegendItem(
            isDarkTheme = isDarkTheme,
            color = Color(0xFF4CAF50),
            label = "Remaining",
            amount = "LKR 31,600"
        )
        LegendItem(
            isDarkTheme = isDarkTheme,
            color = Color(0xFFFFA726),
            label = "Savings",
            amount = "LKR 20,000"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDarkTheme) Color(0xFF312A40) else Color(0xFFF3EFFF))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Do you want more detailed information about your monthly spending and trends?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) Color(0xFFF0E9FF) else Color(0xFF423C5A),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            )
            Text(
                text = "Tap below to view deeper insights",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDarkTheme) Color(0xFFCBBFDE) else Color(0xFF7F7496),
                    fontSize = 12.sp
                )
            )
        }

        InsightsButton(
            isDarkTheme = isDarkTheme,
            onClick = onViewInsightsClick
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun MonthlySummaryCardPreview() {
    MaterialTheme {
        MonthlySummaryCard(
            isDarkTheme = false,
            onViewInsightsClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

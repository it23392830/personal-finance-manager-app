package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.WeeklyTrendItem

@Composable
fun ExpenseWeeklyChart(
    weeklyData: List<WeeklyTrendItem>,
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
                "Weekly Spending Trend",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.TextPrimary
            )

            Spacer(Modifier.height(32.dp))

            val maxAmount = (weeklyData.maxOfOrNull { it.amount } ?: 1).coerceAtLeast(1)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = "LKR ${item.amount / 1000}k",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseColors.TextMuted
                            )
                            Spacer(Modifier.height(8.dp))
                            
                            val barHeightRatio = item.amount.toFloat() / maxAmount
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .fillMaxHeight(barHeightRatio.coerceAtLeast(0.05f))
                                    .background(
                                        color = if (item.amount > 0) ExpenseColors.HeaderRed else ExpenseColors.SurfaceGrey,
                                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                    )
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                color = ExpenseColors.TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

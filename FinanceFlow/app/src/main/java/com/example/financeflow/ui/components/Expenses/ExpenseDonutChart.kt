package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.fmtLKR

@Composable
fun ExpenseDonutChart(
    essentialTotal: Int,
    discretionaryTotal: Int,
    totalSpent: Int,
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
                "Essential vs Discretionary",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.TextPrimary
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut Chart
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val essentialRatio = if (totalSpent > 0) essentialTotal.toFloat() / totalSpent else 0f
                    val discretionaryRatio = 1f - essentialRatio

                    Canvas(modifier = Modifier.size(100.dp)) {
                        val strokeWidth = 12.dp.toPx()
                        
                        // Essential Arc (Amber)
                        drawArc(
                            color = ExpenseColors.MustAmber,
                            startAngle = -90f,
                            sweepAngle = 360f * essentialRatio,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        
                        // Discretionary Arc (Blue/Primary)
                        drawArc(
                            color = ExpenseColors.Primary,
                            startAngle = -90f + (360f * essentialRatio),
                            sweepAngle = 360f * discretionaryRatio,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = fmtLKR(totalSpent),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseColors.TextPrimary
                        )
                    }
                }

                Spacer(Modifier.width(24.dp))

                // Stats
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DonutStatItem(
                        label = "Essential",
                        amount = essentialTotal,
                        percentage = if (totalSpent > 0) (essentialTotal * 100 / totalSpent) else 0,
                        color = ExpenseColors.MustAmber
                    )
                    DonutStatItem(
                        label = "Discretionary",
                        amount = discretionaryTotal,
                        percentage = if (totalSpent > 0) (discretionaryTotal * 100 / totalSpent) else 0,
                        color = ExpenseColors.Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutStatItem(
    label: String,
    amount: Int,
    percentage: Int,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(99.dp)))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = ExpenseColors.TextMuted)
            Text(
                "${fmtLKR(amount)} ($percentage%)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.TextPrimary
            )
        }
    }
}

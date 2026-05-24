package com.example.financeflow.ui.components.Home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun PieChartComponent(
    isDarkTheme: Boolean = false,
    totalIncome: Float,
    expenses: Float,
    savings: Float,
    remaining: Float,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "pie_chart_progress"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.88f,
        animationSpec = tween(durationMillis = 800),
        label = "pie_chart_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "pie_chart_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val chartData = listOf(
        Color(0xFFFF6B6B) to expenses,
        Color(0xFF4CAF50) to remaining,
        Color(0xFFFFA726) to savings
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .alpha(alpha)
        ) {
            var startAngle = -90f
            chartData.forEach { (color, value) ->
                val sweep = ((value / totalIncome) * 360f) * progress
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 54f, cap = StrokeCap.Round)
                )
                startAngle += (value / totalIncome) * 360f
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LKR 120,000",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color(0xFFF7F2FF) else Color(0xFF1A1A1E)
                )
            )
            Text(
                text = "Total Income",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDarkTheme) Color(0xFFB9B0CA) else Color(0xFF8A829C)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PieChartComponentPreview() {
    MaterialTheme {
        PieChartComponent(
            isDarkTheme = false,
            totalIncome = 120000f,
            expenses = 68400f,
            savings = 20000f,
            remaining = 31600f
        )
    }
}

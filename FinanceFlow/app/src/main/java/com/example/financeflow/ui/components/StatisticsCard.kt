package com.example.financeflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// LifetimeStatisticsCard
// Shows total saved + avg savings rate with gradient chips, and "Save First" banner
@Composable
fun LifetimeStatisticsCard(
    totalSaved: String = "LKR 301,600",
    avgSavingsRate: String = "26.8%",
    periodLabel: String = "Total Saved (6 mon)"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Lifetime Statistics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientStatChip(
                    label = periodLabel,
                    value = totalSaved,
                    gradientColors = listOf(Color(0xFFF5A623), Color(0xFFFF6F00)),
                    modifier = Modifier.weight(1f)
                )
                GradientStatChip(
                    label = "Avg. Savings Rate",
                    value = avgSavingsRate,
                    gradientColors = listOf(Color(0xFF43A047), Color(0xFF1B5E20)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            SaveFirstBanner()
        }
    }
}
@Composable
fun GradientStatChip(
    label: String,
    value: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
@Composable
fun SaveFirstBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Save First Model Active",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Savings allocated automatically when income is added",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Illustrative icon
            Icon(
                imageVector = Icons.Default.CurrencyExchange,
                contentDescription = "Save First",
                tint = OrangeAccent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
// Previews
@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewLifetimeStatisticsCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LifetimeStatisticsCard()
        }
    }
}

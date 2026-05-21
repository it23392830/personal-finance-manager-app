package com.example.financeflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.theme.CardWhite

@Composable
fun SavingsInsightsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Savings Insights",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            InsightRow(
                icon = Icons.Outlined.Lightbulb,
                iconTint = Color(0xFFFFB100),
                iconBg = Color(0xFFFFF8E1),
                text = "You've saved 15% more than last month! Keep it up."
            )

            Spacer(modifier = Modifier.height(12.dp))

            InsightRow(
                icon = Icons.Outlined.TrendingUp,
                iconTint = Color(0xFF4CAF50),
                iconBg = Color(0xFFE8F5E9),
                text = "Based on your current rate, you'll reach your 'MacBook' goal in 4 months."
            )
        }
    }
}

@Composable
private fun InsightRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.Gray,
            lineHeight = 18.sp
        )
    }
}

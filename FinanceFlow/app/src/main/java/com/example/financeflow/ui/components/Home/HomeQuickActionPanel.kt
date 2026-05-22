package com.example.financeflow.ui.components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeQuickActionPanel(
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onViewSavingsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    emoji = "➕",
                    label = "Add Income",
                    bgColor = Color(0xFFDCFCE7),
                    onClick = onAddIncomeClick
                )
                QuickActionButton(
                    emoji = "💸",
                    label = "Add Expense",
                    bgColor = Color(0xFFFEE2E2),
                    onClick = onAddExpenseClick
                )
                QuickActionButton(
                    emoji = "🏦",
                    label = "View Savings",
                    bgColor = Color(0xFFE0F2FE),
                    onClick = onViewSavingsClick
                )
                QuickActionButton(
                    emoji = "📋",
                    label = "Transactions",
                    bgColor = Color(0xFFF3E8FF),
                    onClick = onTransactionsClick
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    emoji: String,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp).clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(bgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color(0xFF374151),
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

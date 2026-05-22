package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors

@Composable
fun ExpenseReminderCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ExpenseColors.PurpleBg, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = ExpenseColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column {
            Text(
                "Daily Reminder Active",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.TextPrimary
            )
            Text(
                "We'll nudge you at 8:00 PM to log your daily spending.",
                fontSize = 11.sp,
                color = ExpenseColors.TextMuted
            )
        }
    }
}

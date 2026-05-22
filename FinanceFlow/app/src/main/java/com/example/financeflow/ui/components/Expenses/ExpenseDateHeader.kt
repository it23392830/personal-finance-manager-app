package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.fmtLKR

@Composable
fun ExpenseDateHeader(
    dateLabel: String,
    dayTotal: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(ExpenseColors.SurfaceGrey, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = ExpenseColors.TextMuted
        )
        Text(
            text = fmtLKR(dayTotal),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ExpenseColors.TextPrimary
        )
    }
}

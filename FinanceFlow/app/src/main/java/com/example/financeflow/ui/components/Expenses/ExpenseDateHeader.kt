package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.fmtLKR
import com.example.financeflow.ui.theme.FinanceFlowTheme

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

@Preview(showBackground = true)
@Composable
fun ExpenseDateHeaderPreview() {
    FinanceFlowTheme {
        ExpenseDateHeader(
            dateLabel = "Today, 24 Oct",
            dayTotal = 2500,
            modifier = Modifier.padding(16.dp)
        )
    }
}

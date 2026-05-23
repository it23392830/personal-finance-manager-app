package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.fmtLKR
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseWarningBanner(
    budgetUsedPct: Float,
    remaining: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = ExpenseColors.AmberWarning,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ExpenseColors.MustBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = ExpenseColors.MustAmber,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Budget Alert: ${budgetUsedPct.toInt()}% used",
                    fontSize = 13.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = ExpenseColors.MustText
                )
                Text(
                    "You have ${fmtLKR(remaining)} left for this month.",
                    fontSize = 11.sp,
                    color = ExpenseColors.MustText.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseWarningBannerPreview() {
    FinanceFlowTheme {
        ExpenseWarningBanner(
            budgetUsedPct = 85f,
            remaining = 5000
        )
    }
}

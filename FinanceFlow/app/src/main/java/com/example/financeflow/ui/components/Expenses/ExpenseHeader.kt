package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.components.common.FeatureMonthHeader
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseHeader(
    selectedMonth: String,
    onMonthChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ExpensesColors = getExpensesColors(false)
) {
    FeatureMonthHeader(
        title = "Expense Tracker",
        subtitle = "low-friction tracking for busy days",
        selectedMonth = selectedMonth,
        monthOptions = listOf("May 2026", "April 2026", "March 2026", "February 2026", "January 2026"),
        onMonthSelected = onMonthChange,
        modifier = modifier,
        headerColor = colors.HeaderRed
    )
}

@Preview(showBackground = true)
@Composable
fun ExpenseHeaderPreview() {
    FinanceFlowTheme {
        MaterialTheme {
            Box(modifier = Modifier.padding(16.dp)) {
                ExpenseHeader(
                    selectedMonth = "May 2026",
                    onMonthChange = {},
                    onAddClick = {}
                )
            }
        }
    }
}

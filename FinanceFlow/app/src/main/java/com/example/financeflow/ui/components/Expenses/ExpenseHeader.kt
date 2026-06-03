package com.example.financeflow.ui.components.Expenses

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financeflow.ui.components.common.FeatureMonthHeader

@Composable
fun ExpenseHeader(
    selectedMonth: String,
    onMonthChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ExpensesColors = getExpensesColors(false),
    availableMonths: List<String> = emptyList()
) {
    FeatureMonthHeader(
        title = "Expense Tracker",
        subtitle = "low-friction tracking for busy days",
        selectedMonth = selectedMonth,
        monthOptions = availableMonths,
        onMonthSelected = onMonthChange,
        modifier = modifier,
        headerColor = colors.HeaderRed
    )
}
package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.ExpenseUiItem
import com.example.financeflow.ui.expenses.HARDCODED_EXPENSES
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseTodayList(
    isDarkTheme: Boolean = false,
    todayExpenses: List<ExpenseUiItem>,
    openMenuId: Int?,
    onMenuToggle: (Int) -> Unit,
    onEdit: (ExpenseUiItem) -> Unit,
    onDelete: (ExpenseUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(isDarkTheme)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Today",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.TextPrimary
                )
                Surface(
                    color = colors.SurfaceGrey,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = todayExpenses.size.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (todayExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No expenses today. Tap a category to add one.",
                        fontSize = 13.sp,
                        color = colors.TextMuted
                    )
                }
            } else {
                Column {
                    todayExpenses.forEach { exp ->
                        ExpenseItemRow(
                            item = exp,
                            isMenuOpen = openMenuId == exp.id,
                            onMenuToggle = { onMenuToggle(exp.id) },
                            onEdit = { onEdit(exp) },
                            onDelete = { onDelete(exp) },
                            colors = colors
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTodayListPreview() {
    FinanceFlowTheme {
        ExpenseTodayList(
            todayExpenses = HARDCODED_EXPENSES.take(3),
            openMenuId = null,
            onMenuToggle = {},
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTodayListEmptyPreview() {
    FinanceFlowTheme {
        ExpenseTodayList(
            todayExpenses = emptyList(),
            openMenuId = null,
            onMenuToggle = {},
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
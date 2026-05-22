package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.ExpenseUiItem

@Composable
fun ExpenseTodayList(
    todayExpenses: List<ExpenseUiItem>,
    openMenuId: Int?,
    onMenuToggle: (Int) -> Unit,
    onEdit: (ExpenseUiItem) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ExpenseColors.CardBg),
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
                    color = ExpenseColors.TextPrimary
                )
                Surface(
                    color = ExpenseColors.SurfaceGrey,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = todayExpenses.size.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseColors.TextMuted,
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
                        color = ExpenseColors.TextMuted
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
                            onDelete = { onDelete(exp.id) }
                        )
                    }
                }
            }
        }
    }
}

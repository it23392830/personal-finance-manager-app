package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHeader(
    selectedMonth: String,
    onMonthChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ExpensesColors = getExpensesColors(false),
    availableMonths: List<String> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.HeaderRed)
            .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Expense Tracker",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "low-friction tracking for busy days",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Month Selector Box
        // Month Selector Box (simple dropdown)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = colors.CardBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedMonth.ifBlank { "" },
                        color = colors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.TextMuted
                    )
                }
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.CardBg)
            ) {
                availableMonths.forEach { m ->
                    DropdownMenuItem(text = { Text(m) }, onClick = { expanded = false; onMonthChange(m) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseHeaderPreview() {
    FinanceFlowTheme {
        ExpenseHeader(
            selectedMonth = "2026-05",
            onMonthChange = {},
            onAddClick = {}
        )
    }
}
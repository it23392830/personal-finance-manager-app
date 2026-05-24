package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun FilterToggleButton(
    active: Boolean,
    hasDot: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(false)
    Surface(
        onClick = onClick,
        color = if (active) colors.HeaderRed else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (active) null else androidx.compose.foundation.BorderStroke(1.dp, colors.Border),
        modifier = modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    tint = if (active) Color.White else colors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                if (hasDot) {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (active) Color.White else colors.HeaderRed, RoundedCornerShape(99.dp))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterToggleButtonPreview() {
    FinanceFlowTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterToggleButton(active = false, hasDot = false, onClick = {})
            FilterToggleButton(active = true, hasDot = false, onClick = {})
            FilterToggleButton(active = false, hasDot = true, onClick = {})
            FilterToggleButton(active = true, hasDot = true, onClick = {})
        }
    }
}

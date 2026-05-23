package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun SortPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) ExpenseColors.PrimaryText else Color.White,
        shape = RoundedCornerShape(99.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, ExpenseColors.Border),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
            color = if (selected) Color.White else ExpenseColors.TextMuted,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SortPillPreview() {
    FinanceFlowTheme {
        SortPill(
            label = "Newest",
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SortPillUnselectedPreview() {
    FinanceFlowTheme {
        SortPill(
            label = "Oldest",
            selected = false,
            onClick = {}
        )
    }
}

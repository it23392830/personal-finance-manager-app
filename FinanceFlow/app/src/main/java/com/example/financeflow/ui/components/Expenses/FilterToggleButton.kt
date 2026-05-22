package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.expenses.ExpenseColors

@Composable
fun FilterToggleButton(
    active: Boolean,
    hasDot: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (active) ExpenseColors.HeaderRed else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (active) null else androidx.compose.foundation.BorderStroke(1.dp, ExpenseColors.Border),
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
                    tint = if (active) Color.White else ExpenseColors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                if (hasDot) {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (active) Color.White else ExpenseColors.HeaderRed, RoundedCornerShape(99.dp))
                    )
                }
            }
        }
    }
}

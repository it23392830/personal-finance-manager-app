package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.expenses.PARENT_CATS

@Composable
fun ExpenseFilterPanel(
    filterType: String,             onFilterTypeChange: (String) -> Unit,
    filterCategory: String,         onFilterCategoryChange: (String) -> Unit,
    filterPayment: String,          onFilterPaymentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ExpenseColors.SurfaceGrey, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section 1 — Type
        FilterSection(label = "Type") {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all", "essential", "discretionary").forEach { type ->
                    FilterPill(
                        label = type.replaceFirstChar { it.uppercase() },
                        selected = filterType == type,
                        onClick = { onFilterTypeChange(type) }
                    )
                }
            }
        }

        // Section 2 — Payment
        FilterSection(label = "Payment") {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all", "card", "cash", "bank", "wallet").forEach { method ->
                    FilterPill(
                        label = method.replaceFirstChar { it.uppercase() },
                        selected = filterPayment == method,
                        onClick = { onFilterPaymentChange(method) }
                    )
                }
            }
        }

        // Section 3 — Category
        FilterSection(label = "Category") {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill(
                    label = "All",
                    selected = filterCategory == "all",
                    onClick = { onFilterCategoryChange("all") }
                )
                PARENT_CATS.forEach { cat ->
                    FilterPill(
                        label = cat.label,
                        selected = filterCategory == cat.id,
                        onClick = { onFilterCategoryChange(cat.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseColors.TextMuted)
        content()
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) ExpenseColors.HeaderRed else Color.White,
        shape = RoundedCornerShape(99.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, ExpenseColors.Border)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color.White else ExpenseColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

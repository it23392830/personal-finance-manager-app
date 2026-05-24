package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseQuickAdd(
    isDarkTheme: Boolean = false,
    recentCategoryIds: List<String>,
    onCategoryClick: (categoryId: String) -> Unit,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quick Add",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.TextPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            // 4-column grid of PARENT_CATS
            val rows = PARENT_CATS.chunked(4)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowItems.forEach { cat ->
                            CategoryQuickItem(
                                isDarkTheme = isDarkTheme,
                                category = cat,
                                onClick = { onCategoryClick(cat.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty spots if last row has < 4 items
                        if (rowItems.size < 4) {
                            repeat(4 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            if (recentCategoryIds.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Recently Used",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.TextMuted
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentCategoryIds.forEach { id ->
                        val cat = getCat(id)
                        Surface(
                            onClick = { onCategoryClick(id) },
                            color = colors.SurfaceGrey,
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 14.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(cat.label, fontSize = 12.sp, color = colors.TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryQuickItem(
    category: CategoryDef,
    onClick: () -> Unit,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(isDarkTheme)
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(category.bgColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(category.emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.label,
            fontSize = 11.sp,
            color = colors.TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseQuickAddPreview() {
    FinanceFlowTheme {
        ExpenseQuickAdd(
            recentCategoryIds = listOf("food_coffee", "transport_ride", "food_dining"),
            onCategoryClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
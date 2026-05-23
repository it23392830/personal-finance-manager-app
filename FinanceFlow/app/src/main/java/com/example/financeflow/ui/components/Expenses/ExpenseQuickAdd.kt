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
    recentCategoryIds: List<String>,
    onCategoryClick: (categoryId: String) -> Unit,
    onCustomClick: () -> Unit,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quick Add",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseColors.TextPrimary
                )
                Button(
                    onClick = onCustomClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseColors.HeaderRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("+ Add Expenses", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.White)
                }
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
                    color = ExpenseColors.TextMuted
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentCategoryIds.forEach { id ->
                        val cat = getCat(id)
                        Surface(
                            onClick = { onCategoryClick(id) },
                            color = ExpenseColors.SurfaceGrey,
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 14.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(cat.label, fontSize = 12.sp, color = ExpenseColors.TextPrimary)
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
    modifier: Modifier = Modifier
) {
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
            color = ExpenseColors.TextPrimary,
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
            onCustomClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

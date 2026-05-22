package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*

@Composable
fun ExpenseSmartSuggestions(
    suggestions: List<SuggestionUiItem>,
    onSuggestionClick: (SuggestionUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                "⚡ Smart Suggestions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.PrimaryText
            )

            Spacer(Modifier.height(12.dp))

            suggestions.forEach { suggestion ->
                val category = getCat(suggestion.categoryId)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onSuggestionClick(suggestion) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(category.bgColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(category.emoji, fontSize = 18.sp)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            suggestion.description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ExpenseColors.TextPrimary
                        )
                        Text(
                            "~${fmtLKR(suggestion.amount)} • ${suggestion.count}x",
                            fontSize = 11.sp,
                            color = ExpenseColors.TextMuted
                        )
                    }

                    Surface(
                        color = ExpenseColors.PrimaryBorder,
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            suggestion.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseColors.PrimaryText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

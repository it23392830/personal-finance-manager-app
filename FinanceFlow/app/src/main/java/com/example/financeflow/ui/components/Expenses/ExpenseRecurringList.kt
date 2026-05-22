package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*

@Composable
fun ExpenseRecurringList(
    recurringList: List<RecurringUiItem>,
    onToggleActive: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeItems = recurringList.filter { it.isActive }
    val monthlyTotal = activeItems.sumOf { it.amount }

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
                    "🕐 Upcoming Payments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseColors.TextPrimary
                )
                Surface(
                    color = ExpenseColors.PrimaryLight,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = activeItems.size.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseColors.PrimaryText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                recurringList
                    .filter { !it.missed }
                    .sortedBy { it.nextDue }
                    .forEach { item ->
                        RecurringItemRow(
                            item = item,
                            onToggleActive = { onToggleActive(item.id) }
                        )
                    }
            }

            Spacer(Modifier.height(20.dp))

            // Total Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ExpenseColors.SurfaceGrey, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Monthly Recurring Total",
                        fontSize = 13.sp,
                        color = ExpenseColors.TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        fmtLKR(monthlyTotal),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringItemRow(
    item: RecurringUiItem,
    onToggleActive: () -> Unit
) {
    val category = getCat(item.categoryId)
    // Simplified days calculation (string based for UI layer only)
    val daysUntil = calculateDaysUntil(item.nextDue, TODAY)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (item.isActive) 1f else 0.55f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(category.bgColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(category.emoji, fontSize = 18.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ExpenseColors.TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.frequency, fontSize = 11.sp, color = ExpenseColors.TextMuted)
                Spacer(Modifier.width(6.dp))
                // Hardcoded type badge logic for recurring
                val type = if (item.categoryId == "rent" || item.categoryId == "bills") "Essential" else "Discretionary"
                Surface(
                    color = if (type == "Essential") ExpenseColors.MustBg else ExpenseColors.PrimaryLight,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        type,
                        fontSize = 9.sp,
                        color = if (type == "Essential") ExpenseColors.MustText else ExpenseColors.PrimaryText,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(fmtLKR(item.amount), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(
                "in ${daysUntil}d",
                fontSize = 11.sp,
                color = if (daysUntil <= 5) ExpenseColors.MustAmber else ExpenseColors.TextMuted,
                fontWeight = if (daysUntil <= 5) FontWeight.Bold else FontWeight.Normal
            )
        }

        Switch(
            checked = item.isActive,
            onCheckedChange = { onToggleActive() },
            scale = 0.8f
        )
    }
}

private fun calculateDaysUntil(targetDate: String, today: String): Int {
    return try {
        val targetDay = targetDate.split("-").last().toInt()
        val todayDay = today.split("-").last().toInt()
        val diff = targetDay - todayDay
        if (diff < 0) diff + 30 else diff // Very rough approximation
    } catch (e: Exception) {
        5
    }
}

private fun Modifier.scale(scale: Float): Modifier = this // Helper as scale isn't a direct Modifier param for Switch

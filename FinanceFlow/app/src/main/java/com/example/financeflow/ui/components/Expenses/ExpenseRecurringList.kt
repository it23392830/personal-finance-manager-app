package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseRecurringList(
    isDarkTheme: Boolean = false,
    recurringList: List<RecurringUiItem>,
    onToggleActive: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeItems = recurringList.filter { it.isActive }
    val monthlyTotal = activeItems.sumOf { it.amount }
    val colors = getExpensesColors(isDarkTheme)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.CardBg),
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
                    color = colors.TextPrimary
                )
                Surface(
                    color = colors.PrimaryLight,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = activeItems.size.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.PrimaryText,
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
                            isDarkTheme = isDarkTheme,
                            onToggleActive = { onToggleActive(item.id) }
                        )
                    }
            }

            Spacer(Modifier.height(20.dp))

            // Total Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.SurfaceGrey, RoundedCornerShape(12.dp))
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
                        color = colors.TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        fmtLKR(monthlyTotal),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringItemRow(
    item: RecurringUiItem,
    isDarkTheme: Boolean = false,
    onToggleActive: () -> Unit
) {
    val category = getCat(item.categoryId)
    val colors = getExpensesColors(isDarkTheme)
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
                    color = colors.TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.frequency, fontSize = 11.sp, color = colors.TextMuted)
                    Spacer(Modifier.width(6.dp))
                    // Hardcoded type badge logic for recurring
                    val type = if (item.categoryId == "rent" || item.categoryId == "bills") "Essential" else "Discretionary"
                    Surface(
                        color = if (type == "Essential") colors.MustBg else colors.PrimaryLight,
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            type,
                            fontSize = 9.sp,
                            color = if (type == "Essential") colors.MustText else colors.PrimaryText,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(fmtLKR(item.amount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextPrimary)
            Text(
                "in ${daysUntil}d",
                fontSize = 11.sp,
                color = if (daysUntil <= 5) colors.MustAmber else colors.TextMuted,
                fontWeight = if (daysUntil <= 5) FontWeight.Bold else FontWeight.Normal
            )
        }

        Switch(
            checked = item.isActive,
            onCheckedChange = { onToggleActive() },
            modifier = Modifier.scale(0.8f)
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

@Preview(showBackground = true)
@Composable
fun ExpenseRecurringListPreview() {
    FinanceFlowTheme {
        ExpenseRecurringList(
            recurringList = HARDCODED_RECURRING,
            onToggleActive = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

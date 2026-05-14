package com.example.financeflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ──────────────────────────────────────────────
// Data models
// ──────────────────────────────────────────────

data class ExpenseCategoryData(
    val categoryName: String,
    val amount: String,
    val transactionCount: Int,
    val budgetFraction: Float,            // 0f..1f  (spent / budget)
    val budgetAmount: String? = null,
    val icon: ImageVector,
    val accentColor: Color,
    val secondaryColor: Color = accentColor.copy(alpha = 0.45f),
    val trend: Float? = null              // % change vs last period
)

// Pre-built category palettes
object ExpensePalette {
    val food       = Color(0xFFFF8C67) to Color(0xFFFFBDA0)
    val transport  = Color(0xFF5C7BE0) to Color(0xFF9DB3F4)
    val shopping   = Color(0xFFFF6B8B) to Color(0xFFFFABBD)
    val health     = Color(0xFF4EBA82) to Color(0xFF8FDDB0)
    val housing    = Color(0xFF9B72E8) to Color(0xFFD4AEFF)
    val education  = Color(0xFF4EC9C9) to Color(0xFF88E0D8)
    val entertain  = Color(0xFFFFB347) to Color(0xFFFFD699)
    val savings    = Color(0xFF6FCF97) to Color(0xFFA8E6C0)
}

// ──────────────────────────────────────────────
// Public composable — full card
// ──────────────────────────────────────────────

@Composable
fun ExpenseCategoryCard(
    data: ExpenseCategoryData,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clampedFraction = data.budgetFraction.coerceIn(0f, 1f)
    val isOverBudget = data.budgetFraction > 1f
    val barColor = if (isOverBudget) Color(0xFFFF4D6D) else data.accentColor
    val barSecondary = if (isOverBudget) Color(0xFFFF9DB5) else data.secondaryColor

    val animatedFraction by animateFloatAsState(
        targetValue = clampedFraction,
        animationSpec = tween(850, easing = EaseOutCubic),
        label = "expense_bar"
    )

    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = data.accentColor.copy(alpha = 0.18f),
                spotColor = data.accentColor.copy(alpha = 0.10f)
            ),
        shape = cardShape,
        onClick = onClick ?: {},
        enabled = onClick != null,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Top row: icon, name, amount ──────────
            CategoryTopRow(
                icon = data.icon,
                name = data.categoryName,
                amount = data.amount,
                accent = data.accentColor,
                secondary = data.secondaryColor,
                trend = data.trend
            )

            // ── Budget bar ───────────────────────────
            CategoryBudgetBar(
                progress = animatedFraction,
                accent = barColor,
                secondary = barSecondary,
                isOverBudget = isOverBudget
            )

            // ── Footer: transactions + budget label ──
            CategoryFooter(
                transactionCount = data.transactionCount,
                budgetAmount = data.budgetAmount,
                fraction = data.budgetFraction,
                isOverBudget = isOverBudget,
                accent = data.accentColor
            )
        }
    }
}

// ──────────────────────────────────────────────
// Inline row variant (use inside LazyColumn directly)
// ──────────────────────────────────────────────

@Composable
fun ExpenseCategoryRow(
    data: ExpenseCategoryData,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = data.budgetFraction.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "row_bar"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.linearGradient(
                        listOf(data.accentColor.copy(alpha = 0.18f), data.secondaryColor.copy(alpha = 0.10f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.categoryName,
                tint = data.accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = data.categoryName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF1D1530)
                )
                Text(
                    text = data.amount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1D1530)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mini bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(data.secondaryColor.copy(alpha = 0.18f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(data.accentColor, data.secondaryColor)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${data.transactionCount} transactions",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFADABBB)
            )
        }
    }
}

// ──────────────────────────────────────────────
// Sub-composables
// ──────────────────────────────────────────────

@Composable
private fun CategoryTopRow(
    icon: ImageVector,
    name: String,
    amount: String,
    accent: Color,
    secondary: Color,
    trend: Float?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.18f), secondary.copy(alpha = 0.10f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1D1530),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                trend?.let {
                    val sign = if (it >= 0) "+" else ""
                    val trendColor = if (it >= 0) Color(0xFFFF7E9D) else Color(0xFF6FCF97)
                    Text(
                        text = "${sign}${"%.1f".format(it)}% vs last month",
                        style = MaterialTheme.typography.labelSmall,
                        color = trendColor
                    )
                }
            }
        }

        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = Color(0xFF1D1530)
        )
    }
}

@Composable
private fun CategoryBudgetBar(
    progress: Float,
    accent: Color,
    secondary: Color,
    isOverBudget: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(secondary.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(listOf(accent, secondary))
                    )
            )
        }

        if (isOverBudget) {
            Text(
                text = "Over budget",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFFFF4D6D)
            )
        }
    }
}

@Composable
private fun CategoryFooter(
    transactionCount: Int,
    budgetAmount: String?,
    fraction: Float,
    isOverBudget: Boolean,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Receipt,
                contentDescription = null,
                tint = Color(0xFFADABBB),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "$transactionCount transactions",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFADABBB)
            )
        }

        budgetAmount?.let {
            val used = "${"%.0f".format(fraction * 100)}%"
            val color = if (isOverBudget) Color(0xFFFF4D6D) else accent
            Text(
                text = "$used of $it",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = color
            )
        }
    }
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3EEFF)
@Composable
fun ExpenseCategoryCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ExpenseCategoryCard(
                data = ExpenseCategoryData(
                    categoryName = "Food & Dining",
                    amount = "$640",
                    transactionCount = 18,
                    budgetFraction = 0.72f,
                    budgetAmount = "$900",
                    icon = Icons.Rounded.Restaurant,
                    accentColor = ExpensePalette.food.first,
                    secondaryColor = ExpensePalette.food.second,
                    trend = 12.5f
                )
            )
            ExpenseCategoryCard(
                data = ExpenseCategoryData(
                    categoryName = "Shopping",
                    amount = "$1,240",
                    transactionCount = 9,
                    budgetFraction = 1.24f,
                    budgetAmount = "$1,000",
                    icon = Icons.Rounded.ShoppingBag,
                    accentColor = ExpensePalette.shopping.first,
                    secondaryColor = ExpensePalette.shopping.second,
                    trend = -5.2f
                )
            )
            ExpenseCategoryCard(
                data = ExpenseCategoryData(
                    categoryName = "Transport",
                    amount = "$185",
                    transactionCount = 22,
                    budgetFraction = 0.37f,
                    budgetAmount = "$500",
                    icon = Icons.Rounded.DirectionsCar,
                    accentColor = ExpensePalette.transport.first,
                    secondaryColor = ExpensePalette.transport.second
                )
            )
        }
    }
}

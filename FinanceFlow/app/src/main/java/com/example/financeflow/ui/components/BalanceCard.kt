package com.example.financeflow.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ──────────────────────────────────────────────
// Data model
// ──────────────────────────────────────────────

data class BalanceCardData(
    val totalBalance: String,
    val income: String,
    val expenses: String,
    val incomeLabel: String = "Income",
    val expensesLabel: String = "Expenses",
    val cardTitle: String = "Total Balance",
    val changePercent: Float = 0f,       // e.g. 3.2 → "+3.2%"
    val changeLabel: String = "vs last month"
)

// ──────────────────────────────────────────────
// Palette (pastel purple fintech)
// ──────────────────────────────────────────────

private val GradientStart  = Color(0xFF7C5CBF)   // deep soft purple
private val GradientEnd    = Color(0xFFB48FE0)   // lavender mid
private val GradientAccent = Color(0xFFD4AEFF)   // pale lilac highlight
private val SurfaceWhite   = Color(0xFFFAF7FF)
private val IncomeGreen    = Color(0xFF6FCF97)
private val ExpenseRose    = Color(0xFFFF7E9D)

// ──────────────────────────────────────────────
// Public composable
// ──────────────────────────────────────────────

@Composable
fun BalanceCard(
    data: BalanceCardData,
    modifier: Modifier = Modifier,
    onVisibilityToggle: ((Boolean) -> Unit)? = null
) {
    var balanceVisible by remember { mutableStateOf(true) }

    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = cardShape,
                ambientColor = GradientStart.copy(alpha = 0.35f),
                spotColor = GradientStart.copy(alpha = 0.25f)
            )
            .clip(cardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd, GradientAccent),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 600f)
                )
            )
    ) {
        // Decorative circles for depth
        DecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            // ── Header row ──────────────────────────
            BalanceCardHeader(
                title = data.cardTitle,
                visible = balanceVisible,
                onToggle = {
                    balanceVisible = !balanceVisible
                    onVisibilityToggle?.invoke(balanceVisible)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── Balance amount ───────────────────────
            BalanceAmount(
                amount = data.totalBalance,
                visible = balanceVisible
            )

            // ── Change chip ──────────────────────────
            if (data.changePercent != 0f) {
                Spacer(modifier = Modifier.height(10.dp))
                ChangeChip(
                    percent = data.changePercent,
                    label = data.changeLabel
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            Divider(color = SurfaceWhite.copy(alpha = 0.25f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // ── Income / Expense row ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceStat(
                    label = data.incomeLabel,
                    amount = if (balanceVisible) data.income else "••••••",
                    tint = IncomeGreen,
                    isIncome = true
                )
                BalanceStat(
                    label = data.expensesLabel,
                    amount = if (balanceVisible) data.expenses else "••••••",
                    tint = ExpenseRose,
                    isIncome = false
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Sub-composables
// ──────────────────────────────────────────────

@Composable
private fun DecorativeCircles() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = 220.dp, y = (-40).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 280.dp, y = 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

@Composable
private fun BalanceCardHeader(
    title: String,
    visible: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.80f),
            letterSpacing = 0.8.sp
        )
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (visible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                contentDescription = if (visible) "Hide balance" else "Show balance",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BalanceAmount(
    amount: String,
    visible: Boolean
) {
    val displayText = if (visible) amount else "••••••••"
    Text(
        text = displayText,
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        ),
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ChangeChip(
    percent: Float,
    label: String
) {
    val isPositive = percent >= 0
    val sign = if (isPositive) "+" else ""
    val bg = if (isPositive)
        Color(0xFF6FCF97).copy(alpha = 0.22f)
    else
        Color(0xFFFF7E9D).copy(alpha = 0.22f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowUp,
            contentDescription = null,
            tint = if (isPositive) IncomeGreen else ExpenseRose,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "$sign${"%.1f".format(percent)}%  $label",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.90f)
        )
    }
}

@Composable
private fun BalanceStat(
    label: String,
    amount: String,
    tint: Color,
    isIncome: Boolean
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(tint)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3EEFF)
@Composable
fun BalanceCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            BalanceCard(
                data = BalanceCardData(
                    totalBalance = "$24,530.00",
                    income = "$8,200.00",
                    expenses = "$3,670.00",
                    changePercent = 3.2f,
                    changeLabel = "vs last month"
                )
            )
        }
    }
}

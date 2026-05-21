package com.example.financeflow.ui.components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

// ─────────────────────────────────────────────
//  Design Tokens
// ─────────────────────────────────────────────
private val CardWhite       = Color(0xFFFFFFFF)
private val TextPrimary     = Color(0xFF1A1A2E)
private val TextSecondary   = Color(0xFF6B7280)
private val PrimaryPurple   = Color(0xFF7C4DFF)

// Pastel icon backgrounds
val PastelGreen   = Color(0xFFE8F5E9)
val PastelRed     = Color(0xFFFFEBEE)
val PastelBlue    = Color(0xFFE3F2FD)
val PastelOrange  = Color(0xFFFFF3E0)
val PastelPurple  = Color(0xFFEDE7FF)

val IconGreen     = Color(0xFF2DBD6E)
val IconRed       = Color(0xFFFF5252)
val IconBlue      = Color(0xFF2196F3)
val IconOrange    = Color(0xFFFF9800)
val IconPurple    = Color(0xFF7C4DFF)

// ─────────────────────────────────────────────
//  Data model
// ─────────────────────────────────────────────
data class SummaryCardData(
    val title: String,
    val amount: Long,
    val currencySymbol: String = "LKR",
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val badgeText: String? = null,       // e.g. "28 %"
    val badgeColor: Color = PrimaryPurple
)

// ─────────────────────────────────────────────
//  Hardcoded sample list  (remove / replace later)
// ─────────────────────────────────────────────
fun moneyFlowSampleData(): List<SummaryCardData> = listOf(
    SummaryCardData(
        title           = "Total Income",
        amount          = 187_500L,
        icon            = Icons.Outlined.TrendingUp,
        iconTint        = IconGreen,
        iconBackground  = PastelGreen
    ),
    SummaryCardData(
        title           = "Allocated to Goals",
        amount          = 53_200L,
        icon            = Icons.Outlined.Savings,
        iconTint        = IconPurple,
        iconBackground  = PastelPurple,
        badgeText       = "28 %"
    ),
    SummaryCardData(
        title           = "Reserved for Must Expenses",
        amount          = 52_000L,
        icon            = Icons.Outlined.CreditCard,
        iconTint        = IconRed,
        iconBackground  = PastelRed
    ),
    SummaryCardData(
        title           = "Available for Optional Spending",
        amount          = 13_900L,
        icon            = Icons.Outlined.AttachMoney,
        iconTint        = IconOrange,
        iconBackground  = PastelOrange
    )
)

/**
 * SummaryCard
 */
@Composable
fun SummaryCard(
    data: SummaryCardData,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation     = 3.dp,
                shape         = RoundedCornerShape(16.dp),
                ambientColor  = Color(0xFF7C4DFF).copy(alpha = 0.07f),
                spotColor     = Color(0xFF7C4DFF).copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier          = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(data.iconBackground),
                contentAlignment  = Alignment.Center
            ) {
                Icon(
                    imageVector        = data.icon,
                    contentDescription = null,
                    tint               = data.iconTint,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text  = data.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color      = TextSecondary,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text  = "${data.currencySymbol} ${"%,d".format(data.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                )
            }

            data.badgeText?.let { badge ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = data.badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text     = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            color      = data.badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * MoneyFlowSection
 */
@Composable
fun MoneyFlowSection(
    items: List<SummaryCardData> = moneyFlowSampleData(),
    onIncomeClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onSavingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text  = "Your Money Flow",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                fontSize   = 18.sp
            )
        )
        items.forEach { item ->
            SummaryCard(
                data = item,
                onClick = {
                    when (item.title) {
                        "Total Income" -> onIncomeClick()
                        "Allocated to Goals" -> onGoalsClick()
                        "Reserved for Must Expenses" -> onExpensesClick()
                        "Available for Optional Spending" -> onSavingsClick()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun SummaryCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MoneyFlowSection()
        }
    }
}

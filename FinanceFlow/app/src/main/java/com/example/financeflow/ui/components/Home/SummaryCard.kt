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
import com.example.financeflow.ui.theme.CardWhite

//  Design Tokens - Light Mode
private val LightTextPrimary     = Color(0xFF1A1A2E)
private val LightTextSecondary   = Color(0xFF6B7280)
private val LightCardBg          = Color.White

//  Design Tokens - Dark Mode
private val DarkTextPrimary      = Color(0xFFE8E8E8)
private val DarkTextSecondary    = Color(0xFFB0B0B0)
private val DarkCardBg           = Color(0xFF2A2A3E)

private val PrimaryPurple   = Color(0xFF7C4DFF)

data class SummaryCardColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBg: Color
)

private fun getSummaryCardColors(isDarkTheme: Boolean): SummaryCardColors =
    if (isDarkTheme) {
        SummaryCardColors(
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            cardBg = DarkCardBg
        )
    } else {
        SummaryCardColors(
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            cardBg = LightCardBg
        )
    }

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

//  Data model
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

//  Hardcoded sample list
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
    isDarkTheme: Boolean = false,
    data: SummaryCardData,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = getSummaryCardColors(isDarkTheme)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation     = 3.dp,
                shape         = RoundedCornerShape(16.dp),
                ambientColor  = PrimaryPurple.copy(alpha = 0.07f),
                spotColor     = PrimaryPurple.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBg)
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
                        color      = colors.textSecondary,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text  = "${data.currencySymbol} ${"%,d".format(data.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color      = colors.textPrimary,
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
    isDarkTheme: Boolean = false,
    items: List<SummaryCardData> = moneyFlowSampleData(),
    onIncomeClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onSavingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = getSummaryCardColors(isDarkTheme)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text  = "Your Money Flow",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = colors.textPrimary,
                fontSize   = 18.sp
            )
        )
        items.forEach { item ->
            SummaryCard(
                isDarkTheme = isDarkTheme,
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

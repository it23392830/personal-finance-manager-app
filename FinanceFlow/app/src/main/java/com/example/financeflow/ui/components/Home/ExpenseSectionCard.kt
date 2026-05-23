package com.example.financeflow.ui.components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.savings.CardWhite

//  Design Tokens
// CardWhite is provided by SavingsCard.kt in the same package
private val LightTextPrimary     = Color(0xFF1A1A2E)
private val LightTextSecondary   = Color(0xFF6B7280)
private val LightDividerColor    = Color(0xFFF0EBF8)

private val DarkTextPrimary      = Color(0xFFE8E8E8)
private val DarkTextSecondary    = Color(0xFFB0B0B0)
private val DarkDividerColor     = Color(0xFF3A3A4E)

data class ExpenseBreakdownColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val dividerColor: Color
)

private fun getExpenseBreakdownColors(isDarkTheme: Boolean): ExpenseBreakdownColors =
    if (isDarkTheme) {
        ExpenseBreakdownColors(
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            dividerColor = DarkDividerColor
        )
    } else {
        ExpenseBreakdownColors(
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            dividerColor = LightDividerColor
        )
    }

// Must Expenses palette
private val MustHeaderBg    = Color(0xFFFFEBEE)
private val MustHeaderText  = Color(0xFFD32F2F)
private val MustIcon        = Color(0xFFFF5252)
private val MustAmount      = Color(0xFFFF5252)

// Optional Expenses palette
private val OptHeaderBg     = Color(0xFFE8F5E9)
private val OptHeaderText   = Color(0xFF2E7D32)
private val OptIcon         = Color(0xFF2DBD6E)
private val OptAmount       = Color(0xFF2DBD6E)
//  Data models
enum class ExpenseType { MUST, OPTIONAL }

data class ExpenseItem(
    val name: String,
    val amount: Long,
    val currencySymbol: String = "LKR"
)

data class ExpenseSectionData(
    val type: ExpenseType,
    val totalAmount: Long,
    val items: List<ExpenseItem>,
    val currencySymbol: String = "LKR"
) {
    val title: String
        get() = when (type) {
            ExpenseType.MUST     -> "Must Expenses"
            ExpenseType.OPTIONAL -> "Optional Expenses"
        }

    val icon: ImageVector
        get() = when (type) {
            ExpenseType.MUST     -> Icons.Outlined.Shield
            ExpenseType.OPTIONAL -> Icons.Outlined.AttachMoney
        }

    val headerBg: Color
        get() = when (type) {
            ExpenseType.MUST     -> MustHeaderBg
            ExpenseType.OPTIONAL -> OptHeaderBg
        }

    val headerText: Color
        get() = when (type) {
            ExpenseType.MUST     -> MustHeaderText
            ExpenseType.OPTIONAL -> OptHeaderText
        }

    val iconTint: Color
        get() = when (type) {
            ExpenseType.MUST     -> MustIcon
            ExpenseType.OPTIONAL -> OptIcon
        }

    val amountColor: Color
        get() = when (type) {
            ExpenseType.MUST     -> MustAmount
            ExpenseType.OPTIONAL -> OptAmount
        }
}
//  Hardcoded sample  (remove / replace later)
fun expenseSampleData(): List<ExpenseSectionData> = listOf(
    ExpenseSectionData(
        type        = ExpenseType.MUST,
        totalAmount = 52_000L,
        items       = listOf(
            ExpenseItem("Rent",          34_000L),
            ExpenseItem("Utilities",      8_500L),
            ExpenseItem("Subscriptions",  5_200L),
            ExpenseItem("Internet",       4_300L)
        )
    ),
    ExpenseSectionData(
        type        = ExpenseType.OPTIONAL,
        totalAmount = 68_400L,
        items       = listOf(
            ExpenseItem("Food & Dining",  28_400L),
            ExpenseItem("Transport",      18_600L),
            ExpenseItem("Entertainment",  12_900L),
            ExpenseItem("Shopping",        8_500L)
        )
    )
)
/**
 * ExpenseSectionCard
 *
 * Renders a grouped expense section (Must or Optional) with a
 * coloured header row and individual line items.
 *
 * @param data      Section data. Replace with ViewModel state later.
 * @param modifier  External layout modifier.
 */
@Composable
fun ExpenseSectionCard(
    isDarkTheme: Boolean = false,
    data: ExpenseSectionData,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) Color(0xFF2A2A3E) else CardWhite
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 3.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF7C4DFF).copy(alpha = 0.06f),
                spotColor    = Color(0xFF7C4DFF).copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
    ) {
        Column {
            ExpenseSectionHeader(data = data)
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical   = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                data.items.forEachIndexed { index, item ->
                    ExpenseLineItem(
                        isDarkTheme    = isDarkTheme,
                        item           = item,
                        showDivider    = index < data.items.lastIndex
                    )
                }
            }
        }
    }
}
/**
 * ExpenseBreakdownSection
 *
 * Renders the complete "Expense Breakdown" section with title
 * and all [ExpenseSectionCard] items stacked.
 */
@Composable
fun ExpenseBreakdownSection(
    isDarkTheme: Boolean = false,
    sections: List<ExpenseSectionData> = expenseSampleData(),
    modifier: Modifier = Modifier
) {
    val colors = getExpenseBreakdownColors(isDarkTheme)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text  = "Expense Breakdown",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = colors.textPrimary,
                fontSize   = 18.sp
            )
        )
        sections.forEach { section ->
            ExpenseSectionCard(isDarkTheme = isDarkTheme, data = section)
        }
    }
}
//  Private sub-composables

@Composable
private fun ExpenseSectionHeader(data: ExpenseSectionData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(data.headerBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = data.icon,
                contentDescription = null,
                tint               = data.iconTint,
                modifier           = Modifier.size(18.dp)
            )
            Text(
                text  = data.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = data.headerText,
                    fontSize   = 14.sp
                )
            )
        }

        Text(
            text  = "${data.currencySymbol} ${"%,d".format(data.totalAmount)}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = data.amountColor,
                fontSize   = 14.sp
            )
        )
    }
}

@Composable
private fun ExpenseLineItem(
    isDarkTheme: Boolean = false,
    item: ExpenseItem,
    showDivider: Boolean
) {
    val colors = getExpenseBreakdownColors(isDarkTheme)
    Column {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color    = colors.textSecondary,
                    fontSize = 13.sp
                )
            )
            Text(
                text      = "${item.currencySymbol} ${"%,d".format(item.amount)}",
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color      = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp
                ),
                textAlign = TextAlign.End
            )
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color     = colors.dividerColor
            )
        }
    }
}
//  Preview
@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun ExpenseBreakdownPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpenseBreakdownSection()
        }
    }
}

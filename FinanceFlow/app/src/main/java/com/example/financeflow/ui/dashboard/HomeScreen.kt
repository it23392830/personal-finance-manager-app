package com.example.financeflow.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.Home.BalanceCard
import com.example.financeflow.ui.components.Home.BalanceCardData
import com.example.financeflow.ui.components.Home.ExpenseBreakdownSection
import com.example.financeflow.ui.components.Home.MoneyFlowSection
import com.example.financeflow.ui.components.Home.QuickActionRow
import com.example.financeflow.ui.components.Home.expenseSampleData
import com.example.financeflow.ui.components.Home.moneyFlowSampleData
import com.example.financeflow.ui.components.savings.GoalProgressCard
import com.example.financeflow.ui.components.savings.GoalProgressData

// ─────────────────────────────────────────────
//  Design Tokens
// ─────────────────────────────────────────────
private val ScaffoldBg       = Color(0xFFF5F3FF)
private val TextPrimary      = Color(0xFF1A1A2E)
private val TextSecondary    = Color(0xFF6B7280)
private val CardWhite        = Color.White
private val ProgressTrackBg  = Color(0xFFFFE0E0)

// ─────────────────────────────────────────────
//  Hardcoded sample data
// ─────────────────────────────────────────────
private val sampleBalanceData = BalanceCardData(
    userName = "Kavindu",
    availableBalance = 35_000L,
    totalIncome = 120_000L,
    totalExpenses = 37_500L,
    totalSaved = 53_200L,
    streakDays = 3
)

private val sampleGoalData = GoalProgressData(
    goalTitle = "MacBook Pro M4 Goal",
    currentAmount = 11_200L,
    targetAmount = 490_000L,
    daysRemaining = 267,
    dailySavingsNeeded = 1_794L,
    currentDailyRate = 1_774L
)

private data class IncomeSourceItem(
    val source: String,
    val amount: Long,
    val currencySymbol: String = "LKR",
    val isPositive: Boolean = true
)

private val sampleIncomeSources = listOf(
    IncomeSourceItem("Salary",         135_000L),
    IncomeSourceItem("Freelance",       45_000L),
    IncomeSourceItem("AdSense (USD)",    5_200L),
    IncomeSourceItem("Crypto Trading",  2_300L, isPositive = true)
)

private data class MonthlySummaryData(
    val month: String         = "May 2026",
    val savingsRatePercent: Int = 28,
    val optionalBudgetPercent: Int = 17
)

private val sampleMonthlySummary = MonthlySummaryData()

private const val OPTIONAL_BUDGET_USED_PERCENT = 83
private val OPTIONAL_BUDGET_REMAINING          = 13_900L

/**
 * HomeScreen
 */
@Composable
fun HomeScreen(
    onAddIncomeClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onIncomeClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onSavingsClick: () -> Unit = {},
    onGoalCardClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScaffoldBg)
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                top    = 20.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                BalanceCard(
                    data = sampleBalanceData,
                    onThemeClick = onThemeClick,
                    onProfileClick = onProfileClick
                )
            }

            item {
                QuickActionRow(
                    onAddIncomeClick = onAddIncomeClick,
                    onAddExpenseClick = onAddExpenseClick
                )
            }

            item {
                MoneyFlowSection(
                    items = moneyFlowSampleData(),
                    onIncomeClick = onIncomeClick,
                    onGoalsClick = onGoalsClick,
                    onExpensesClick = onExpensesClick,
                    onSavingsClick = onSavingsClick
                )
            }

            item {
                SectionHeader(title = "Savings Goal")
                Spacer(modifier = Modifier.height(10.dp))
                GoalProgressCard(
                    data = sampleGoalData,
                    onClick = onGoalCardClick
                )
            }

            item {
                MonthlySummarySection(data = sampleMonthlySummary)
            }

            item {
                IncomeSourcesCard(sources = sampleIncomeSources)
            }

            item {
                ExpenseBreakdownSection(sections = expenseSampleData())
            }

            item {
                BudgetUsageBar(
                    usedPercent      = OPTIONAL_BUDGET_USED_PERCENT,
                    remainingAmount  = OPTIONAL_BUDGET_REMAINING
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text     = title,
        modifier = modifier,
        style    = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color      = TextPrimary,
            fontSize   = 18.sp
        )
    )
}

@Composable
private fun MonthlySummarySection(data: MonthlySummaryData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = "${data.month} Summary")

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            SummaryStatTile(
                label      = "Savings Rate",
                value      = "${data.savingsRatePercent}%",
                icon       = Icons.Outlined.Savings,
                background = Color(0xFFE8F5E9),
                iconTint   = Color(0xFF2DBD6E),
                textColor  = Color(0xFF2DBD6E),
                modifier   = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            SummaryStatTile(
                label      = "Optional Budget",
                value      = "${data.optionalBudgetPercent}%",
                icon       = Icons.Outlined.AttachMoney,
                background = Color(0xFFFFEBEE),
                iconTint   = Color(0xFFFF5252),
                textColor  = Color(0xFFFF5252),
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryStatTile(
    label: String,
    value: String,
    icon: ImageVector,
    background: Color,
    iconTint: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation    = 3.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = iconTint.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier            = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = iconTint,
                modifier           = Modifier.size(28.dp)
            )
            Text(
                text  = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = textColor,
                    fontSize   = 28.sp
                )
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun IncomeSourcesCard(sources: List<IncomeSourceItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 3.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF7C4DFF).copy(alpha = 0.07f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = "Income Sources",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    fontSize   = 15.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            sources.forEachIndexed { index, source ->
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = source.source,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color    = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text  = if (source.isPositive)
                            "LKR ${"%,d".format(source.amount)}"
                        else
                            "+LKR ${"%,d".format(source.amount)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color      = if (source.isPositive) TextPrimary
                            else Color(0xFF2DBD6E),
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp
                        )
                    )
                }
                if (index < sources.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color     = Color(0xFFF0EBF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetUsageBar(
    usedPercent: Int,
    remainingAmount: Long,
    currencySymbol: String = "LKR"
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) usedPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 800),
        label         = "budgetProgress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 3.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFFFF5252).copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "$usedPercent% Optional Budget Used",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                        fontSize   = 13.sp
                    )
                )
                Text(
                    text  = "$currencySymbol ${"%,d".format(remainingAmount)} remaining",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ProgressTrackBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF5252), Color(0xFFFF8A80))
                            )
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

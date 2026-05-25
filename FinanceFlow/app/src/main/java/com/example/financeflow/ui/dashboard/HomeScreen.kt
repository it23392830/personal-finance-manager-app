package com.example.financeflow.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.Home.BalanceCard
import com.example.financeflow.ui.components.Home.BalanceCardData
import com.example.financeflow.ui.components.Home.ExpenseBreakdownSection
import com.example.financeflow.ui.components.Home.MoneyFlowSection
import com.example.financeflow.ui.components.Home.MonthlySummaryCard
import com.example.financeflow.ui.components.Home.QuickActionRow
import com.example.financeflow.ui.components.Home.ExpenseItem
import com.example.financeflow.ui.components.Home.ExpenseSectionData
import com.example.financeflow.ui.components.Home.ExpenseType
import com.example.financeflow.ui.components.Home.IconGreen
import com.example.financeflow.ui.components.Home.IconOrange
import com.example.financeflow.ui.components.Home.IconPurple
import com.example.financeflow.ui.components.Home.IconRed
import com.example.financeflow.ui.components.Home.PastelGreen
import com.example.financeflow.ui.components.Home.PastelOrange
import com.example.financeflow.ui.components.Home.PastelPurple
import com.example.financeflow.ui.components.Home.PastelRed
import com.example.financeflow.ui.components.Home.SummaryCardData
import com.example.financeflow.ui.components.savings.GoalProgressCard
import com.example.financeflow.ui.components.savings.GoalProgressData
import com.example.financeflow.viewmodel.dashboard.DashboardViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.max
import kotlin.math.roundToInt

// ─────────────────────────────────────────────
//  Design Tokens - Light Mode
// ─────────────────────────────────────────────
private val LightScaffoldBg       = Color(0xFFF5F3FF)
private val LightTextPrimary      = Color(0xFF1A1A2E)
private val LightTextSecondary    = Color(0xFF6B7280)
private val LightCardWhite        = Color.White
private val LightProgressTrackBg  = Color(0xFFFFE0E0)

// ─────────────────────────────────────────────
//  Design Tokens - Dark Mode
// ─────────────────────────────────────────────
private val DarkScaffoldBg        = Color(0xFF1A1A2E)
private val DarkTextPrimary       = Color(0xFFE8E8E8)
private val DarkTextSecondary     = Color(0xFFB0B0B0)
private val DarkCardBg            = Color(0xFF2A2A3E)
private val DarkProgressTrackBg   = Color(0xFF3A2A3E)

// ─────────────────────────────────────────────
//  Color Selection Helper
// ─────────────────────────────────────────────
data class HomeScreenColors(
    val scaffoldBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBg: Color,
    val progressTrackBg: Color
)

private fun getHomeScreenColors(isDarkTheme: Boolean): HomeScreenColors =
    if (isDarkTheme) {
        HomeScreenColors(
            scaffoldBg = DarkScaffoldBg,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            cardBg = DarkCardBg,
            progressTrackBg = DarkProgressTrackBg
        )
    } else {
        HomeScreenColors(
            scaffoldBg = LightScaffoldBg,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            cardBg = LightCardWhite,
            progressTrackBg = LightProgressTrackBg
        )
    }

private data class IncomeSourceItem(
    val source: String,
    val amount: Long,
    val currencySymbol: String = "LKR",
    val isPositive: Boolean = true
)

/**
 * HomeScreen
 */
@Composable
fun HomeScreen(
    isDarkTheme: Boolean = false,
    streakDays: Int = 0,
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddIncomeClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onIncomeClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onSavingsClick: () -> Unit = {},
    onGoalCardClick: () -> Unit = {},
    onViewInsightsClick: () -> Unit = {},
    onStreakClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    unreadNotificationCount: Int = 0
) {
    val colors = getHomeScreenColors(isDarkTheme)
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val remainingBalance by viewModel.remainingBalance.collectAsState()
    val incomeSources by viewModel.incomeSources.collectAsState()
    val expenseSectionSummary by viewModel.expenseSectionSummary.collectAsState()
    val goalProgress by viewModel.goalProgress.collectAsState()
    val monthlySummary by viewModel.monthlySummary.collectAsState()
    val userName by viewModel.userName.collectAsState()

    val balanceCardData = BalanceCardData(
        userName = userName,
        availableBalance = remainingBalance.toLong(),
        totalIncome = totalIncome.toLong(),
        totalExpenses = totalExpenses.toLong(),
        totalSaved = totalSavings.toLong(),
        streakDays = streakDays
    )

    val savingsPct = if (totalIncome > 0.0) ((totalSavings / totalIncome) * 100).roundToInt() else 0
    val moneyFlowItems = listOf(
        SummaryCardData(
            title = "Total Income",
            amount = totalIncome.toLong(),
            icon = Icons.Outlined.TrendingUp,
            iconTint = IconGreen,
            iconBackground = PastelGreen
        ),
        SummaryCardData(
            title = "Allocated to Goals",
            amount = totalSavings.toLong(),
            icon = Icons.Outlined.Savings,
            iconTint = IconPurple,
            iconBackground = PastelPurple,
            badgeText = if (savingsPct > 0) "$savingsPct %" else null
        ),
        SummaryCardData(
            title = "Reserved for Must Expenses",
            amount = expenseSectionSummary.mustTotal.toLong(),
            icon = Icons.Outlined.CreditCard,
            iconTint = IconRed,
            iconBackground = PastelRed
        ),
        SummaryCardData(
            title = "Available for Optional Spending",
            amount = max(0.0, remainingBalance).toLong(),
            icon = Icons.Outlined.AttachMoney,
            iconTint = IconOrange,
            iconBackground = PastelOrange
        )
    )

    val goalCardData = goalProgress.firstOrNull()?.let { goal ->
        GoalProgressData(
            goalTitle = goal.title,
            currentAmount = goal.currentAmount.toLong(),
            targetAmount = goal.targetAmount.toLong(),
            daysRemaining = goal.daysRemaining,
            dailySavingsNeeded = goal.dailySavingsNeeded.toLong(),
            currentDailyRate = goal.currentDailyRate.toLong()
        )
    } ?: GoalProgressData(
        goalTitle = "No active goals",
        currentAmount = 0L,
        targetAmount = 1L,
        daysRemaining = 0,
        dailySavingsNeeded = 0L,
        currentDailyRate = 0L
    )

    val expenseSections = listOf(
        ExpenseSectionData(
            type = ExpenseType.MUST,
            totalAmount = expenseSectionSummary.mustTotal.toLong(),
            items = expenseSectionSummary.mustItems.map { item ->
                ExpenseItem(name = item.label, amount = item.amount.toLong())
            }
        ),
        ExpenseSectionData(
            type = ExpenseType.OPTIONAL,
            totalAmount = expenseSectionSummary.optionalTotal.toLong(),
            items = expenseSectionSummary.optionalItems.map { item ->
                ExpenseItem(name = item.label, amount = item.amount.toLong())
            }
        )
    ).filter { it.totalAmount > 0 || it.items.isNotEmpty() }

    val incomeSourceItems = incomeSources.map { source ->
        IncomeSourceItem(source = source.source, amount = source.amount.toLong())
    }
    val listState = rememberLazyListState()
    val showHeaderIcons by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scaffoldBg)
    ) {
        LazyColumn(
            state          = listState,
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                top    = 20.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // add space so the icons placed at the top-left sit visually above the card
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                BalanceCard(
                    isDarkTheme = isDarkTheme,
                    data = balanceCardData,
                    onStreakClick = onStreakClick,
                    onThemeClick = onThemeClick,
                    onProfileClick = onProfileClick
                )
            }

            item {
                QuickActionRow(
                    isDarkTheme = isDarkTheme,
                    onAddIncomeClick = onAddIncomeClick,
                    onAddExpenseClick = onAddExpenseClick
                )
            }

            item {
                MoneyFlowSection(
                    isDarkTheme = isDarkTheme,
                    items = moneyFlowItems,
                    onIncomeClick = onIncomeClick,
                    onGoalsClick = onGoalsClick,
                    onExpensesClick = onExpensesClick,
                    onSavingsClick = onSavingsClick
                )
            }

            item {
                SectionHeader(isDarkTheme = isDarkTheme, title = "Savings Goal")
                Spacer(modifier = Modifier.height(10.dp))
                GoalProgressCard(
                    isDarkTheme = isDarkTheme,
                    data = goalCardData,
                    onClick = onGoalCardClick
                )
            }

            item {
                IncomeSourcesCard(isDarkTheme = isDarkTheme, sources = incomeSourceItems)
            }

            item {
                ExpenseBreakdownSection(isDarkTheme = isDarkTheme, sections = expenseSections)
            }

            item {
                val usedPercent = if (totalIncome > 0.0) ((totalExpenses / totalIncome) * 100).roundToInt() else 0
                BudgetUsageBar(
                    isDarkTheme = isDarkTheme,
                    usedPercent = usedPercent.coerceIn(0, 100),
                    remainingAmount = max(0.0, remainingBalance).toLong()
                )
            }

            item {
                MonthlySummaryCard(
                    isDarkTheme = isDarkTheme,
                    totalIncome = monthlySummary.income,
                    totalExpenses = monthlySummary.expenses,
                    totalSavings = monthlySummary.savings,
                    remainingBalance = monthlySummary.remainingBalance,
                    onViewInsightsClick = onViewInsightsClick
                )
            }
        }
        if (showHeaderIcons) {
            // Theme icon at top-left
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onThemeClick) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            contentDescription = if (isDarkTheme) "Dark Mode" else "Light Mode",
                            tint = colors.textPrimary
                        )
                    }
                }

                // Profile + notification icons at top-right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = colors.textPrimary
                        )
                    }
                    Box {
                        IconButton(onClick = onNotificationClick) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = colors.textPrimary
                            )
                        }

                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                                    .size(18.dp)
                                    .background(Color(0xFFFF5E4D), RoundedCornerShape(99.dp))
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isDarkTheme) Color(0xFF1A1A2E) else Color(0xFFF5F3FF),
                                        shape = RoundedCornerShape(99.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    isDarkTheme: Boolean = false,
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = getHomeScreenColors(isDarkTheme)
    Text(
        text     = title,
        modifier = modifier,
        style    = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color      = colors.textPrimary,
            fontSize   = 18.sp
        )
    )
}

@Composable
private fun IncomeSourcesCard(isDarkTheme: Boolean = false, sources: List<IncomeSourceItem>) {
    val colors = getHomeScreenColors(isDarkTheme)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 3.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF7C4DFF).copy(alpha = 0.07f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBg)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = "Income Sources",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = colors.textPrimary,
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
                            color    = colors.textSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text  = if (source.isPositive)
                            "LKR ${"%,d".format(source.amount)}"
                        else
                            "+LKR ${"%,d".format(source.amount)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color      = if (source.isPositive) colors.textPrimary
                            else Color(0xFF2DBD6E),
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp
                        )
                    )
                }
                if (index < sources.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color     = if (isDarkTheme) Color(0xFF3A3A4E) else Color(0xFFF0EBF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetUsageBar(
    isDarkTheme: Boolean = false,
    usedPercent: Int,
    remainingAmount: Long,
    currencySymbol: String = "LKR"
) {
    val colors = getHomeScreenColors(isDarkTheme)
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
            .background(colors.cardBg)
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
                        color      = colors.textPrimary,
                        fontSize   = 13.sp
                    )
                )
                Text(
                    text  = "$currencySymbol ${"%,d".format(remainingAmount)} remaining",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color    = colors.textSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colors.progressTrackBg)
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

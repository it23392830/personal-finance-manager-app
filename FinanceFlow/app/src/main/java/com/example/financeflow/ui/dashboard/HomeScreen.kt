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
import com.example.financeflow.ui.notifications.SAMPLE_UNREAD_NOTIFICATION_COUNT
import com.example.financeflow.ui.components.Home.BalanceCard
import com.example.financeflow.ui.components.Home.BalanceCardData
import com.example.financeflow.ui.components.Home.ExpenseBreakdownSection
import com.example.financeflow.ui.components.Home.MoneyFlowSection
import com.example.financeflow.ui.components.Home.MonthlySummaryCard
import com.example.financeflow.ui.components.Home.QuickActionRow
import com.example.financeflow.ui.components.Home.expenseSampleData
import com.example.financeflow.ui.components.Home.moneyFlowSampleData
import com.example.financeflow.ui.components.savings.GoalProgressCard
import com.example.financeflow.ui.components.savings.GoalProgressData

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

private const val OPTIONAL_BUDGET_USED_PERCENT = 83
private val OPTIONAL_BUDGET_REMAINING          = 13_900L

/**
 * HomeScreen
 */
@Composable
fun HomeScreen(
    isDarkTheme: Boolean = false,
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
    unreadNotificationCount: Int = SAMPLE_UNREAD_NOTIFICATION_COUNT
) {
    val colors = getHomeScreenColors(isDarkTheme)
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
                    data = sampleBalanceData,
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
                    items = moneyFlowSampleData(),
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
                    data = sampleGoalData,
                    onClick = onGoalCardClick
                )
            }

            item {
                IncomeSourcesCard(isDarkTheme = isDarkTheme, sources = sampleIncomeSources)
            }

            item {
                ExpenseBreakdownSection(isDarkTheme = isDarkTheme, sections = expenseSampleData())
            }

            item {
                BudgetUsageBar(
                    isDarkTheme = isDarkTheme,
                    usedPercent      = OPTIONAL_BUDGET_USED_PERCENT,
                    remainingAmount  = OPTIONAL_BUDGET_REMAINING
                )
            }

            item {
                MonthlySummaryCard(
                    isDarkTheme = isDarkTheme,
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

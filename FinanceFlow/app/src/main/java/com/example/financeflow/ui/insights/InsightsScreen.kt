           package com.example.financeflow.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import com.example.financeflow.ui.components.common.FeatureMonthHeader
import com.example.financeflow.ui.components.insights.ActivityLevel
import com.example.financeflow.ui.components.insights.BreakdownItem
import com.example.financeflow.ui.components.insights.CalendarCard
import com.example.financeflow.ui.components.insights.CalendarDay
import com.example.financeflow.ui.components.insights.ExpenseBreakdownCard
import com.example.financeflow.ui.components.insights.FinancialHealthCard
import com.example.financeflow.ui.components.insights.InsightsColors
import com.example.financeflow.ui.components.insights.MonthlyComparisonCard
import com.example.financeflow.ui.components.insights.ComparisonRow
import com.example.financeflow.ui.components.insights.ChangeDirection
import com.example.financeflow.ui.components.insights.InsightItem
import com.example.financeflow.ui.components.insights.InsightType
import com.example.financeflow.ui.components.insights.SmartInsightsSection
import com.example.financeflow.ui.components.insights.getInsightsColors
import com.example.financeflow.ui.expenses.getCat
import com.example.financeflow.viewmodel.dashboard.DashboardViewModel
import com.example.financeflow.model.Expense
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private data class DayDetail(
    val dayName: String,
    val monthDay: String,
    val incomeEntries: Int,
    val expenseEntries: Int,
    val savingsEntries: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    isDarkTheme: Boolean = false,
    onViewReports: () -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val expenses by viewModel.expenses.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val savings by viewModel.savings.collectAsState()
    val goals by viewModel.goals.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    val currentMonth = remember { YearMonth.now() }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf(currentMonth.format(formatter)) }

    val monthOptions = remember(incomes, expenses, savings) {
        val monthSet = mutableSetOf<YearMonth>()
        incomes.forEach { item ->
            val ym = YearMonth.from(item.date.toDate().toInstant().atZone(ZoneId.systemDefault()))
            monthSet.add(ym)
        }
        expenses.forEach { item ->
            val ym = YearMonth.from(item.date.toDate().toInstant().atZone(ZoneId.systemDefault()))
            monthSet.add(ym)
        }
        savings.forEach { item ->
            val parsed = runCatching { YearMonth.parse(item.month, formatter) }.getOrNull()
            if (parsed != null) monthSet.add(parsed)
        }
        val fallback = if (monthSet.isEmpty()) listOf(currentMonth) else monthSet.toList()
        fallback.sortedByDescending { it }.map { it.format(formatter) }
    }

    androidx.compose.runtime.LaunchedEffect(monthOptions) {
        if (monthOptions.isNotEmpty() && selectedMonth !in monthOptions) {
            selectedMonth = monthOptions.first()
        }
    }

    val selectedYearMonth = remember(selectedMonth) {
        runCatching { YearMonth.parse(selectedMonth, formatter) }.getOrElse { currentMonth }
    }

    androidx.compose.runtime.LaunchedEffect(selectedYearMonth) {
        selectedDay = null
    }

    val startDayOffset = remember(selectedYearMonth) {
        val firstDayValue = selectedYearMonth.atDay(1).dayOfWeek.value
        firstDayValue % 7
    }

    val dailyActivity = remember(selectedYearMonth, incomes, expenses, savings) {
        viewModel.getDailyActivityFor(selectedYearMonth)
    }

    val calendarDays = remember(dailyActivity) {
        dailyActivity.map { day ->
            val total = day.incomeCount + day.expenseCount + day.savingsCount
            val activity = when {
                total <= 0 -> ActivityLevel.NONE
                total <= 1 -> ActivityLevel.LOW
                total <= 3 -> ActivityLevel.MEDIUM
                else -> ActivityLevel.HIGH
            }
            CalendarDay(
                dayOfMonth = day.dayOfMonth,
                activity = activity,
                incomeEntries = day.incomeCount,
                expenseEntries = day.expenseCount,
                savingsEntries = day.savingsCount
            )
        }
    }

    val dayDetail = selectedDay?.let { day ->
        val activity = dailyActivity.firstOrNull { it.dayOfMonth == day }
        val date = selectedYearMonth.atDay(day)
        activity?.let { act ->
            DayDetail(
                dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                monthDay = "${date.month.name.lowercase().replaceFirstChar { char -> char.uppercase() }} ${date.dayOfMonth}",
                incomeEntries = act.incomeCount,
                expenseEntries = act.expenseCount,
                savingsEntries = act.savingsCount
            )
        }
    }

    val currentSummary = remember(selectedYearMonth, incomes, expenses, savings) {
        viewModel.getMonthlySummaryFor(selectedYearMonth)
    }
    val previousSummary = remember(selectedYearMonth, incomes, expenses, savings) {
        viewModel.getMonthlySummaryFor(selectedYearMonth.minusMonths(1))
    }

    val savingsRate = if (currentSummary.income > 0.0) (currentSummary.savings / currentSummary.income) * 100 else 0.0
    val consistency = if (selectedYearMonth.lengthOfMonth() > 0) {
        (dailyActivity.count { it.incomeCount + it.expenseCount + it.savingsCount > 0 } * 100.0 / selectedYearMonth.lengthOfMonth())
    } else 0.0
    val averageGoalProgress = if (goals.isNotEmpty()) goals.map { it.progressPercentage }.average() else 0.0
    val score = (savingsRate * 0.4 + consistency * 0.3 + averageGoalProgress * 0.3).coerceIn(0.0, 100.0)
    val scoreLabel = when {
        score >= 80 -> "Excellent – Keep Leading!"
        score >= 60 -> "Good – Keep Improving!"
        score >= 40 -> "Fair – Room to Grow"
        else -> "Needs Attention"
    }

    val insightItems = listOf(
        InsightItem(
            type = InsightType.NEUTRAL,
            icon = Icons.Default.CheckCircle,
            title = "Monthly Income",
            body = "LKR ${"%,.0f".format(currentSummary.income)}",
            actionText = null
        ),
        InsightItem(
            type = InsightType.NEUTRAL,
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            title = "Monthly Expenses",
            body = "LKR ${"%,.0f".format(currentSummary.expenses)}",
            actionText = null
        ),
        InsightItem(
            type = InsightType.POSITIVE,
            icon = Icons.Default.CheckCircle,
            title = "Monthly Savings",
            body = "LKR ${"%,.0f".format(currentSummary.savings)}",
            actionText = null
        ),
        InsightItem(
            type = InsightType.NEUTRAL,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Remaining Balance",
            body = "LKR ${"%,.0f".format(currentSummary.remainingBalance)}",
            actionText = null
        )
    )

    val comparisonRows = buildComparisonRows(currentSummary, previousSummary)
    val expenseBreakdown = buildExpenseBreakdown(expenses, selectedYearMonth)
    val scrollState = rememberScrollState()
    val colors = getInsightsColors(isDarkTheme)

    Scaffold(containerColor = colors.BgPurple) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureMonthHeader(
                title = "Financial Insights",
                subtitle = "Understand your money habits",
                selectedMonth = selectedMonth,
                monthOptions = monthOptions,
                onMonthSelected = { selectedMonth = it },
                headerColor = if (isDarkTheme) Color(0xFF5B4AA8) else Color(0xFF8B5CF6)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onViewReports,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.PrimaryPurple,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "View Reports (Daily/Weekly/Monthly)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                CalendarCard(
                    isDarkTheme = isDarkTheme,
                    month = selectedMonth,
                    startDayOffset = startDayOffset,
                    days = calendarDays,
                    selectedDay = selectedDay,
                    onDaySelected = { day -> selectedDay = day.dayOfMonth }
                )

                if (dayDetail != null) {
                    DayDetailCard(detail = dayDetail, colors = colors)
                }

                FinancialHealthCard(
                    isDarkTheme = isDarkTheme,
                    score = score.roundToInt(),
                    label = scoreLabel,
                    savingsRate = "${"%.1f".format(savingsRate)}%",
                    consistency = "${"%.0f".format(consistency)}/100",
                    goalProgress = "${"%.1f".format(averageGoalProgress)}%"
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colors.CardWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        SmartInsightsSection(isDarkTheme = isDarkTheme, items = insightItems)
                    }
                }

                ExpenseBreakdownCard(
                    isDarkTheme = isDarkTheme,
                    mustTotal = expenseBreakdown.mustTotal,
                    mustPct = expenseBreakdown.mustPct,
                    optionalTotal = expenseBreakdown.optionalTotal,
                    optionalPct = expenseBreakdown.optionalPct,
                    mustItems = expenseBreakdown.mustItems,
                    optionalItems = expenseBreakdown.optionalItems
                )
                MonthlyComparisonCard(isDarkTheme = isDarkTheme, rows = comparisonRows)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DayDetailCard(detail: DayDetail, colors: InsightsColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.DayDetailBg)
            .border(1.dp, colors.PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${detail.dayName}  ${detail.monthDay}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.TextDark
            )

            HorizontalDivider(color = colors.PrimaryPurple.copy(alpha = 0.15f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayEntryChip("Income", detail.incomeEntries, colors.IncomeGreen, colors)
                DayEntryChip("Expenses", detail.expenseEntries, Color(0xFFEF4444), colors)
                DayEntryChip("Savings", detail.savingsEntries, colors.PrimaryPurple, colors)
            }
        }
    }
}

@Composable
private fun DayEntryChip(label: String, count: Int, color: Color, colors: InsightsColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count ${if (count == 1) "entry" else "entries"}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = color
        )
        Text(text = label, fontSize = 11.sp, color = colors.TextMuted)
    }
}

private data class ExpenseBreakdownUi(
    val mustTotal: String,
    val mustPct: String,
    val optionalTotal: String,
    val optionalPct: String,
    val mustItems: List<BreakdownItem>,
    val optionalItems: List<BreakdownItem>
)

private fun buildComparisonRows(current: com.example.financeflow.repository.dashboard.MonthlySummary, previous: com.example.financeflow.repository.dashboard.MonthlySummary): List<ComparisonRow> {
    fun percentChange(currentValue: Double, previousValue: Double): Double {
        return if (previousValue > 0.0) ((currentValue - previousValue) / previousValue) * 100 else if (currentValue > 0.0) 100.0 else 0.0
    }

    fun directionFor(pct: Double): ChangeDirection = when {
        pct > 1.0 -> ChangeDirection.UP
        pct < -1.0 -> ChangeDirection.DOWN
        else -> ChangeDirection.FLAT
    }

    fun formatAmount(value: Double): String = "LKR ${"%,.0f".format(value)}"

    val incomeChange = percentChange(current.income, previous.income)
    val expenseChange = percentChange(current.expenses, previous.expenses)
    val savingsChange = percentChange(current.savings, previous.savings)

    return listOf(
        ComparisonRow(
            label = "Income Change",
            subtitle = "${formatAmount(current.income)} vs last month",
            badge = "${if (incomeChange >= 0) "+" else ""}${"%.1f".format(incomeChange)}%",
            direction = directionFor(incomeChange)
        ),
        ComparisonRow(
            label = "Expense Change",
            subtitle = "${formatAmount(current.expenses)} vs last month",
            badge = "${if (expenseChange >= 0) "+" else ""}${"%.1f".format(expenseChange)}%",
            direction = directionFor(expenseChange)
        ),
        ComparisonRow(
            label = "Savings Change",
            subtitle = "${formatAmount(current.savings)} vs last month",
            badge = "${if (savingsChange >= 0) "+" else ""}${"%.1f".format(savingsChange)}%",
            direction = directionFor(savingsChange)
        )
    )
}

private fun buildExpenseBreakdown(expenses: List<Expense>, month: YearMonth): ExpenseBreakdownUi {
    val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val monthExpenses = expenses.filter { it.date.toDate().time in start..end }

    val mustExpenses = monthExpenses.filter { it.isFixed }
    val optionalExpenses = monthExpenses.filter { !it.isFixed }

    fun toBreakdownItems(list: List<Expense>): List<BreakdownItem> {
        return list.groupBy { exp ->
            val cat = getCat(exp.category)
            cat.parentLabel ?: cat.label
        }.map { (label, items) ->
            val total = items.sumOf { it.amount }
            BreakdownItem(label = label, amount = "LKR ${"%,.0f".format(total)}")
        }.sortedByDescending { item ->
            item.amount.replace("LKR", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        }
    }

    val mustItems = toBreakdownItems(mustExpenses)
    val optionalItems = toBreakdownItems(optionalExpenses)
    val mustTotalValue = mustExpenses.sumOf { it.amount }
    val optionalTotalValue = optionalExpenses.sumOf { it.amount }
    val totalValue = mustTotalValue + optionalTotalValue

    val mustPct = if (totalValue > 0.0) (mustTotalValue / totalValue) * 100 else 0.0
    val optionalPct = if (totalValue > 0.0) (optionalTotalValue / totalValue) * 100 else 0.0

    return ExpenseBreakdownUi(
        mustTotal = "LKR ${"%,.0f".format(mustTotalValue)}",
        mustPct = "${"%.0f".format(mustPct)}% of total",
        optionalTotal = "LKR ${"%,.0f".format(optionalTotalValue)}",
        optionalPct = "${"%.0f".format(optionalPct)}% of total",
        mustItems = mustItems,
        optionalItems = optionalItems
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun InsightsScreenPreview() {
    MaterialTheme {
        InsightsScreen()
    }
}

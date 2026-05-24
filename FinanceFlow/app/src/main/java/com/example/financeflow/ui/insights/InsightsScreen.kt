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
import com.example.financeflow.ui.components.common.FeatureMonthHeader
import com.example.financeflow.ui.components.insights.CalendarCard
import com.example.financeflow.ui.components.insights.ExpenseBreakdownCard
import com.example.financeflow.ui.components.insights.FinancialHealthCard
import com.example.financeflow.ui.components.insights.InsightsColors
import com.example.financeflow.ui.components.insights.MonthlyComparisonCard
import com.example.financeflow.ui.components.insights.SmartInsightsSection
import com.example.financeflow.ui.components.insights.getInsightsColors
import com.example.financeflow.ui.components.insights.sampleMay2026Days

private data class DayDetail(
    val dayName: String,
    val monthDay: String,
    val incomeEntries: Int,
    val expenseEntries: Int,
    val savingsEntries: Int
)

private val fakeDayDetails: Map<Int, DayDetail> = mapOf(
    1 to DayDetail("Friday", "May 1", 0, 1, 0),
    2 to DayDetail("Saturday", "May 2", 1, 2, 1),
    3 to DayDetail("Sunday", "May 3", 0, 2, 0),
    4 to DayDetail("Monday", "May 4", 0, 0, 0),
    5 to DayDetail("Tuesday", "May 5", 2, 1, 1),
    6 to DayDetail("Wednesday", "May 6", 1, 3, 0),
    7 to DayDetail("Thursday", "May 7", 0, 2, 0),
    8 to DayDetail("Friday", "May 8", 0, 0, 0),
    9 to DayDetail("Saturday", "May 9", 1, 1, 0),
    10 to DayDetail("Sunday", "May 10", 2, 3, 1),
    11 to DayDetail("Monday", "May 11", 1, 2, 0),
    12 to DayDetail("Tuesday", "May 12", 0, 0, 0),
    13 to DayDetail("Wednesday", "May 13", 0, 0, 0),
    14 to DayDetail("Thursday", "May 14", 0, 1, 0),
    15 to DayDetail("Friday", "May 15", 1, 2, 1),
    16 to DayDetail("Saturday", "May 16", 0, 2, 0),
    17 to DayDetail("Sunday", "May 17", 3, 4, 1),
    18 to DayDetail("Monday", "May 18", 1, 2, 0),
    19 to DayDetail("Tuesday", "May 19", 0, 0, 0),
    20 to DayDetail("Wednesday", "May 20", 0, 0, 0),
    21 to DayDetail("Thursday", "May 21", 0, 2, 0),
    22 to DayDetail("Friday", "May 22", 2, 3, 1),
    23 to DayDetail("Saturday", "May 23", 0, 1, 0),
    24 to DayDetail("Sunday", "May 24", 1, 2, 0),
    25 to DayDetail("Monday", "May 25", 0, 0, 0),
    26 to DayDetail("Tuesday", "May 26", 0, 0, 0),
    27 to DayDetail("Wednesday", "May 27", 0, 0, 0),
    28 to DayDetail("Thursday", "May 28", 1, 2, 1),
    29 to DayDetail("Friday", "May 29", 0, 3, 0),
    30 to DayDetail("Saturday", "May 30", 2, 4, 1),
    31 to DayDetail("Sunday", "May 31", 1, 2, 0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    isDarkTheme: Boolean = false,
    onViewReports: () -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf<Int?>(6) }
    var selectedMonth by remember { mutableStateOf("May 2026") }
    val dayDetail = selectedDay?.let { fakeDayDetails[it] }
    val monthOptions = listOf("May 2026", "April 2026", "March 2026", "February 2026", "January 2026")
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
                    startDayOffset = 5,
                    days = sampleMay2026Days,
                    selectedDay = selectedDay,
                    onDaySelected = { day -> selectedDay = day.dayOfMonth }
                )

                if (dayDetail != null) {
                    DayDetailCard(detail = dayDetail, colors = colors)
                }

                FinancialHealthCard(
                    isDarkTheme = isDarkTheme,
                    score = 23,
                    label = "Good - Keep Improving!",
                    savingsRate = "28.8%",
                    consistency = "75/100",
                    goalProgress = "2.2"
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colors.CardWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        SmartInsightsSection(isDarkTheme = isDarkTheme)
                    }
                }

                ExpenseBreakdownCard(isDarkTheme = isDarkTheme)
                MonthlyComparisonCard(isDarkTheme = isDarkTheme)
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

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun InsightsScreenPreview() {
    MaterialTheme {
        InsightsScreen()
    }
}

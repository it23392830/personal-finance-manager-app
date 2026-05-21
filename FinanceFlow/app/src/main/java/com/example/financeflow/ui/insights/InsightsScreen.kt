package com.example.financeflow.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.insights.*

// ─── Colors ───────────────────────────────────────────────────────────────────
private val BgPurple      = Color(0xFFF3ECFF)
private val PrimaryPurple = Color(0xFF8B5CF6)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextDark      = Color(0xFF1E1B2E)
private val TextMuted     = Color(0xFF9CA3AF)
private val DayDetailBg   = Color(0xFFE8F4FD)   // light blue info card
private val IncomeGreen   = Color(0xFF22C55E)

// ─── Fake day detail data ─────────────────────────────────────────────────────
private data class DayDetail(
    val dayName: String,
    val monthDay: String,
    val incomeEntries: Int,
    val expenseEntries: Int,
    val savingsEntries: Int
)

private val fakeDayDetails: Map<Int, DayDetail> = mapOf(
    1  to DayDetail("Friday",    "May 1",  0, 1, 0),
    2  to DayDetail("Saturday",  "May 2",  1, 2, 1),
    3  to DayDetail("Sunday",    "May 3",  0, 2, 0),
    4  to DayDetail("Monday",    "May 4",  0, 0, 0),
    5  to DayDetail("Tuesday",   "May 5",  2, 1, 1),
    6  to DayDetail("Wednesday", "May 6",  1, 3, 0),
    7  to DayDetail("Thursday",  "May 7",  0, 2, 0),
    8  to DayDetail("Friday",    "May 8",  0, 0, 0),
    9  to DayDetail("Saturday",  "May 9",  1, 1, 0),
    10 to DayDetail("Sunday",    "May 10", 2, 3, 1),
    11 to DayDetail("Monday",    "May 11", 1, 2, 0),
    12 to DayDetail("Tuesday",   "May 12", 0, 0, 0),
    13 to DayDetail("Wednesday", "May 13", 0, 0, 0),
    14 to DayDetail("Thursday",  "May 14", 0, 1, 0),
    15 to DayDetail("Friday",    "May 15", 1, 2, 1),
    16 to DayDetail("Saturday",  "May 16", 0, 2, 0),
    17 to DayDetail("Sunday",    "May 17", 3, 4, 1),
    18 to DayDetail("Monday",    "May 18", 1, 2, 0),
    19 to DayDetail("Tuesday",   "May 19", 0, 0, 0),
    20 to DayDetail("Wednesday", "May 20", 0, 0, 0),
    21 to DayDetail("Thursday",  "May 21", 0, 2, 0),
    22 to DayDetail("Friday",    "May 22", 2, 3, 1),
    23 to DayDetail("Saturday",  "May 23", 0, 1, 0),
    24 to DayDetail("Sunday",    "May 24", 1, 2, 0),
    25 to DayDetail("Monday",    "May 25", 0, 0, 0),
    26 to DayDetail("Tuesday",   "May 26", 0, 0, 0),
    27 to DayDetail("Wednesday", "May 27", 0, 0, 0),
    28 to DayDetail("Thursday",  "May 28", 1, 2, 1),
    29 to DayDetail("Friday",    "May 29", 0, 3, 0),
    30 to DayDetail("Saturday",  "May 30", 2, 4, 1),
    31 to DayDetail("Sunday",    "May 31", 1, 2, 0)
)

/**
 * InsightsScreen
 *
 * Main Financial Insights screen matching the Figma design.
 * Contains:
 *  – "View Reports" button  → navigates to DailyReportScreen
 *  – Activity Calendar with day-click → updates detail card
 *  – Financial Health Score card
 *  – Smart Insights section
 *  – Expense Breakdown card
 *  – Monthly Comparison card
 *
 * @param onViewReports  Callback for the "View Reports" button (nav to Daily).
 * @param onNavigateUp   Back navigation callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onViewReports: () -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
    // Currently selected calendar day (default = day 6, matching Figma)
    var selectedDay by remember { mutableStateOf<Int?>(6) }
    val dayDetail = selectedDay?.let { fakeDayDetails[it] }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BgPurple,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Financial Insights",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = PrimaryPurple
                        )
                        Text(
                            text = "Understand your money habits",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Reports",
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPurple)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── View Reports button ───────────────────────────────────────────
            Button(
                onClick = onViewReports,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
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

            // ── Activity Calendar ─────────────────────────────────────────────
            CalendarCard(
                month = "May 2026",
                startDayOffset = 5,        // May 2026 starts on Friday
                days = sampleMay2026Days,
                selectedDay = selectedDay,
                onDaySelected = { day -> selectedDay = day.dayOfMonth }
            )

            // ── Day detail card (appears after a day is selected) ─────────────
            if (dayDetail != null) {
                DayDetailCard(detail = dayDetail)
            }

            // ── Financial Health Score ────────────────────────────────────────
            FinancialHealthCard(
                score = 23,
                label = "Good – Keep Improving!",
                savingsRate = "28.8%",
                consistency = "75/100",
                goalProgress = "2.2"
            )

            // ── Smart Insights ────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    SmartInsightsSection()
                }
            }

            // ── Expense Breakdown ─────────────────────────────────────────────
            ExpenseBreakdownCard()

            // ── Monthly Comparison ────────────────────────────────────────────
            MonthlyComparisonCard()

            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * DayDetailCard
 *
 * Light-blue information card shown below the calendar after a day is tapped.
 * Matches the Figma design with dayName, monthDay, and three entry counts.
 */
@Composable
private fun DayDetailCard(detail: DayDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DayDetailBg)
            .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // Date heading
            Text(
                text = "${detail.dayName}  ${detail.monthDay}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDark
            )

            HorizontalDivider(color = PrimaryPurple.copy(alpha = 0.15f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayEntryChip("Income",   detail.incomeEntries,  IncomeGreen)
                DayEntryChip("Expenses", detail.expenseEntries, Color(0xFFEF4444))
                DayEntryChip("Savings",  detail.savingsEntries, PrimaryPurple)
            }
        }
    }
}

@Composable
private fun DayEntryChip(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count ${if (count == 1) "entry" else "entries"}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = color
        )
        Text(text = label, fontSize = 11.sp, color = TextMuted)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Composable
fun InsightsScreenPreview() {
    MaterialTheme {
        InsightsScreen()
    }
}
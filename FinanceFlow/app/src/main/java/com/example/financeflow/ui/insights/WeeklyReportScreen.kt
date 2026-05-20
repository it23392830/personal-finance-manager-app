package com.example.financeflow.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.insights.ReportToggle

// ─── Colors ───────────────────────────────────────────────────────────────────
private val BgPurple      = Color(0xFFF3ECFF)
private val PrimaryPurple = Color(0xFF8B5CF6)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextDark      = Color(0xFF1E1B2E)
private val TextMuted     = Color(0xFF9CA3AF)
private val IncomeGreen   = Color(0xFF22C55E)
private val ExpenseRed    = Color(0xFFEF4444)
private val GreenBg       = Color(0xFFEFFFF4)
private val RedBg         = Color(0xFFFFEEEE)

/**
 * WeeklyReportScreen
 *
 * Shows the weekly financial summary for May 1–7, 2026:
 *  - Total Income / Total Expenses (with % badges)
 *  - Net Savings
 *  - Weekly Insights rows (Avg Daily Expense, Top Category, Savings Added)
 *
 * @param onNavigateUp   Back navigation.
 * @param onTabSelected  Called with "Daily" or "Monthly" if the toggle is tapped.
 * @param onClose        Dismiss the reports flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    onNavigateUp: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BgPurple,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Financial Reports",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = PrimaryPurple
                        )
                        Text(
                            text = "Daily, Weekly & Monthly summaries",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = PrimaryPurple)
                    }
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark)
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

            // ── Report toggle – "Weekly" is active ────────────────────────────
            ReportToggle(
                selected = "Weekly",
                onSelect = { tab -> if (tab != "Weekly") onTabSelected(tab) }
            )

            // ── Report card ───────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp),
                        ambientColor = PrimaryPurple.copy(alpha = 0.1f),
                        spotColor = PrimaryPurple.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Report – May 1-7, 2026",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        PdfButton()
                    }

                    Spacer(Modifier.height(16.dp))

                    // Income + Expenses row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Income
                        WeeklySummaryCard(
                            label = "Total Income",
                            amount = "Rs45,000",
                            badge = "+12%",
                            badgeBg = IncomeGreen,
                            badgeText = Color.White,
                            labelColor = IncomeGreen,
                            bg = GreenBg,
                            modifier = Modifier.weight(1f)
                        )
                        // Total Expenses
                        WeeklySummaryCard(
                            label = "Total Expenses",
                            amount = "Rs18,650",
                            badge = "-5%",
                            badgeBg = ExpenseRed,
                            badgeText = Color.White,
                            labelColor = ExpenseRed,
                            bg = RedBg,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Net Savings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Net Savings", fontSize = 12.sp, color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                            Text("Rs26,350", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                        }
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE9E2FF))
                    Spacer(Modifier.height(16.dp))

                    // Weekly insights label
                    Text("Weekly Insights", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)

                    Spacer(Modifier.height(12.dp))

                    // Three insight rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WeeklyInsightRow("Avg. Daily Expense", "Rs2,664", isHighlighted = false)
                        WeeklyInsightRow("Top Category", "Food & Dining", isHighlighted = true)
                        WeeklyInsightRow("Savings Added", "Rs12,600", isHighlighted = false, valueColor = PrimaryPurple)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Coloured summary card for income / expenses */
@Composable
private fun WeeklySummaryCard(
    label: String,
    amount: String,
    badge: String,
    badgeBg: Color,
    badgeText: Color,
    labelColor: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(shape = RoundedCornerShape(14.dp), color = bg, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Label + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontSize = 11.sp, color = labelColor, fontWeight = FontWeight.SemiBold)
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(text = amount, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = labelColor)
        }
    }
}

/** Insight detail row */
@Composable
private fun WeeklyInsightRow(
    label: String,
    value: String,
    isHighlighted: Boolean,
    valueColor: Color = Color(0xFF1E1B2E)
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF9F6FF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 13.sp, color = Color(0xFF9CA3AF))
            Text(
                text = value,
                fontSize = if (isHighlighted) 15.sp else 14.sp,
                fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun WeeklyReportScreenPreview() {
    MaterialTheme {
        WeeklyReportScreen()
    }
}
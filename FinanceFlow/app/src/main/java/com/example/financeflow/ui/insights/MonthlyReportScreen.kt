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

// ─── Fake data ────────────────────────────────────────────────────────────────
private data class MonthlyLineItem(val label: String, val amount: String, val isIncome: Boolean)

private val fakeIncomeItems = listOf(
    MonthlyLineItem("Salary",    "Rs135,000", true),
    MonthlyLineItem("Freelance", "Rs45,000",  true),
    MonthlyLineItem("AdSense",   "Rs5,200",   true),
    MonthlyLineItem("Crypto",    "Rs2,300",   true)
)

private val fakeExpenseItems = listOf(
    MonthlyLineItem("Rent & Utilities", "Rs38,500", false),
    MonthlyLineItem("Food & Dining",    "Rs28,400", false),
    MonthlyLineItem("Transport",        "Rs18,600", false),
    MonthlyLineItem("Entertainment",    "Rs12,900", false),
    MonthlyLineItem("Shopping",         "Rs8,500",  false)
)

/**
 * MonthlyReportScreen
 *
 * Displays the "Monthly Report – April 2026" with:
 *  - Three summary chips: Income / Expenses / Saved %
 *  - Net Savings row
 *  - Income Breakdown list
 *  - Expense Breakdown list
 *
 * @param onNavigateUp  Back navigation.
 * @param onTabSelected Called when the user taps "Daily" or "Weekly" in the toggle.
 * @param onClose       Dismiss the reports flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportScreen(
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

            // ── Report toggle – "Monthly" is active ───────────────────────────
            ReportToggle(
                selected = "Monthly",
                onSelect = { tab -> if (tab != "Monthly") onTabSelected(tab) }
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

                    // Report title + PDF badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Report – April 2026",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        PdfButton()
                    }

                    Spacer(Modifier.height(16.dp))

                    // Three summary chips: Income / Expenses / Saved
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MonthSummaryChip("Income",   "Rs188K", GreenBg,              IncomeGreen, Modifier.weight(1f))
                        MonthSummaryChip("Expenses", "Rs120K", Color(0xFFFFEEEE),    ExpenseRed,  Modifier.weight(1f))
                        MonthSummaryChip("Saved",    "28%",    Color(0xFFEFF6FF),    PrimaryPurple, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(14.dp))

                    // Net Savings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Net Savings", fontSize = 12.sp, color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                            Text("Rs67,100", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
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

                    // Income Breakdown
                    Text("Income Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        fakeIncomeItems.forEach { item ->
                            MonthlyLineItemRow(item)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Expense Breakdown
                    Text("Expense Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        fakeExpenseItems.forEach { item ->
                            MonthlyLineItemRow(item)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Summary chip for Income / Expenses / Saved */
@Composable
private fun MonthSummaryChip(
    label: String,
    value: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(shape = RoundedCornerShape(12.dp), color = bg, modifier = modifier) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
    }
}

/** A label + amount row inside the breakdown sections */
@Composable
private fun MonthlyLineItemRow(item: MonthlyLineItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.label, fontSize = 14.sp, color = TextDark)
        Text(
            text = item.amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (item.isIncome) IncomeGreen else ExpenseRed
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun MonthlyReportScreenPreview() {
    MaterialTheme {
        MonthlyReportScreen()
    }
}
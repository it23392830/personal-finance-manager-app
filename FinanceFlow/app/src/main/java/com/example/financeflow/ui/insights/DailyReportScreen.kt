package com.example.financeflow.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
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
private val DividerColor  = Color(0xFFE9E2FF)

// ─── Fake transaction data ────────────────────────────────────────────────────
private data class DailyTransaction(
    val category: String,
    val time: String,
    val amount: String    // negative prefix for expenses
)

private val fakeDailyTransactions = listOf(
    DailyTransaction("Food",      "2:30 PM", "-Rs850"),
    DailyTransaction("Transport", "9:15 AM", "-Rs420"),
    DailyTransaction("Shopping",  "6:45 PM", "-Rs2,150")
)

/**
 * DailyReportScreen
 *
 * Shows "Today's Report" with summary chips (Income / Expenses / Savings)
 * and a list of individual transactions.
 * Includes the [ReportToggle] for switching between Daily / Weekly / Monthly.
 *
 * @param onNavigateUp      Back navigation.
 * @param onTabSelected     Called with "Daily", "Weekly", or "Monthly" when toggle changes.
 * @param onClose           Close / dismiss the reports flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReportScreen(
    isDarkTheme: Boolean = false,
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
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDark
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

            // ── Tab toggle ────────────────────────────────────────────────────
            ReportToggle(
                selected = "Daily",
                onSelect = { tab -> if (tab != "Daily") onTabSelected(tab) }
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

                    // Report header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Report – May 6, 2026",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        PdfButton()
                    }

                    Spacer(Modifier.height(16.dp))

                    // Summary chips row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DailySummaryChip("Income",   "Rs0",     GreenBg,  IncomeGreen, Modifier.weight(1f))
                        DailySummaryChip("Expenses", "Rs3,420", Color(0xFFFFEEEE), ExpenseRed, Modifier.weight(1f))
                        DailySummaryChip("Savings",  "Rs0",     GreenBg,  IncomeGreen, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(16.dp))

                    // Transactions list header
                    Text(
                        text = "Transactions (${fakeDailyTransactions.size})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    Spacer(Modifier.height(10.dp))

                    // Transaction rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        fakeDailyTransactions.forEach { tx ->
                            DailyTransactionRow(tx)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Small coloured summary chip (Income / Expenses / Savings) */
@Composable
private fun DailySummaryChip(
    label: String,
    amount: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, color = textColor, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

/** Single transaction row */
@Composable
private fun DailyTransactionRow(tx: DailyTransaction) {
    Surface(
        shape = RoundedCornerShape(12.dp),
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
            Column {
                Text(text = tx.category, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1E1B2E))
                Text(text = tx.time, fontSize = 11.sp, color = Color(0xFF9CA3AF))
            }
            Text(
                text = tx.amount,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ExpenseRed
            )
        }
    }
}

/** Small PDF download badge */
@Composable
fun PdfButton(onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF3ECFF),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = "PDF", tint = PrimaryPurple, modifier = Modifier.size(14.dp))
            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun DailyReportScreenPreview() {
    MaterialTheme {
        DailyReportScreen()
    }
}
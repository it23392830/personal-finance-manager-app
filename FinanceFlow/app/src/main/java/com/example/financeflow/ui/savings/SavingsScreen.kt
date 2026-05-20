package com.example.financeflow.ui.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.components.*
// SavingsScreen
//
// Root composable for the Savings section of FinanceFlow.
// Uses a LazyColumn so the entire screen is scrollable.
//
// no backend logic.
//
// Color palette:
//   Background : #EDE2FF  (BackgroundPurple)
//   Cards      : #FFFFFF  (CardWhite)
//   Accent     : #F5A623  (OrangeAccent)
//   Green      : #4CAF50  (GreenAccent)
@Composable
fun SavingsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPurple),                 // #EDE2FF background
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 20.dp,
            bottom = 32.dp                                 // extra bottom padding
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp) // 16dp gap between cards
    ) {
        // Title, subtitle, dark-mode + person icons, month selector dropdown
        item {
            HeaderCard(
                selectedMonth = "May 2026",
                onMonthSelected = { /* month selection — UI only */ }
            )
        }
        // Large amount, income + savings-rate mini chips, CTA button
        item {
            SavingsThisMonthCard(
                amount = "LKR 53,200",
                totalIncome = "LKR 187,500",
                savingRate = "28%",
                onAddNewSaving = { /* UI stub */ }
            )
        }
        // Two gradient chips + Save First Model Active banner
        item {
            LifetimeStatisticsCard(
                totalSaved = "LKR 301,600",
                avgSavingsRate = "26.8%",
                periodLabel = "Total Saved (6 mon)"
            )
        }
        // Hardcoded goals with orange progress bars, targets and 3-dot menus
        item {
            SavingsByGoalCard(goals = dummyGoals)
        }
        // Section title + one card per history entry
        item {
            SavingsHistoryCard(entries = dummyHistory)
        }
        // Two insight rows with coloured icon chips
        item {
            SavingsInsightsCard()
        }
    }
}
// Preview
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "SavingsScreen – Full"
)
@Composable
fun PreviewSavingsScreen() {
    // Wrap in MaterialTheme so Material3 components render correctly in preview
    androidx.compose.material3.MaterialTheme {
        SavingsScreen()
    }
}

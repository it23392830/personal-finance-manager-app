package com.example.financeflow.ui.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.components.*

@Composable
fun SavingsScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPurple),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 20.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                onAddNewSaving = {
                    navController.navigate(Routes.GOAL_DETAILS)
                }
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

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "SavingsScreen – Full"
)
@Composable
fun PreviewSavingsScreen() {
    androidx.compose.material3.MaterialTheme {
        SavingsScreen(navController = rememberNavController())
    }
}

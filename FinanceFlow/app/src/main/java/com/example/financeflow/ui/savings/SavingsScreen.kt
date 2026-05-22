package com.example.financeflow.ui.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.components.savings.BackgroundPurple
import com.example.financeflow.ui.components.savings.HeaderCard
import com.example.financeflow.ui.components.savings.LifetimeStatisticsCard
import com.example.financeflow.ui.components.savings.SavingGoal
import com.example.financeflow.ui.components.savings.SavingHistoryEntry
import com.example.financeflow.ui.components.savings.SavingsByGoalCard
import com.example.financeflow.ui.components.savings.SavingsHistoryCard
import com.example.financeflow.ui.components.SavingsInsightsCard
import com.example.financeflow.ui.components.savings.SavingsThisMonthCard
import com.example.financeflow.ui.components.savings.defaultGoals
import com.example.financeflow.ui.components.savings.dummyHistory

@Composable
fun SavingsScreen(navController: NavController) {

    val goals: SnapshotStateList<SavingGoal> = remember {
        mutableStateListOf(*defaultGoals.toTypedArray())
    }

    val history: SnapshotStateList<SavingHistoryEntry> = remember {
        mutableStateListOf(*dummyHistory.toTypedArray())
    }

    var editingGoal by remember { mutableStateOf<SavingGoal?>(null) }
    var showEditSavingsPopup by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<SavingGoal?>(null) }
    var selectedHistory by remember { mutableStateOf<SavingHistoryEntry?>(null) }

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
        item {
            HeaderCard(
                selectedMonth = "May 2026",
                onMonthSelected = { /* UI only */ }
            )
        }

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

        item {
            LifetimeStatisticsCard(
                totalSaved = "LKR 301,600",
                avgSavingsRate = "26.8%",
                periodLabel = "Total Saved (6 mon)"
            )
        }

        item {
            SavingsByGoalCard(
                goals = goals,
                onEditClick = { goal -> editingGoal = goal },
                onDeleteClick = { goal ->
                    selectedGoal = goal
                    selectedHistory = null
                    showDeleteDialog = true
                }
            )
        }

        item {
            SavingsHistoryCard(
                entries = history,
                onEditClick = { showEditSavingsPopup = true },
                onDeleteClick = { historyEntry ->
                    selectedHistory = historyEntry
                    selectedGoal = null
                    showDeleteDialog = true
                }
            )
        }

        item {
            SavingsInsightsCard()
        }
    }

    editingGoal?.let { goal ->
        EditGoalAllocationScreen(
            goalName = goal.name,
            allocatedAmount = goal.savedAmount,
            targetAmount = goal.targetAmount,
            progressPercent = goal.progressPercent,
            progressLabel = goal.progressLabel,
            onDismiss = { editingGoal = null }
        )
    }

    if (showEditSavingsPopup) {
        EditSavingsRecordScreen(
            onDismiss = { showEditSavingsPopup = false }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDelete = {
                selectedGoal?.let { goals.remove(it) }
                selectedHistory?.let { history.remove(it) }
                selectedGoal = null
                selectedHistory = null
                showDeleteDialog = false
            },
            onDismiss = {
                selectedGoal = null
                selectedHistory = null
                showDeleteDialog = false
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "SavingsScreen - Full")
@Composable
fun PreviewSavingsScreen() {
    MaterialTheme {
        SavingsScreen(navController = rememberNavController())
    }
}

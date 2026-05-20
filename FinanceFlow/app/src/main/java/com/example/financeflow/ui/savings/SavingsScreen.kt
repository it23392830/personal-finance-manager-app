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

// SavingsScreen
//
// Root composable for the Savings section of FinanceFlow.
//
// State managed here (no ViewModel):
//   - goals       : mutableStateListOf for live add/remove
//   - history     : mutableStateListOf for live history deletes
//   - editingGoal : the goal currently being edited
//   - showEditSavingsPopup : controls the savings-record edit popup
//   - showDeleteDialog : controls the delete confirmation popup
//
// Flow:
//   Three-dot icon -> ActionMenuCard ->
//     Edit   -> opens EditGoalAllocationScreen
//     Delete -> opens DeleteConfirmationDialog and removes selected item
@Composable
fun SavingsScreen(navController: NavController) {

    // Live goal list that supports edit/delete UI updates immediately.
    val goals: SnapshotStateList<SavingGoal> = remember {
        mutableStateListOf(*defaultGoals.toTypedArray())
    }

    // Live history list that supports delete actions immediately.
    val history: SnapshotStateList<SavingHistoryEntry> = remember {
        mutableStateListOf(*dummyHistory.toTypedArray())
    }

    // Holds the goal currently selected for editing.
    var editingGoal by remember { mutableStateOf<SavingGoal?>(null) }

    // Controls the history-record edit popup overlay.
    var showEditSavingsPopup by remember { mutableStateOf(false) }

    // Controls the delete confirmation popup.
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Tracks which goal should be deleted when the dialog is confirmed.
    var selectedGoal by remember { mutableStateOf<SavingGoal?>(null) }

    // Tracks which history item should be deleted when the dialog is confirmed.
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
        // 1. Header card
        item {
            HeaderCard(
                selectedMonth = "May 2026",
                onMonthSelected = { /* UI only */ },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // 2. Savings This Month card
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

        // 3. Lifetime Statistics card
        item {
            LifetimeStatisticsCard(
                totalSaved = "LKR 301,600",
                avgSavingsRate = "26.8%",
                periodLabel = "Total Saved (6 mon)"
            )
        }

        // 4. Savings by Goal card with live edit/delete actions
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

        // 5. Savings History
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

        // 6. Savings Insights card
        item {
            SavingsInsightsCard()
        }
    }

    // Floating edit overlay shown when a goal is selected.
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

    // Overlay popup for editing a savings-history record.
    if (showEditSavingsPopup) {
        EditSavingsRecordScreen(
            onDismiss = { showEditSavingsPopup = false }
        )
    }

    // Shared delete dialog for goals and history items.
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

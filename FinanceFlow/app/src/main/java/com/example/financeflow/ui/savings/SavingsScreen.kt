package com.example.financeflow.ui.savings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.model.Saving
import com.example.financeflow.model.SavingGoal
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.components.SavingsInsightsCard
import com.example.financeflow.ui.components.savings.HeaderCard
import com.example.financeflow.ui.components.savings.LifetimeStatisticsCard
import com.example.financeflow.ui.components.savings.SavingHistoryEntry
import com.example.financeflow.ui.components.savings.SavingsByGoalCard
import com.example.financeflow.ui.components.savings.SavingsHistoryCard
import com.example.financeflow.ui.components.savings.SavingsThisMonthCard
import com.example.financeflow.ui.components.savings.getSavingsColors
import com.example.financeflow.ui.components.savings.SavingGoal as SavingGoalUi
import com.example.financeflow.viewmodel.savings.SavingsViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun SavingsScreen(
    isDarkTheme: Boolean = false,
    navController: NavController,
    viewModel: SavingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val savings by viewModel.savings.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentMonth = remember { YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")) }
    val monthOptions = remember(savings) {
        val firestoreMonths = savings.map { it.month }.filter { it.isNotBlank() }.distinct()
        (listOf(currentMonth) + firestoreMonths).distinct()
    }

    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var editingGoal by remember { mutableStateOf<SavingGoal?>(null) }
    var editingSaving by remember { mutableStateOf<Saving?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<SavingGoal?>(null) }
    var selectedSaving by remember { mutableStateOf<Saving?>(null) }

    val colors = getSavingsColors(isDarkTheme)
    val monthSavings = remember(savings, selectedMonth) {
        savings.filter { it.month == selectedMonth }
    }
    val totalSavedThisMonth = monthSavings.sumOf { it.amountSaved }
    val totalIncomeThisMonth = monthSavings.sumOf { it.totalIncome }
    val rateThisMonth = calculateSavingRate(totalSavedThisMonth, totalIncomeThisMonth)
    val lifetimeSaved = savings.sumOf { it.amountSaved }
    val averageRate = if (savings.isNotEmpty()) savings.map { it.savingRate }.average() else 0.0
    val goalUiItems = remember(goals) { goals.map { it.toGoalUi() } }
    val historyItems = remember(savings) { savings.map { it.toHistoryEntry() } }

    LaunchedEffect(toastMessage, errorMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderCard(
                isDarkTheme = isDarkTheme,
                selectedMonth = selectedMonth,
                monthOptions = monthOptions,
                onMonthSelected = { selectedMonth = it }
            )
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SavingsThisMonthCard(
                    isDarkTheme = isDarkTheme,
                    amount = totalSavedThisMonth.toLkr(),
                    totalIncome = totalIncomeThisMonth.toLkr(),
                    savingRate = "${rateThisMonth.toInt()}%",
                    onAddNewSaving = { navController.navigate(Routes.ADD_SAVING) }
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                LifetimeStatisticsCard(
                    isDarkTheme = isDarkTheme,
                    totalSaved = lifetimeSaved.toLkr(),
                    avgSavingsRate = "${"%.1f".format(averageRate)}%",
                    periodLabel = "Total Saved (${savings.size} records)"
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SavingsByGoalCard(
                    isDarkTheme = isDarkTheme,
                    goals = goalUiItems,
                    onEditClick = { goalUi -> editingGoal = goals.firstOrNull { it.id == goalUi.id } },
                    onDeleteClick = { goalUi ->
                        selectedGoal = goals.firstOrNull { it.id == goalUi.id }
                        selectedSaving = null
                        showDeleteDialog = true
                    }
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SavingsHistoryCard(
                    isDarkTheme = isDarkTheme,
                    entries = historyItems,
                    onEditClick = { historyEntry ->
                        editingSaving = savings.firstOrNull { it.id == historyEntry.id }
                    },
                    onDeleteClick = { historyEntry ->
                        selectedSaving = savings.firstOrNull { it.id == historyEntry.id }
                        selectedGoal = null
                        showDeleteDialog = true
                    }
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SavingsInsightsCard(isDarkTheme = isDarkTheme)
            }
        }
    }

    editingGoal?.let { goal ->
        EditGoalAllocationScreen(
            isDarkTheme = isDarkTheme,
            goalName = goal.goalName,
            allocatedAmount = goal.currentAmount.toPlainAmount(),
            targetAmount = goal.targetAmount.toPlainAmount(),
            progressPercent = goal.progress,
            progressLabel = "${"%.1f".format(goal.progress * 100)}% complete",
            onSave = { name, currentAmount, targetAmount ->
                viewModel.updateGoal(
                    goal.copy(
                        goalName = name,
                        currentAmount = currentAmount,
                        targetAmount = targetAmount
                    )
                )
                editingGoal = null
            },
            onDismiss = { editingGoal = null }
        )
    }

    editingSaving?.let { saving ->
        EditSavingsRecordScreen(
            isDarkTheme = isDarkTheme,
            saving = saving,
            onSave = { updatedSaving ->
                viewModel.updateSaving(updatedSaving)
                editingSaving = null
            },
            onDismiss = { editingSaving = null }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            isDarkTheme = isDarkTheme,
            onDelete = {
                selectedGoal?.let { viewModel.deleteGoal(it.id) }
                selectedSaving?.let { viewModel.deleteSaving(it.id) }
                selectedGoal = null
                selectedSaving = null
                showDeleteDialog = false
            },
            onDismiss = {
                selectedGoal = null
                selectedSaving = null
                showDeleteDialog = false
            }
        )
    }
}

/** Maps Firestore SavingGoal into the UI card model. */
private fun SavingGoal.toGoalUi(): SavingGoalUi {
    val safeProgress = if (targetAmount > 0.0) {
        (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        progress.coerceIn(0f, 1f)
    }
    return SavingGoalUi(
        name = goalName,
        savedAmount = currentAmount.toLkr(),
        targetAmount = "Target: ${targetAmount.toLkr()}",
        progressPercent = safeProgress,
        progressLabel = "${"%.1f".format(safeProgress * 100)}% complete",
        id = id
    )
}

/** Maps Firestore Saving into the existing history card model. */
private fun Saving.toHistoryEntry(): SavingHistoryEntry {
    return SavingHistoryEntry(
        month = month,
        date = date,
        savingRate = "${savingRate.toInt()}%",
        income = totalIncome.toLkr(),
        saved = amountSaved.toLkr(),
        id = id
    )
}

/** Calculates saving rate as a percentage. */
private fun calculateSavingRate(amountSaved: Double, totalIncome: Double): Double {
    return if (totalIncome > 0.0) (amountSaved / totalIncome) * 100.0 else 0.0
}

/** Formats a number as Sri Lankan rupees. */
private fun Double.toLkr(): String = "LKR ${"%,.0f".format(this)}"

/** Formats an editable numeric value without currency symbols. */
private fun Double.toPlainAmount(): String = "%.0f".format(this)

@Preview(showBackground = true, showSystemUi = true, name = "SavingsScreen - Full")
@Composable
fun PreviewSavingsScreen() {
    MaterialTheme {
        SavingsScreen(navController = rememberNavController())
    }
}

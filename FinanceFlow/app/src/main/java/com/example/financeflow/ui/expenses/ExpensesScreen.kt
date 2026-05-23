package com.example.financeflow.ui.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeflow.ui.components.expenses.AddExpenseDialog
import com.example.financeflow.ui.components.expenses.BottomClockCard
import com.example.financeflow.ui.components.expenses.BudgetCard
import com.example.financeflow.ui.components.expenses.ExpenseHeader
import com.example.financeflow.ui.components.expenses.ExpenseListCard
import com.example.financeflow.ui.components.expenses.ExpenseSpacing
import com.example.financeflow.ui.components.expenses.LightPink
import com.example.financeflow.ui.components.expenses.QuickAddCard
import com.example.financeflow.ui.components.expenses.ScreenBackground
import com.example.financeflow.ui.components.expenses.SmartSuggestionsCard
import com.example.financeflow.viewmodel.expense.ExpenseViewModel

@Composable
fun ExpensesScreen(
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.addExpenseForm.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White,
                        ScreenBackground,
                        LightPink.copy(alpha = 0.58f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ExpenseSpacing)
        ) {
            item {
                ExpenseHeader(
                    selectedMonth = uiState.selectedMonth,
                    onMonthSelected = viewModel::selectMonth
                )
            }

            item {
                BudgetCard(uiState = uiState)
            }

            item {
                QuickAddCard(
                    onAddExpense = { viewModel.openAddExpense() },
                    onCategoryClick = { viewModel.openAddExpense(it) }
                )
            }

            item {
                SmartSuggestionsCard(
                    onSuggestionClick = { category, description ->
                        viewModel.openAddExpense(category)
                        viewModel.updateDescription(description)
                    }
                )
            }

            item {
                ExpenseListCard(
                    expenses = uiState.visibleExpenses,
                    onDelete = viewModel::deleteExpense
                )
            }

            item {
                BottomClockCard()
            }
        }

        if (uiState.showAddExpense) {
            AddExpenseDialog(
                formState = formState,
                isSaving = uiState.isSaving,
                errorMessage = uiState.errorMessage,
                onDismiss = viewModel::closeAddExpense,
                onExpenseTypeChange = viewModel::updateExpenseType,
                onAmountChange = viewModel::updateAmount,
                onCategoryChange = viewModel::updateCategory,
                onDescriptionChange = viewModel::updateDescription,
                onPaymentMethodChange = viewModel::updatePaymentMethod,
                onDateChange = viewModel::updateDate,
                onSave = viewModel::saveExpense
            )
        }
    }
}

package com.example.financeflow.viewmodel.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.*
import com.example.financeflow.repository.income.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    init {
        // Start loading and observe income sources and available months
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // income sources
            launch {
                repository.getIncomeSourcesFlow().collect { sources ->
                    val defaults = IncomeSource.values().map { it.label }
                    val merged = (sources + defaults).distinct()
                    _uiState.value = _uiState.value.copy(incomeSources = merged)
                }
            }

            // available months
            launch {
                try {
                    val pairs = repository.getAvailableMonths()
                    val months = pairs.map { (y, m) ->
                        com.example.financeflow.ui.components.Income.MonthYear(y, m)
                    }
                    _uiState.value = _uiState.value.copy(availableMonths = months)
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
        // Ensure initial month is current month
        val c = java.util.Calendar.getInstance()
        setSelectedMonth(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1)
    }

    fun setSelectedMonth(year: Int, month: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year, selectedMonth = month)
        // Observe current-month transactions (excluding future dates)
        viewModelScope.launch {
            repository.getCurrentMonthTransactionsFlow(year, month).collect { list ->
                updateUiStateWithTransactions(list)
            }
        }
    }

    private fun updateUiStateWithTransactions(list: List<Income>) {
        val total = list.sumOf { it.amount }
        val recent = list.take(10) // Show more in recent if available
        val grouped = list.groupBy { it.source }.map { (sourceStr, items) ->
            IncomeBySource(
                source = sourceStr,
                totalAmount = items.sumOf { it.amount },
                transactionCount = items.size,
                percentage = if (total > 0) (items.sumOf { it.amount } / total) * 100 else 0.0
            )
        }.sortedByDescending { it.totalAmount }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isSuccess = true,
            totalIncome = total,
            currentMonthTransactions = list,
            recentTransactions = recent,
            incomeBySource = grouped
        )
    }

    fun showAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = true) }
    fun dismissAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = false) }
    fun showEditDialog(income: Income) { _uiState.value = _uiState.value.copy(showEditDialog = true, selectedIncome = income) }
    fun dismissEditDialog() { _uiState.value = _uiState.value.copy(showEditDialog = false, selectedIncome = null) }
    fun showDeleteDialog(income: Income) { _uiState.value = _uiState.value.copy(showDeleteDialog = true, selectedIncome = income) }
    fun dismissDeleteDialog() { _uiState.value = _uiState.value.copy(showDeleteDialog = false, selectedIncome = null) }

    fun addIncome(income: Income) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false)
                repository.addIncome(income)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false)
                repository.updateIncome(income)
                
                // The real-time listener will eventually update the UI, 
                // but we can update it in-place for immediate feedback.
                val current = _uiState.value
                
                val updatedList = current.currentMonthTransactions.map { item ->
                    if (item.id == income.id) {
                        // Preserve metadata that wasn't part of the update if necessary
                        income.copy(
                            userId = if (income.userId.isBlank()) item.userId else income.userId,
                            createdAt = item.createdAt
                        )
                    } else item
                }

                updateUiStateWithTransactions(updatedList)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false)
                repository.deleteIncome(incomeId)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    suspend fun getIncomeById(incomeId: String): Income? {
        return repository.getIncomeById(incomeId)
    }

    fun addIncomeSource(name: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.addIncomeSource(name)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    suspend fun getAvailableMonths(): List<com.example.financeflow.ui.components.Income.MonthYear> {
        val pairs = repository.getAvailableMonths()
        return pairs.map { (y, m) -> com.example.financeflow.ui.components.Income.MonthYear(y, m) }
    }
}
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
        // Observe all incomes in real-time and update summary UI
        viewModelScope.launch {
            repository.getIncomesFlow().collect { list ->
                val total = list.sumOf { it.amount }
                val recent = list.take(5)
                val grouped = list.groupBy { it.source }.map { (sourceStr, items) ->
                    val sourceEnum = try {
                        IncomeSource.valueOf(sourceStr)
                    } catch (e: Exception) {
                        IncomeSource.OTHER
                    }
                    IncomeBySource(
                        source = sourceEnum,
                        totalAmount = items.sumOf { it.amount },
                        transactionCount = items.size,
                        percentage = 0.0
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalIncome = total,
                    recentTransactions = recent,
                    incomeBySource = grouped
                )
            }
        }
    }

    fun setSelectedMonth(year: Int, month: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year, selectedMonth = month)
        // optional: observe monthly flow and update totals
        viewModelScope.launch {
            repository.getIncomesForMonthFlow(year, month).collect { list ->
                val total = list.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(totalIncome = total)
            }
        }
    }

    fun showAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = true) }
    fun dismissAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = false) }
    fun showEditDialog(income: Income) { _uiState.value = _uiState.value.copy(showEditDialog = true, selectedIncome = income) }
    fun dismissEditDialog() { _uiState.value = _uiState.value.copy(showEditDialog = false, selectedIncome = null) }
    fun showDeleteDialog(income: Income) { _uiState.value = _uiState.value.copy(showDeleteDialog = true, selectedIncome = income) }
    fun dismissDeleteDialog() { _uiState.value = _uiState.value.copy(showDeleteDialog = false, selectedIncome = null) }

    fun addIncome(income: Income) {
        viewModelScope.launch {
            repository.addIncome(income)
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            repository.updateIncome(income)
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            repository.deleteIncome(incomeId)
        }
    }

    suspend fun getIncomeById(incomeId: String): Income? {
        return repository.getIncomeById(incomeId)
    }
}
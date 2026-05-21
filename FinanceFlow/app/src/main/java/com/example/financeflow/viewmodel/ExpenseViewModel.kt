package com.example.financeflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Expense
import com.example.financeflow.repository.expense.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(
        amount: Double,
        category: String,
        subCategory: String,
        paymentMethod: String,
        note: String,
        expenseType: String,
        isRecurring: Boolean = false,
        recurringFrequency: String = ""
    ) {
        viewModelScope.launch {
            val newExpense = Expense(
                id = UUID.randomUUID().toString(),
                amount = amount,
                category = category,
                subCategory = subCategory,
                paymentMethod = paymentMethod,
                note = note,
                expenseType = expenseType,
                isRecurring = isRecurring,
                recurringFrequency = recurringFrequency,
                date = System.currentTimeMillis()
            )
            repository.addExpense(newExpense)
        }
    }

    fun syncExpenses() {
        viewModelScope.launch {
            repository.syncUnsyncedExpenses()
        }
    }
}

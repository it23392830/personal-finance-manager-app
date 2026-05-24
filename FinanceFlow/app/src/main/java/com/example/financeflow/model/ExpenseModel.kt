package com.example.financeflow.model

import com.example.financeflow.ui.expenses.CategoryBreakdownItem
import com.example.financeflow.ui.expenses.ExpenseUiItem
import com.google.firebase.Timestamp

/**
 * Core expense record stored in Firestore.
 */
data class Expense(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "CASH",
    val notes: String = "",
    val isFixed: Boolean = false,
    val isPaid: Boolean = false,
    val date: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Fixed expense record stored in Firestore.
 */
data class FixedExpense(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val isPaid: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * UI state for the Expenses screen.
 */
data class ExpenseUiState(
    val isLoading: Boolean = false,
    val selectedMonth: String = "",
    val availableMonths: List<String> = emptyList(),
    val totalExpense: Double = 0.0,
    val todayExpenses: List<ExpenseUiItem> = emptyList(),
    val currentMonthTransactions: List<ExpenseUiItem> = emptyList(),
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val fixedPayments: List<Expense> = emptyList(),
    val errorMessage: String? = null
)

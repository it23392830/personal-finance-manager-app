package com.example.financeflow.model

import com.example.financeflow.ui.expenses.CategoryBreakdownItem
import com.example.financeflow.ui.expenses.ExpenseUiItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Domain model for an expense record stored under users/{uid}/expenses/{expenseId}
 */
data class Expense(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "",
    val notes: String = "",
    @get:PropertyName("fixed") @set:PropertyName("fixed") var isFixed: Boolean = false,
    @get:PropertyName("fixedExpense") @set:PropertyName("fixedExpense") var isFixedExpense: Boolean = false,
    @get:PropertyName("paid") @set:PropertyName("paid") var isPaid: Boolean = false,
    val templateId: String? = null,
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
    val todayExpenses: List<com.example.financeflow.ui.expenses.ExpenseUiItem> = emptyList(),
    val currentMonthTransactions: List<com.example.financeflow.ui.expenses.ExpenseUiItem> = emptyList(),
    val categoryBreakdown: List<com.example.financeflow.ui.expenses.CategoryBreakdownItem> = emptyList(),
    val fixedPayments: List<FixedExpenseDisplayItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Helper model to show fixed payment templates and their status for the current month.
 */
data class FixedExpenseDisplayItem(
    val template: FixedExpense,
    val isApplied: Boolean,
    val monthExpenseId: String? = null
)

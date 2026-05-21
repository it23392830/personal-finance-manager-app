package com.example.financeflow.model

import com.google.firebase.Timestamp

/**
 * Represents an income source category (e.g. Salary, Freelance, AdSense, Crypto).
 */
enum class IncomeSource(val label: String, val iconRes: String) {
    SALARY("Salary", "ic_salary"),
    FREELANCE("Freelance", "ic_freelance"),
    ADSENSE("AdSense", "ic_adsense"),
    CRYPTO("Crypto", "ic_crypto"),
    INVESTMENT("Investment", "ic_investment"),
    RENTAL("Rental", "ic_rental"),
    OTHER("Other", "ic_other")
}

/**
 * Supported currencies for income entries.
 */
enum class Currency(val code: String, val symbol: String, val label: String) {
    LKR("LKR", "LKR", "LKR (Sri Lankan Rupee)"),
    USD("USD", "$", "USD (US Dollar)"),
    EUR("EUR", "€", "EUR (Euro)"),
    GBP("GBP", "£", "GBP (British Pound)")
}

/**
 * Core income entry stored in Firestore.
 *
 * Firestore collection path: users/{uid}/income/{incomeId}
 */
data class Income(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = Currency.LKR.code,
    val source: String = IncomeSource.SALARY.name,
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Aggregated view of incomes grouped by [IncomeSource] for a given month.
 * Computed locally from a list of [Income] entries.
 */
data class IncomeBySource(
    val source: IncomeSource,
    val totalAmount: Double,
    val transactionCount: Int,
    val percentage: Double          // 0–100
)

/**
 * UI state for the Income screen.
 */
data class IncomeUiState(
    val isLoading: Boolean = false,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 5,           // 1-based (1 = January)
    val totalIncome: Double = 0.0,
    val displayCurrency: Currency = Currency.LKR,
    val incomeBySource: List<IncomeBySource> = emptyList(),
    val recentTransactions: List<Income> = emptyList(),
    val daysUntilNextSalary: Int? = null,
    val errorMessage: String? = null,

    // Dialog visibility flags
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedIncome: Income? = null    // The entry being edited / deleted
)
package com.example.financeflow.model

data class Expense(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val subCategory: String = "",
    val paymentMethod: String = "", // Cash, Card, Bank Transfer, Digital Wallet
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val expenseType: String = "ESSENTIAL", // ESSENTIAL, DISCRETIONARY
    val isRecurring: Boolean = false,
    val recurringFrequency: String = "", // Daily, Weekly, Monthly
    val tags: List<String> = emptyList()
)

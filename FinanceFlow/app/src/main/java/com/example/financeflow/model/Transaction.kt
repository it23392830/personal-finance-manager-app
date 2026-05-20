package com.example.financeflow.model

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // income, saving, expense
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val amountInBaseCurrency: Double = 0.0,

    // income fields
    val source: String = "", // salary, freelance, adsense, crypto

    // expense fields
    val category: String = "",
    val expenseType: String = "", // must, optional
    val paymentMethod: String = "", // cash, card

    // saving fields
    val goalId: String = "",

    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
package com.example.financeflow.model

/**
 * Firestore model for one monthly saving record.
 */
data class Saving(
    val id: String = "",
    val amountSaved: Double = 0.0,
    val totalIncome: Double = 0.0,
    val savingRate: Double = 0.0,
    val month: String = "",
    val date: String = "",
    val goalName: String = "",
    val description: String = "",
    val targetAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.financeflow.model

/**
 * Firestore model for a savings goal allocation.
 */
data class SavingGoal(
    val id: String = "",
    val goalName: String = "",
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val progress: Float = 0f
)

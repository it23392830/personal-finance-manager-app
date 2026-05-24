package com.example.financeflow.model

/**
 * Firestore model for the signed-in user's profile settings.
 */
data class UserProfile(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImage: String = "",
    val baseCurrency: String = "LKR",
    val expenseTracker: String = "Real-Time",
    val pushNotifications: Boolean = true,
    val dailyReminder: Boolean = true,
    val weeklyReport: Boolean = true,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

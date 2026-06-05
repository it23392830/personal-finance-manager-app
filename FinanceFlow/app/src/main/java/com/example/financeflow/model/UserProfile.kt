package com.example.financeflow.model

import com.example.financeflow.data.model.Streak

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
    val streak: Streak = Streak(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

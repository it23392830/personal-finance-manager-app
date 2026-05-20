package com.example.financeflow.model

data class UserPreferences(
    val userId: String = "",
    val baseCurrency: String = "LKR",
    val expenseTrackingMode: String = "daily", // realtime, daily
    val dailyReminderTime: String = "21:00"
)
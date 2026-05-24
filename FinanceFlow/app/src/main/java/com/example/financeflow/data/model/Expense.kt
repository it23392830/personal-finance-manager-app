package com.example.financeflow.data.model

/**
 * Minimal expense shape required by the streak system.
 *
 * `date` stores the expense day in ISO-8601 format (yyyy-MM-dd).
 * `createdAt` stores the day the expense was actually created. The streak
 * feature uses both fields so only same-day logs and yesterday-only recoveries
 * can affect the streak.
 */
data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: String = "",
    val createdAt: String = ""
)

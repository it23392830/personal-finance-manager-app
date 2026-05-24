package com.example.financeflow.data.model

data class Streak(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val freezeState: Boolean = false,
    val missedDays: Int = 0,
    val lastExpenseDate: String = "",
    val streakStatus: String = STATUS_BROKEN
) {
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_FROZEN = "FROZEN"
        const val STATUS_BROKEN = "BROKEN"
    }
}

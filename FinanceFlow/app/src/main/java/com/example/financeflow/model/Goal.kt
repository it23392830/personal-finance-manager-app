package com.example.financeflow.model

import com.google.firebase.Timestamp

/**
 * Core Goal domain model.
 */
data class Goal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val targetAmount: Double = 0.0,
    val currentSavedAmount: Double = 0.0,
    val currency: String = "LKR",
    val deadlineDate: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isCompleted: Boolean = false,
    val unlockedBadges: List<String> = emptyList(),
    val notificationsEnabled: Boolean = true
) {
    val progressPercentage: Double
        get() = if (targetAmount > 0) (currentSavedAmount / targetAmount * 100).coerceIn(0.0, 100.0) else 0.0

    val remainingAmount: Double
        get() = (targetAmount - currentSavedAmount).coerceAtLeast(0.0)

    val daysRemaining: Long
        get() {
            val nowSeconds = Timestamp.now().seconds
            val deadlineSeconds = deadlineDate.seconds
            return ((deadlineSeconds - nowSeconds) / 86400).coerceAtLeast(0)
        }

    val monthsRemaining: Double
        get() = daysRemaining / 30.0

    val dailySavingTarget: Double
        get() = if (daysRemaining > 0) remainingAmount / daysRemaining else remainingAmount

    val monthlySavingTarget: Double
        get() = if (monthsRemaining > 0) remainingAmount / monthsRemaining else remainingAmount

    val isOnTrack: Boolean
        get() {
            val totalDuration = (deadlineDate.seconds - createdAt.seconds).toDouble()
            if (totalDuration <= 0) return true
            val elapsed = (Timestamp.now().seconds - createdAt.seconds).toDouble()
            val timeProgress = (elapsed / totalDuration).coerceIn(0.0, 1.0)
            val savingsProgress = progressPercentage / 100.0
            return savingsProgress >= timeProgress
        }
}

enum class GoalBadge(val id: String, val label: String, val threshold: Double, val emoji: String) {
    STARTED("BADGE_STARTED", "Getting Started", 0.01, "🚀"),
    QUARTER("BADGE_25", "Quarter Way", 0.25, "🥉"),
    HALFWAY("BADGE_50", "Half Way", 0.50, "🥈"),
    THREE_QUARTER("BADGE_75", "Almost There", 0.75, "🥇"),
    COMPLETED("BADGE_100", "Goal Achieved", 1.00, "🏆");

    companion object {
        fun checkNewBadges(goal: Goal): List<GoalBadge> {
            val progress = goal.progressPercentage / 100.0
            return values().filter { badge ->
                progress >= badge.threshold && badge.id !in goal.unlockedBadges
            }
        }
    }
}

fun Goal.toMap(): Map<String, Any> = mapOf(
    "userId" to userId,
    "title" to title,
    "description" to description,
    "category" to category,
    "targetAmount" to targetAmount,
    "currentSavedAmount" to currentSavedAmount,
    "currency" to currency,
    "deadlineDate" to deadlineDate,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "isCompleted" to isCompleted,
    "unlockedBadges" to unlockedBadges,
    "notificationsEnabled" to notificationsEnabled
)

fun Map<String, Any>.toGoal(id: String): Goal = Goal(
    id = id,
    userId = this["userId"] as? String ?: "",
    title = this["title"] as? String ?: "",
    description = this["description"] as? String ?: "",
    category = this["category"] as? String ?: "General",
    targetAmount = (this["targetAmount"] as? Number)?.toDouble() ?: 0.0,
    currentSavedAmount = (this["currentSavedAmount"] as? Number)?.toDouble() ?: 0.0,
    currency = this["currency"] as? String ?: "LKR",
    deadlineDate = this["deadlineDate"] as? Timestamp ?: Timestamp.now(),
    createdAt = this["createdAt"] as? Timestamp ?: Timestamp.now(),
    updatedAt = this["updatedAt"] as? Timestamp ?: Timestamp.now(),
    isCompleted = this["isCompleted"] as? Boolean ?: false,
    unlockedBadges = (this["unlockedBadges"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    notificationsEnabled = this["notificationsEnabled"] as? Boolean ?: true
)

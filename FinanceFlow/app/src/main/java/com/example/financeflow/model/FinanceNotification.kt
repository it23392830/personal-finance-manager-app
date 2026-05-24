package com.example.financeflow.model

/**
 * Domain model used by the notification UI and repository.
 *
 * It intentionally mirrors the requested Notification shape while avoiding a
 * name clash with Android's own Notification class.
 */
data class FinanceNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String,
    val isRead: Boolean
)

/**
 * Supported notification type values stored in Room and Firestore.
 */
object FinanceNotificationType {
    const val MORNING = "MORNING"
    const val MISSED = "MISSED"
    const val STREAK = "STREAK"
    const val SAVINGS = "SAVINGS"
}

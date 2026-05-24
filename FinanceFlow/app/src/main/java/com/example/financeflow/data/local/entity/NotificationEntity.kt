package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for one notification shown inside FinanceFlow.
 *
 * The core fields match the notification requirement, while userId keeps local
 * records separated when different Firebase users sign in on the same device.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String,
    val isRead: Boolean
)

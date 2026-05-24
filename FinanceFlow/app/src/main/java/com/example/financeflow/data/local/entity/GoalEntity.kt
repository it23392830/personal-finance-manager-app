package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val category: String,
    val targetAmount: Double,
    val currentSavedAmount: Double,
    val currency: String,
    val deadlineDate: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isCompleted: Boolean,
    val unlockedBadgesCsv: String
)

package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room cache for one Firestore saving goal.
 */
@Entity(tableName = "saving_goals")
data class SavingGoalEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val goalName: String = "",
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val progress: Float = 0f
)

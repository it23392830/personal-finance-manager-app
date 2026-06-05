package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room cache for one Firestore saving record.
 *
 * The userId column keeps cached records separated when different Firebase
 * users sign in on the same device.
 */
@Entity(tableName = "savings")
data class SavingEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val goalId: String = "",
    val amountSaved: Double = 0.0,
    val incomeSource: String = "",
    val savingRate: Double = 0.0,
    val month: String = "",
    val date: String = "",
    val goalName: String = "",
    val description: String = "",
    val targetAmount: Double = 0.0,
    val createdAt: Long = 0L
)

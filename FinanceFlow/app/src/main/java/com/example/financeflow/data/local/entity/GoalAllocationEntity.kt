package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_allocations")
data class GoalAllocationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val goalId: String,
    val amount: Double,
    val monthlyTarget: Double,
    val monthYear: String,
    val note: String,
    val allocatedAt: Long
)
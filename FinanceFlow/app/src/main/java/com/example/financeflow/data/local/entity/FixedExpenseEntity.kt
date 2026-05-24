package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixed_expense")
data class FixedExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val isPaid: Boolean = false,
    val createdAt: Long = 0L
)

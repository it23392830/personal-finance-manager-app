package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "",
    val notes: String = "",
    val isFixed: Boolean = false,
    val isPaid: Boolean = false,
    val date: Long = 0L,
    val createdAt: Long = 0L
)

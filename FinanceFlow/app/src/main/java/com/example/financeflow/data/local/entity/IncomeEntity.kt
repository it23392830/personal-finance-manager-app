package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a local copy of an Income record.
 * Fields mirror the domain model but store timestamps as epoch millis (Long)
 */
@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val source: String = "",
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val description: String = "",
    val notes: String = "",
    val date: Long = 0L,
    val createdAt: Long = 0L
)

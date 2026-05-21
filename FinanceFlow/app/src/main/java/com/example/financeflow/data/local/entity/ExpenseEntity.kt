package com.example.financeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financeflow.model.Expense

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val category: String,
    val subCategory: String,
    val paymentMethod: String,
    val date: Long,
    val note: String,
    val expenseType: String,
    val isRecurring: Boolean,
    val recurringFrequency: String,
    val tags: String, // Stored as comma-separated values for simplicity
    val isSynced: Boolean = false
)

fun ExpenseEntity.toDomainModel(): Expense {
    return Expense(
        id = id,
        userId = userId,
        amount = amount,
        category = category,
        subCategory = subCategory,
        paymentMethod = paymentMethod,
        date = date,
        note = note,
        expenseType = expenseType,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        tags = if (tags.isEmpty()) emptyList() else tags.split(",")
    )
}

fun Expense.toLocalEntity(isSynced: Boolean = false): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        userId = userId,
        amount = amount,
        category = category,
        subCategory = subCategory,
        paymentMethod = paymentMethod,
        date = date,
        note = note,
        expenseType = expenseType,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        tags = tags.joinToString(","),
        isSynced = isSynced
    )
}

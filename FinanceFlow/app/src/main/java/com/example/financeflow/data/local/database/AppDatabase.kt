package com.example.financeflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.dao.FixedExpenseDao
import com.example.financeflow.data.local.entity.IncomeEntity
import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.entity.ExpenseEntity
import com.example.financeflow.data.local.entity.FixedExpenseEntity
import com.example.financeflow.data.local.dao.GoalDao
import com.example.financeflow.data.local.dao.GoalAllocationDao
import com.example.financeflow.data.local.entity.GoalAllocationEntity

/**
 * Room database for the app. Add additional entities here as the app grows.
 */
@Database(entities = [IncomeEntity::class, ExpenseEntity::class, FixedExpenseEntity::class, GoalEntity::class, GoalAllocationEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun fixedExpenseDao(): FixedExpenseDao
    abstract fun goalDao(): GoalDao
    abstract fun goalAllocationDao(): GoalAllocationDao
}
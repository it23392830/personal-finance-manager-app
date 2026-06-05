package com.example.financeflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeflow.data.local.dao.ChatMessageDao
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.dao.FixedExpenseDao
import com.example.financeflow.data.local.dao.NotificationDao
import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.data.local.entity.ChatMessageEntity
import com.example.financeflow.data.local.entity.IncomeEntity
import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.entity.ExpenseEntity
import com.example.financeflow.data.local.entity.FixedExpenseEntity
import com.example.financeflow.data.local.dao.GoalDao
import com.example.financeflow.data.local.dao.GoalAllocationDao
import com.example.financeflow.data.local.entity.GoalAllocationEntity
import com.example.financeflow.data.local.entity.GoalEntity
import com.example.financeflow.data.local.entity.NotificationEntity
import com.example.financeflow.data.local.entity.SavingEntity
import com.example.financeflow.data.local.entity.SavingGoalEntity

/**
 * Room database for the app. Add additional entities here as the app grows.
 */
@Database(entities = [IncomeEntity::class, ExpenseEntity::class, FixedExpenseEntity::class, GoalEntity::class, GoalAllocationEntity::class, NotificationEntity::class, SavingEntity::class, SavingGoalEntity::class, ChatMessageEntity::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun fixedExpenseDao(): FixedExpenseDao
    abstract fun goalDao(): GoalDao
    abstract fun goalAllocationDao(): GoalAllocationDao
    abstract fun notificationDao(): NotificationDao
    abstract fun savingDao(): SavingDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun chatMessageDao(): ChatMessageDao
}
package com.example.financeflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.entity.ExpenseEntity

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}

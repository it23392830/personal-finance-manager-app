package com.example.financeflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.entity.IncomeEntity

/**
 * Room database for the app. Add additional entities here as the app grows.
 */
@Database(entities = [IncomeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
}

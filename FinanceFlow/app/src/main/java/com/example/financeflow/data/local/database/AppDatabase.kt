package com.example.financeflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.dao.NotificationDao
import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.data.local.entity.IncomeEntity
import com.example.financeflow.data.local.entity.NotificationEntity
import com.example.financeflow.data.local.entity.SavingEntity
import com.example.financeflow.data.local.entity.SavingGoalEntity

/**
 * Room database for the app. Add additional entities here as the app grows.
 */
@Database(
    entities = [
        IncomeEntity::class,
        NotificationEntity::class,
        SavingEntity::class,
        SavingGoalEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao

    /** DAO used by NotificationRepository and NotificationViewModel. */
    abstract fun notificationDao(): NotificationDao

    /** DAO used by SavingsRepository for cached saving records. */
    abstract fun savingDao(): SavingDao

    /** DAO used by SavingsRepository for cached saving goals. */
    abstract fun savingGoalDao(): SavingGoalDao
}

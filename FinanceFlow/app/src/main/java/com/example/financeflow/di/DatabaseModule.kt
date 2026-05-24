package com.example.financeflow.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.dao.NotificationDao
import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide Room database and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Adds the notifications table without clearing the user's existing income
     * cache when upgrading from database version 1.
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `notifications` (
                    `id` TEXT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `message` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `type` TEXT NOT NULL,
                    `isRead` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Adds local Room caches for Savings records and Savings goals.
     */
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `savings` (
                    `id` TEXT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `amountSaved` REAL NOT NULL,
                    `totalIncome` REAL NOT NULL,
                    `savingRate` REAL NOT NULL,
                    `month` TEXT NOT NULL,
                    `date` TEXT NOT NULL,
                    `goalName` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `targetAmount` REAL NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `saving_goals` (
                    `id` TEXT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `goalName` TEXT NOT NULL,
                    `currentAmount` REAL NOT NULL,
                    `targetAmount` REAL NOT NULL,
                    `progress` REAL NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "financeflow_db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    @Singleton
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()

    @Provides
    @Singleton
    fun provideSavingDao(db: AppDatabase): SavingDao = db.savingDao()

    @Provides
    @Singleton
    fun provideSavingGoalDao(db: AppDatabase): SavingGoalDao = db.savingGoalDao()
}

package com.example.financeflow.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.dao.NotificationDao
import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.dao.FixedExpenseDao
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

    /**
     * Adds Expense and FixedExpense tables from main branch.
     */
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `expenses` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `amount` REAL NOT NULL,
                    `description` TEXT NOT NULL,
                    `date` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `fixed_expenses` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `amount` REAL NOT NULL,
                    `description` TEXT NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `startDate` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Fallback migration from version 4 -> 5 using destructive migration,
     * in case schema differences exist between the two merge paths.
     */
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No-op — all tables should already exist from prior migrations.
            // This migration exists to bump the version after merging both branches.
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "financeflow_db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .fallbackToDestructiveMigration()
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

    @Provides
    @Singleton
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideFixedExpenseDao(db: AppDatabase): FixedExpenseDao = db.fixedExpenseDao()
}

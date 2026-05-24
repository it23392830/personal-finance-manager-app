package com.example.financeflow.di

import android.content.Context
import androidx.room.Room
import com.example.financeflow.data.local.dao.IncomeDao
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "financeflow_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    @Singleton
    fun provideExpenseDao(db: AppDatabase): com.example.financeflow.data.local.dao.ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideFixedExpenseDao(db: AppDatabase): com.example.financeflow.data.local.dao.FixedExpenseDao = db.fixedExpenseDao()

    @Provides
    @Singleton
    fun provideGoalDao(db: AppDatabase): com.example.financeflow.data.local.dao.GoalDao = db.goalDao()

    @Provides
    @Singleton
    fun provideGoalAllocationDao(db: AppDatabase): com.example.financeflow.data.local.dao.GoalAllocationDao = db.goalAllocationDao()
}
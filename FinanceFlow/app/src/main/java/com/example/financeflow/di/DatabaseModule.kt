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
        return Room.databaseBuilder(context, AppDatabase::class.java, "financeflow_db").build()
    }

    @Provides
    @Singleton
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()
}

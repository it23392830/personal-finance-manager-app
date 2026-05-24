package com.example.financeflow.data.local.dao

import androidx.room.*
import com.example.financeflow.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for IncomeEntity. Provides Flow queries for reactive UI.
 */
@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Query("DELETE FROM income WHERE id = :id")
    suspend fun deleteIncomeById(id: String)

    @Query("SELECT * FROM income ORDER BY date DESC")
    fun getAllIncomeFlow(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income WHERE userId = :userId ORDER BY date DESC")
    fun getAllIncomeFlowForUser(userId: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomesBetweenFlow(start: Long, end: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income WHERE userId = :userId AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomesBetweenFlowForUser(start: Long, end: Long, userId: String): Flow<List<IncomeEntity>>

    /**
     * Counts income records in a date range for the current user.
     *
     * Notification workers use this to decide whether the 9 PM missed activity
     * reminder should be created.
     */
    @Query("SELECT COUNT(*) FROM income WHERE userId = :userId AND date BETWEEN :start AND :end")
    suspend fun countIncomeForUserBetween(userId: String, start: Long, end: Long): Int

    @Query("SELECT * FROM income WHERE id = :id LIMIT 1")
    suspend fun getIncomeById(id: String): IncomeEntity?

    @Query("SELECT * FROM income WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getIncomeByIdForUser(id: String, userId: String): IncomeEntity?

    @Query("DELETE FROM income WHERE userId = :userId")
    suspend fun deleteIncomesForUser(userId: String)
}

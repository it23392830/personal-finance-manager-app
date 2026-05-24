package com.example.financeflow.data.local.dao

import androidx.room.*
import com.example.financeflow.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesFlowForUser(userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesBetweenFlow(start: Long, end: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE userId = :userId AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesBetweenFlowForUser(start: Long, end: Long, userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: String): ExpenseEntity?

    @Query("SELECT * FROM expense WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getExpenseByIdForUser(id: String, userId: String): ExpenseEntity?

    @Query("DELETE FROM expense WHERE userId = :userId")
    suspend fun deleteExpensesForUser(userId: String)

}

package com.example.financeflow.data.local.dao

import androidx.room.*
import com.example.financeflow.data.local.entity.FixedExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixedExpense(item: FixedExpenseEntity)

    @Update
    suspend fun updateFixedExpense(item: FixedExpenseEntity)

    @Query("DELETE FROM fixed_expense WHERE id = :id")
    suspend fun deleteFixedExpense(id: String)

    @Query("SELECT * FROM fixed_expense ORDER BY createdAt DESC")
    fun getAllFixedExpensesFlow(): Flow<List<FixedExpenseEntity>>

    @Query("SELECT * FROM fixed_expense WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllFixedExpensesFlowForUser(userId: String): Flow<List<FixedExpenseEntity>>

    @Query("DELETE FROM fixed_expense WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}

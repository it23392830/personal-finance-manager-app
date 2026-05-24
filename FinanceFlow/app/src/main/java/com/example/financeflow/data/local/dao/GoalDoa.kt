package com.example.financeflow.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllGoalsFlowForUser(userId: String): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE id = :goalId AND userId = :userId")
    suspend fun getGoalByIdForUser(goalId: String, userId: String): GoalEntity?

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: String): GoalEntity?

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: String)

    @Query("DELETE FROM goals WHERE userId = :userId")
    suspend fun deleteGoalsForUser(userId: String)
}
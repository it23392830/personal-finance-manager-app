package com.example.financeflow.data.local.dao

import androidx.room.*
import com.example.financeflow.data.local.entity.GoalAllocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalAllocationDao {
    @Query("SELECT * FROM goal_allocations WHERE goalId = :goalId ORDER BY allocatedAt DESC")
    fun getAllocationsFlowForGoal(goalId: String): Flow<List<GoalAllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: GoalAllocationEntity)

    @Query("DELETE FROM goal_allocations WHERE id = :id")
    suspend fun deleteAllocationById(id: String)
}
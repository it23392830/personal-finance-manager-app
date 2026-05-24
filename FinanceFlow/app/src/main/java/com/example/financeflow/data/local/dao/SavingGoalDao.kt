package com.example.financeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financeflow.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room operations for cached saving goals.
 */
@Dao
interface SavingGoalDao {

    /** Inserts or replaces one saving goal. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingGoalEntity)

    /** Inserts or replaces many saving goals during Firestore sync. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<SavingGoalEntity>)

    /** Streams current user's goals from local storage. */
    @Query("SELECT * FROM saving_goals WHERE userId = :userId ORDER BY goalName ASC")
    fun getGoalsForUser(userId: String): Flow<List<SavingGoalEntity>>

    /** Fetches current user's goals once. */
    @Query("SELECT * FROM saving_goals WHERE userId = :userId ORDER BY goalName ASC")
    suspend fun getGoalsSnapshotForUser(userId: String): List<SavingGoalEntity>

    /** Deletes one saving goal by id. */
    @Query("DELETE FROM saving_goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    /** Clears cached goals for a user before replacing with a remote snapshot. */
    @Query("DELETE FROM saving_goals WHERE userId = :userId")
    suspend fun deleteGoalsForUser(userId: String)
}

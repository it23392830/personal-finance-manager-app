package com.example.financeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financeflow.data.local.entity.SavingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room operations for cached saving records.
 */
@Dao
interface SavingDao {

    /** Inserts or replaces one saving record. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: SavingEntity)

    /** Inserts or replaces many saving records during Firestore sync. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavings(savings: List<SavingEntity>)

    /** Streams current user's savings from local storage, newest first. */
    @Query("SELECT * FROM savings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSavingsForUser(userId: String): Flow<List<SavingEntity>>

    /** Fetches current user's savings once, newest first. */
    @Query("SELECT * FROM savings WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getSavingsSnapshotForUser(userId: String): List<SavingEntity>

    /** Deletes one saving record by id. */
    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteSavingById(id: String)

    /** Clears cached savings for a user before replacing with a remote snapshot. */
    @Query("DELETE FROM savings WHERE userId = :userId")
    suspend fun deleteSavingsForUser(userId: String)
}

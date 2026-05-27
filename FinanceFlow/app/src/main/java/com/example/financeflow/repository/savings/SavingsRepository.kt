package com.example.financeflow.repository.savings

import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.data.local.entity.SavingEntity
import com.example.financeflow.data.local.entity.SavingGoalEntity
import com.example.financeflow.data.remote.SavingsFirestoreService
import com.example.financeflow.model.Saving
import com.example.financeflow.model.SavingGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Result wrapper used by SavingsRepository operations.
 */
sealed class SavingsResult<out T> {
    data class Success<out T>(val data: T) : SavingsResult<T>()
    data class Error(val exception: Exception) : SavingsResult<Nothing>()
}

/**
 * Repository boundary for the Savings module.
 *
 * Room is the local source of truth for the UI. Firestore remains the remote
 * source, and realtime snapshots are synced into Room by this repository.
 */
@Singleton
class SavingsRepository(
    private val service: SavingsFirestoreService,
    private val savingDao: SavingDao,
    private val savingGoalDao: SavingGoalDao
) {
    /** Converts a cached saving entity to the app domain model. */
    private fun savingEntityToDomain(entity: SavingEntity): Saving {
        return Saving(
            id = entity.id,
            goalId = entity.goalId,
            amountSaved = entity.amountSaved,
            totalIncome = entity.totalIncome,
            savingRate = entity.savingRate,
            month = entity.month,
            date = entity.date,
            goalName = entity.goalName,
            description = entity.description,
            targetAmount = entity.targetAmount,
            createdAt = entity.createdAt
        )
    }

    /** Converts a domain saving record to a Room cache entity. */
    private fun savingToEntity(saving: Saving, userId: String): SavingEntity {
        return SavingEntity(
            id = saving.id,
            userId = userId,
            goalId = saving.goalId,
            amountSaved = saving.amountSaved,
            totalIncome = saving.totalIncome,
            savingRate = calculateSavingRate(saving.amountSaved, saving.totalIncome),
            month = saving.month,
            date = saving.date,
            goalName = saving.goalName,
            description = saving.description,
            targetAmount = saving.targetAmount,
            createdAt = saving.createdAt
        )
    }

    /** Converts a cached goal entity to the app domain model. */
    private fun goalEntityToDomain(entity: SavingGoalEntity): SavingGoal {
        return SavingGoal(
            id = entity.id,
            goalName = entity.goalName,
            currentAmount = entity.currentAmount,
            targetAmount = entity.targetAmount,
            progress = entity.progress
        )
    }

    /** Converts a domain saving goal to a Room cache entity. */
    private fun goalToEntity(goal: SavingGoal, userId: String): SavingGoalEntity {
        return SavingGoalEntity(
            id = goal.id,
            userId = userId,
            goalName = goal.goalName,
            currentAmount = goal.currentAmount,
            targetAmount = goal.targetAmount,
            progress = calculateGoalProgress(goal)
        )
    }

    /** Adds one saving remotely, then stores the saved record in Room. */
    suspend fun addSaving(saving: Saving): SavingsResult<String> = runSavingRequest {
        val userId = service.currentUserId()
        val id = service.addSaving(saving)
        savingDao.insertSaving(savingToEntity(saving.copy(id = id), userId))
        id
    }

    /** Fetches saving records once from Room. */
    suspend fun getSavings(): SavingsResult<List<Saving>> = runSavingRequest {
        val userId = service.currentUserId()
        savingDao.getSavingsSnapshotForUser(userId).map { savingEntityToDomain(it) }
    }

    /** Streams saving records from Room so Compose gets instant local updates. */
    fun getSavingsFlow(): Flow<List<Saving>> {
        val userId = runCatching { service.currentUserId() }.getOrNull() ?: return flowOf(emptyList())
        return savingDao.getSavingsForUser(userId).map { list ->
            list.map { savingEntityToDomain(it) }
        }
    }

    /** Updates Room first, then writes the same saving record to Firestore. */
    suspend fun updateSaving(saving: Saving): SavingsResult<Unit> = runSavingRequest {
        val userId = service.currentUserId()
        savingDao.insertSaving(savingToEntity(saving, userId))
        service.updateSaving(saving)
    }

    /** Deletes one saving record from Room and Firestore. */
    suspend fun deleteSaving(savingId: String): SavingsResult<Unit> = runSavingRequest {
        savingDao.deleteSavingById(savingId)
        service.deleteSaving(savingId)
    }

    /** Adds one goal remotely, then stores the saved goal in Room. */
    suspend fun addGoal(goal: SavingGoal): SavingsResult<String> = runSavingRequest {
        val userId = service.currentUserId()
        val id = service.addGoal(goal)
        savingGoalDao.insertGoal(goalToEntity(goal.copy(id = id), userId))
        id
    }

    /** Fetches saving goals once from Room. */
    suspend fun getGoals(): SavingsResult<List<SavingGoal>> = runSavingRequest {
        val userId = service.currentUserId()
        savingGoalDao.getGoalsSnapshotForUser(userId).map { goalEntityToDomain(it) }
    }

    /** Streams saving goals from Room. */
    fun getGoalsFlow(): Flow<List<SavingGoal>> {
        val userId = runCatching { service.currentUserId() }.getOrNull() ?: return flowOf(emptyList())
        return savingGoalDao.getGoalsForUser(userId).map { list ->
            list.map { goalEntityToDomain(it) }
        }
    }

    /** Updates Room first, then writes the same goal to Firestore. */
    suspend fun updateGoal(goal: SavingGoal): SavingsResult<Unit> = runSavingRequest {
        val userId = service.currentUserId()
        savingGoalDao.insertGoal(goalToEntity(goal, userId))
        service.updateGoal(goal)
    }

    /** Deletes one saving goal from Room and Firestore. */
    suspend fun deleteGoal(goalId: String): SavingsResult<Unit> = runSavingRequest {
        savingGoalDao.deleteGoalById(goalId)
        service.deleteGoal(goalId)
    }

    /** Collects Firestore saving snapshots and replaces the Room cache. */
    suspend fun syncSavingsFromFirestore() {
        val userId = service.currentUserId()
        service.getSavingsFlow().collect { remoteSavings ->
            savingDao.deleteSavingsForUser(userId)
            savingDao.insertSavings(remoteSavings.map { savingToEntity(it, userId) })
        }
    }

    /** Collects Firestore goal snapshots and replaces the Room cache. */
    suspend fun syncGoalsFromFirestore() {
        val userId = service.currentUserId()
        service.getGoalsFlow().collect { remoteGoals ->
            savingGoalDao.deleteGoalsForUser(userId)
            savingGoalDao.insertGoals(remoteGoals.map { goalToEntity(it, userId) })
        }
    }

    /** Converts Firebase and Room exceptions into a stable repository result. */
    private suspend fun <T> runSavingRequest(block: suspend () -> T): SavingsResult<T> {
        return try {
            SavingsResult.Success(block())
        } catch (error: Exception) {
            SavingsResult.Error(error)
        }
    }

    /** Calculates saving rate as a percentage. */
    private fun calculateSavingRate(amountSaved: Double, totalIncome: Double): Double {
        return if (totalIncome > 0.0) (amountSaved / totalIncome) * 100.0 else 0.0
    }

    /** Calculates goal progress as a 0f..1f fraction. */
    private fun calculateGoalProgress(goal: SavingGoal): Float {
        return if (goal.targetAmount > 0.0) {
            (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
        } else {
            goal.progress.coerceIn(0f, 1f)
        }
    }
}

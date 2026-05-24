package com.example.financeflow.repository.goal

import com.example.financeflow.data.local.dao.GoalAllocationDao
import com.example.financeflow.data.local.dao.GoalDao
import com.example.financeflow.data.local.entity.GoalAllocationEntity
import com.example.financeflow.data.local.entity.GoalEntity
import com.example.financeflow.data.remote.GoalFirebaseService
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val allocationDao: GoalAllocationDao,
    private val firebase: GoalFirebaseService
) {

    // --- mapping helpers ------------------------------------------------
    private fun entityToDomain(e: GoalEntity): Goal = Goal(
        id = e.id,
        userId = e.userId,
        title = e.title,
        description = e.description,
        category = e.category,
        targetAmount = e.targetAmount,
        currentSavedAmount = e.currentSavedAmount,
        currency = e.currency,
        deadlineDate = if (e.deadlineDate > 0) Timestamp(Date(e.deadlineDate)) else Timestamp.now(),
        createdAt = if (e.createdAt > 0) Timestamp(Date(e.createdAt)) else Timestamp.now(),
        updatedAt = if (e.updatedAt > 0) Timestamp(Date(e.updatedAt)) else Timestamp.now(),
        isCompleted = e.isCompleted,
        unlockedBadges = if (e.unlockedBadgesCsv.isBlank()) emptyList() else e.unlockedBadgesCsv.split(',').filter { it.isNotBlank() }
    )

    private fun domainToEntity(g: Goal): GoalEntity = GoalEntity(
        id = g.id,
        userId = g.userId,
        title = g.title,
        description = g.description,
        category = g.category,
        targetAmount = g.targetAmount,
        currentSavedAmount = g.currentSavedAmount,
        currency = g.currency,
        deadlineDate = g.deadlineDate.toDate().time,
        createdAt = g.createdAt.toDate().time,
        updatedAt = g.updatedAt.toDate().time,
        isCompleted = g.isCompleted,
        unlockedBadgesCsv = g.unlockedBadges.joinToString(",")
    )

    private fun allocationEntityToDomain(e: GoalAllocationEntity): GoalAllocation = GoalAllocation(
        id = e.id,
        goalId = e.goalId,
        amount = e.amount,
        monthlyTarget = e.monthlyTarget,
        monthYear = e.monthYear,
        note = e.note,
        allocatedAt = Timestamp(Date(e.allocatedAt))
    )

    private fun allocationDomainToEntity(a: GoalAllocation): GoalAllocationEntity = GoalAllocationEntity(
        id = a.id,
        userId = a.goalId.substringBefore("_"), // placeholder, will be overwritten by repository
        goalId = a.goalId,
        amount = a.amount,
        monthlyTarget = a.monthlyTarget,
        monthYear = a.monthYear,
        note = a.note,
        allocatedAt = a.allocatedAt.toDate().time
    )

    // --- Public API -----------------------------------------------------

    fun observeGoals(): Flow<Result<List<Goal>>> = flow {
        val uid = firebase.currentUserId() ?: throw IllegalStateException("No authenticated user")
        emitAll(goalDao.getAllGoalsFlowForUser(uid).map { list -> Result.success(list.map { entityToDomain(it) }) })
    }.catch { e -> emit(Result.failure(e)) }

    fun observeGoal(goalId: String): Flow<Result<Goal?>> = flow {
        val uid = firebase.currentUserId() ?: throw IllegalStateException("No authenticated user")
        emitAll(goalDao.getAllGoalsFlowForUser(uid).map { list -> Result.success(list.find { it.id == goalId }?.let { entityToDomain(it) }) })
    }.catch { e -> emit(Result.failure(e)) }

    fun observeAllocations(goalId: String): Flow<Result<List<GoalAllocation>>> = flow {
        emitAll(allocationDao.getAllocationsFlowForGoal(goalId).map { list -> Result.success(list.map { allocationEntityToDomain(it) }) })
    }.catch { e -> emit(Result.failure(e)) }

    suspend fun createGoal(goal: Goal): Result<String> = try {
        val id = if (goal.id.isBlank()) UUID.randomUUID().toString() else goal.id
        val uid = firebase.currentUserId() ?: return Result.failure(IllegalStateException("No authenticated user"))
        val now = Timestamp.now()
        val withId = goal.copy(id = id, userId = uid, createdAt = now, updatedAt = now)
        // persist locally
        goalDao.insertGoal(domainToEntity(withId))
        // persist remotely
        val remoteId = firebase.addGoal(withId, id)
        Result.success(remoteId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateGoal(goal: Goal): Result<Unit> = try {
        val uid = firebase.currentUserId() ?: return Result.failure(IllegalStateException("No authenticated user"))
        val updated = goal.copy(userId = uid, updatedAt = Timestamp.now())
        goalDao.insertGoal(domainToEntity(updated))
        firebase.updateGoal(updated)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteGoal(goalId: String): Result<Unit> = try {
        val uid = firebase.currentUserId() ?: return Result.failure(IllegalStateException("No authenticated user"))
        // remove local
        val local = goalDao.getGoalByIdForUser(goalId, uid)
        if (local != null) goalDao.deleteGoalById(goalId)
        // remove allocations local
        allocationDao.getAllocationsFlowForGoal(goalId).first().forEach { allocationDao.deleteAllocationById(it.id) }
        // remote
        firebase.deleteGoal(goalId)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun addAllocation(goalId: String, amount: Double, monthlyTarget: Double, monthYear: String, note: String): Result<Unit> = try {
        val uid = firebase.currentUserId() ?: return Result.failure(IllegalStateException("No authenticated user"))
        val id = UUID.randomUUID().toString()
        val allocation = GoalAllocation(id = id, goalId = goalId, amount = amount, monthlyTarget = monthlyTarget, monthYear = monthYear, note = note)
        // persist locally
        val ent = GoalAllocationEntity(
            id = id,
            userId = uid,
            goalId = goalId,
            amount = amount,
            monthlyTarget = monthlyTarget,
            monthYear = monthYear,
            note = note,
            allocatedAt = allocation.allocatedAt.toDate().time
        )
        allocationDao.insertAllocation(ent)
        // persist remotely
        firebase.addAllocation(goalId, allocation, id)

        // recompute goal saved amount
        val allocations = allocationDao.getAllocationsFlowForGoal(goalId).first()
        val totalSaved = allocations.sumOf { it.amount }
        val gLocal = goalDao.getGoalById(goalId)
        if (gLocal != null) {
            val updated = gLocal.copy(currentSavedAmount = totalSaved, isCompleted = totalSaved >= gLocal.targetAmount)
            goalDao.insertGoal(updated)
            // update remote goal as well
            try { firebase.updateGoal(entityToDomain(updated)) } catch (_: Exception) {}
        }

        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun syncFromFirestore(): Result<Unit> = try {
        val uid = firebase.currentUserId() ?: return Result.failure(IllegalStateException("No authenticated user"))
        val remote = firebase.getAllGoals()
        // replace local
        goalDao.deleteGoalsForUser(uid)
        remote.forEach { goalDao.insertGoal(domainToEntity(it)) }
        // sync allocations (simple approach: fetch per-goal)
        remote.forEach { goal ->
            val allocs = firebase.getAllocations(goal.id)
            // replace local allocations for this goal
            allocationDao.getAllocationsFlowForGoal(goal.id).first().forEach { allocationDao.deleteAllocationById(it.id) }
            allocs.forEach { a -> allocationDao.insertAllocation(GoalAllocationEntity(
                id = a.id,
                userId = goal.userId,
                goalId = a.goalId,
                amount = a.amount,
                monthlyTarget = a.monthlyTarget,
                monthYear = a.monthYear,
                note = a.note,
                allocatedAt = a.allocatedAt.toDate().time
            )) }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveFcmToken(token: String): Result<Unit> = runCatching {
        firebase.updateFcmToken(token)
    }
}
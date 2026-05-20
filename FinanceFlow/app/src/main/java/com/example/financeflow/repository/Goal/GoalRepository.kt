package com.example.financeflow.repository.goal

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.model.toGoal
import com.example.financeflow.model.toGoalAllocation
import com.example.financeflow.model.toMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val userId: String?
        get() = auth.currentUser?.uid

    private fun goalsCollection() = userId?.let {
        firestore.collection("users").document(it).collection("goals")
    }

    private fun allocationsCollection(goalId: String) =
        goalsCollection()?.document(goalId)?.collection("allocations")

    // ─── Goals CRUD ──────────────────────────────────────────────────────────

    fun observeGoals(): Flow<Result<List<Goal>>> {
        val collection = goalsCollection() ?: return flowOf(Result.failure(Exception("User not authenticated")))
        
        return callbackFlow {
            val listener = collection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val goals = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.toGoal(doc.id)
                    } ?: emptyList()
                    trySend(Result.success(goals))
                }
            awaitClose { listener.remove() }
        }
    }

    fun observeGoal(goalId: String): Flow<Result<Goal?>> {
        val collection = goalsCollection() ?: return flowOf(Result.failure(Exception("User not authenticated")))

        return callbackFlow {
            val listener = collection.document(goalId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val goal = snapshot?.data?.toGoal(snapshot.id)
                    trySend(Result.success(goal))
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun createGoal(goal: Goal): Result<String> = runCatching {
        val uid = userId ?: throw Exception("User not authenticated")
        val collection = goalsCollection() ?: throw Exception("Firestore reference failed")
        val doc = collection.document()
        val newGoal = goal.copy(
            id = doc.id,
            userId = uid,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        doc.set(newGoal.toMap()).await()
        doc.id
    }

    suspend fun updateGoal(goal: Goal): Result<Unit> = runCatching {
        val collection = goalsCollection() ?: throw Exception("User not authenticated")
        val updatedGoal = goal.copy(updatedAt = Timestamp.now())
        collection.document(goal.id).set(updatedGoal.toMap()).await()
    }

    suspend fun deleteGoal(goalId: String): Result<Unit> = runCatching {
        val collection = goalsCollection() ?: throw Exception("User not authenticated")
        collection.document(goalId).delete().await()
    }

    // ─── Allocations ─────────────────────────────────────────────────────────

    suspend fun addAllocation(
        goalId: String,
        amount: Double,
        note: String = ""
    ): Result<List<GoalBadge>> = runCatching {
        val collection = goalsCollection() ?: throw Exception("User not authenticated")
        val allocations = allocationsCollection(goalId) ?: throw Exception("Allocation reference failed")
        
        val goalRef = collection.document(goalId)
        val allocationRef = allocations.document()

        var newBadges: List<GoalBadge> = emptyList()

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(goalRef)
            val currentGoal = snapshot.data?.toGoal(snapshot.id)
                ?: throw Exception("Goal not found")

            val newSaved = currentGoal.currentSavedAmount + amount
            val updatedGoal = currentGoal.copy(
                currentSavedAmount = newSaved,
                isCompleted = newSaved >= currentGoal.targetAmount,
                updatedAt = Timestamp.now()
            )

            newBadges = GoalBadge.checkNewBadges(updatedGoal)
            val allBadges = updatedGoal.unlockedBadges + newBadges.map { it.id }
            val finalGoal = updatedGoal.copy(unlockedBadges = allBadges)

            val allocation = GoalAllocation(
                id = allocationRef.id,
                goalId = goalId,
                amount = amount,
                note = note,
                allocatedAt = Timestamp.now()
            )
            transaction.set(allocationRef, allocation.toMap())
            transaction.set(goalRef, finalGoal.toMap())
        }.await()

        newBadges
    }

    fun observeAllocations(goalId: String): Flow<Result<List<GoalAllocation>>> {
        val collection = allocationsCollection(goalId) ?: return flowOf(Result.failure(Exception("User not authenticated")))

        return callbackFlow {
            val listener = collection
                .orderBy("allocatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val allocations = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.toGoalAllocation(doc.id)
                    } ?: emptyList()
                    trySend(Result.success(allocations))
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun saveFcmToken(token: String): Result<Unit> = runCatching {
        val uid = userId ?: throw Exception("User not authenticated")
        firestore.collection("users").document(uid)
            .update("fcmToken", token)
            .await()
    }
}

package com.example.financeflow.data.remote

import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalFirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val uid get() = auth.currentUser?.uid ?: error("User not logged in")

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun addGoal(goal: Goal, id: String? = null): String {
        return if (!id.isNullOrBlank()) {
            firestore.collection("users").document(uid).collection("goals").document(id)
                .set(goal).await()
            id
        } else {
            val ref = firestore.collection("users").document(uid).collection("goals").add(goal).await()
            ref.id
        }
    }

    suspend fun updateGoal(goal: Goal) {
        firestore.collection("users").document(uid).collection("goals").document(goal.id).set(goal).await()
    }

    suspend fun deleteGoal(id: String) {
        firestore.collection("users").document(uid).collection("goals").document(id).delete().await()
    }

    suspend fun getAllGoals(): List<Goal> {
        val snap = firestore.collection("users").document(uid).collection("goals").get().await()
        return snap.documents.mapNotNull { it.toObject(Goal::class.java)?.copy(id = it.id) }
    }

    // Allocations: nested collection users/{uid}/goals/{goalId}/allocations
    suspend fun addAllocation(goalId: String, allocation: GoalAllocation, id: String? = null): String {
        val coll = firestore.collection("users").document(uid).collection("goals").document(goalId).collection("allocations")
        return if (!id.isNullOrBlank()) {
            coll.document(id).set(allocation).await(); id
        } else {
            val r = coll.add(allocation).await(); r.id
        }
    }

    suspend fun getAllocations(goalId: String): List<GoalAllocation> {
        val snap = firestore.collection("users").document(uid).collection("goals").document(goalId).collection("allocations").get().await()
        return snap.documents.mapNotNull { it.toObject(GoalAllocation::class.java)?.copy(id = it.id) }
    }

    fun observeAllocationsFlow(goalId: String): Flow<List<GoalAllocation>> = callbackFlow {
        val listener = firestore.collection("users").document(uid).collection("goals").document(goalId)
            .collection("allocations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(GoalAllocation::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateFcmToken(token: String) {
        firestore.collection("users").document(uid).update("fcmToken", token).await()
    }
}

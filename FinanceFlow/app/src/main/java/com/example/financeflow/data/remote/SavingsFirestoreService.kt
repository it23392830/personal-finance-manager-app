package com.example.financeflow.data.remote

import com.example.financeflow.model.Saving
import com.example.financeflow.model.SavingGoal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

/**
 * Firestore API for the Savings module.
 *
 * Data path:
 * users/{userId}/savings/{savingId}
 * users/{userId}/savingGoals/{goalId}
 */
@Singleton
class SavingsFirestoreService(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    /** Returns the logged-in Firebase user id or throws when the user is missing. */
    fun currentUserId(): String {
        return auth.currentUser?.uid ?: error("User not logged in")
    }

    /** Collection reference for the current user's saving records. */
    private fun savingsCollection() =
        firestore.collection("users").document(currentUserId()).collection("savings")

    /** Collection reference for the current user's saving goals. */
    private fun goalsCollection() =
        firestore.collection("users").document(currentUserId()).collection("savingGoals")

    /** Adds one saving record and returns its Firestore id. */
    suspend fun addSaving(saving: Saving): String {
        val id = if (saving.id.isBlank()) savingsCollection().document().id else saving.id
        val data = saving.copy(id = id)
        savingsCollection().document(id).set(data).await()
        return id
    }

    /** Fetches saving records once, newest first. */
    suspend fun getSavings(): List<Saving> {
        return savingsCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(Saving::class.java)?.copy(id = document.id)
            }
    }

    /** Streams saving records in realtime, newest first. */
    fun getSavingsFlow(): Flow<List<Saving>> = callbackFlow {
        val listener = savingsCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val savings = snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(Saving::class.java)?.copy(id = document.id)
                }
                trySend(savings)
            }

        awaitClose { listener.remove() }
    }

    /** Updates one saving record. */
    suspend fun updateSaving(saving: Saving) {
        savingsCollection().document(saving.id)
            .set(saving)
            .await()
    }

    /** Deletes one saving record. */
    suspend fun deleteSaving(savingId: String) {
        savingsCollection().document(savingId).delete().await()
    }

    /** Adds one savings goal and returns its Firestore id. */
    suspend fun addGoal(goal: SavingGoal): String {
        val id = if (goal.id.isBlank()) goalsCollection().document().id else goal.id
        goalsCollection().document(id).set(goal.copy(id = id, progress = calculateGoalProgress(goal))).await()
        return id
    }

    /** Fetches saving goals once. */
    suspend fun getGoals(): List<SavingGoal> {
        return goalsCollection()
            .orderBy("goalName", Query.Direction.ASCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(SavingGoal::class.java)?.copy(id = document.id)
            }
    }

    /** Streams savings goals in realtime. */
    fun getGoalsFlow(): Flow<List<SavingGoal>> = callbackFlow {
        val listener = goalsCollection()
            .orderBy("goalName", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val goals = snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(SavingGoal::class.java)?.copy(id = document.id)
                }
                trySend(goals)
            }

        awaitClose { listener.remove() }
    }

    /** Updates one savings goal and recalculates progress automatically. */
    suspend fun updateGoal(goal: SavingGoal) {
        goalsCollection().document(goal.id)
            .set(goal.copy(progress = calculateGoalProgress(goal)))
            .await()
    }

    /** Deletes one savings goal. */
    suspend fun deleteGoal(goalId: String) {
        goalsCollection().document(goalId).delete().await()
    }



    /** Calculates goal progress as a 0f..1f fraction. */
    private fun calculateGoalProgress(goal: SavingGoal): Float {
        return if (goal.targetAmount > 0.0) {
            (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
        } else {
            0f
        }
    }
}

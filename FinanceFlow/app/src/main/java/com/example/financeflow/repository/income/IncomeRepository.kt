package com.example.financeflow.repository.income

import com.example.financeflow.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles all Firestore CRUD operations for Income entries.
 *
 * Collection path: users/{uid}/income
 */
@Singleton
class IncomeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val uid get() = auth.currentUser?.uid ?: error("User not logged in")

    /** Returns a real-time [Flow] of all incomes for the authenticated user. */
    fun getIncomesFlow(): Flow<List<Income>> = callbackFlow {
        val listener = firestore
            .collection("users").document(uid)
            .collection("income")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Income::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Returns a real-time [Flow] of incomes filtered by [year] and [month] (1-based). */
    fun getIncomesForMonthFlow(year: Int, month: Int): Flow<List<Income>> = callbackFlow {
        // Build start/end timestamps for the given month
        val start = Calendar.getInstance().apply {
            set(year, month - 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val end = Calendar.getInstance().apply {
            set(year, month - 1, 1, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }.time

        val listener = firestore
            .collection("users").document(uid)
            .collection("income")
            .whereGreaterThanOrEqualTo("date", com.google.firebase.Timestamp(start))
            .whereLessThanOrEqualTo("date", com.google.firebase.Timestamp(end))
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Income::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Adds a new income entry to Firestore. Returns the generated document ID. */
    suspend fun addIncome(income: Income): String {
        val ref = firestore
            .collection("users").document(uid)
            .collection("income")
            .add(income.copy(userId = uid))
            .await()
        return ref.id
    }

    /** Updates an existing income entry (matched by [income.id]). */
    suspend fun updateIncome(income: Income) {
        firestore
            .collection("users").document(uid)
            .collection("income")
            .document(income.id)
            .set(income)
            .await()
    }

    /** Permanently deletes the income entry with the given [incomeId]. */
    suspend fun deleteIncome(incomeId: String) {
        firestore
            .collection("users").document(uid)
            .collection("income")
            .document(incomeId)
            .delete()
            .await()
    }
}

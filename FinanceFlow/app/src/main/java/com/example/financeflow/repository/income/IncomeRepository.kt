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
        // Listen to all incomes ordered by date; filter out future-dated
        // transactions client-side so newly-added documents appear immediately
        val listener = firestore
            .collection("users").document(uid)
            .collection("income")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val now = java.util.Date()
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Income::class.java)?.copy(id = doc.id)
                }?.filter { inc ->
                    // Exclude future-dated transactions
                    inc.date.toDate().before(now) || inc.date.toDate().time == now.time
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

        var end = Calendar.getInstance().apply {
            set(year, month - 1, 1, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }.time

        // Do not include future dates
        val today = java.util.Date()
        if (end.after(today)) end = today

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

    /** Real-time list of transactions for a specific month. Includes everything up to the end of that month. */
    fun getCurrentMonthTransactionsFlow(year: Int, month: Int): Flow<List<Income>> = callbackFlow {
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
                // Filter out future dates on the client side to handle items added exactly "now"
                val now = java.util.Date()
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Income::class.java)?.copy(id = doc.id)
                }?.filter { it.date.toDate().before(now) || it.date.toDate().time == now.time } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Real-time list of custom income sources for the user. */
    fun getIncomeSourcesFlow(): Flow<List<String>> = callbackFlow {
        val listener = firestore
            .collection("users").document(uid)
            .collection("incomeSources")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.getString("sourceName") ?: doc.id
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Adds a new custom income source under users/{uid}/incomeSources. Returns generated id. */
    suspend fun addIncomeSource(sourceName: String): String {
        val data = mapOf("sourceName" to sourceName)
        val ref = firestore
            .collection("users").document(uid)
            .collection("incomeSources")
            .add(data)
            .await()
        return ref.id
    }

    /** Returns available months (year, month) derived from existing income documents up to today. */
    suspend fun getAvailableMonths(): List<Pair<Int, Int>> {
        val today = java.util.Date()
        val snapshot = firestore
            .collection("users").document(uid)
            .collection("income")
            .whereLessThanOrEqualTo("date", com.google.firebase.Timestamp(today))
            .get()
            .await()

        val set = mutableSetOf<Pair<Int, Int>>()
        snapshot.documents.forEach { doc ->
            val inc = doc.toObject(Income::class.java) ?: return@forEach
            val cal = Calendar.getInstance().apply { time = inc.date.toDate() }
            val pair = cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
            set.add(pair)
        }

        val list = set.toList().sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })

        // If empty, return current month only
        if (list.isEmpty()) {
            val c = Calendar.getInstance()
            return listOf(c.get(Calendar.YEAR) to (c.get(Calendar.MONTH) + 1))
        }

        return list
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
        // Use update() with explicit fields to avoid replacing metadata unintentionally
        val data = mapOf(
            "amount" to income.amount,
            "currency" to income.currency,
            "source" to income.source,
            "description" to income.description,
            "date" to income.date,
            // Do not overwrite userId or createdAt unless explicitly intended
        )

        firestore
            .collection("users").document(uid)
            .collection("income")
            .document(income.id)
            .update(data)
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

    /** Fetches a single Income by id (one-shot). */
    suspend fun getIncomeById(incomeId: String): Income? {
        val doc = firestore
            .collection("users").document(uid)
            .collection("income")
            .document(incomeId)
            .get()
            .await()

        return doc?.toObject(Income::class.java)?.copy(id = doc.id)
    }
}

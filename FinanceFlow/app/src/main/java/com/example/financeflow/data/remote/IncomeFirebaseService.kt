package com.example.financeflow.data.remote

import com.example.financeflow.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Thin wrapper around Firestore operations for incomes. Keeps Firestore-specific
 * code isolated so higher-level repository can coordinate local+remote sync.
 */
@Singleton
class IncomeFirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val uid get() = auth.currentUser?.uid ?: error("User not logged in")

    /**
     * Returns the current authenticated user's uid or null if not logged in.
     */
    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun addIncome(income: Income, id: String? = null): String {
        return if (!id.isNullOrBlank()) {
            firestore.collection("users").document(uid).collection("incomes").document(id)
                .set(income).await()
            id
        } else {
            val ref = firestore.collection("users").document(uid).collection("incomes").add(income).await()
            ref.id
        }
    }

    suspend fun updateIncome(income: Income) {
        val data = mapOf(
            "amount" to income.amount,
            "currency" to income.currency,
            "source" to income.source,
            "description" to income.description,
            "date" to income.date
        )
        firestore.collection("users").document(uid).collection("incomes").document(income.id).update(data).await()
    }

    suspend fun deleteIncome(id: String) {
        firestore.collection("users").document(uid).collection("incomes").document(id).delete().await()
    }

    suspend fun getAllIncomes(): List<Income> {
        val snap = firestore.collection("users").document(uid).collection("incomes").get().await()
        return snap.documents.mapNotNull { it.toObject(Income::class.java)?.copy(id = it.id) }
    }

    /**
     * Real-time flow for income source names stored under users/{uid}/incomeSources
     */
    fun getIncomeSourcesFlow(): Flow<List<String>> = callbackFlow {
        val listener = firestore
            .collection("users").document(uid)
            .collection("incomeSources")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc -> doc.getString("sourceName") ?: doc.id } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addIncomeSource(sourceName: String): String {
        val data = mapOf("sourceName" to sourceName)
        val ref = firestore.collection("users").document(uid).collection("incomeSources").add(data).await()
        return ref.id
    }

    /**
     * Reads exchange rates from a config document or falls back to defaults.
     */
    suspend fun getExchangeRates(): Map<String, Double> {
        val defaults = mapOf("LKR" to 1.0, "USD" to 300.0, "EUR" to 320.0, "GBP" to 370.0)
        return try {
            val doc = firestore.collection("config").document("exchangeRates").get().await()
            if (doc.exists()) {
                val usd = doc.getDouble("USD") ?: defaults["USD"]!!
                val eur = doc.getDouble("EUR") ?: defaults["EUR"]!!
                val gbp = doc.getDouble("GBP") ?: defaults["GBP"]!!
                mapOf("LKR" to 1.0, "USD" to usd, "EUR" to eur, "GBP" to gbp)
            } else defaults
        } catch (e: Exception) {
            defaults
        }
    }
}

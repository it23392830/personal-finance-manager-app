package com.example.financeflow.data.remote

import com.example.financeflow.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseFirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val uid get() = auth.currentUser?.uid ?: error("User not logged in")

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun addExpense(expense: Expense, id: String? = null): String {
        return if (!id.isNullOrBlank()) {
            firestore.collection("users").document(uid).collection("expenses").document(id)
                .set(expense).await()
            id
        } else {
            val ref = firestore.collection("users").document(uid).collection("expenses").add(expense).await()
            ref.id
        }
    }

    suspend fun updateExpense(expense: Expense) {
        firestore.collection("users").document(uid).collection("expenses").document(expense.id).set(expense).await()
    }

    suspend fun deleteExpense(id: String) {
        firestore.collection("users").document(uid).collection("expenses").document(id).delete().await()
    }

    suspend fun getAllExpenses(): List<Expense> {
        val snap = firestore.collection("users").document(uid).collection("expenses").get().await()
        return snap.documents.mapNotNull { it.toObject(Expense::class.java)?.copy(id = it.id) }
    }

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

    // Fixed expenses
    suspend fun addFixedExpense(fixed: com.example.financeflow.model.FixedExpense, id: String? = null): String {
        val docRef = firestore.collection("users").document(uid).collection("fixedExpenses")
        return if (!id.isNullOrBlank()) {
            docRef.document(id).set(fixed).await(); id
        } else {
            val r = docRef.add(fixed).await(); r.id
        }
    }

    suspend fun updateFixedExpense(fixed: com.example.financeflow.model.FixedExpense) {
        firestore.collection("users").document(uid).collection("fixedExpenses").document(fixed.id).set(fixed).await()
    }

    suspend fun deleteFixedExpense(id: String) {
        firestore.collection("users").document(uid).collection("fixedExpenses").document(id).delete().await()
    }

    suspend fun getAllFixedExpenses(): List<com.example.financeflow.model.FixedExpense> {
        val snap = firestore.collection("users").document(uid).collection("fixedExpenses").get().await()
        return snap.documents.mapNotNull { it.toObject(com.example.financeflow.model.FixedExpense::class.java)?.copy(id = it.id) }
    }

}

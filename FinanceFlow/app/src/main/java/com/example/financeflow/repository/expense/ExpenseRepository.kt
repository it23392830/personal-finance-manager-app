package com.example.financeflow.repository.expense

import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.entity.toDomainModel
import com.example.financeflow.data.local.entity.toLocalEntity
import com.example.financeflow.data.remote.FirestoreService
import com.example.financeflow.model.Expense
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) {
    private val userId: String get() = auth.currentUser?.uid ?: ""

    fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addExpense(expense: Expense) {
        val expenseWithUser = expense.copy(userId = userId)
        // Save to local Room DB first
        expenseDao.insertExpense(expenseWithUser.toLocalEntity(isSynced = false))
        
        // Try to sync with Firestore
        try {
            firestoreService.userDocument()
                .collection("expenses")
                .document(expenseWithUser.id)
                .set(expenseWithUser)
                .await()
            expenseDao.markAsSynced(expenseWithUser.id)
        } catch (e: Exception) {
            // Offline or error, will sync later
        }
    }

    suspend fun syncUnsyncedExpenses() {
        val unsynced = expenseDao.getUnsyncedExpenses()
        unsynced.forEach { entity ->
            try {
                firestoreService.userDocument()
                    .collection("expenses")
                    .document(entity.id)
                    .set(entity.toDomainModel())
                    .await()
                expenseDao.markAsSynced(entity.id)
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }
}

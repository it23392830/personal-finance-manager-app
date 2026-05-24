package com.example.financeflow.repository.notification

import com.example.financeflow.data.local.dao.IncomeDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads today's finance activity for reminder decisions.
 *
 * Income is checked from Room because that module already syncs Firestore into
 * the local cache. Expenses are checked from Firestore transactions/expenses
 * collections because the current Expenses screen does not yet have a Room
 * repository in this branch.
 */
@Singleton
class DailyActivityRepository @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /** Returns the current user's id or null when no Firebase user is logged in. */
    private fun currentUserId(): String? = auth.currentUser?.uid

    /** Calculates the start and end timestamps for the current local day. */
    private fun todayBounds(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            timeInMillis = start.timeInMillis
            add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
        return start.timeInMillis to end.timeInMillis
    }

    /** Checks whether the signed-in user has at least one income record today. */
    suspend fun hasIncomeToday(): Boolean {
        val userId = currentUserId() ?: return false
        val (start, end) = todayBounds()
        return incomeDao.countIncomeForUserBetween(userId, start, end) > 0
    }

    /** Checks whether the signed-in user has at least one expense record today. */
    suspend fun hasExpenseToday(): Boolean {
        val userId = currentUserId() ?: return false
        val (start, end) = todayBounds()
        val todayDate = dateFormatter.format(start)
        val userDocument = firestore.collection("users").document(userId)

        val transactionExpenseCount = runCatching {
            userDocument.collection("transactions")
                .whereEqualTo("type", "expense")
                .whereGreaterThanOrEqualTo("timestamp", start)
                .whereLessThanOrEqualTo("timestamp", end)
                .limit(1)
                .get()
                .await()
                .size()
        }.getOrDefault(0)

        if (transactionExpenseCount > 0) return true

        val expenseDateCount = runCatching {
            userDocument.collection("expenses")
                .whereEqualTo("date", todayDate)
                .limit(1)
                .get()
                .await()
                .size()
        }.getOrDefault(0)

        if (expenseDateCount > 0) return true

        return runCatching {
            userDocument.collection("expenses")
                .whereEqualTo("expenseDate", todayDate)
                .limit(1)
                .get()
                .await()
                .size() > 0
        }.getOrDefault(false)
    }
}

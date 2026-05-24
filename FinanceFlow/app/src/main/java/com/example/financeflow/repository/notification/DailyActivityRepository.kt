package com.example.financeflow.repository.notification

import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.remote.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads today's finance activity for reminder decisions.
 *
 * Income is checked from Room because that module already syncs Firestore into
 * the local cache. Expenses are checked through FirestoreService so the same
 * LocalDate conversion rules are reused across reminders and streak logic.
 */
@Singleton
class DailyActivityRepository @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    /** Returns the current user's id or null when no Firebase user is logged in. */
    private fun currentUserId(): String? = auth.currentUser?.uid

    /** Calculates the start and end timestamps for the current local day. */
    private fun todayBounds(): Pair<Long, Long> {
        val startOfDay = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now(zoneId)
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli() - 1
        return startOfDay to endOfDay
    }

    /** Checks whether the signed-in user has at least one income record today. */
    suspend fun hasIncomeToday(): Boolean {
        val userId = currentUserId() ?: return false
        val (start, end) = todayBounds()
        return incomeDao.countIncomeForUserBetween(userId, start, end) > 0
    }

    /** Checks whether the signed-in user has at least one expense record today. */
    suspend fun hasExpenseToday(): Boolean {
        if (currentUserId() == null) return false
        return firestoreService.hasExpenseOnDate(LocalDate.now(zoneId))
    }
}

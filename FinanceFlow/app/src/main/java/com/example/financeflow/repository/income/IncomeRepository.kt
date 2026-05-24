package com.example.financeflow.repository.income

import com.example.financeflow.data.local.dao.IncomeDao
import com.example.financeflow.data.local.entity.IncomeEntity
import com.example.financeflow.data.remote.IncomeFirebaseService
import com.example.financeflow.model.Income
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that synchronizes Income records between local Room database and
 * Firebase Firestore. Local data (Room) is used as the source of truth for
 * UI flows and is synchronized with Firestore in the background.
 */
@Singleton
class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firebaseService: IncomeFirebaseService
) {

    // --- Mapping helpers -------------------------------------------------
    private fun entityToDomain(e: IncomeEntity): Income = Income(
        id = e.id,
        userId = e.userId,
        amount = e.amount,
        currency = e.currency,
        source = e.source,
        description = e.description,
        date = Timestamp(Date(e.date)),
        createdAt = Timestamp(Date(e.createdAt))
    )

    private fun domainToEntity(i: Income): IncomeEntity = IncomeEntity(
        id = i.id,
        userId = i.userId,
        source = i.source,
        amount = i.amount,
        currency = i.currency,
        description = i.description,
        notes = "",
        date = i.date.toDate().time,
        createdAt = i.createdAt.toDate().time
    )

    // --- Public APIs -----------------------------------------------------

    /**
     * Returns a Flow backed by Room. The UI should collect this for fast local
     * updates. Callers can invoke [syncFromFirestore] to refresh local cache.
     */
    fun getIncomesFlow(): Flow<List<Income>> {
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        return incomeDao.getAllIncomeFlowForUser(uid).map { list -> list.map { entityToDomain(it) } }
    }

    /** Returns incomes for a month using local DB (start/end are month boundaries). */
    fun getIncomesForMonthFlow(year: Int, month: Int): Flow<List<Income>> {
        val start = Calendar.getInstance().apply { set(year, month - 1, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val endCal = Calendar.getInstance().apply { set(year, month - 1, 1, 23, 59, 59); set(Calendar.MILLISECOND, 999); add(Calendar.MONTH, 1); add(Calendar.DAY_OF_MONTH, -1) }
        val end = endCal.timeInMillis
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        return incomeDao.getIncomesBetweenFlowForUser(start, end, uid).map { list -> list.map { entityToDomain(it) } }
    }

    /** Returns current-month transactions flow (local DB). */
    fun getCurrentMonthTransactionsFlow(year: Int, month: Int): Flow<List<Income>> = getIncomesForMonthFlow(year, month)

    /** Real-time list of custom income sources (proxied to firebase service). */
    fun getIncomeSourcesFlow(): Flow<List<String>> = firebaseService.getIncomeSourcesFlow()

    /** Adds a custom income source to Firestore. */
    suspend fun addIncomeSource(name: String): String = firebaseService.addIncomeSource(name)

    /** Fetch exchange rates from remote config (fallbacks used when missing). */
    suspend fun getExchangeRates(): Map<String, Double> = firebaseService.getExchangeRates()

    /** Adds a new income: insert locally, then ensure it exists in Firestore. */
    suspend fun addIncome(income: Income): String {
        val id = if (income.id.isBlank()) UUID.randomUUID().toString() else income.id

        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        // Ensure the domain object carries the agreed id and userId
        val withId = income.copy(id = id, userId = uid)

        // Persist locally first (fast)
        val entity = domainToEntity(withId)
        incomeDao.insertIncome(entity)

        // Persist remotely (ensures remote id matches local id)
        firebaseService.addIncome(withId, id)

        return id
    }

    /** Update income locally and remotely. */
    suspend fun updateIncome(income: Income) {
        // update local DB
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        val withUser = income.copy(userId = uid)
        val entity = domainToEntity(withUser)
        incomeDao.insertIncome(entity)

        // update remote
        firebaseService.updateIncome(withUser)
    }

    /** Delete income locally and remotely. */
    suspend fun deleteIncome(incomeId: String) {
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        // Ensure we only delete the current user's record locally
        val local = incomeDao.getIncomeByIdForUser(incomeId, uid)
        if (local != null) {
            incomeDao.deleteIncomeById(incomeId)
            firebaseService.deleteIncome(incomeId)
        }
    }

    /** Get a single income (prefer local DB). */
    suspend fun getIncomeById(incomeId: String): Income? {
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        val e = incomeDao.getIncomeByIdForUser(incomeId, uid)
        return e?.let { entityToDomain(it) }
    }

    /**
     * Synchronize local Room database with the latest data from Firestore.
     * This will overwrite local records with remote values (upsert semantics).
     */
    suspend fun syncFromFirestore() {
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        val remote = firebaseService.getAllIncomes()
        // Replace local records for this user with remote snapshot
        incomeDao.deleteIncomesForUser(uid)
        remote.forEach { inc ->
            val entity = domainToEntity(inc)
            incomeDao.insertIncome(entity)
        }
    }

    /** Returns available months computed from local data. */
    suspend fun getAvailableMonths(): List<Pair<Int, Int>> {
        val uid = firebaseService.currentUserId() ?: error("No authenticated user")
        val list = incomeDao.getAllIncomeFlowForUser(uid).first()
        val set = mutableSetOf<Pair<Int, Int>>()
        list.forEach { e ->
            val cal = Calendar.getInstance().apply { timeInMillis = e.date }
            set.add(cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1))
        }
        if (set.isEmpty()) {
            val c = Calendar.getInstance()
            return listOf(c.get(Calendar.YEAR) to (c.get(Calendar.MONTH) + 1))
        }
        return set.toList().sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
    }
}

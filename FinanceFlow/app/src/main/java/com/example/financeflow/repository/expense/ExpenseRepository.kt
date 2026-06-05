package com.example.financeflow.repository.expense

import com.example.financeflow.data.local.dao.ExpenseDao
import com.example.financeflow.data.local.dao.FixedExpenseDao
import com.example.financeflow.data.local.entity.ExpenseEntity
import com.example.financeflow.data.local.entity.FixedExpenseEntity
import com.example.financeflow.data.repository.StreakRepository
import com.example.financeflow.data.remote.ExpenseFirebaseService
import com.example.financeflow.model.Expense
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao,
    private val fixedDao: FixedExpenseDao,
    private val firebase: ExpenseFirebaseService,
    private val streakRepository: StreakRepository
) {

    fun getAllForUserFlow(userId: String): Flow<List<Expense>> {
        return dao.getAllExpensesFlowForUser(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun addExpense(expense: Expense) {
        val id = if (expense.id.isBlank()) UUID.randomUUID().toString() else expense.id
        val uid = firebase.currentUserId() ?: error("No authenticated user")
        val toSave = expense.copy(id = id, userId = uid, createdAt = Timestamp.now())
        dao.insertExpense(toSave.toEntity())
        firebase.addExpense(toSave, id)
        streakRepository.addExpense(toSave.toStreakExpense())
    }

    suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense.toEntity())
        firebase.updateExpense(expense)
        streakRepository.updateStreak(forceWidgetRefresh = true)
    }

    suspend fun deleteExpense(id: String) {
        dao.deleteExpenseById(id)
        firebase.deleteExpense(id)
        streakRepository.updateStreak(forceWidgetRefresh = true)
    }

    suspend fun syncFromFirestore() {
        val uid = firebase.currentUserId() ?: return
        val remote = firebase.getAllExpenses()
        // replace local user's expenses with remote snapshot
        dao.deleteExpensesForUser(uid)
        remote.forEach { dao.insertExpense(it.toEntity()) }
    }
    
    // --- Fixed expenses support -----------------------------------------
    suspend fun addFixedExpense(fixed: com.example.financeflow.model.FixedExpense) {
        val uid = firebase.currentUserId() ?: error("No authenticated user")
        val id = if (fixed.id.isBlank()) java.util.UUID.randomUUID().toString() else fixed.id
        val withId = fixed.copy(id = id, userId = uid)
        // persist locally
        val ent = withId.toEntity()
        fixedDao.insertFixedExpense(ent)
        firebase.addFixedExpense(withId, id)
    }

    suspend fun updateFixedExpense(fixed: com.example.financeflow.model.FixedExpense) {
        fixedDao.updateFixedExpense(fixed.toEntity())
        firebase.updateFixedExpense(fixed)
    }

    suspend fun deleteFixedExpense(id: String) {
        fixedDao.deleteFixedExpense(id)
        firebase.deleteFixedExpense(id)
    }

    suspend fun syncFixedFromFirestore() {
        val uid = firebase.currentUserId() ?: return
        val remote = firebase.getAllFixedExpenses()
        android.util.Log.d("FIXED_DEBUG", "syncFixedFromFirestore: Firestore returned ${remote.size} templates for uid=$uid")
        // replace local
        fixedDao.deleteAllForUser(uid)
        remote.forEach {
            val withUid = if (it.userId.isBlank()) it.copy(userId = uid) else it
            fixedDao.insertFixedExpense(withUid.toEntity())
        }
        android.util.Log.d("FIXED_DEBUG", "syncFixedFromFirestore: inserted ${remote.size} templates into Room")
    }

    fun getAllFixedForUserFlow(userId: String) = fixedDao.getAllFixedExpensesFlowForUser(userId).map { it.map { fe -> fe.toDomain() } }

    suspend fun getExchangeRates(): Map<String, Double> = firebase.getExchangeRates()

    suspend fun getAvailableMonths(): List<Pair<Int, Int>> {
        val uid = firebase.currentUserId() ?: return emptyList()
        val list = dao.getAllExpensesFlowForUser(uid).first()
        val set = mutableSetOf<Pair<Int, Int>>()
        val cal = java.util.Calendar.getInstance()
        list.forEach { e ->
            cal.timeInMillis = e.date
            set.add(cal.get(java.util.Calendar.YEAR) to (cal.get(java.util.Calendar.MONTH) + 1))
        }
        if (set.isEmpty()) {
            val c = java.util.Calendar.getInstance()
            return listOf(c.get(java.util.Calendar.YEAR) to (c.get(java.util.Calendar.MONTH) + 1))
        }
        return set.toList().sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
    }

}

private fun ExpenseEntity.toDomain(): Expense = Expense(
    id = id,
    userId = userId,
    amount = amount,
    currency = currency,
    category = category,
    description = description,
    paymentMethod = paymentMethod,
    notes = notes,
    isFixed = isFixed,
    isFixedExpense = isFixedExpense,
    isPaid = isPaid,
    templateId = templateId,
    date = Timestamp(date / 1000, 0),
    createdAt = Timestamp(createdAt / 1000, 0)
)

private fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    userId = userId,
    amount = amount,
    currency = currency,
    category = category,
    description = description,
    paymentMethod = paymentMethod,
    notes = notes,
    isFixed = isFixed,
    isFixedExpense = isFixedExpense,
    isPaid = isPaid,
    templateId = templateId,
    date = date.toDate().time,
    createdAt = createdAt.toDate().time
)

private fun FixedExpenseEntity.toDomain(): com.example.financeflow.model.FixedExpense = com.example.financeflow.model.FixedExpense(
    id = id,
    userId = userId,
    name = name,
    amount = amount,
    category = category,
    isPaid = isPaid,
    createdAt = Timestamp(createdAt / 1000, 0)
)

private fun com.example.financeflow.model.FixedExpense.toEntity(): FixedExpenseEntity = FixedExpenseEntity(
    id = id,
    userId = userId,
    name = name,
    amount = amount,
    category = category,
    isPaid = isPaid,
    createdAt = createdAt.toDate().time
)

private fun Expense.toStreakExpense(): com.example.financeflow.data.model.Expense {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val zoneId = ZoneId.systemDefault()

    fun Timestamp.toLocalDateString(): String {
        return Instant.ofEpochSecond(seconds, nanoseconds.toLong())
            .atZone(zoneId)
            .toLocalDate()
            .format(formatter)
    }

    return com.example.financeflow.data.model.Expense(
        id = id,
        amount = amount,
        category = category,
        description = description,
        date = date.toLocalDateString(),
        createdAt = createdAt.toLocalDateString()
    )
}

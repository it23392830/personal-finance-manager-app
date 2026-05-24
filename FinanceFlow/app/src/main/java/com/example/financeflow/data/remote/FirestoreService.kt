package com.example.financeflow.data.remote

import com.example.financeflow.data.model.Expense
import com.example.financeflow.data.model.Streak
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun currentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun hasAuthenticatedUser(): Boolean = auth.currentUser != null

    fun userDocument() =
        firestore.collection("users")
            .document(currentUserId())

    fun transactionsCollection() =
        userDocument().collection("transactions")

    fun goalsCollection() =
        userDocument().collection("goals")

    fun preferencesDocument() =
        userDocument()
            .collection("preferences")
            .document("user_preferences")

    fun expensesCollection() =
        userDocument().collection("expenses")

    suspend fun getTrackedExpenses(): List<Expense> {
        if (!hasAuthenticatedUser()) return emptyList()
        val snapshot = expensesCollection().get().await()
        return snapshot.documents.mapNotNull(::mapExpenseDocument)
    }

    suspend fun hasExpenseOnDate(targetDate: LocalDate): Boolean {
        return getTrackedExpenses().any { expense ->
            expense.date == targetDate.format(formatter)
        }
    }

    suspend fun getStreak(): Streak {
        if (!hasAuthenticatedUser()) return Streak()
        val snapshot = userDocument().get().await()
        val streakMap = snapshot.get("streak") as? Map<*, *> ?: emptyMap<String, Any?>()

        return Streak(
            currentStreak = (streakMap["currentStreak"] as? Number)?.toInt() ?: 0,
            bestStreak = (streakMap["bestStreak"] as? Number)?.toInt() ?: 0,
            freezeState = streakMap["freezeState"] as? Boolean ?: false,
            missedDays = (streakMap["missedDays"] as? Number)?.toInt() ?: 0,
            lastExpenseDate = streakMap["lastExpenseDate"] as? String ?: "",
            streakStatus = streakMap["streakStatus"] as? String ?: Streak.STATUS_BROKEN
        )
    }

    suspend fun saveStreak(
        streak: Streak,
        freezeAnimation: Boolean
    ) {
        if (!hasAuthenticatedUser()) return
        val streakPayload = mapOf(
            "currentStreak" to streak.currentStreak,
            "bestStreak" to streak.bestStreak,
            "freezeState" to streak.freezeState,
            "missedDays" to streak.missedDays,
            "lastExpenseDate" to streak.lastExpenseDate,
            "freezeAnimation" to freezeAnimation,
            "streakStatus" to streak.streakStatus
        )

        userDocument()
            .set(mapOf("streak" to streakPayload), SetOptions.merge())
            .await()
    }

    private fun mapExpenseDocument(document: DocumentSnapshot): Expense? {
        val expenseDate = document.get("date").toExpenseDateString()
            ?: document.get("expenseDate").toExpenseDateString()
            ?: return null

        val createdAtDate = document.get("createdAt").toExpenseDateString()
            ?: expenseDate

        return Expense(
            id = document.id,
            amount = document.getDouble("amount") ?: 0.0,
            category = document.getString("category").orEmpty(),
            description = document.getString("description").orEmpty(),
            date = expenseDate,
            createdAt = createdAtDate
        )
    }

    private fun Any?.toExpenseDateString(): String? {
        return when (this) {
            null -> null
            is String -> parseStringDate(this)
            is Timestamp -> this.toLocalDate().format(formatter)
            is java.util.Date -> this.toInstant().atZone(zoneId).toLocalDate().format(formatter)
            is Number -> Instant.ofEpochMilli(this.toLong()).atZone(zoneId).toLocalDate().format(formatter)
            else -> null
        }
    }

    private fun Timestamp.toLocalDate(): LocalDate {
        return Instant.ofEpochSecond(seconds, nanoseconds.toLong())
            .atZone(zoneId)
            .toLocalDate()
    }

    private fun parseStringDate(value: String): String? {
        return when {
            value.isBlank() -> null
            else -> {
                try {
                    LocalDate.parse(value, formatter).format(formatter)
                } catch (_: DateTimeParseException) {
                    runCatching {
                        Instant.parse(value).atZone(zoneId).toLocalDate().format(formatter)
                    }.getOrNull()
                }
            }
        }
    }
}

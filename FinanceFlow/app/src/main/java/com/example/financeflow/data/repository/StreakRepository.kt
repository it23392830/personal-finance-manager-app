package com.example.financeflow.data.repository

import com.example.financeflow.data.model.Expense
import com.example.financeflow.data.model.Streak
import com.example.financeflow.data.remote.FirestoreService
import com.example.financeflow.domain.usecase.CalculateStreakUseCase
import com.example.financeflow.domain.usecase.StreakCalculationResult
import com.example.financeflow.widgets.StreakWidgetUpdater
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

data class StreakSnapshot(
    val streak: Streak,
    val eligibleDates: Set<LocalDate>,
    val freezeDates: Set<LocalDate>
)

@Singleton
class StreakRepository @Inject constructor(
    private val firestoreService: FirestoreService,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val streakWidgetUpdater: StreakWidgetUpdater
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val _refreshEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val refreshEvents: SharedFlow<Unit> = _refreshEvents.asSharedFlow()

    /**
     * Recalculates the streak after an expense has already been written.
     *
     * The repository intentionally rebuilds the streak from Firestore history
     * so duplicate same-day logs never add extra points.
     */
    suspend fun addExpense(expense: Expense): Streak {
        val expenseDate = expense.date.toLocalDateOrNull()
        val today = LocalDate.now(zoneId)
        val isYesterdayRecovery = expenseDate == today.minusDays(1)

        return if (isYesterdayRecovery) {
            recoverPreviousDay(expense)
        } else {
            updateStreak(forceWidgetRefresh = true)
        }
    }

    suspend fun updateStreak(forceWidgetRefresh: Boolean = false): Streak {
        return recalculateAndPersist(forceWidgetRefresh = forceWidgetRefresh).streak
    }

    suspend fun checkMissedDays(): Streak {
        return recalculateAndPersist(forceWidgetRefresh = true).streak
    }

    /**
     * Rebuilds the streak after a yesterday recovery entry.
     *
     * Only the immediately previous day is allowed to recover a gap. Older
     * retroactive expenses remain saved in Firestore, but the use case will not
     * count them for streak progression.
     */
    suspend fun recoverPreviousDay(expense: Expense): Streak {
        val today = LocalDate.now(zoneId)
        val expenseDate = expense.date.toLocalDateOrNull()
        require(expenseDate == today.minusDays(1)) {
            "Only yesterday can be recovered for the streak."
        }

        return recalculateAndPersist(forceWidgetRefresh = true).streak
    }

    suspend fun freezeStreak(): Streak {
        val previous = firestoreService.getStreak()
        val result = recalculate()
        if (result.streak.streakStatus == Streak.STATUS_FROZEN) {
            persistResult(
                previous = previous,
                result = result,
                forceWidgetRefresh = true
            )
        }
        return result.streak
    }

    suspend fun restartStreak(): Streak {
        return recalculateAndPersist(forceWidgetRefresh = true).streak
    }

    suspend fun getCurrentStreak(): Streak {
        return recalculateAndPersist(forceWidgetRefresh = false).streak
    }

    suspend fun getStreakSnapshot(): StreakSnapshot {
        val result = recalculateAndPersist(forceWidgetRefresh = false)
        return StreakSnapshot(
            streak = result.streak,
            eligibleDates = result.eligibleDates,
            freezeDates = result.freezeDates
        )
    }

    suspend fun hasExpenseLoggedToday(): Boolean {
        val today = LocalDate.now(zoneId)
        return firestoreService.hasExpenseOnDate(today)
    }

    private suspend fun recalculateAndPersist(
        forceWidgetRefresh: Boolean
    ): StreakCalculationResult {
        val previous = firestoreService.getStreak()
        val result = recalculate()
        persistResult(
            previous = previous,
            result = result,
            forceWidgetRefresh = forceWidgetRefresh
        )
        return result
    }

    private suspend fun recalculate(): StreakCalculationResult {
        val previous = firestoreService.getStreak()
        val expenses = firestoreService.getTrackedExpenses()
        return calculateStreakUseCase(
            expenses = expenses,
            previousStreak = previous,
            today = LocalDate.now(zoneId)
        )
    }

    /**
     * Saves the streak only when it changed, then notifies widgets and any
     * active ViewModels that a fresh snapshot is ready.
     */
    private suspend fun persistResult(
        previous: Streak,
        result: StreakCalculationResult,
        forceWidgetRefresh: Boolean
    ) {
        val streakChanged = previous != result.streak
        val shouldPlayFreezeAnimation = result.enteredFreezeMode

        if (streakChanged || shouldPlayFreezeAnimation) {
            firestoreService.saveStreak(
                streak = result.streak,
                freezeAnimation = shouldPlayFreezeAnimation
            )
            _refreshEvents.tryEmit(Unit)
        }

        if (streakChanged || forceWidgetRefresh) {
            streakWidgetUpdater.enqueueRefresh()
        }
    }

    private fun String.toLocalDateOrNull(): LocalDate? {
        if (isBlank()) return null
        return try {
            LocalDate.parse(this, formatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

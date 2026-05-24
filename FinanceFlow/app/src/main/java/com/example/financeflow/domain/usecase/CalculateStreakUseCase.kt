package com.example.financeflow.domain.usecase

import com.example.financeflow.data.model.Expense
import com.example.financeflow.data.model.Streak
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class StreakCalculationResult(
    val streak: Streak,
    val eligibleDates: Set<LocalDate>,
    val freezeDates: Set<LocalDate>,
    val enteredFreezeMode: Boolean
)

class CalculateStreakUseCase @Inject constructor() {

    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Rebuilds the whole streak from eligible expense dates.
     *
     * The calculation intentionally ignores older retroactive expenses. An
     * expense only becomes streak-eligible when it was created on the same day,
     * or on the immediately following day for the one-day recovery rule.
     */
    operator fun invoke(
        expenses: List<Expense>,
        previousStreak: Streak,
        today: LocalDate = LocalDate.now(zoneId)
    ): StreakCalculationResult {
        val eligibleDates = expenses
            .mapNotNull(::toEligibleDate)
            .filter { !it.isAfter(today) }
            .toSet()

        val bestStreak = calculateBestStreak(eligibleDates)
        val latestExpenseDate = eligibleDates.maxOrNull()
        val freezeDates = calculateFreezeDates(eligibleDates, today)

        if (latestExpenseDate == null) {
            return StreakCalculationResult(
                streak = Streak(
                    currentStreak = 0,
                    bestStreak = maxOf(previousStreak.bestStreak, bestStreak),
                    freezeState = false,
                    missedDays = 0,
                    lastExpenseDate = "",
                    streakStatus = Streak.STATUS_BROKEN
                ),
                eligibleDates = emptySet(),
                freezeDates = emptySet(),
                enteredFreezeMode = false
            )
        }

        val daysSinceLatestExpense = java.time.temporal.ChronoUnit.DAYS.between(latestExpenseDate, today)
        if (daysSinceLatestExpense >= 3) {
            val frozenStreak = Streak(
                currentStreak = 0,
                bestStreak = maxOf(previousStreak.bestStreak, bestStreak),
                freezeState = true,
                missedDays = 2,
                lastExpenseDate = latestExpenseDate.format(formatter),
                streakStatus = Streak.STATUS_FROZEN
            )

            return StreakCalculationResult(
                streak = frozenStreak,
                eligibleDates = eligibleDates,
                freezeDates = freezeDates,
                enteredFreezeMode = !previousStreak.freezeState
            )
        }

        val currentSegment = calculateCurrentSegment(eligibleDates, today)
        val currentStreak = currentSegment.completedDays
        val streakStatus = if (currentStreak > 0) {
            Streak.STATUS_ACTIVE
        } else {
            Streak.STATUS_BROKEN
        }

        return StreakCalculationResult(
            streak = Streak(
                currentStreak = currentStreak,
                bestStreak = maxOf(previousStreak.bestStreak, bestStreak),
                freezeState = false,
                missedDays = currentSegment.missedDays,
                lastExpenseDate = latestExpenseDate.format(formatter),
                streakStatus = streakStatus
            ),
            eligibleDates = eligibleDates,
            freezeDates = freezeDates,
            enteredFreezeMode = false
        )
    }

    private fun toEligibleDate(expense: Expense): LocalDate? {
        val expenseDate = expense.date.toLocalDateOrNull() ?: return null
        val createdDate = expense.createdAt.toLocalDateOrNull() ?: expenseDate

        return when {
            createdDate == expenseDate -> expenseDate
            createdDate == expenseDate.plusDays(1) -> expenseDate
            else -> null
        }
    }

    private fun calculateBestStreak(eligibleDates: Set<LocalDate>): Int {
        if (eligibleDates.isEmpty()) return 0

        val sortedDates = eligibleDates.sorted()
        var best = 1
        var running = 1

        for (index in 1 until sortedDates.size) {
            val previous = sortedDates[index - 1]
            val current = sortedDates[index]
            if (current == previous.plusDays(1)) {
                running += 1
            } else {
                running = 1
            }
            best = maxOf(best, running)
        }

        return best
    }

    /**
     * Counts the current streak segment near today.
     *
     * The segment may temporarily skip yesterday once, but only if there is an
     * older completed day to bridge to. That is the one-day recovery rule.
     */
    private fun calculateCurrentSegment(
        eligibleDates: Set<LocalDate>,
        today: LocalDate
    ): CurrentSegment {
        var cursor = today
        var completedDays = 0
        var missedDays = 0

        while (true) {
            if (eligibleDates.contains(cursor)) {
                completedDays += 1
                cursor = cursor.minusDays(1)
                continue
            }

            if (cursor == today) {
                cursor = cursor.minusDays(1)
                continue
            }

            if (cursor == today.minusDays(1) &&
                missedDays == 0 &&
                eligibleDates.any { it.isBefore(cursor) }
            ) {
                missedDays = 1
                cursor = cursor.minusDays(1)
                continue
            }

            break
        }

        return CurrentSegment(completedDays = completedDays, missedDays = missedDays)
    }

    /**
     * Marks the day a freeze happened. For any gap of 2+ missed days we display
     * the second missed day as the blue freeze cell in the calendar.
     */
    private fun calculateFreezeDates(
        eligibleDates: Set<LocalDate>,
        today: LocalDate
    ): Set<LocalDate> {
        if (eligibleDates.isEmpty()) return emptySet()

        val sortedDates = eligibleDates.sorted()
        val freezeDates = mutableSetOf<LocalDate>()

        for (index in 0 until sortedDates.lastIndex) {
            val current = sortedDates[index]
            val next = sortedDates[index + 1]
            val gapDays = java.time.temporal.ChronoUnit.DAYS.between(current, next)
            if (gapDays >= 3) {
                freezeDates += current.plusDays(2)
            }
        }

        val latestExpenseDate = sortedDates.last()
        val daysSinceLatestExpense = java.time.temporal.ChronoUnit.DAYS.between(latestExpenseDate, today)
        if (daysSinceLatestExpense >= 3) {
            freezeDates += latestExpenseDate.plusDays(2)
        }

        return freezeDates.filterNot { it.isAfter(today) }.toSet()
    }

    private fun String.toLocalDateOrNull(): LocalDate? {
        if (isBlank()) return null
        return try {
            LocalDate.parse(this, formatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private data class CurrentSegment(
        val completedDays: Int,
        val missedDays: Int
    )
}

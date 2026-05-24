package com.example.financeflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.data.repository.StreakRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StreakUiState(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val freeze: Boolean = false,
    val status: String = "",
    val missedDays: Int = 0
)

data class StreakCalendarUiState(
    val isLoading: Boolean = false,
    val completedDates: Set<LocalDate> = emptySet(),
    val freezeDates: Set<LocalDate> = emptySet(),
    val visibleMonths: List<YearMonth> = listOf(YearMonth.now()),
    val selectedMonthIndex: Int = 0
)

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    private val _calendarState = MutableStateFlow(StreakCalendarUiState())
    val calendarState: StateFlow<StreakCalendarUiState> = _calendarState.asStateFlow()

    init {
        viewModelScope.launch {
            streakRepository.refreshEvents.collect {
                refresh()
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _calendarState.value = _calendarState.value.copy(isLoading = true)

            runCatching {
                streakRepository.checkMissedDays()
                streakRepository.getStreakSnapshot()
            }.onSuccess { snapshot ->
                _uiState.value = StreakUiState(
                    currentStreak = snapshot.streak.currentStreak,
                    bestStreak = snapshot.streak.bestStreak,
                    freeze = snapshot.streak.freezeState,
                    status = snapshot.streak.streakStatus,
                    missedDays = snapshot.streak.missedDays
                )

                val months = buildVisibleMonths(
                    dates = snapshot.eligibleDates + snapshot.freezeDates
                )

                _calendarState.value = StreakCalendarUiState(
                    isLoading = false,
                    completedDates = snapshot.eligibleDates,
                    freezeDates = snapshot.freezeDates,
                    visibleMonths = months,
                    selectedMonthIndex = months.lastIndex
                )
            }.onFailure {
                _uiState.value = StreakUiState(status = "BROKEN")
                _calendarState.value = _calendarState.value.copy(isLoading = false)
            }
        }
    }

    fun selectPreviousMonth() {
        val current = _calendarState.value
        if (current.selectedMonthIndex > 0) {
            _calendarState.value = current.copy(selectedMonthIndex = current.selectedMonthIndex - 1)
        }
    }

    fun selectNextMonth() {
        val current = _calendarState.value
        if (current.selectedMonthIndex < current.visibleMonths.lastIndex) {
            _calendarState.value = current.copy(selectedMonthIndex = current.selectedMonthIndex + 1)
        }
    }

    private fun buildVisibleMonths(dates: Set<LocalDate>): List<YearMonth> {
        val currentMonth = YearMonth.now()
        val earliestMonth = dates.minOrNull()?.let(YearMonth::from)
            ?: currentMonth.minusMonths(5)

        val months = mutableListOf<YearMonth>()
        var cursor = earliestMonth
        while (!cursor.isAfter(currentMonth)) {
            months += cursor
            cursor = cursor.plusMonths(1)
        }

        if (months.size >= 6) return months

        val paddedMonths = mutableListOf<YearMonth>()
        var paddedCursor = currentMonth.minusMonths(5)
        while (!paddedCursor.isAfter(currentMonth)) {
            paddedMonths += paddedCursor
            paddedCursor = paddedCursor.plusMonths(1)
        }
        return paddedMonths
    }
}

package com.example.financeflow.ui.streak.Streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.presentation.viewmodel.StreakViewModel
import com.example.financeflow.ui.components.streak.CurrentStreakCard
import com.example.financeflow.ui.components.streak.EncouragementCard
import com.example.financeflow.ui.components.streak.FreezeStatusCard
import com.example.financeflow.ui.components.streak.StreakCalendar
import com.example.financeflow.ui.components.streak.StreakCalendarDay
import com.example.financeflow.ui.components.streak.StreakDayState
import com.example.financeflow.ui.components.streak.StreakTopBar
import com.example.financeflow.ui.components.streak.StreakWidget
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

enum class StreakVisualState {
    Active,
    Frozen,
    Zero
}

private data class StreakWidgetData(
    val icon: String,
    val title: String,
    val value: String
)

private data class StreakMonthData(
    val monthLabel: String,
    val days: List<StreakCalendarDay>
)

private fun buildMonthDays(
    month: YearMonth,
    completedDates: Set<LocalDate>,
    freezeDates: Set<LocalDate>,
    today: LocalDate
): List<StreakCalendarDay> {
    val cells = mutableListOf<StreakCalendarDay>()
    val leadingEmptyDays = month.atDay(1).dayOfWeek.value % 7

    repeat(leadingEmptyDays) {
        cells += StreakCalendarDay("", StreakDayState.Empty)
    }

    (1..month.lengthOfMonth()).forEach { day ->
        val date = month.atDay(day)
        val state = when {
            date in freezeDates -> StreakDayState.Freeze
            date == today -> StreakDayState.Current
            date in completedDates -> StreakDayState.Completed
            date.isAfter(today) -> StreakDayState.Future
            else -> StreakDayState.Missed
        }

        cells += StreakCalendarDay(
            label = day.toString(),
            state = state
        )
    }

    while (cells.size % 7 != 0) {
        cells += StreakCalendarDay("", StreakDayState.Empty)
    }

    return cells
}

@Composable
fun StreakScreen(
    isDarkTheme: Boolean = false,
    viewModel: StreakViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendarState by viewModel.calendarState.collectAsState()
    val visualState = when {
        uiState.freeze -> StreakVisualState.Frozen
        uiState.currentStreak == 0 -> StreakVisualState.Zero
        else -> StreakVisualState.Active
    }
    val backgroundColor = if (isDarkTheme) Color(0xFF111827) else Color(0xFFFFFBF8)
    val today = LocalDate.now()

    val selectedMonth = calendarState.visibleMonths
        .getOrElse(calendarState.selectedMonthIndex) { YearMonth.now() }
        .toMonthData(
            completedDates = calendarState.completedDates,
            freezeDates = calendarState.freezeDates,
            today = today
        )

    val widgets = listOf(
        StreakWidgetData("🔥", "Current Streak", "${uiState.currentStreak} Days"),
        StreakWidgetData(
            "❄",
            if (uiState.freeze) "Freeze Mode" else "Missed Days",
            if (uiState.freeze) "Frozen" else uiState.missedDays.toString()
        ),
        StreakWidgetData("⭐", "Best Streak", "${uiState.bestStreak} Days"),
        StreakWidgetData("📅", "Status", uiState.status.ifBlank { "BROKEN" })
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StreakTopBar(
                streakCount = uiState.currentStreak,
                visualState = visualState,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            CurrentStreakCard(
                streakCount = uiState.currentStreak,
                visualState = visualState,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            FreezeStatusCard(
                visualState = visualState,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            StreakCalendar(
                monthLabel = selectedMonth.monthLabel,
                days = selectedMonth.days,
                isDarkTheme = isDarkTheme,
                canNavigatePrevious = calendarState.selectedMonthIndex > 0,
                canNavigateNext = calendarState.selectedMonthIndex < calendarState.visibleMonths.lastIndex,
                onPreviousMonth = viewModel::selectPreviousMonth,
                onNextMonth = viewModel::selectNextMonth
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(widgets) { widget ->
                    StreakWidget(
                        icon = widget.icon,
                        title = widget.title,
                        value = widget.value,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }

        item {
            EncouragementCard(
                visualState = visualState,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun YearMonth.toMonthData(
    completedDates: Set<LocalDate>,
    freezeDates: Set<LocalDate>,
    today: LocalDate
): StreakMonthData {
    val monthLabel = "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
    return StreakMonthData(
        monthLabel = monthLabel,
        days = buildMonthDays(
            month = this,
            completedDates = completedDates,
            freezeDates = freezeDates,
            today = today
        )
    )
}

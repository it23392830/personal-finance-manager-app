package com.example.financeflow.ui.screens.streak

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.components.streak.CurrentStreakCard
import com.example.financeflow.ui.components.streak.EncouragementCard
import com.example.financeflow.ui.components.streak.FreezeStatusCard
import com.example.financeflow.ui.components.streak.StreakCalendar
import com.example.financeflow.ui.components.streak.StreakCalendarDay
import com.example.financeflow.ui.components.streak.StreakDayState
import com.example.financeflow.ui.components.streak.StreakTopBar
import com.example.financeflow.ui.components.streak.StreakWidget

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
    daysInMonth: Int,
    leadingEmptyDays: Int,
    statesByDay: Map<Int, StreakDayState>
): List<StreakCalendarDay> {
    val cells = mutableListOf<StreakCalendarDay>()

    repeat(leadingEmptyDays) {
        cells += StreakCalendarDay("", StreakDayState.Empty)
    }

    (1..daysInMonth).forEach { day ->
        cells += StreakCalendarDay(
            label = day.toString(),
            state = statesByDay[day] ?: StreakDayState.Missed
        )
    }

    while (cells.size % 7 != 0) {
        cells += StreakCalendarDay("", StreakDayState.Empty)
    }

    return cells
}

@Composable
fun StreakScreen(isDarkTheme: Boolean = false) {
    val visualState = StreakVisualState.Active
    val streakCount = if (visualState == StreakVisualState.Zero) 0 else 7
    val backgroundColor = if (isDarkTheme) Color(0xFF111827) else Color(0xFFFFFBF8)

    val streakMonths = remember {
        listOf(
            StreakMonthData(
                monthLabel = "January 2026",
                days = buildMonthDays(
                    daysInMonth = 31,
                    leadingEmptyDays = 4,
                    statesByDay = buildMap {
                        put(3, StreakDayState.Completed)
                        put(4, StreakDayState.Completed)
                        put(5, StreakDayState.Completed)
                        put(8, StreakDayState.Freeze)
                        put(11, StreakDayState.Completed)
                        put(12, StreakDayState.Completed)
                        put(18, StreakDayState.Completed)
                        put(19, StreakDayState.Completed)
                        put(24, StreakDayState.Completed)
                        put(25, StreakDayState.Completed)
                    }
                )
            ),
            StreakMonthData(
                monthLabel = "February 2026",
                days = buildMonthDays(
                    daysInMonth = 28,
                    leadingEmptyDays = 0,
                    statesByDay = buildMap {
                        (1..6).forEach { put(it, StreakDayState.Completed) }
                        put(7, StreakDayState.Freeze)
                        (8..13).forEach { put(it, StreakDayState.Completed) }
                        put(15, StreakDayState.Completed)
                        put(20, StreakDayState.Completed)
                        put(21, StreakDayState.Completed)
                        put(22, StreakDayState.Completed)
                    }
                )
            ),
            StreakMonthData(
                monthLabel = "March 2026",
                days = buildMonthDays(
                    daysInMonth = 31,
                    leadingEmptyDays = 0,
                    statesByDay = buildMap {
                        (2..8).forEach { put(it, StreakDayState.Completed) }
                        put(9, StreakDayState.Freeze)
                        (10..14).forEach { put(it, StreakDayState.Completed) }
                        put(20, StreakDayState.Completed)
                        put(21, StreakDayState.Completed)
                        put(29, StreakDayState.Completed)
                    }
                )
            ),
            StreakMonthData(
                monthLabel = "April 2026",
                days = buildMonthDays(
                    daysInMonth = 30,
                    leadingEmptyDays = 3,
                    statesByDay = buildMap {
                        (1..5).forEach { put(it, StreakDayState.Completed) }
                        put(6, StreakDayState.Freeze)
                        (10..14).forEach { put(it, StreakDayState.Completed) }
                        put(19, StreakDayState.Completed)
                        put(20, StreakDayState.Completed)
                        put(27, StreakDayState.Completed)
                        put(28, StreakDayState.Completed)
                    }
                )
            ),
            StreakMonthData(
                monthLabel = "May 2026",
                days = buildMonthDays(
                    daysInMonth = 31,
                    leadingEmptyDays = 5,
                    statesByDay = buildMap {
                        (2..8).forEach { put(it, StreakDayState.Completed) }
                        put(9, StreakDayState.Freeze)
                        (10..13).forEach { put(it, StreakDayState.Completed) }
                        (14..15).forEach { put(it, StreakDayState.Missed) }
                        (16..23).forEach { put(it, StreakDayState.Completed) }
                        put(24, StreakDayState.Current)
                        (25..31).forEach { put(it, StreakDayState.Future) }
                    }
                )
            )
        )
    }
    var selectedMonthIndex by remember { mutableIntStateOf(streakMonths.lastIndex) }
    val selectedMonth = streakMonths[selectedMonthIndex]

    val widgets = remember {
        listOf(
            StreakWidgetData("🔥", "Current Streak", "7 Days"),
            StreakWidgetData("❄", "Freeze Available", "1 Remaining"),
            StreakWidgetData("📅", "Best Streak", "20 Days"),
            StreakWidgetData("⭐", "Consistency", "85%")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StreakTopBar(
                streakCount = streakCount,
                visualState = visualState,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            CurrentStreakCard(
                streakCount = streakCount,
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
                canNavigatePrevious = selectedMonthIndex > 0,
                canNavigateNext = selectedMonthIndex < streakMonths.lastIndex,
                onPreviousMonth = {
                    if (selectedMonthIndex > 0) {
                        selectedMonthIndex -= 1
                    }
                },
                onNextMonth = {
                    if (selectedMonthIndex < streakMonths.lastIndex) {
                        selectedMonthIndex += 1
                    }
                }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StreakScreenPreview() {
    MaterialTheme {
        StreakScreen()
    }
}

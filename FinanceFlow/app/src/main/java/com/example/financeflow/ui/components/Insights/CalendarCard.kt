package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Activity level colors ────────────────────────────────────────────────────
val ActivityNone   = Color(0xFFD9D9D9)
val ActivityLow    = Color(0xFFB8F5C4)
val ActivityMedium = Color(0xFF74E18A)
val ActivityHigh   = Color(0xFF16B84E)

private val PrimaryPurple  = Color(0xFF8B5CF6)
private val CardWhite      = Color(0xFFFFFFFF)
private val TextDark       = Color(0xFF1E1B2E)
private val TextMuted      = Color(0xFF9CA3AF)

/** Activity level enum for a calendar day */
enum class ActivityLevel { NONE, LOW, MEDIUM, HIGH }

/** Data holder for a single calendar day cell */
data class CalendarDay(
    val dayOfMonth: Int,
    val activity: ActivityLevel,
    val incomeEntries: Int = 0,
    val expenseEntries: Int = 0,
    val savingsEntries: Int = 0
)

/** Maps ActivityLevel → display color */
fun ActivityLevel.color(): Color = when (this) {
    ActivityLevel.NONE   -> ActivityNone
    ActivityLevel.LOW    -> ActivityLow
    ActivityLevel.MEDIUM -> ActivityMedium
    ActivityLevel.HIGH   -> ActivityHigh
}

// ─── Fake data for May 2026 ───────────────────────────────────────────────────
val sampleMay2026Days: List<CalendarDay> = buildList {
    // May 2026 starts on Friday (index 5 in SUN-SAT grid → offset 5 empty cells)
    val activities = listOf(
        ActivityLevel.NONE, ActivityLevel.NONE, ActivityLevel.NONE,
        ActivityLevel.NONE, ActivityLevel.NONE, ActivityLevel.LOW,   // 1,2
        ActivityLevel.MEDIUM,                                          // 3
        ActivityLevel.NONE, ActivityLevel.LOW, ActivityLevel.HIGH,   // 4,5,6
        ActivityLevel.MEDIUM, ActivityLevel.NONE, ActivityLevel.NONE, ActivityLevel.NONE, // 7..
        ActivityLevel.LOW, ActivityLevel.MEDIUM, ActivityLevel.HIGH,
        ActivityLevel.LOW, ActivityLevel.NONE, ActivityLevel.NONE, ActivityLevel.MEDIUM,
        ActivityLevel.HIGH, ActivityLevel.MEDIUM, ActivityLevel.LOW,
        ActivityLevel.NONE, ActivityLevel.NONE, ActivityLevel.NONE,
        ActivityLevel.LOW, ActivityLevel.MEDIUM, ActivityLevel.HIGH,
        ActivityLevel.LOW
    )
    activities.forEachIndexed { index, act ->
        add(
            CalendarDay(
                dayOfMonth     = index + 1,
                activity       = act,
                incomeEntries  = if (act == ActivityLevel.HIGH) 2 else if (act == ActivityLevel.MEDIUM) 1 else 0,
                expenseEntries = if (act != ActivityLevel.NONE) (1..3).random() else 0,
                savingsEntries = if (act == ActivityLevel.HIGH) 1 else 0
            )
        )
    }
}

/**
 * CalendarCard
 *
 * Shows a monthly activity calendar.
 * Clicking a day selects it (purple border) and calls [onDaySelected].
 *
 * @param month          Display title e.g. "May 2026".
 * @param startDayOffset Weekday offset of the 1st (0=SUN … 6=SAT). May 2026 starts on Friday = 5.
 * @param days           List of [CalendarDay] for the month.
 * @param selectedDay    Currently selected day-of-month (null = none).
 * @param onDaySelected  Callback with the clicked [CalendarDay].
 */
@Composable
fun CalendarCard(
    isDarkTheme: Boolean = false,
    month: String = "May 2026",
    startDayOffset: Int = 5,
    days: List<CalendarDay> = sampleMay2026Days,
    selectedDay: Int? = null,
    onDaySelected: (CalendarDay) -> Unit = {}
) {
    val colors = getInsightsColors(isDarkTheme)
    val weekHeaders = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Month title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colors.PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Activity Calendar – $month",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.TextDark
                )
            }

            Spacer(Modifier.height(12.dp))

            // Week day headers
            Row(modifier = Modifier.fillMaxWidth()) {
                weekHeaders.forEach { header ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = header,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.TextMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Build grid rows
            // Total cells = offset + days.size, padded to next multiple of 7
            val totalCells = ((startDayOffset + days.size + 6) / 7) * 7
            val cells: List<CalendarDay?> = buildList {
                repeat(startDayOffset) { add(null) }
                addAll(days)
                val remaining = totalCells - size
                repeat(remaining) { add(null) }
            }

            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                val isSelected = day.dayOfMonth == selectedDay
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(day.activity.color())
                                        .then(
                                            if (isSelected)
                                                Modifier.border(2.dp, colors.PrimaryPurple, RoundedCornerShape(8.dp))
                                            else Modifier
                                        )
                                        .clickable { onDaySelected(day) }
                                ) {
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (day.activity == ActivityLevel.HIGH) Color.White
                                        else colors.TextDark
                                    )
                                }
                            } else {
                                // empty cell
                                Spacer(Modifier.size(36.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "No activity" to ActivityNone,
                    "Low"         to ActivityLow,
                    "Medium"      to ActivityMedium,
                    "High"        to ActivityHigh
                ).forEach { (label, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(color)
                        )
                        Text(text = label, fontSize = 10.sp, color = colors.TextMuted)
                    }
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun CalendarCardPreview() {
    var selected by remember { mutableStateOf<Int?>(6) }
    CalendarCard(selectedDay = selected, onDaySelected = { selected = it.dayOfMonth })
}

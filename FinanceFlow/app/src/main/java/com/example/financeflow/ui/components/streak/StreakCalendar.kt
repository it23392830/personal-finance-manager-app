package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class StreakDayState {
    Completed,
    Current,
    Freeze,
    Missed,
    Future,
    Empty
}

data class StreakCalendarDay(
    val label: String,
    val state: StreakDayState
)

@Composable
fun StreakCalendar(
    monthLabel: String,
    days: List<StreakCalendarDay>,
    isDarkTheme: Boolean = false,
    canNavigatePrevious: Boolean = true,
    canNavigateNext: Boolean = false,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val headers = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Streak Calendar",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF382D49),
            modifier = Modifier.padding(start = 6.dp, bottom = 10.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = if (isDarkTheme) Color(0xFF1F2937) else Color.White,
            shadowElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "<",
                        modifier = Modifier
                            .alpha(if (canNavigatePrevious) 1f else 0.35f)
                            .clickable(enabled = canNavigatePrevious, onClick = onPreviousMonth),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF9B8EAF)
                    )
                    Text(
                        text = monthLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF3C3250)
                    )
                    Text(
                        text = ">",
                        modifier = Modifier
                            .alpha(if (canNavigateNext) 1f else 0.35f)
                            .clickable(enabled = canNavigateNext, onClick = onNextMonth),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF9B8EAF)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    headers.forEach { header ->
                        Text(
                            text = header,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF9B90AD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                days.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        week.forEach { day ->
                            CalendarDayBubble(
                                day = day,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayBubble(
    day: StreakCalendarDay,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val background = when (day.state) {
        StreakDayState.Completed -> if (isDarkTheme) Color(0xFFEA580C) else Color(0xFFFF8C42)
        StreakDayState.Current -> Color.Transparent
        StreakDayState.Freeze -> if (isDarkTheme) Color(0xFF1E3A8A) else Color(0xFFDDEBFF)
        StreakDayState.Missed -> if (isDarkTheme) Color(0xFF374151) else Color(0xFFE6E2EC)
        StreakDayState.Future -> if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
        StreakDayState.Empty -> Color.Transparent
    }

    val textColor = when (day.state) {
        StreakDayState.Completed -> Color.White
        StreakDayState.Current -> Color.White
        StreakDayState.Freeze -> if (isDarkTheme) Color(0xFFDBEAFE) else Color(0xFF5082C8)
        StreakDayState.Missed -> if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFFAAA0B8)
        StreakDayState.Future -> if (isDarkTheme) Color(0xFF334155) else Color(0xFFD1D5DB)
        StreakDayState.Empty -> Color.Transparent
    }

    val bubbleModifier = if (day.state == StreakDayState.Current) {
        Modifier
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = Color(0x66FF9E59),
                spotColor = Color(0x66FF9E59)
            )
            .background(
                Brush.radialGradient(
                    if (isDarkTheme) {
                        listOf(Color(0xFFF59E0B), Color(0xFFEA580C))
                    } else {
                        listOf(Color(0xFFFFB071), Color(0xFFFF8C42))
                    }
                ),
                CircleShape
            )
    } else {
        Modifier.background(background, CircleShape)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (day.state == StreakDayState.Empty) {
            Spacer(modifier = Modifier.size(40.dp))
        } else {
            Box(
                modifier = bubbleModifier
                    .size(40.dp)
                    .then(
                        if (day.state == StreakDayState.Current) {
                            Modifier.border(2.dp, Color(0xFFFFD2AF), CircleShape)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (day.state == StreakDayState.Freeze) "❄" else day.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakCalendarPreview() {
    val days = listOf(
        StreakCalendarDay("", StreakDayState.Empty),
        StreakCalendarDay("", StreakDayState.Empty),
        StreakCalendarDay("", StreakDayState.Empty),
        StreakCalendarDay("", StreakDayState.Empty),
        StreakCalendarDay("", StreakDayState.Empty),
        StreakCalendarDay("1", StreakDayState.Missed),
        StreakCalendarDay("2", StreakDayState.Completed),
        StreakCalendarDay("3", StreakDayState.Completed),
        StreakCalendarDay("4", StreakDayState.Completed),
        StreakCalendarDay("5", StreakDayState.Completed),
        StreakCalendarDay("6", StreakDayState.Completed),
        StreakCalendarDay("7", StreakDayState.Completed),
        StreakCalendarDay("8", StreakDayState.Completed),
        StreakCalendarDay("9", StreakDayState.Freeze),
        StreakCalendarDay("10", StreakDayState.Completed),
        StreakCalendarDay("11", StreakDayState.Completed),
        StreakCalendarDay("12", StreakDayState.Completed),
        StreakCalendarDay("13", StreakDayState.Completed),
        StreakCalendarDay("14", StreakDayState.Missed),
        StreakCalendarDay("15", StreakDayState.Missed),
        StreakCalendarDay("16", StreakDayState.Completed),
        StreakCalendarDay("17", StreakDayState.Completed),
        StreakCalendarDay("18", StreakDayState.Completed),
        StreakCalendarDay("19", StreakDayState.Completed),
        StreakCalendarDay("20", StreakDayState.Completed),
        StreakCalendarDay("21", StreakDayState.Completed),
        StreakCalendarDay("22", StreakDayState.Completed),
        StreakCalendarDay("23", StreakDayState.Completed),
        StreakCalendarDay("24", StreakDayState.Current),
        StreakCalendarDay("25", StreakDayState.Future),
        StreakCalendarDay("26", StreakDayState.Future),
        StreakCalendarDay("27", StreakDayState.Future),
        StreakCalendarDay("28", StreakDayState.Future),
        StreakCalendarDay("29", StreakDayState.Future),
        StreakCalendarDay("30", StreakDayState.Future),
        StreakCalendarDay("31", StreakDayState.Future)
    )

    MaterialTheme {
        StreakCalendar(
            monthLabel = "May 2026",
            days = days,
            isDarkTheme = false,
            canNavigatePrevious = true,
            canNavigateNext = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

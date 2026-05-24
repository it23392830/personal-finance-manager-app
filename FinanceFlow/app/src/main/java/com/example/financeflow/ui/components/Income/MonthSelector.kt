package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Month
import java.time.format.TextStyle
import java.util.*

private val LightTextDark = Color(0xFF1F2937)
private val LightMuted = Color(0xFF6B7280)
private val LightFieldBg = Color.White
private val LightBorder = Color(0xFFD1D5DB)
private val LightAccent = Color(0xFF22C55E)

private val DarkTextDark = Color(0xFFE8E8E8)
private val DarkMuted = Color(0xFFB0B0B0)
private val DarkFieldBg = Color(0xFF2A2A3E)
private val DarkBorder = Color(0xFF3A3A4E)
private val DarkAccent = Color(0xFF2DBD6E)

private data class MonthSelectorColors(
    val textDark: Color,
    val muted: Color,
    val fieldBg: Color,
    val border: Color,
    val accent: Color
)

private fun getMonthSelectorColors(isDarkTheme: Boolean): MonthSelectorColors =
    if (isDarkTheme) {
        MonthSelectorColors(DarkTextDark, DarkMuted, DarkFieldBg, DarkBorder, DarkAccent)
    } else {
        MonthSelectorColors(LightTextDark, LightMuted, LightFieldBg, LightBorder, LightAccent)
    }

/**
 * Month + Year pair passed to [MonthSelector].
 */
data class MonthYear(val year: Int, val month: Int /* 1-based */) {
    override fun toString(): String {
        val monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        return "$monthName $year"
    }
}

/**
 * Generates a list of the last [count] months ending at (or before) [currentYear]/[currentMonth].
 */
fun generateMonthOptions(
    currentYear: Int,
    currentMonth: Int,
    count: Int = 12
): List<MonthYear> {
    val list = mutableListOf<MonthYear>()
    var year = currentYear
    var month = currentMonth
    repeat(count) {
        list.add(MonthYear(year, month))
        month--
        if (month == 0) {
            month = 12
            year--
        }
    }
    return list
}

/**
 * A dropdown that lets the user pick a month+year combination.
 *
 * @param selected     Currently selected [MonthYear].
 * @param options      Full list of selectable [MonthYear] options.
 * @param onSelected   Callback fired when the user picks a different month.
 * @param modifier     Optional [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelector(
    isDarkTheme: Boolean = false,
    selected: MonthYear,
    options: List<MonthYear>,
    onSelected: (MonthYear) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getMonthSelectorColors(isDarkTheme)
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        // ── Trigger field ────────────────────────────────────────────────────
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand month selector",
                    tint = colors.accent
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.border,
                focusedTextColor = colors.textDark,
                unfocusedTextColor = colors.textDark,
                focusedContainerColor = colors.fieldBg,
                unfocusedContainerColor = colors.fieldBg
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        // ── Dropdown menu ────────────────────────────────────────────────────
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { monthYear ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = monthYear.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (monthYear == selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (monthYear == selected) colors.accent else colors.textDark
                        )
                    },
                    onClick = {
                        onSelected(monthYear)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
private fun MonthSelectorPreview() {
    val options = generateMonthOptions(2026, 5)
    MonthSelector(
        isDarkTheme = false,
        selected = options.first(),
        options = options,
        onSelected = {},
        modifier = Modifier.padding(16.dp)
    )
}
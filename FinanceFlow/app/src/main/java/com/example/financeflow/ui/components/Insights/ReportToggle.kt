package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ToggleBg       = Color(0xFFE5E1F0)   // outer pill background
private val SelectedBg     = Color(0xFFFFFFFF)   // selected tab
private val SelectedText   = Color(0xFF1E1B2E)
private val UnselectedText = Color(0xFF9CA3AF)

/**
 * ReportToggle
 *
 * A segmented pill control with three tabs: Daily | Weekly | Monthly.
 * Matches the Figma design: white rounded selected chip on a gray pill.
 *
 * @param selected   The currently active tab ("Daily", "Weekly", or "Monthly").
 * @param onSelect   Callback with the newly selected tab label.
 */
@Composable
fun ReportToggle(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Daily", "Weekly", "Monthly")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(ToggleBg)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selected
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isSelected) SelectedBg else Color.Transparent)
                        .clickable { onSelect(tab) }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) SelectedText else UnselectedText
                    )
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun ReportTogglePreview() {
    ReportToggle(selected = "Weekly", onSelect = {})
}
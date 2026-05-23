package com.example.financeflow.ui.components.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.theme.CardWhite
import com.example.financeflow.ui.theme.OrangeAccent

data class SavingHistoryEntry(
    val month: String,
    val date: String,
    val savingRate: String,
    val income: String,
    val saved: String
)

val dummyHistory = listOf(
    SavingHistoryEntry("May 2026", "2026.05.05", "28%", "LKR 196,400", "LKR 53,200"),
    SavingHistoryEntry("Apr 2026", "2026.04.25", "28%", "LKR 187,500", "LKR 53,200"),
    SavingHistoryEntry("Mar 2026", "2026.03.05", "26%", "LKR 191,000", "LKR 48,900")
)

@Composable
fun SavingsHistoryCard(
    isDarkTheme: Boolean = false,
    entries: List<SavingHistoryEntry> = dummyHistory,
    onEditClick: () -> Unit = {},
    onDeleteClick: (SavingHistoryEntry) -> Unit = {}
) {
    val colors = getSavingsColors(isDarkTheme)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Savings History",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        entries.forEach { entry ->
            HistoryItem(
                isDarkTheme = isDarkTheme,
                entry = entry,
                onEditClick = onEditClick,
                onDeleteClick = { onDeleteClick(entry) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun HistoryItem(
    isDarkTheme: Boolean = false,
    entry: SavingHistoryEntry,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = getSavingsColors(isDarkTheme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.formBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = entry.month,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "Income: ${entry.income}",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Saved: ${entry.saved}",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accent.copy(alpha = 0.4f))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Rate ${entry.savingRate}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = colors.textSecondary
                    )
                }

                ActionMenuCard(
                    isDarkTheme = isDarkTheme,
                    expanded = expanded,
                    onDismiss = { expanded = false },
                    onEditClick = {
                        onEditClick()
                        expanded = false
                    },
                    onDeleteClick = {
                        onDeleteClick()
                        expanded = false
                    }
                )
            }
        }
    }
}

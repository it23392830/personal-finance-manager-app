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
    val incomeSource: String,
    val saved: String,
    val id: String = ""
)

val dummyHistory = listOf(
    SavingHistoryEntry("June 2026", "05/06/2026", "22%", "Salary", "LKR 10,000"),
    SavingHistoryEntry("May 2026", "25/05/2026", "15%", "Freelancing", "LKR 8,000"),
    SavingHistoryEntry("Apr 2026", "10/04/2026", "10%", "Google AdSense", "LKR 5,000")
)

@Composable
fun SavingsHistoryCard(
    isDarkTheme: Boolean = false,
    entries: List<SavingHistoryEntry> = dummyHistory,
    onEditClick: (SavingHistoryEntry) -> Unit = {},
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
                onEditClick = { onEditClick(entry) },
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
                    text = "Source: ${entry.incomeSource}",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Saved: ${entry.saved}",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Rate: ${entry.savingRate}",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Date: ${entry.date}",
                    fontSize = 12.sp,
                    color = colors.muted
                )
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

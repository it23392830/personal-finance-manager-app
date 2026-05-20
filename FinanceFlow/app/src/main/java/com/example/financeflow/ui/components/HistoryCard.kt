package com.example.financeflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data model for a single history entry
data class SavingHistoryEntry(
    val month: String,
    val date: String,
    val savingRate: String,
    val income: String,
    val saved: String
)

// Hardcoded dummy history entries
val dummyHistory = listOf(
    SavingHistoryEntry(
        month = "May 2026",
        date = "2026.05.05",
        savingRate = "28%",
        income = "LKR 187,500",
        saved = "LKR 53,200"
    ),
    SavingHistoryEntry(
        month = "Apr 2026",
        date = "2026.04.25",
        savingRate = "28%",
        income = "LKR 187,500",
        saved = "LKR 53,200"
    ),
    SavingHistoryEntry(
        month = "Mar 2026",
        date = "2026.03.05",
        savingRate = "26%",
        income = "LKR 191,000",
        saved = "LKR 48,900"
    )
)

@Composable
fun SavingsHistoryCard(
    entries: List<SavingHistoryEntry> = dummyHistory,
    onEditClick: () -> Unit = {},
    onDeleteClick: (SavingHistoryEntry) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Savings History",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        entries.forEach { entry ->
            HistoryItem(
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
    entry: SavingHistoryEntry,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    // Controls whether the three-dot menu is expanded for this row.
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.month,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = entry.date,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = entry.savingRate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangeAccent
                )

                Spacer(modifier = Modifier.width(4.dp))

                Box {
                    // The MoreVert icon anchors the action menu for each record.
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    ActionMenuCard(
                        expanded = menuExpanded,
                        onDismiss = { menuExpanded = false },
                        onEditClick = {
                            onEditClick()
                            menuExpanded = false
                        },
                        onDeleteClick = {
                            onDeleteClick()
                            menuExpanded = false
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Income", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.income,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Saved", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.saved,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangeAccent
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewSavingsHistoryCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SavingsHistoryCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewHistoryItem() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HistoryItem(entry = dummyHistory[0])
        }
    }
}

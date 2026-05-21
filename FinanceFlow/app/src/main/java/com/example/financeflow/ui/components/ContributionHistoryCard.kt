package com.example.financeflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data model for a single contribution history entry

data class ContributionEntry(
    val month: String,     // e.g. "May 2026"
    val date: String,      // e.g. "2026.05.05"
    val title: String,     // e.g. "MacBook Pro M4"
    val amount: String     // e.g. "LKR 160000.00"
)

// Hardcoded dummy contribution entries
// ─────────────────────────────────────────────────────────────────────────────
val dummyContributions = listOf(
    ContributionEntry(
        month  = "May 2026",
        date   = "2026.05.05",
        title  = "MacBook Pro M4",
        amount = "LKR 160,000.00"
    ),
    ContributionEntry(
        month  = "Apr 2026",
        date   = "2026.04.15",
        title  = "Emergency Deposit",
        amount = "LKR 170,000.00"
    ),
    ContributionEntry(
        month  = "Mar 2026",
        date   = "2026.03.18",
        title  = "Vacation Deposit",
        amount = "LKR 165,500.00"
    ),
    ContributionEntry(
        month  = "Feb 2026",
        date   = "2026.02.09",
        title  = "Travel Deposit",
        amount = "LKR 158,000.31"
    )
)


// ContributionHistoryCard
//
// A single white rounded card representing one contribution entry.
// Layout:
//   LEFT  → month (bold), date (gray small), title (medium)
//   RIGHT → amount (bold, dark)
//
// Parameters:
//   entry  – the ContributionEntry to display

@Composable
fun ContributionHistoryCard(entry: ContributionEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Left: month / date / title
            Column(modifier = Modifier.weight(1f)) {

                // Month – bold, dark
                Text(
                    text = entry.month,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Date – small gray
                Text(
                    text = entry.date,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Title – medium weight
                Text(
                    text = entry.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444)
                )
            }

            // ── Right: amount
            Text(
                text = entry.amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

// ContributionHistoryList
//
// Convenience composable that renders the full list of contribution cards
// with consistent vertical spacing. Used directly inside GoalDetailsScreen.

@Composable
fun ContributionHistoryList(entries: List<ContributionEntry> = dummyContributions) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        entries.forEach { entry ->
            ContributionHistoryCard(entry = entry)
        }
    }
}

// Previews

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewContributionHistoryCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ContributionHistoryCard(entry = dummyContributions[0])
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewContributionHistoryList() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ContributionHistoryList()
        }
    }
}
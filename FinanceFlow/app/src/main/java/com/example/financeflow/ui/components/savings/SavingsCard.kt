package com.example.financeflow.ui.components.savings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderCard(
    isDarkTheme: Boolean = false,
    selectedMonth: String = "May 2026",
    onMonthSelected: (String) -> Unit = {}
) {
    val months = listOf("Jan 2026", "Feb 2026", "Mar 2026", "Apr 2026", "May 2026", "Jun 2026")
    var expanded by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(selectedMonth) }
    val colors = getSavingsColors(isDarkTheme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Savings Overview", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                    Text(text = "Track your saving habits & allocations", fontSize = 12.sp, color = colors.muted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(border = BorderStroke(1.5.dp, colors.accent), shape = RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentMonth, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = colors.accent)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    months.forEach { month ->
                        DropdownMenuItem(text = { Text(text = month) }, onClick = {
                            currentMonth = month; onMonthSelected(month); expanded = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsThisMonthCard(
    isDarkTheme: Boolean = false,
    amount: String = "LKR 53,200",
    totalIncome: String = "LKR 187,500",
    savingRate: String = "28%",
    onAddNewSaving: () -> Unit = {}
) {
    val colors = getSavingsColors(isDarkTheme)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Savings This Month", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = colors.accent)

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStatCard(isDarkTheme = isDarkTheme, label = "Total Income", value = totalIncome, borderColor = colors.success, modifier = Modifier.weight(1f))
                MiniStatCard(isDarkTheme = isDarkTheme, label = "Saving Rate", value = savingRate, borderColor = colors.accent, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onAddNewSaving, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.White)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "+ Add New Saving", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MiniStatCard(
    isDarkTheme: Boolean = false,
    label: String,
    value: String,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = getSavingsColors(isDarkTheme)
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(12.dp)).background(colors.cardBg).padding(horizontal = 12.dp, vertical = 10.dp)) {
        Column {
            Text(text = label, fontSize = 11.sp, color = colors.muted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        }
    }
}

@Composable
fun SavingsByGoalCard(
    isDarkTheme: Boolean = false,
    goals: List<SavingGoal> = defaultGoals,
    onEditClick: (SavingGoal) -> Unit = {},
    onDeleteClick: (SavingGoal) -> Unit = {}
) {
    val colors = getSavingsColors(isDarkTheme)
    Card(modifier = Modifier.fillMaxWidth().shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = colors.cardBg)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Savings by Goal", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            if (goals.isEmpty()) {
                Text(text = "No goals yet. Add one to get started!", fontSize = 13.sp, color = colors.muted, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                goals.forEachIndexed { index, goal ->
                    GoalProgressItem(isDarkTheme = isDarkTheme, goal = goal, onEditClick = { onEditClick(goal) }, onDeleteClick = { onDeleteClick(goal) })
                    if (index < goals.lastIndex) {
                        Divider(modifier = Modifier.padding(vertical = 14.dp), color = colors.progressTrack)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewHeaderCard() {
    MaterialTheme { Box(modifier = Modifier.padding(16.dp)) { HeaderCard() } }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewSavingsThisMonthCard() {
    MaterialTheme { Box(modifier = Modifier.padding(16.dp)) { SavingsThisMonthCard() } }
}

package com.example.financeflow.ui.goals

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.viewmodel.goal.GoalViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ─── Formatters ──────────────────────────────────────────────────────────────

private fun formatCurrency(amount: Double, currency: String = "LKR"): String {
    return if (currency == "LKR") {
        "LKR ${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(amount)}"
    } else {
        "$currency ${String.format(Locale.US, "%.2f", amount)}"
    }
}

private fun formatDate(timestamp: com.google.firebase.Timestamp): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

// ─── Integrated Goals Dashboard ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.goalListState.collectAsState()
    val detailState by viewModel.goalDetailState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddContribution by remember { mutableStateOf(false) }

    // Auto-select first goal on load if none selected
    LaunchedEffect(uiState.goals) {
        if (detailState.goal == null && uiState.goals.isNotEmpty()) {
            viewModel.loadGoalDetail(uiState.goals.first().id)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            item {
                GoalsHeader(onCreateGoal = onNavigateToCreate)
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.6f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF8A2BE2))
                        }
                    }
                }
                uiState.goals.isEmpty() -> {
                    item { EmptyGoalsState(onCreateGoal = onNavigateToCreate) }
                }
                else -> {
                    item {
                        Text(
                            text = "All Goals (${uiState.goals.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
                        )
                    }

                    // Compact List of Goals at the top
                    items(items = uiState.goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            isSelected = detailState.goal?.id == goal.id,
                            onClick = { viewModel.loadGoalDetail(goal.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }

                    // Integrated Detail Section opens at the bottom
                    if (detailState.goal != null) {
                        val goal = detailState.goal!!
                        val themeColor = getCategoryColor(goal.category)
                        
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            GoalDetailHeader(goal)
                        }

                        item {
                            GoalSummaryGrid(goal)
                        }

                        if (!goal.isOnTrack) {
                            item {
                                GoalWarningCard(goal)
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0F0F0))
                                    .padding(4.dp)
                            ) {
                                TabButton(
                                    text = "Milestones", 
                                    selected = selectedTab == 0, 
                                    onClick = { selectedTab = 0 }, 
                                    modifier = Modifier.weight(1f)
                                )
                                TabButton(
                                    text = "Contributions", 
                                    selected = selectedTab == 1, 
                                    onClick = { selectedTab = 1 }, 
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (selectedTab == 0) {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    GoalMilestonesSection(goal)
                                }
                            }
                            item {
                                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                    GoalInsightSection(goal)
                                }
                            }
                        } else {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    MonthlyContributionsHeader(onAddClick = { showAddContribution = true })
                                }
                            }
                            if (detailState.allocations.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No contribution records yet",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                items(items = detailState.allocations, key = { it.id }) { allocation ->
                                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                        AllocationItem(allocation, goal.currency)
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { showAddContribution = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Contribution", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = onNavigateToCreate,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        ) {
                            Text("+ Create New Goal", color = Color(0xFF8A2BE2), fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAddContribution && detailState.goal != null) {
        AddContributionDialog(
            goalId = detailState.goal!!.id,
            onDismiss = { showAddContribution = false },
            viewModel = viewModel
        )
    }
}

// ─── Goal Summary Components ────────────────────────────────────────────────

@Composable
private fun GoalSummaryGrid(goal: Goal) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            label = "Required Saved",
            value = "Rs. ${String.format(Locale.US, "%,.0f", goal.monthlySavingTarget)}",
            subLabel = "Monthly Target",
            color = Color(0xFFE8EAF6),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "Current Rate",
            value = "Rs. ${String.format(Locale.US, "%,.0f", goal.monthlySavingTarget * 0.92)}",
            subLabel = "monthly average",
            color = Color(0xFFE8F5E9),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(label: String, value: String, subLabel: String, color: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = color) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subLabel, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun GoalWarningCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
        border = BorderStroke(1.dp, Color(0xFFFFF176))
    ) {
        Row(
            modifier = Modifier.padding(12.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFFBC02D), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Need to increase contributions", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(text = "You need LKR 4,100 more per month.", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
            }
        }
    }
}

// ─── Sub-Components ─────────────────────────────────────────────────────────

@Composable
private fun GoalDetailHeader(goal: Goal) {
    val gradientColors = when (goal.category) {
        "Lifestyle" -> listOf(Color(0xFF00C853), Color(0xFFB2FF59))
        "Security" -> listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
        else -> listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))
    }
    
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.background(Brush.verticalGradient(gradientColors)).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(getCategoryIcon(goal.category), null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(goal.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                    Text(
                        text = if (goal.isOnTrack) "On Track" else "Behind", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                        style = MaterialTheme.typography.labelSmall, 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Target: ${formatCurrency(goal.targetAmount, goal.currency)}", 
                color = Color.White, 
                style = MaterialTheme.typography.bodyMedium, 
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", color = Color.White, fontWeight = FontWeight.Bold)
                Text("${String.format(Locale.US, "%.1f", goal.progressPercentage)}%", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (goal.progressPercentage / 100).toFloat() }, 
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), 
                color = Color.White, 
                trackColor = Color.White.copy(alpha = 0.3f), 
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatCurrency(goal.currentSavedAmount, goal.currency), color = Color.White, fontSize = 12.sp)
                Text(formatCurrency(goal.targetAmount, goal.currency), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GoalMilestonesSection(goal: Goal) {
    var selectedBadge by remember { mutableStateOf<GoalBadge?>(null) }
    if (selectedBadge != null) {
        MilestoneDetailDialog(
            badge = selectedBadge!!, 
            unlocked = selectedBadge!!.id in goal.unlockedBadges, 
            onDismiss = { selectedBadge = null }
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "${goal.title} Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        GoalBadge.values().forEach { badge ->
            val unlocked = badge.id in goal.unlockedBadges
            MilestoneItem(badge = badge, unlocked = unlocked, goal = goal, onClick = { selectedBadge = badge })
        }
    }
}

@Composable
private fun MilestoneItem(badge: GoalBadge, unlocked: Boolean, goal: Goal, onClick: () -> Unit) {
    val categoryColor = getCategoryColor(goal.category)
    Card(
        onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (unlocked) Color.White else Color(0xFFF5F5F5)),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape, 
                color = if (unlocked) categoryColor.copy(alpha = 0.15f) else Color(0xFFF0F0F0), 
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = badge.emoji, 
                        fontSize = 20.sp, 
                        modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.3f }
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(badge.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (unlocked) Color.Black else Color.Gray)
                Text(
                    text = if (unlocked) "Achieved" else "Required: ${(badge.threshold * 100).toInt()}%", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Color.Gray
                )
            }
            if (unlocked) {
                Surface(shape = RoundedCornerShape(8.dp), color = categoryColor.copy(alpha = 0.1f), modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = "Unlocked", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                        style = MaterialTheme.typography.labelSmall, 
                        color = categoryColor, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneDetailDialog(badge: GoalBadge, unlocked: Boolean, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = badge.emoji, fontSize = 64.sp, modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.4f })
                Spacer(Modifier.height(20.dp))
                Text(badge.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (unlocked) "Great job! You reached this milestone by saving ${(badge.threshold * 100).toInt()}% of your target." 
                    else "Reach ${(badge.threshold * 100).toInt()}% to unlock this badge!", 
                    textAlign = TextAlign.Center, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.Gray
                )
                Spacer(Modifier.height(32.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) { 
                    Text("Got it") 
                }
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick, modifier = modifier, shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Text(text, modifier = Modifier.padding(vertical = 10.dp), textAlign = TextAlign.Center, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = if (selected) Color.Black else Color.Gray, fontSize = 14.sp)
    }
}

@Composable
private fun StatusChip(isOnTrack: Boolean) {
    val (color, text) = if (isOnTrack) Pair(Color(0xFFE8F5E9), "On Track") else Pair(Color(0xFFFFF3E0), "Behind")
    Surface(shape = RoundedCornerShape(8.dp), color = color) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isOnTrack) Color(0xFF2E7D32) else Color(0xFFEF6C00))
    }
}

@Composable
private fun AllocationItem(allocation: GoalAllocation, currency: String) {
    val isMet = allocation.amount >= allocation.monthlyTarget
    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White), 
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF5F5F5), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = allocation.monthYear, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = "Target: ${formatCurrency(allocation.monthlyTarget, currency)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = formatCurrency(allocation.amount, currency), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isMet) Color(0xFF2E7D32) else Color(0xFFC62828))
                Surface(shape = RoundedCornerShape(8.dp), color = if (isMet) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                    Text(text = if (isMet) "Met" else "Below", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = if (isMet) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddContributionDialog(
    goalId: String,
    onDismiss: () -> Unit,
    viewModel: GoalViewModel
) {
    val uiState by viewModel.addAllocationState.collectAsState()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetAllocationState()
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Contribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                AddContributionField("Month", uiState.monthYear, viewModel::onAllocationMonthYearChanged)
                AddContributionField("Contribution Amount (LKR)", uiState.amount, viewModel::onAllocationAmountChanged, KeyboardType.Decimal)
                AddContributionField("Monthly Target (LKR)", uiState.monthlyTarget, viewModel::onAllocationMonthlyTargetChanged, KeyboardType.Decimal)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.submitAllocation(goalId) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("+ Add Contribution", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddContributionField(label: String, value: String, onValueChange: (String) -> Unit, type: KeyboardType = KeyboardType.Text) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = type),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}

// ─── Helpers ────────────────────────────────────────────────────────────────

private fun getCategoryColor(category: String): Color = when (category) {
    "Technology" -> Color(0xFF8A2BE2)
    "Security" -> Color(0xFF2196F3)
    "Lifestyle" -> Color(0xFF4CAF50)
    else -> Color(0xFF9C27B0)
}

private fun getCategoryIcon(category: String): ImageVector = when (category) {
    "Technology" -> Icons.Default.Laptop
    "Security" -> Icons.Default.Shield
    "Lifestyle" -> Icons.Default.Flight
    else -> Icons.Default.Star
}

@Composable
fun GoalDetailScreen(
    goalId: String,
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    LaunchedEffect(goalId) {
        viewModel.loadGoalDetail(goalId)
    }
    GoalsScreen(viewModel = viewModel)
}

@Composable
private fun GoalsHeader(onCreateGoal: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))))
            .padding(24.dp)
            .padding(top = 16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Financial Goals", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track your progress & stay motivated", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
                Button(
                    onClick = onCreateGoal,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Goal", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("May 2026", color = Color.White, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MonthlyContributionsHeader(onAddClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Monthly Contributions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = onAddClick, 
            shape = RoundedCornerShape(12.dp), 
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), 
            modifier = Modifier.height(32.dp), 
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("+ Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyGoalsState(onCreateGoal: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎯", fontSize = 64.sp)
        Text("No goals yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Button(onClick = onCreateGoal, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) { Text("Create a Goal") }
    }
}

@Composable
private fun GoalInsightSection(goal: Goal) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Goal Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        InsightCard(
            title = "Keep it up!", 
            description = "At your current rate, you'll reach your ${goal.title} goal in ~${String.format(Locale.US, "%.1f", goal.monthsRemaining)} months.", 
            icon = Icons.AutoMirrored.Filled.TrendingUp, 
            color = Color(0xFFE3F2FD)
        )
        InsightCard(
            title = "3 Active Goals", 
            description = "You're managing 3 financial goals simultaneously.", 
            icon = Icons.Default.Flag, 
            color = Color(0xFFF3E5F5)
        )
    }
}

@Composable
private fun InsightCard(title: String, description: String, icon: ImageVector, color: Color) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.createGoalState.collectAsState()
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetCreateGoalState()
            onNavigateBack()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(value = uiState.title, onValueChange = viewModel::onCreateTitleChanged, label = { Text("Goal Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.targetAmount, onValueChange = viewModel::onCreateTargetAmountChanged, label = { Text("Target Amount (LKR)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Text("Timeframe: ${uiState.deadlineMonths} months")
            Slider(value = uiState.deadlineMonths.toFloat(), onValueChange = { viewModel.onCreateDeadlineMonthsChanged(it.toInt()) }, valueRange = 1f..36f)
            Button(onClick = viewModel::submitCreateGoal, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) { Text("Create Goal") }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(goal.category)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) categoryColor.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) categoryColor else Color(0xFFEEEEEE)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = categoryColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(getCategoryIcon(goal.category), null, tint = categoryColor)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(goal.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(goal.progressPercentage).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                )
                StatusChip(goal.isOnTrack)
            }
        }
    }
}

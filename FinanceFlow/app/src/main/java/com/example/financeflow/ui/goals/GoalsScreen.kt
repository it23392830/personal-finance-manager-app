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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.viewmodel.goal.GoalViewModel
import java.text.NumberFormat
import java.util.*

// ─── Formatters ──────────────────────────────────────────────────────────────

fun formatCurrency(amount: Double, currency: String = "LKR"): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply { 
        maximumFractionDigits = 0 
    }
    val formatted = formatter.format(amount)
    return if (currency == "LKR") "LKR $formatted" else "$currency $formatted"
}

// ─── Category Helpers ────────────────────────────────────────────────────────

fun getCategoryColor(category: String): Color = when (category) {
    "Technology" -> Color(0xFF8A2BE2) // Purple
    "Security"   -> Color(0xFF2196F3) // Blue
    "Lifestyle"  -> Color(0xFF4CAF50) // Green
    else         -> Color(0xFF9C27B0)
}

fun getCategoryGradient(category: String): List<Color> = when (category) {
    "Technology" -> listOf(Color(0xFF8A2BE2), Color(0xFF6F00FF))
    "Security"   -> listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
    "Lifestyle"  -> listOf(Color(0xFF4CAF50), Color(0xFF00C853))
    else         -> listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7))
}

fun getCategoryIcon(category: String): ImageVector = when (category) {
    "Technology" -> Icons.Default.Laptop
    "Security"   -> Icons.Default.Shield
    "Lifestyle"  -> Icons.Default.Flight
    "Vehicle"    -> Icons.Default.DirectionsCar
    "Home"       -> Icons.Default.Home
    "Education"  -> Icons.Default.School
    "Health"     -> Icons.Default.Favorite
    else         -> Icons.Default.Star
}

fun getColorFromName(name: String): Color = when (name) {
    "Purple" -> Color(0xFF7C4DFF)
    "Blue"   -> Color(0xFF2196F3)
    "Green"  -> Color(0xFF4CAF50)
    "Yellow" -> Color(0xFFFFEB3B)
    "Orange" -> Color(0xFFFF9800)
    "Red"    -> Color(0xFFF44336)
    "Pink"   -> Color(0xFFE91E63)
    "Black"  -> Color(0xFF000000)
    else     -> Color.Gray
}

// ─── Integrated Goals Dashboard ──────────────────────────────────────────────

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
    var showCreateGoal by remember { mutableStateOf(false) }
    var selectedBadge by remember { mutableStateOf<GoalBadge?>(null) }

    LaunchedEffect(uiState.goals) {
        if (detailState.goal == null && uiState.goals.isNotEmpty()) {
            viewModel.loadGoalDetail(uiState.goals.first().id)
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            item {
                GoalsHeader(
                    selectedGoal = detailState.goal,
                    onCreateGoal = { showCreateGoal = true }
                )
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.6f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF6F00FF))
                        }
                    }
                }
                uiState.goals.isEmpty() -> {
                    item { EmptyGoalsState(onCreateGoal = { showCreateGoal = true }) }
                }
                else -> {
                    item {
                        Text(
                            text = "All Goals (${uiState.goals.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
                        )
                    }

                    items(items = uiState.goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            isSelected = detailState.goal?.id == goal.id,
                            onClick = { viewModel.loadGoalDetail(goal.id) },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    if (detailState.goal != null) {
                        val goal = detailState.goal!!
                        val categoryColor = getCategoryColor(goal.category)
                        
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            GoalDetailLargeCard(goal)
                        }

                        item {
                            GoalSummaryGrid(goal)
                        }

                        item { GoalWarningCard(goal) }

                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 20.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFFF2F2F2))
                                    .padding(4.dp)
                            ) {
                                TabButton(
                                    text = "Milestones", 
                                    selected = selectedTab == 0, 
                                    onClick = { selectedTab = 0 }, 
                                    activeColor = categoryColor,
                                    modifier = Modifier.weight(1f)
                                )
                                TabButton(
                                    text = "Contributions", 
                                    selected = selectedTab == 1, 
                                    onClick = { selectedTab = 1 }, 
                                    activeColor = categoryColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (selectedTab == 0) {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    GoalMilestonesSection(goal, onBadgeClick = { selectedBadge = it })
                                }
                            }
                        } else {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    MonthlyContributionsHeader(onAddClick = { showAddContribution = true })
                                }
                            }
                            if (detailState.allocations.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                                        Text("No contributions tagged yet", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                    }
                                }
                            } else {
                                items(items = detailState.allocations, key = { it.id }) { allocation ->
                                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                                        AllocationItem(allocation, goal.currency)
                                    }
                                }
                            }
                        }

                        item {
                            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                                GoalInsightSection(goal)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { showCreateGoal = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6F00FF))
                        ) {
                            Text("+ Create New Goal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(40.dp)) }
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

    if (showCreateGoal) {
        CreateGoalDialog(
            onDismiss = { showCreateGoal = false },
            viewModel = viewModel
        )
    }

    if (selectedBadge != null && detailState.goal != null) {
        MilestoneDetailDialog(
            badge = selectedBadge!!,
            unlocked = selectedBadge!!.id in detailState.goal!!.unlockedBadges,
            goal = detailState.goal!!,
            onDismiss = { selectedBadge = null }
        )
    }
}

// ─── Sub-Components ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalCard(
    goal: Goal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(goal.category)
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) categoryColor else Color(0xFFEEEEEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = categoryColor,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(getCategoryIcon(goal.category), null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                    Text("${goal.category}  ${goal.daysRemaining}d left", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                StatusChip(goal.isOnTrack)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = (goal.progressPercentage / 100).toFloat(),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Color.Black,
                trackColor = Color(0xFFF5F5F5),
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "LKR ${formatter.format(goal.currentSavedAmount)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 12.sp)
                Text(text = String.format(Locale.US, "%.1f %%", goal.progressPercentage), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 12.sp)
                Text(text = "LKR ${formatter.format(goal.targetAmount)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GoalsHeader(
    selectedGoal: Goal?,
    onCreateGoal: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("May 2026") }
    val months = listOf("May 2026", "April 2026", "March 2026", "February 2026", "January 2026")

    val primaryColor = Color(0xFF6F00FF)
    val secondaryColor = Color(0xFF8A2BE2)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(primaryColor, secondaryColor)))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Financial Goals", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track your progress & stay motivated", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
                Surface(
                    onClick = onCreateGoal,
                    color = Color.White.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ New Goal", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                onClick = { expanded = true },
                color = Color.White.copy(alpha = 0.2f), 
                shape = RoundedCornerShape(12.dp), 
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(selectedMonth, color = Color.White, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)) {
                months.forEach { month -> DropdownMenuItem(text = { Text(month, color = Color.Black) }, onClick = { selectedMonth = month; expanded = false }) }
            }
        }
    }
}

@Composable
private fun GoalDetailLargeCard(goal: Goal) {
    val gradientColors = getCategoryGradient(goal.category)
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.background(Brush.horizontalGradient(gradientColors)).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(getCategoryIcon(goal.category), null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(goal.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text("${goal.daysRemaining} days", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (goal.description.isNotEmpty()) Text(goal.description, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            Text("Target: ${formatCurrency(goal.targetAmount)}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(String.format(Locale.US, "%.1f %%", goal.progressPercentage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = (goal.progressPercentage / 100).toFloat(), modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), color = Color.White, trackColor = Color.White.copy(alpha = 0.3f), strokeCap = StrokeCap.Round)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatCurrency(goal.currentSavedAmount), color = Color.White, fontSize = 13.sp)
                Text(formatCurrency(goal.targetAmount), color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GoalSummaryGrid(goal: Goal) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard("Required Money", "Rs. 23,900", "to stay on track", Color(0xFFDDE7F9), Color(0xFF1976D2), Modifier.weight(1f))
        SummaryCard("Current Rate", "Rs. 21,250", "monthly average", Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(label: String, value: String, subLabel: String, color: Color, labelColor: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = color) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = labelColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subLabel, style = MaterialTheme.typography.labelSmall, color = labelColor.copy(alpha = 0.8f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun GoalWarningCard(goal: Goal) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9E7)), border = BorderStroke(1.dp, Color(0xFFF7DC6F))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, null, tint = Color(0xFFD4AC0D), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Need to increase contributions", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF917217))
                Text("You need LKR 2,650 more per month.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF917217).copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun MonthlyContributionsHeader(onAddClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Monthly Contributions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedButton(onClick = onAddClick, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.height(32.dp)) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GoalInsightSection(goal: Goal) {
    val categoryColor = getCategoryColor(goal.category)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Goal Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = categoryColor.copy(alpha = 0.1f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = categoryColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Keep it up!", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Text("At your current rate, you'll reach your ${goal.title} goal in approximately 20 months.", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, activeColor: Color = Color.Black) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(20.dp), color = if (selected) Color.White else Color.Transparent, shadowElevation = if (selected) 2.dp else 0.dp) {
        Text(text = text, modifier = Modifier.padding(vertical = 10.dp), textAlign = TextAlign.Center, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = if (selected) activeColor else Color.Gray, fontSize = 14.sp)
    }
}

@Composable
private fun StatusChip(isOnTrack: Boolean) {
    val (color, text) = if (isOnTrack) Pair(Color(0xFFE8F5E9), "On Track") else Pair(Color(0xFFFFEDE6), "Behind")
    Surface(shape = RoundedCornerShape(10.dp), color = color) {
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isOnTrack) Color(0xFF2E7D32) else Color(0xFFD35400))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MilestoneItem(badge: GoalBadge, unlocked: Boolean, goal: Goal, onClick: () -> Unit) {
    val categoryColor = getCategoryColor(goal.category)
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = if (unlocked) Color.White else Color(0xFFF5F5F5), border = BorderStroke(1.dp, if (unlocked) categoryColor.copy(alpha = 0.5f) else Color(0xFFEEEEEE))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = if (unlocked) categoryColor.copy(alpha = 0.15f) else Color(0xFFF0F0F0), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(text = badge.emoji, fontSize = 20.sp, modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.3f }) }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(badge.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (unlocked) Color.Black else Color.Gray)
                Text(text = if (unlocked) "Achieved" else "Required: ${(badge.threshold * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            if (unlocked) Icon(Icons.Default.CheckCircle, null, tint = categoryColor, modifier = Modifier.size(20.dp)) else Icon(Icons.Default.Lock, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun GoalMilestonesSection(goal: Goal, onBadgeClick: (GoalBadge) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("${goal.title} Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        GoalBadge.entries.forEach { badge ->
            val unlocked = badge.id in goal.unlockedBadges
            MilestoneItem(badge, unlocked, goal, onClick = { onBadgeClick(badge) })
        }
    }
}

@Composable
private fun MilestoneDetailDialog(badge: GoalBadge, unlocked: Boolean, goal: Goal, onDismiss: () -> Unit) {
    val categoryColor = getCategoryColor(goal.category)
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(shape = CircleShape, color = if (unlocked) categoryColor.copy(alpha = 0.1f) else Color(0xFFFAFAFA), modifier = Modifier.size(80.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(text = badge.emoji, fontSize = 40.sp, modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.3f }) }
                }
                Text(badge.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = if (unlocked) "Congratulations! You've unlocked this milestone." else "Keep saving to unlock this milestone!", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = categoryColor)) { Text("Great!") }
            }
        }
    }
}

@Composable
private fun AllocationItem(allocation: GoalAllocation, currency: String) {
    val isMet = allocation.amount >= allocation.monthlyTarget
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFF0F0F0))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF5F5F5), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = allocation.monthYear, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Target: ${formatCurrency(allocation.monthlyTarget, currency)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = formatCurrency(allocation.amount, currency), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isMet) Color(0xFF2E7D32) else Color(0xFFC62828))
                Surface(shape = RoundedCornerShape(8.dp), color = if (isMet) Color.Black else Color(0xFFEEEEEE)) {
                    Text(text = if (isMet) "Met" else "Below", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = if (isMet) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddContributionDialog(goalId: String, onDismiss: () -> Unit, viewModel: GoalViewModel) {
    val uiState by viewModel.addAllocationState.collectAsState()
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) { viewModel.resetAllocationState(); onDismiss() } }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Add Contribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                AddContributionField("Month", uiState.monthYear, viewModel::onAllocationMonthYearChanged)
                AddContributionField("Amount (LKR)", uiState.amount, viewModel::onAllocationAmountChanged, KeyboardType.Decimal)
                AddContributionField("Target (LKR)", uiState.monthlyTarget, viewModel::onAllocationMonthlyTargetChanged, KeyboardType.Decimal)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = Color.Gray) }
                    Button(onClick = { viewModel.submitAllocation(goalId) }, modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Add") }
                }
            }
        }
    }
}

@Composable
private fun AddContributionField(label: String, value: String, onValueChange: (String) -> Unit, type: KeyboardType = KeyboardType.Text) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = type))
    }
}

// ─── Create Goal Dialog (Replicating Mockup) ───────────────────────────────────

@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    viewModel: GoalViewModel
) {
    val uiState by viewModel.createGoalState.collectAsState()
    val icons = listOf("💻", "🛡️", "✈️", "🚐", "🏠", "🎓", "❤️", "⭐", "🎯", "📱", "🌴", "💰")
    val categories = listOf("Technology", "Security", "Lifestyle", "Vehicle", "Home", "Education", "Health", "Other")
    val colors = listOf("Purple", "Blue", "Green", "Yellow", "Orange", "Red", "Pink", "Black")

    LaunchedEffect(uiState.isSuccess) { 
        if (uiState.isSuccess) { 
            viewModel.resetCreateGoalState()
            onDismiss() 
        } 
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3E5F5),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color(0xFF9C27B0), modifier = Modifier.padding(4.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Create Goal", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.Black)
                    }
                }

                // Icon Grid
                Text("Icon", fontWeight = FontWeight.Bold, color = Color.Black)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icons.take(6).forEach { icon ->
                        IconSelectionBox(icon, isSelected = uiState.icon == icon, onClick = { viewModel.onCreateIconChanged(icon) })
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icons.drop(6).forEach { icon ->
                        IconSelectionBox(icon, isSelected = uiState.icon == icon, onClick = { viewModel.onCreateIconChanged(icon) })
                    }
                }

                // Goal Name
                CreateGoalField("Goal Name", uiState.title, viewModel::onCreateTitleChanged, "e.g. - Paris Trip")

                // Amounts Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CreateGoalField("Target Amount", uiState.targetAmount, viewModel::onCreateTargetAmountChanged, "(LKR)", Modifier.weight(1f), KeyboardType.Decimal)
                    CreateGoalField("Current Savings", uiState.currentSavings, viewModel::onCreateCurrentSavingsChanged, "(LKR)", Modifier.weight(1f), KeyboardType.Decimal)
                }

                // Deadline
                CreateGoalField("Deadline", uiState.deadline, viewModel::onCreateDeadlineChanged, "01/07/2027")

                // Monthly Target
                CreateGoalField("Monthly Contribution Target (LKR)", uiState.monthlyTarget, viewModel::onCreateMonthlyTargetChanged, "Auto-calculated if empty")

                // Category & Color
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CreateGoalDropdown("Category", uiState.category, categories, viewModel::onCreateCategoryChanged, Modifier.weight(1.1f))
                    CreateGoalDropdown("Colour", uiState.color, colors, viewModel::onCreateColorChanged, Modifier.weight(0.9f), isColor = true)
                }

                // Description
                CreateGoalField("Description (Optional)", uiState.description, viewModel::onCreateDescriptionChanged, "What's this goal for?", singleLine = false)

                Spacer(Modifier.height(8.dp))

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.submitCreateGoal() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                    ) {
                        Text("Create Goal", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun IconSelectionBox(icon: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFFF3E5F5) else Color(0xFFF5F5F5),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF9C27B0)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 20.sp)
        }
    }
}

@Composable
private fun CreateGoalField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            textStyle = TextStyle(fontSize = 13.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGoalDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isColor: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F5F5),
                onClick = { expanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isColor) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getColorFromName(value))
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = 10.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, 
                        null, 
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .width(IntrinsicSize.Max)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                if (isColor) {
                                    Box(Modifier.size(12.dp).clip(CircleShape).background(getColorFromName(option)))
                                    Spacer(Modifier.width(12.dp))
                                }
                                Text(
                                    text = option,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        onClick = { onSelected(option); expanded = false },
                        contentPadding = PaddingValues(0.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(onNavigateBack: () -> Unit, viewModel: GoalViewModel = hiltViewModel()) {
    // Redundant as we use Dialog, but kept for compatibility
    val uiState by viewModel.createGoalState.collectAsState()
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) { viewModel.resetCreateGoalState(); onNavigateBack() } }
    Scaffold(topBar = { TopAppBar(title = { Text("New Goal") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            OutlinedTextField(value = uiState.title, onValueChange = viewModel::onCreateTitleChanged, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.targetAmount, onValueChange = viewModel::onCreateTargetAmountChanged, label = { Text("Target (LKR)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Button(onClick = viewModel::submitCreateGoal, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Create Goal") }
        }
    }
}

@Composable
private fun EmptyGoalsState(onCreateGoal: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎯", fontSize = 64.sp)
        Text("No goals yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
        Button(onClick = onCreateGoal, modifier = Modifier.padding(top = 16.dp)) { Text("Create a Goal") }
    }
}

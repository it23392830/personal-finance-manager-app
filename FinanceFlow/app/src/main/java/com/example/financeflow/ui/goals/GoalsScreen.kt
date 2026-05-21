package com.example.financeflow.ui.goals

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

// ─── Goal List Screen ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.goalListState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                            Box(Modifier.fillParentMaxHeight(0.6f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF8A2BE2))
                            }
                        }
                    }
                    uiState.error != null -> {
                        item { ErrorState(message = uiState.error!!) }
                    }
                    uiState.goals.isEmpty() -> {
                        item { EmptyGoalsState(onCreateGoal = onNavigateToCreate) }
                    }
                    else -> {
                        item {
                            Text(
                                "All Goals (${uiState.goals.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
                            )
                        }
                        items(items = uiState.goals, key = { it.id }) { goal ->
                            GoalCard(
                                goal = goal,
                                onClick = { onNavigateToDetail(goal.id) },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }
                        item {
                            Button(
                                onClick = onNavigateToCreate,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Create New Goal", fontWeight = FontWeight.Bold)
                            }
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsHeader(onCreateGoal: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))
                )
            )
            .padding(24.dp)
            .padding(top = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Financial Goals",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Track your progress & stay motivated",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Button(
                    onClick = onCreateGoal,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New Goal", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "May 2026",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ─── Goal Card ────────────────────────────────────────────────────────────────

@Composable
fun GoalCard(
    goal: Goal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = Color(0xFF2D2D2D)
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getCategoryColor(goal.category).copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getCategoryIcon(goal.category),
                            contentDescription = null,
                            tint = getCategoryColor(goal.category),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(Modifier.width(12.dp))
                
                Column(Modifier.weight(1f)) {
                    Text(
                        goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${goal.category} · ${goal.daysRemaining}d left",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                StatusChip(isOnTrack = goal.isOnTrack)
            }
            
            Spacer(Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { (goal.progressPercentage / 100).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = Color(0xFFF0F0F0),
                strokeCap = StrokeCap.Round
            )
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatCurrency(goal.currentSavedAmount, goal.currency),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    "${String.format(Locale.US, "%.1f", goal.progressPercentage)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    formatCurrency(goal.targetAmount, goal.currency),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun getCategoryColor(category: String): Color = when (category) {
    "Technology" -> Color(0xFF2196F3)
    "Security" -> Color(0xFF4CAF50)
    "Lifestyle" -> Color(0xFFFF9800)
    else -> Color(0xFF9C27B0)
}

private fun getCategoryIcon(category: String): ImageVector = when (category) {
    "Technology" -> Icons.Default.Laptop
    "Security" -> Icons.Default.Shield
    "Lifestyle" -> Icons.Default.Flight
    else -> Icons.Default.Star
}

@Composable
private fun StatusChip(isOnTrack: Boolean) {
    val (color, text) = if (isOnTrack) {
        Pair(Color(0xFFE8F5E9), "On Track")
    } else {
        Pair(Color(0xFFFFF3E0), "Behind")
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isOnTrack) Color(0xFF2E7D32) else Color(0xFFEF6C00)
        )
    }
}

// ─── Goal Detail Screen ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.goalDetailState.collectAsState()
    var showAddAllocation by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(goalId) {
        viewModel.loadGoalDetail(goalId)
    }

    if (uiState.newlyUnlockedBadges.isNotEmpty()) {
        BadgeUnlockDialog(
            badges = uiState.newlyUnlockedBadges,
            onDismiss = { viewModel.clearNewBadges() }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to delete this goal?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteGoal(goalId, onNavigateBack, { showDeleteDialog = false }) }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.goal?.title ?: "Goal Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Outlined.Delete, "Delete", tint = Color.Red) }
                }
            )
        },
        bottomBar = {
            if (uiState.goal?.isCompleted == false) {
                Box(Modifier.padding(16.dp)) {
                    Button(
                        onClick = { showAddAllocation = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                    ) {
                        Icon(Icons.Default.Add, "Add funds")
                        Spacer(Modifier.width(8.dp))
                        Text("Add Funds", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8A2BE2))
                }
            }
            uiState.goal == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Goal not found") }
            }
            else -> {
                val goal = uiState.goal!!
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    item { GoalDetailHeader(goal) }
                    
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F0F0))
                                .padding(4.dp)
                        ) {
                            TabButton(text = "Milestones", selected = selectedTab == 0, onClick = { selectedTab = 0 }, modifier = Modifier.weight(1f))
                            TabButton(text = "Contributions", selected = selectedTab == 1, onClick = { selectedTab = 1 }, modifier = Modifier.weight(1f))
                        }
                    }

                    if (selectedTab == 0) {
                        item { GoalMilestonesSection(goal) }
                        item { GoalInsightSection(goal) }
                    } else {
                        items(items = uiState.allocations) { allocation ->
                            AllocationItem(allocation, goal.currency)
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAddAllocation && uiState.goal != null) {
        AddAllocationBottomSheet(goalId = goalId, goalTitle = uiState.goal!!.title, onDismiss = { showAddAllocation = false }, viewModel = viewModel)
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Text(
            text, modifier = Modifier.padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun GoalDetailHeader(goal: Goal) {
    val gradientColors = if (goal.category == "Lifestyle") listOf(Color(0xFF00C853), Color(0xFFB2FF59)) else listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.background(Brush.verticalGradient(gradientColors)).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(getCategoryIcon(goal.category), null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(goal.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.weight(1f))
                StatusChipDetail(isOnTrack = goal.isOnTrack)
            }
            Spacer(Modifier.height(8.dp))
            Text("Target: ${formatCurrency(goal.targetAmount, goal.currency)}", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", color = Color.White, fontWeight = FontWeight.Bold)
                Text("${String.format(Locale.US, "%.1f", goal.progressPercentage)}%", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (goal.progressPercentage / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = Color.White, trackColor = Color.White.copy(alpha = 0.3f), strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatCurrency(goal.currentSavedAmount, goal.currency), color = Color.White, fontSize = 12.sp)
                Text(formatCurrency(goal.targetAmount, goal.currency), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StatusChipDetail(isOnTrack: Boolean) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
        Text(if (isOnTrack) "On Track" else "Behind", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GoalInsightSection(goal: Goal) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Goal Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        InsightCard(title = "Keep it up!", description = "At your current rate, you'll reach your ${goal.title} goal in approximately ${String.format(Locale.US, "%.1f", goal.monthsRemaining)} months.", icon = Icons.Default.TrendingUp, color = Color(0xFFE3F2FD))
        InsightCard(title = "3 Active Goals", description = "You're managing 3 financial goals simultaneously. Focus on your primary goal first.", icon = Icons.Default.Flag, color = Color(0xFFF3E5F5))
    }
}

@Composable
private fun InsightCard(title: String, description: String, icon: ImageVector, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Row(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun GoalMilestonesSection(goal: Goal) {
    var selectedBadge by remember { mutableStateOf<GoalBadge?>(null) }
    if (selectedBadge != null) {
        MilestoneDetailDialog(badge = selectedBadge!!, unlocked = selectedBadge!!.id in goal.unlockedBadges, onDismiss = { selectedBadge = null })
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("${goal.title} Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        GoalBadge.entries.forEach { badge ->
            val unlocked = badge.id in goal.unlockedBadges
            MilestoneItem(badge = badge, unlocked = unlocked, onClick = { selectedBadge = badge })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MilestoneItem(badge: GoalBadge, unlocked: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (unlocked) Color.White else Color(0xFFF5F5F5)),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = if (unlocked) Color(0xFFE8F5E9) else Color(0xFFE0E0E0), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    if (unlocked) Icon(Icons.Default.Check, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    else Text(badge.emoji, fontSize = 16.sp, modifier = Modifier.graphicsLayer { alpha = 0.5f })
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(badge.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (unlocked) Color.Black else Color.Gray)
                Text(if (unlocked) "Achieved" else "Required: ${(badge.threshold * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            if (unlocked) {
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3E5F5), modifier = Modifier.padding(start = 8.dp)) {
                    Text("Unlocked", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF8A2BE2), fontWeight = FontWeight.Bold)
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
                Text(if (unlocked) badge.emoji else "🔒", fontSize = 64.sp)
                Spacer(Modifier.height(20.dp))
                Text(badge.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(if (unlocked) "Great job! You reached this milestone by saving ${(badge.threshold * 100).toInt()}% of your target." else "Reach ${(badge.threshold * 100).toInt()}% to unlock this badge!", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) {
                    Text("Got it")
                }
            }
        }
    }
}

@Composable
private fun AllocationItem(allocation: GoalAllocation, currency: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color(0xFFE8F5E9), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = Color(0xFF2E7D32)) }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(allocation.note.ifBlank { "Funds added" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(formatDate(allocation.allocatedAt), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Text("+${formatCurrency(allocation.amount, currency)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
    }
    HorizontalDivider(color = Color(0xFFEEEEEE))
}

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
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("New Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            OutlinedTextField(value = uiState.title, onValueChange = viewModel::onCreateTitleChanged, label = { Text("Goal Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.targetAmount, onValueChange = viewModel::onCreateTargetAmountChanged, label = { Text("Target Amount (LKR)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Text("Timeframe: ${uiState.deadlineMonths} months")
            Slider(value = uiState.deadlineMonths.toFloat(), onValueChange = { viewModel.onCreateDeadlineMonthsChanged(it.toInt()) }, valueRange = 1f..36f)
            Button(onClick = viewModel::submitCreateGoal, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) {
                Text("Create Goal")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAllocationBottomSheet(goalId: String, goalTitle: String, onDismiss: () -> Unit, viewModel: GoalViewModel) {
    val uiState by viewModel.addAllocationState.collectAsState()
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onDismiss() }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Add Funds to $goalTitle", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = uiState.amount, onValueChange = viewModel::onAllocationAmountChanged, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.note, onValueChange = viewModel::onAllocationNoteChanged, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.submitAllocation(goalId) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) { Text("Add Funds") }
        }
    }
}

@Composable
fun BadgeUnlockDialog(badges: List<GoalBadge>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 48.sp)
                Text("Badge Unlocked!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                badges.forEach { Text("${it.emoji} ${it.label}", style = MaterialTheme.typography.bodyLarge) }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))) { Text("Awesome!") }
            }
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
private fun ErrorState(message: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Text(message, textAlign = TextAlign.Center)
    }
}

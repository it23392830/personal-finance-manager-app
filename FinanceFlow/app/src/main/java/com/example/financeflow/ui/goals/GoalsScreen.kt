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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Goals", fontWeight = FontWeight.Bold)
                        if (uiState.goals.isNotEmpty()) {
                            Text(
                                "${uiState.goals.count { !it.isCompleted }} active · ${uiState.goals.count { it.isCompleted }} completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Create Goal")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorState(
                    message = uiState.error!!,
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.goals.isEmpty() -> {
                EmptyGoalsState(
                    onCreateGoal = onNavigateToCreate,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val activeGoals = uiState.goals.filter { !it.isCompleted }
                    val completedGoals = uiState.goals.filter { it.isCompleted }

                    if (activeGoals.isNotEmpty()) {
                        item {
                            Text(
                                "Active",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(items = activeGoals, key = { it.id }) { goal ->
                            GoalCard(
                                goal = goal,
                                onClick = { onNavigateToDetail(goal.id) }
                            )
                        }
                    }

                    if (completedGoals.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Completed",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(items = completedGoals, key = { it.id }) { goal ->
                            GoalCard(
                                goal = goal,
                                onClick = { onNavigateToDetail(goal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Goal Card ────────────────────────────────────────────────────────────────

@Composable
fun GoalCard(
    goal: Goal,
    onClick: () -> Unit
) {
    val progressColor = when {
        goal.isCompleted -> MaterialTheme.colorScheme.tertiary
        goal.isOnTrack -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                if (goal.isCompleted) {
                    Text("🏆", fontSize = 20.sp)
                } else {
                    StatusChip(isOnTrack = goal.isOnTrack)
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (goal.progressPercentage / 100).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatCurrency(goal.currentSavedAmount, goal.currency),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = progressColor
                )
                Text(
                    "${String.format(Locale.US, "%.1f", goal.progressPercentage)}% of ${formatCurrency(goal.targetAmount, goal.currency)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusChip(isOnTrack: Boolean) {
    val (color, text) = if (isOnTrack) {
        Pair(MaterialTheme.colorScheme.primaryContainer, "On Track")
    } else {
        Pair(MaterialTheme.colorScheme.errorContainer, "Behind")
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
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
            text = { Text("Are you sure you want to delete this goal? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(
                            goalId = goalId,
                            onSuccess = onNavigateBack,
                            onError = { showDeleteDialog = false }
                        )
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.goal?.title ?: "Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.goal?.isCompleted == false) {
                ExtendedFloatingActionButton(
                    onClick = { showAddAllocation = true },
                    icon = { Icon(Icons.Default.Add, "Add funds") },
                    text = { Text("Add Funds") }
                )
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.goal == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Goal not found")
                }
            }
            else -> {
                val goal = uiState.goal!!
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { GoalProgressSection(goal) }
                    item { GoalInsightSection(goal) }
                    item { GoalStrategySection(goal) }

                    if (goal.unlockedBadges.isNotEmpty()) {
                        item { GoalBadgesSection(goal) }
                    }

                    if (uiState.allocations.isNotEmpty()) {
                        item {
                            Text("Allocation History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        items(items = uiState.allocations, key = { it.id }) { allocation ->
                            AllocationItem(allocation = allocation, currency = goal.currency)
                        }
                    }
                }
            }
        }
    }

    if (showAddAllocation && uiState.goal != null) {
        AddAllocationBottomSheet(
            goalId = goalId,
            goalTitle = uiState.goal!!.title,
            onDismiss = {
                showAddAllocation = false
                viewModel.resetAllocationState()
            },
            viewModel = viewModel
        )
    }
}

// ─── Progress Section ─────────────────────────────────────────────────────────

@Composable
private fun GoalProgressSection(goal: Goal) {
    val progressColor = when {
        goal.isCompleted -> MaterialTheme.colorScheme.tertiary
        goal.isOnTrack -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                CircularProgressIndicator(
                    progress = { (goal.progressPercentage / 100).toFloat() },
                    modifier = Modifier.size(140.dp),
                    strokeWidth = 12.dp,
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${String.format(Locale.US, "%.1f", goal.progressPercentage)}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                    Text("saved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                GoalStatItem(label = "Saved", value = formatCurrency(goal.currentSavedAmount, goal.currency))
                GoalStatItem(label = "Target", value = formatCurrency(goal.targetAmount, goal.currency))
                GoalStatItem(label = "Remaining", value = formatCurrency(goal.remainingAmount, goal.currency))
            }
        }
    }
}

@Composable
private fun GoalStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ─── Insight Section ──────────────────────────────────────────────────────────

@Composable
private fun GoalInsightSection(goal: Goal) {
    if (goal.isCompleted) return

    val (insightColor, insightIcon, insightText) = if (goal.isOnTrack) {
        Triple(MaterialTheme.colorScheme.primary, Icons.Default.CheckCircle, "You're on track! Keep up the current savings rate to hit your goal.")
    } else {
        Triple(MaterialTheme.colorScheme.error, Icons.Default.Warning, "You're behind schedule. Consider increasing your monthly savings.")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = insightColor.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, insightColor.copy(alpha = 0.3f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(insightIcon, contentDescription = null, tint = insightColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(if (goal.isOnTrack) "On Track" else "Needs Attention", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = insightColor)
                Spacer(Modifier.height(4.dp))
                Text(insightText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

// ─── Strategy Section ─────────────────────────────────────────────────────────

@Composable
private fun GoalStrategySection(goal: Goal) {
    if (goal.isCompleted) return

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Saving Strategy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StrategyCard(modifier = Modifier.weight(1f), icon = "📅", label = "Daily Target", value = formatCurrency(goal.dailySavingTarget, goal.currency))
                StrategyCard(modifier = Modifier.weight(1f), icon = "📆", label = "Monthly Target", value = formatCurrency(goal.monthlySavingTarget, goal.currency))
            }
        }
    }
}

@Composable
private fun StrategyCard(modifier: Modifier = Modifier, icon: String, label: String, value: String) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ─── Badges Section ───────────────────────────────────────────────────────────

@Composable
private fun GoalBadgesSection(goal: Goal) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Earned Badges", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GoalBadge.entries.forEach { badge ->
                    val unlocked = badge.id in goal.unlockedBadges
                    BadgeItem(badge = badge, unlocked = unlocked)
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: GoalBadge, unlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.3f }) {
        Surface(shape = CircleShape, color = if (unlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(44.dp)) {
            Box(contentAlignment = Alignment.Center) { Text(badge.emoji, fontSize = 20.sp) }
        }
        Spacer(Modifier.height(4.dp))
        Text(badge.label, style = MaterialTheme.typography.labelSmall)
    }
}

// ─── Allocation Item ──────────────────────────────────────────────────────────

@Composable
private fun AllocationItem(allocation: GoalAllocation, currency: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(allocation.note.ifBlank { "Funds added" }, style = MaterialTheme.typography.bodyMedium)
                Text(formatDate(allocation.allocatedAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text("+${formatCurrency(allocation.amount, currency)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

// ─── Create Goal Screen ───────────────────────────────────────────────────────

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
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            OutlinedTextField(value = uiState.title, onValueChange = viewModel::onCreateTitleChanged, label = { Text("Goal Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.targetAmount, onValueChange = viewModel::onCreateTargetAmountChanged, label = { Text("Target Amount (LKR)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            
            Text("Timeframe: ${uiState.deadlineMonths} months")
            Slider(value = uiState.deadlineMonths.toFloat(), onValueChange = { viewModel.onCreateDeadlineMonthsChanged(it.toInt()) }, valueRange = 1f..36f)

            Button(onClick = viewModel::submitCreateGoal, modifier = Modifier.fillMaxWidth().height(52.dp), enabled = !uiState.isSubmitting) {
                if (uiState.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Create Goal")
            }
        }
    }
}

// ─── Add Allocation Bottom Sheet ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAllocationBottomSheet(
    goalId: String,
    goalTitle: String,
    onDismiss: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.addAllocationState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onDismiss()
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Add Funds to $goalTitle", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = uiState.amount, onValueChange = viewModel::onAllocationAmountChanged, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.note, onValueChange = viewModel::onAllocationNoteChanged, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.submitAllocation(goalId) }, modifier = Modifier.fillMaxWidth().height(52.dp), enabled = !uiState.isSubmitting) {
                Text("Add Funds")
            }
        }
    }
}

// ─── Badge Unlock Dialog ──────────────────────────────────────────────────────

@Composable
fun BadgeUnlockDialog(badges: List<GoalBadge>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 48.sp)
                Text("Badge Unlocked!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                badges.forEach { badge ->
                    Text("${badge.emoji} ${badge.label}", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Awesome!") }
            }
        }
    }
}

// ─── Empty / Error States ─────────────────────────────────────────────────────

@Composable
private fun EmptyGoalsState(onCreateGoal: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🎯", fontSize = 64.sp)
        Text("No goals yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Button(onClick = onCreateGoal, modifier = Modifier.padding(top = 16.dp)) { Text("Create a Goal") }
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

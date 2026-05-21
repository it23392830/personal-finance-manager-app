package com.example.financeflow.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.ui.components.goals.*
import com.example.financeflow.viewmodel.goal.GoalViewModel

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
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }

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
                    onCreateGoal = { 
                        viewModel.resetCreateGoalState()
                        showCreateGoal = true 
                    }
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
                    item { EmptyGoalsState(onCreateGoal = { 
                        viewModel.resetCreateGoalState()
                        showCreateGoal = true 
                    }) }
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
                            onEdit = {
                                viewModel.setEditGoal(goal)
                                showCreateGoal = true
                            },
                            onDelete = {
                                goalToDelete = goal
                            },
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
                            onClick = { 
                                viewModel.resetCreateGoalState()
                                showCreateGoal = true 
                            },
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

    if (goalToDelete != null) {
        DeleteGoalConfirmationDialog(
            goalTitle = goalToDelete!!.title,
            onConfirm = {
                viewModel.deleteGoal(goalToDelete!!.id) {
                    goalToDelete = null
                }
            },
            onDismiss = { goalToDelete = null }
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

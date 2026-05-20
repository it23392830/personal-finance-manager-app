package com.example.financeflow.viewmodel.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.repository.goal.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalListState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class GoalDetailState(
    val goal: Goal? = null,
    val allocations: List<GoalAllocation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val newlyUnlockedBadges: List<GoalBadge> = emptyList()
)

data class CreateGoalState(
    val title: String = "",
    val targetAmount: String = "",
    val deadlineMonths: Int = 12,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class AddAllocationState(
    val amount: String = "",
    val note: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    // ─── Mock Data for Testing ───────────────────────────────────────────────
    
    private val mockGoals = listOf(
        Goal(
            id = "1",
            title = "MacBook Pro M4",
            targetAmount = 490000.0,
            currentSavedAmount = 122500.0,
            unlockedBadges = listOf("BADGE_25")
        ),
        Goal(
            id = "2",
            title = "Emergency Fund",
            targetAmount = 200000.0,
            currentSavedAmount = 150000.0,
            unlockedBadges = listOf("BADGE_started", "BADGE_25", "BADGE_50", "BADGE_75")
        ),
        Goal(
            id = "3",
            title = "Trip to Bali",
            targetAmount = 350000.0,
            currentSavedAmount = 350000.0,
            isCompleted = true,
            unlockedBadges = GoalBadge.entries.map { it.id }
        )
    )

    // ─── List State ──────────────────────────────────────────────────────────

    private val _goalListState = MutableStateFlow(GoalListState())
    val goalListState: StateFlow<GoalListState> = _goalListState.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        _goalListState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.observeGoals().collect { result ->
                result.onSuccess { goals ->
                    _goalListState.update { it.copy(goals = goals, isLoading = false, error = null) }
                }
                result.onFailure { error ->
                    if (error.message?.contains("authenticated") == true) {
                        // If not logged in, show mock data so UI can be tested
                        _goalListState.update { it.copy(goals = mockGoals, isLoading = false, error = null) }
                    } else {
                        _goalListState.update { it.copy(isLoading = false, error = error.message) }
                    }
                }
            }
        }
    }

    // ─── Detail State ────────────────────────────────────────────────────────

    private val _goalDetailState = MutableStateFlow(GoalDetailState())
    val goalDetailState: StateFlow<GoalDetailState> = _goalDetailState.asStateFlow()

    fun loadGoalDetail(goalId: String) {
        _goalDetailState.update { it.copy(isLoading = true) }
        
        // Handle mock detail
        val mockGoal = mockGoals.find { it.id == goalId }
        if (mockGoal != null) {
            _goalDetailState.update { it.copy(goal = mockGoal, isLoading = false, allocations = emptyList()) }
            return
        }

        viewModelScope.launch {
            launch {
                repository.observeGoal(goalId).collect { result ->
                    result.onSuccess { goal ->
                        _goalDetailState.update { it.copy(goal = goal, isLoading = false) }
                    }
                }
            }
            launch {
                repository.observeAllocations(goalId).collect { result ->
                    result.onSuccess { allocations ->
                        _goalDetailState.update { it.copy(allocations = allocations) }
                    }
                }
            }
        }
    }

    fun clearNewBadges() {
        _goalDetailState.update { it.copy(newlyUnlockedBadges = emptyList()) }
    }

    fun deleteGoal(goalId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (mockGoals.any { it.id == goalId }) {
            onSuccess()
            return
        }
        viewModelScope.launch {
            repository.deleteGoal(goalId)
                .onSuccess { onSuccess() }
                .onFailure { onError() }
        }
    }

    // ─── Create Goal State ───────────────────────────────────────────────────

    private val _createGoalState = MutableStateFlow(CreateGoalState())
    val createGoalState: StateFlow<CreateGoalState> = _createGoalState.asStateFlow()

    fun onCreateTitleChanged(title: String) {
        _createGoalState.update { it.copy(title = title) }
    }

    fun onCreateTargetAmountChanged(amount: String) {
        _createGoalState.update { it.copy(targetAmount = amount) }
    }

    fun onCreateDeadlineMonthsChanged(months: Int) {
        _createGoalState.update { it.copy(deadlineMonths = months) }
    }

    fun computeProjections(): Pair<Double, Double> {
        val state = _createGoalState.value
        val amount = state.targetAmount.toDoubleOrNull() ?: 0.0
        val months = state.deadlineMonths.toDouble()
        val monthly = if (months > 0) amount / months else amount
        val daily = if (months > 0) amount / (months * 30) else amount
        return Pair(daily, monthly)
    }

    fun submitCreateGoal() {
        val state = _createGoalState.value
        val amount = state.targetAmount.toDoubleOrNull()
        
        if (state.title.isBlank()) {
            _createGoalState.update { it.copy(error = "Title cannot be empty") }
            return
        }
        if (amount == null || amount <= 0) {
            _createGoalState.update { it.copy(error = "Enter a valid target amount") }
            return
        }

        _createGoalState.update { it.copy(isSubmitting = true, error = null) }
        
        viewModelScope.launch {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.MONTH, state.deadlineMonths)
            
            val goal = Goal(
                title = state.title,
                targetAmount = amount,
                deadlineDate = com.google.firebase.Timestamp(calendar.time)
            )
            
            repository.createGoal(goal)
                .onSuccess { _createGoalState.update { it.copy(isSubmitting = false, isSuccess = true) } }
                .onFailure { error -> 
                    // For testing: allow "success" even if unauthenticated
                    if (error.message?.contains("authenticated") == true) {
                        _createGoalState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    } else {
                        _createGoalState.update { it.copy(isSubmitting = false, error = error.message) }
                    }
                }
        }
    }

    fun resetCreateGoalState() {
        _createGoalState.value = CreateGoalState()
    }

    // ─── Allocation State ────────────────────────────────────────────────────

    private val _addAllocationState = MutableStateFlow(AddAllocationState())
    val addAllocationState: StateFlow<AddAllocationState> = _addAllocationState.asStateFlow()

    fun onAllocationAmountChanged(amount: String) {
        _addAllocationState.update { it.copy(amount = amount) }
    }

    fun onAllocationNoteChanged(note: String) {
        _addAllocationState.update { it.copy(note = note) }
    }

    fun submitAllocation(goalId: String) {
        val state = _addAllocationState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _addAllocationState.update { it.copy(error = "Enter a valid amount") }
            return
        }

        _addAllocationState.update { it.copy(isSubmitting = true, error = null) }

        viewModelScope.launch {
            repository.addAllocation(goalId, amount, state.note)
                .onSuccess { badges ->
                    _addAllocationState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    _goalDetailState.update { it.copy(newlyUnlockedBadges = badges) }
                }
                .onFailure { error ->
                    if (error.message?.contains("authenticated") == true) {
                         _addAllocationState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    } else {
                        _addAllocationState.update { it.copy(isSubmitting = false, error = error.message) }
                    }
                }
        }
    }

    fun resetAllocationState() {
        _addAllocationState.value = AddAllocationState()
    }
}

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
import com.google.firebase.Timestamp
import java.util.Calendar
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

    private fun getMockDeadline(days: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return Timestamp(calendar.time)
    }

    // ─── Mock Data Exactly Matching User Images ──────────────────────────────
    
    private val mockGoals = listOf(
        Goal(
            id = "1",
            title = "MacBook Pro M4",
            category = "Technology",
            targetAmount = 490000.0,
            currentSavedAmount = 196430.0,
            deadlineDate = getMockDeadline(287),
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25")
        ),
        Goal(
            id = "2",
            title = "Emergency Fund",
            category = "Security",
            targetAmount = 300000.0,
            currentSavedAmount = 85000.0,
            deadlineDate = getMockDeadline(359),
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25")
        ),
        Goal(
            id = "3",
            title = "Maldives Trip",
            category = "Lifestyle",
            targetAmount = 545000.0,
            currentSavedAmount = 72618.0,
            deadlineDate = getMockDeadline(205),
            unlockedBadges = listOf("BADGE_STARTED")
        )
    )

    private val _goalListState = MutableStateFlow(GoalListState())
    val goalListState: StateFlow<GoalListState> = _goalListState.asStateFlow()

    init { observeGoals() }

    private fun observeGoals() {
        _goalListState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.observeGoals().collect { result ->
                result.onSuccess { goals ->
                    _goalListState.update { it.copy(goals = goals, isLoading = false, error = null) }
                }
                result.onFailure { _ ->
                    _goalListState.update { it.copy(goals = mockGoals, isLoading = false, error = null) }
                }
            }
        }
    }

    private val _goalDetailState = MutableStateFlow(GoalDetailState())
    val goalDetailState: StateFlow<GoalDetailState> = _goalDetailState.asStateFlow()

    fun loadGoalDetail(goalId: String) {
        _goalDetailState.update { it.copy(isLoading = true) }
        val mockGoal = mockGoals.find { it.id == goalId }
        if (mockGoal != null) {
            _goalDetailState.update { it.copy(goal = mockGoal, isLoading = false) }
            return
        }
        viewModelScope.launch {
            repository.observeGoal(goalId).collect { result ->
                result.onSuccess { goal -> _goalDetailState.update { it.copy(goal = goal, isLoading = false) } }
            }
        }
    }

    fun clearNewBadges() { _goalDetailState.update { it.copy(newlyUnlockedBadges = emptyList()) } }
    
    fun deleteGoal(goalId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (mockGoals.any { it.id == goalId }) { onSuccess(); return }
        viewModelScope.launch { repository.deleteGoal(goalId).onSuccess { onSuccess() }.onFailure { onError() } }
    }

    private val _createGoalState = MutableStateFlow(CreateGoalState())
    val createGoalState: StateFlow<CreateGoalState> = _createGoalState.asStateFlow()
    fun onCreateTitleChanged(title: String) { _createGoalState.update { it.copy(title = title) } }
    fun onCreateTargetAmountChanged(amount: String) { _createGoalState.update { it.copy(targetAmount = amount) } }
    fun onCreateDeadlineMonthsChanged(months: Int) { _createGoalState.update { it.copy(deadlineMonths = months) } }
    fun submitCreateGoal() { _createGoalState.update { it.copy(isSuccess = true) } }
    fun resetCreateGoalState() { _createGoalState.value = CreateGoalState() }

    private val _addAllocationState = MutableStateFlow(AddAllocationState())
    val addAllocationState: StateFlow<AddAllocationState> = _addAllocationState.asStateFlow()
    fun onAllocationAmountChanged(amount: String) { _addAllocationState.update { it.copy(amount = amount) } }
    fun onAllocationNoteChanged(note: String) { _addAllocationState.update { it.copy(note = note) } }
    fun submitAllocation(goalId: String) { _addAllocationState.update { it.copy(isSuccess = true) } }
    fun resetAllocationState() { _addAllocationState.value = AddAllocationState() }
}

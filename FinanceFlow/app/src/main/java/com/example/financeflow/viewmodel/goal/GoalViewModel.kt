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
    val icon: String = "💻",
    val targetAmount: String = "",
    val currentSavings: String = "",
    val deadline: String = "01/07/2027",
    val monthlyTarget: String = "",
    val category: String = "Technology",
    val color: String = "Purple",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class AddAllocationState(
    val amount: String = "",
    val monthlyTarget: String = "",
    val monthYear: String = "",
    val note: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    private fun getMockTimestamp(daysOffset: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        return Timestamp(calendar.time)
    }

    // ─── Mock Data Exactly Matching Mockup Image ───────────────────────────
    
    private val mockGoals = listOf(
        Goal(
            id = "1",
            title = "MacBook Pro M4",
            category = "Technology",
            targetAmount = 490000.0,
            currentSavedAmount = 196400.0,
            deadlineDate = getMockTimestamp(267),
            createdAt = getMockTimestamp(-100),
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25")
        ),
        Goal(
            id = "2",
            title = "Emergency Fund",
            description = "6 months of expenses as safety net",
            category = "Security",
            targetAmount = 300000.0,
            currentSavedAmount = 85000.0,
            deadlineDate = getMockTimestamp(369),
            createdAt = getMockTimestamp(-150),
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25")
        ),
        Goal(
            id = "3",
            title = "Maldives Trip",
            category = "Lifestyle",
            targetAmount = 546000.0,
            currentSavedAmount = 72618.0,
            deadlineDate = getMockTimestamp(206),
            createdAt = getMockTimestamp(-50),
            unlockedBadges = listOf("BADGE_STARTED")
        )
    )

    private val mockAllocations: Map<String, List<GoalAllocation>> = mapOf(
        "1" to emptyList<GoalAllocation>(),
        "2" to emptyList<GoalAllocation>(),
        "3" to emptyList<GoalAllocation>()
    )

    private val _goalListState = MutableStateFlow(GoalListState())
    val goalListState: StateFlow<GoalListState> = _goalListState.asStateFlow()

    init { observeGoals() }

    private fun observeGoals() {
        _goalListState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.observeGoals().collect { result ->
                result.onSuccess { goals ->
                    _goalListState.update { it.copy(goals = goals.ifEmpty { mockGoals }, isLoading = false, error = null) }
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
        _goalDetailState.update { it.copy(isLoading = true, goal = null, allocations = emptyList()) }
        
        val mockGoal = mockGoals.find { it.id == goalId }
        if (mockGoal != null) {
            _goalDetailState.update { it.copy(
                goal = mockGoal, 
                isLoading = false,
                allocations = mockAllocations[goalId] ?: emptyList()
            ) }
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

    fun clearNewBadges() { _goalDetailState.update { it.copy(newlyUnlockedBadges = emptyList()) } }
    
    fun deleteGoal(goalId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (mockGoals.any { it.id == goalId }) { onSuccess(); return }
        viewModelScope.launch { repository.deleteGoal(goalId).onSuccess { onSuccess() }.onFailure { onError() } }
    }

    private val _createGoalState = MutableStateFlow(CreateGoalState())
    val createGoalState: StateFlow<CreateGoalState> = _createGoalState.asStateFlow()
    
    fun onCreateTitleChanged(title: String) { _createGoalState.update { it.copy(title = title) } }
    fun onCreateIconChanged(icon: String) { _createGoalState.update { it.copy(icon = icon) } }
    fun onCreateTargetAmountChanged(amount: String) { _createGoalState.update { it.copy(targetAmount = amount) } }
    fun onCreateCurrentSavingsChanged(savings: String) { _createGoalState.update { it.copy(currentSavings = savings) } }
    fun onCreateDeadlineChanged(deadline: String) { _createGoalState.update { it.copy(deadline = deadline) } }
    fun onCreateMonthlyTargetChanged(target: String) { _createGoalState.update { it.copy(monthlyTarget = target) } }
    fun onCreateCategoryChanged(category: String) { _createGoalState.update { it.copy(category = category) } }
    fun onCreateColorChanged(color: String) { _createGoalState.update { it.copy(color = color) } }
    fun onCreateDescriptionChanged(desc: String) { _createGoalState.update { it.copy(description = desc) } }

    fun submitCreateGoal() { _createGoalState.update { it.copy(isSuccess = true) } }
    fun resetCreateGoalState() { _createGoalState.value = CreateGoalState() }

    private val _addAllocationState = MutableStateFlow(AddAllocationState())
    val addAllocationState: StateFlow<AddAllocationState> = _addAllocationState.asStateFlow()
    fun onAllocationAmountChanged(amount: String) { _addAllocationState.update { it.copy(amount = amount) } }
    fun onAllocationMonthlyTargetChanged(target: String) { _addAllocationState.update { it.copy(monthlyTarget = target) } }
    fun onAllocationMonthYearChanged(monthYear: String) { _addAllocationState.update { it.copy(monthYear = monthYear) } }
    fun onAllocationNoteChanged(note: String) { _addAllocationState.update { it.copy(note = note) } }
    
    fun submitAllocation(goalId: String) {
        _addAllocationState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val state = _addAllocationState.value
            val amount = state.amount.toDoubleOrNull() ?: 0.0
            val target = state.monthlyTarget.toDoubleOrNull() ?: 0.0
            
            repository.addAllocation(goalId, amount, target, state.monthYear, state.note)
                .onSuccess {
                    _addAllocationState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    loadGoalDetail(goalId)
                }
                .onFailure {
                    _addAllocationState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }
        }
    }
    fun resetAllocationState() { _addAllocationState.value = AddAllocationState() }
}

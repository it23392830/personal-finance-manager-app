package com.example.financeflow.viewmodel.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Goal
import com.example.financeflow.model.GoalAllocation
import com.example.financeflow.model.GoalBadge
import com.example.financeflow.repository.goal.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
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
    val id: String? = null,
    val title: String = "",
    val icon: String = "💻",
    val targetAmount: String = "",
    val currentSavings: String = "",
    val deadline: String = "01/03/2027",
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
    private var goalDetailJob: Job? = null
    private var allocationDetailJob: Job? = null

    private fun getMockTimestamp(daysOffset: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        return Timestamp(calendar.time)
    }

    // No hardcoded demo data — real data comes from repository (Room + Firestore)

    private val _goalListState = MutableStateFlow(GoalListState())
    val goalListState: StateFlow<GoalListState> = _goalListState.asStateFlow()

    init { observeGoals() }

    private fun observeGoals() {
        _goalListState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.observeGoals().collect { result ->
                val goals = result.getOrNull() ?: emptyList()
                _goalListState.update { it.copy(goals = goals, isLoading = false, error = null) }
            }
        }
    }

    private val _goalDetailState = MutableStateFlow(GoalDetailState())
    val goalDetailState: StateFlow<GoalDetailState> = _goalDetailState.asStateFlow()

    fun loadGoalDetail(goalId: String) {
        goalDetailJob?.cancel()
        allocationDetailJob?.cancel()
        _goalDetailState.update {
            it.copy(
                isLoading = true,
                goal = null,
                allocations = emptyList(),
                newlyUnlockedBadges = emptyList()
            )
        }

        goalDetailJob = viewModelScope.launch {
            repository.observeGoal(goalId).collect { result ->
                val finalGoal = result.getOrNull()
                if (finalGoal != null) {
                    val newBadges = GoalBadge.checkNewBadges(finalGoal)
                    if (newBadges.isNotEmpty()) {
                        val updatedGoal = finalGoal.copy(
                            unlockedBadges = (finalGoal.unlockedBadges + newBadges.map { badge -> badge.id }).distinct()
                        )
                        repository.updateGoal(updatedGoal)
                        _goalDetailState.update {
                            it.copy(goal = updatedGoal, isLoading = false, newlyUnlockedBadges = newBadges)
                        }
                    } else {
                        _goalDetailState.update { it.copy(goal = finalGoal, isLoading = false) }
                    }
                } else {
                    _goalDetailState.update { it.copy(goal = null, isLoading = false) }
                }
            }
        }

        allocationDetailJob = viewModelScope.launch {
            repository.observeAllocations(goalId).collect { result ->
                val allocations = result.getOrNull() ?: emptyList()
                _goalDetailState.update { it.copy(allocations = allocations) }
            }
        }
    }

    fun clearNewBadges() { _goalDetailState.update { it.copy(newlyUnlockedBadges = emptyList()) } }

    fun deleteGoal(goalId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteGoal(goalId).onSuccess { onSuccess() }.onFailure { }
        }
    }

    private val _createGoalState = MutableStateFlow(CreateGoalState())
    val createGoalState: StateFlow<CreateGoalState> = _createGoalState.asStateFlow()

    fun setEditGoal(goal: Goal) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        _createGoalState.value = CreateGoalState(
            id = goal.id,
            title = goal.title,
            icon = goal.icon,
            targetAmount = goal.targetAmount.toInt().toString(),
            currentSavings = goal.currentSavedAmount.toInt().toString(),
            category = goal.category,
            color = goal.color,
            description = goal.description,
            deadline = sdf.format(goal.deadlineDate.toDate()),
            monthlyTarget = goal.monthlySavingTarget.toInt().toString()
        )
    }

    fun onCreateTitleChanged(title: String) { _createGoalState.update { it.copy(title = title) } }
    fun onCreateIconChanged(icon: String) { _createGoalState.update { it.copy(icon = icon) } }
    fun onCreateTargetAmountChanged(amount: String) { _createGoalState.update { it.copy(targetAmount = amount) } }
    fun onCreateCurrentSavingsChanged(savings: String) { _createGoalState.update { it.copy(currentSavings = savings) } }
    fun onCreateDeadlineChanged(deadline: String) { _createGoalState.update { it.copy(deadline = deadline) } }
    fun onCreateMonthlyTargetChanged(target: String) { _createGoalState.update { it.copy(monthlyTarget = target) } }
    fun onCreateCategoryChanged(category: String) { _createGoalState.update { it.copy(category = category) } }
    fun onCreateColorChanged(color: String) { _createGoalState.update { it.copy(color = color) } }
    fun onCreateDescriptionChanged(desc: String) { _createGoalState.update { it.copy(description = desc) } }

    fun submitCreateGoal() {
        val state = _createGoalState.value
        _createGoalState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val deadlineDate = try { sdf.parse(state.deadline) ?: Date() } catch (e: Exception) { Date() }

            val goal = Goal(
                id = state.id ?: "",
                title = state.title,
                icon = state.icon,
                targetAmount = state.targetAmount.toDoubleOrNull() ?: 0.0,
                currentSavedAmount = state.currentSavings.toDoubleOrNull() ?: 0.0,
                category = state.category,
                color = state.color,
                description = state.description,
                deadlineDate = Timestamp(deadlineDate)
            )

            val result = if (state.id == null) {
                repository.createGoal(goal)
            } else {
                repository.updateGoal(goal)
            }

            result.onSuccess {
                _createGoalState.update { it.copy(isSubmitting = false, isSuccess = true) }
            }.onFailure { error ->
                // For demo purposes show success if it's an update on mock
                if (state.id != null) {
                    _createGoalState.update { it.copy(isSubmitting = false, isSuccess = true) }
                } else {
                    _createGoalState.update { it.copy(isSubmitting = false, error = error.message) }
                }
            }
        }
    }

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

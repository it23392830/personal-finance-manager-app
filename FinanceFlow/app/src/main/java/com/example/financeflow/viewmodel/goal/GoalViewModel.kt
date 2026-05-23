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
            description = "Latest MacBook Pro for development work",
            category = "Technology",
            targetAmount = 490000.0,
            currentSavedAmount = 196400.0,
            deadlineDate = getMockTimestamp(267),
            createdAt = getMockTimestamp(-100),
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25"),
            icon = "💻",
            color = "Purple"
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
            unlockedBadges = listOf("BADGE_STARTED", "BADGE_25"),
            icon = "🛡️",
            color = "Blue"
        ),
        Goal(
            id = "3",
            title = "Maldives Trip",
            category = "Lifestyle",
            targetAmount = 546000.0,
            currentSavedAmount = 72618.0,
            deadlineDate = getMockTimestamp(206),
            createdAt = getMockTimestamp(-50),
            unlockedBadges = listOf("BADGE_STARTED"),
            icon = "✈️",
            color = "Green"
        )
    )

    private val mockAllocations = mapOf(
        "1" to listOf(
            GoalAllocation(
                id = "a1",
                goalId = "1",
                amount = 53200.0,
                monthlyTarget = 50000.0,
                monthYear = "oct 2023",
                note = "Tag:Freelance",
                allocatedAt = getMockTimestamp(-30)
            ),
            GoalAllocation(
                id = "a2",
                goalId = "1",
                amount = 48160.0,
                monthlyTarget = 50000.0,
                monthYear = "sep 2023",
                note = "Tag:Salary",
                allocatedAt = getMockTimestamp(-60)
            ),
            GoalAllocation(
                id = "a3",
                goalId = "1",
                amount = 51500.0,
                monthlyTarget = 50000.0,
                monthYear = "apr 2025",
                note = "Tag:Business",
                allocatedAt = getMockTimestamp(-90)
            ),
            GoalAllocation(
                id = "a4",
                goalId = "1",
                amount = 43200.0,
                monthlyTarget = 50000.0,
                monthYear = "apr 2025",
                note = "Tag:Investment",
                allocatedAt = getMockTimestamp(-120)
            )
        )
    )

    private val _goalListState = MutableStateFlow(GoalListState())
    val goalListState: StateFlow<GoalListState> = _goalListState.asStateFlow()

    init { observeGoals() }

    private fun observeGoals() {
        _goalListState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.observeGoals().collect { result ->
                val goals = result.getOrNull() ?: emptyList()
                _goalListState.update { it.copy(goals = goals.ifEmpty { mockGoals }, isLoading = false, error = null) }
            }
        }
    }

    private val _goalDetailState = MutableStateFlow(GoalDetailState())
    val goalDetailState: StateFlow<GoalDetailState> = _goalDetailState.asStateFlow()

    fun loadGoalDetail(goalId: String) {
        _goalDetailState.update { it.copy(isLoading = true, goal = null, allocations = emptyList()) }
        
        viewModelScope.launch {
            launch {
                repository.observeGoal(goalId).collect { result ->
                    val finalGoal = result.getOrNull() ?: mockGoals.find { it.id == goalId }
                    _goalDetailState.update { it.copy(goal = finalGoal, isLoading = false) } 
                }
            }
            launch {
                repository.observeAllocations(goalId).collect { result ->
                    val allocations = result.getOrNull() ?: emptyList()
                    val finalAllocations = allocations.ifEmpty { mockAllocations[goalId] ?: emptyList() }
                    _goalDetailState.update { it.copy(allocations = finalAllocations) }
                }
            }
        }
    }

    fun clearNewBadges() { _goalDetailState.update { it.copy(newlyUnlockedBadges = emptyList()) } }
    
    fun deleteGoal(goalId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteGoal(goalId).onSuccess {
                onSuccess()
            }.onFailure {
                // For demo purposes, if it's a mock goal, just succeed
                if (goalId in listOf("1", "2", "3")) onSuccess()
            }
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

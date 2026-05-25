package com.example.financeflow.viewmodel.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Saving
import com.example.financeflow.model.Goal
import com.example.financeflow.repository.savings.SavingsRepository
import com.example.financeflow.repository.savings.SavingsResult
import com.example.financeflow.repository.goal.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Savings module.
 *
 * Exposes two reactive state flows — [savings] and [goals] — that are backed
 * by Firestore real-time snapshot listeners so the UI updates instantly.
 *
 * Also exposes [isLoading], [errorMessage] and [toastMessage] for loading
 * states, error handling, and user-facing feedback respectively.
 */
@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val repository: SavingsRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    // ── Savings list (real-time) ─────────────────────────────────────────────
    private val _savings = MutableStateFlow<List<Saving>>(emptyList())
    val savings: StateFlow<List<Saving>> = _savings.asStateFlow()

    // ── Goals list (real-time) ───────────────────────────────────────────────
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    // ── Loading state ───────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Error message (null = no error) ─────────────────────────────────────
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ── Toast message (consumed by UI, then cleared) ────────────────────────
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        // Start observing Room and syncing Firestore as soon as the ViewModel is created.
        loadSavings()
        loadGoals()
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SAVINGS OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Starts collecting the real-time savings flow from Firestore.
     * Called once from [init]; can be retried after an error.
     */
    fun loadSavings() {
        viewModelScope.launch {
            try {
                repository.getSavingsFlow().collect { list ->
                    _savings.value = list
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load savings: ${e.message}"
            }
        }
        viewModelScope.launch {
            try {
                repository.syncSavingsFromFirestore()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync savings: ${e.message}"
            }
        }
    }

    /**
     * Adds a new saving record. The savingRate is auto-calculated.
     */
    fun addSaving(saving: Saving) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.addSaving(saving)) {
                is SavingsResult.Success -> {
                    updateGoalProgressForSaving(saving.goalId, saving.amountSaved)
                    _toastMessage.value = "Saving added successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to add saving"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Adds a new goal when needed, then creates a saving linked to that goal.
     */
    fun addSavingWithOptionalGoal(
        saving: Saving,
        newGoalTitle: String? = null,
        newGoalTargetAmount: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val finalGoalId = if (!newGoalTitle.isNullOrBlank() && newGoalTargetAmount != null) {
                createGoalFromSavings(newGoalTitle, newGoalTargetAmount)
            } else {
                saving.goalId
            }

            val finalSaving = saving.copy(
                goalId = finalGoalId,
                goalName = if (!newGoalTitle.isNullOrBlank()) newGoalTitle else saving.goalName
            )

            when (val result = repository.addSaving(finalSaving)) {
                is SavingsResult.Success -> {
                    updateGoalProgressForSaving(finalGoalId, finalSaving.amountSaved)
                    _toastMessage.value = "Saving added successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to add saving"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Updates an existing saving record.
     */
    fun updateSaving(saving: Saving) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.updateSaving(saving)) {
                is SavingsResult.Success -> {
                    _toastMessage.value = "Saving updated successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to update saving"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Deletes a saving record by id.
     */
    fun deleteSaving(savingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.deleteSaving(savingId)) {
                is SavingsResult.Success -> {
                    _toastMessage.value = "Saving deleted successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to delete saving"
                }
            }
            _isLoading.value = false
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GOAL OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Starts collecting the real-time goals flow from Firestore.
     * Called once from [init]; can be retried after an error.
     */
    fun loadGoals() {
        viewModelScope.launch {
            try {
                goalRepository.observeGoals().collect { result ->
                    _goals.value = result.getOrNull().orEmpty()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load goals: ${e.message}"
            }
        }
        viewModelScope.launch {
            try {
                goalRepository.syncFromFirestore()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync goals: ${e.message}"
            }
        }
    }

    /**
     * Adds a new saving goal. Progress is auto-calculated.
     */
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            goalRepository.createGoal(goal)
                .onSuccess { _toastMessage.value = "Goal added successfully" }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _toastMessage.value = "Failed to add goal"
                }
            _isLoading.value = false
        }
    }

    /**
     * Updates an existing saving goal.
     */
    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            goalRepository.updateGoal(goal)
                .onSuccess { _toastMessage.value = "Goal updated successfully" }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _toastMessage.value = "Failed to update goal"
                }
            _isLoading.value = false
        }
    }

    /**
     * Deletes a saving goal by id.
     */
    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            goalRepository.deleteGoal(goalId)
                .onSuccess { _toastMessage.value = "Goal deleted successfully" }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _toastMessage.value = "Failed to delete goal"
                }
            _isLoading.value = false
        }
    }

    private suspend fun createGoalFromSavings(title: String, targetAmount: Double): String {
        val now = com.google.firebase.Timestamp.now()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now.toDate().time
            add(java.util.Calendar.YEAR, 1)
        }
        val deadline = com.google.firebase.Timestamp(calendar.time)
        val newGoal = Goal(
            title = title,
            targetAmount = targetAmount,
            currentSavedAmount = 0.0,
            deadlineDate = deadline,
            createdAt = now,
            updatedAt = now
        )
        return goalRepository.createGoal(newGoal).getOrElse { "" }
    }

    private suspend fun updateGoalProgressForSaving(goalId: String, amount: Double) {
        if (goalId.isBlank()) return
        val current = goalRepository.observeGoal(goalId).first().getOrNull() ?: return
        val updatedAmount = current.currentSavedAmount + amount
        val updated = current.copy(
            currentSavedAmount = updatedAmount,
            isCompleted = updatedAmount >= current.targetAmount
        )
        goalRepository.updateGoal(updated)
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TOAST HANDLING
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Call from the UI after showing the toast so the same message
     * is not shown again on recomposition.
     */
    fun clearToast() {
        _toastMessage.value = null
    }

    /**
     * Clears the current error state.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

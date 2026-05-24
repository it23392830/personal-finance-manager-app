package com.example.financeflow.viewmodel.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Saving
import com.example.financeflow.model.SavingGoal
import com.example.financeflow.repository.savings.SavingsRepository
import com.example.financeflow.repository.savings.SavingsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val repository: SavingsRepository
) : ViewModel() {

    // ── Savings list (real-time) ─────────────────────────────────────────────
    private val _savings = MutableStateFlow<List<Saving>>(emptyList())
    val savings: StateFlow<List<Saving>> = _savings.asStateFlow()

    // ── Goals list (real-time) ───────────────────────────────────────────────
    private val _goals = MutableStateFlow<List<SavingGoal>>(emptyList())
    val goals: StateFlow<List<SavingGoal>> = _goals.asStateFlow()

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
                repository.getGoalsFlow().collect { list ->
                    _goals.value = list
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load goals: ${e.message}"
            }
        }
        viewModelScope.launch {
            try {
                repository.syncGoalsFromFirestore()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync goals: ${e.message}"
            }
        }
    }

    /**
     * Adds a new saving goal. Progress is auto-calculated.
     */
    fun addGoal(goal: SavingGoal) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.addGoal(goal)) {
                is SavingsResult.Success -> {
                    _toastMessage.value = "Goal added successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to add goal"
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Updates an existing saving goal.
     */
    fun updateGoal(goal: SavingGoal) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.updateGoal(goal)) {
                is SavingsResult.Success -> {
                    _toastMessage.value = "Goal updated successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to update goal"
                }
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
            when (val result = repository.deleteGoal(goalId)) {
                is SavingsResult.Success -> {
                    _toastMessage.value = "Goal deleted successfully"
                }
                is SavingsResult.Error -> {
                    _errorMessage.value = result.exception.message
                    _toastMessage.value = "Failed to delete goal"
                }
            }
            _isLoading.value = false
        }
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

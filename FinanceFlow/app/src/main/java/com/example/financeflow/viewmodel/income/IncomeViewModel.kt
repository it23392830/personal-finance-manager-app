package com.example.financeflow.viewmodel.income

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class IncomeUIState(
    val selectedMonth: String = "May 2026",
    val totalIncome: Double = 215500.0,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class IncomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        IncomeUIState()
    )

    val uiState: StateFlow<IncomeUIState>
            = _uiState.asStateFlow()

    fun onMonthSelected(month:String){
        _uiState.value =
            _uiState.value.copy(
                selectedMonth = month
            )
    }

    fun showAddDialog(){
        _uiState.value =
            _uiState.value.copy(
                showAddDialog = true
            )
    }

    fun dismissAddDialog(){
        _uiState.value =
            _uiState.value.copy(
                showAddDialog = false
            )
    }

    fun showEditDialog(){
        _uiState.value =
            _uiState.value.copy(
                showEditDialog = true
            )
    }

    fun dismissEditDialog(){
        _uiState.value =
            _uiState.value.copy(
                showEditDialog = false
            )
    }

    fun showDeleteDialog(){
        _uiState.value =
            _uiState.value.copy(
                showDeleteDialog = true
            )
    }

    fun dismissDeleteDialog(){
        _uiState.value =
            _uiState.value.copy(
                showDeleteDialog = false
            )
    }

    fun addIncome(){}

    fun updateIncome(){}

    fun deleteIncome(){}
}
package com.example.financeflow.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing Authentication workflows. Uses StateFlow to publish UI state,
 * handles login, signup, password resets, and logout actions.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Persistent remember me state collected directly from the repository Flow
    val rememberMe: StateFlow<Boolean> = repository.getRememberMe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Entry point for dispatching UI events.
     */
    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> loginUser(event.email, event.password, event.rememberMe)
            is AuthEvent.Register -> registerUser(event.fullName, event.email, event.phone, event.password)
            is AuthEvent.SendPasswordReset -> sendPasswordReset(event.email)
            is AuthEvent.ResetState -> resetUiState()
        }
    }

    private fun loginUser(email: String, password: String, remember: Boolean) {
        Log.d("AuthFlow", "loginUser: email=$email, remember=$remember")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = repository.login(email, password)
            if (result.isSuccess) {
                Log.d("AuthFlow", "loginUser: Success")
                // If login successful, handle Remember Me
                if (remember) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("AuthFlow", "loginUser: Saving RememberMe for uid=${user?.uid}")
                    repository.setRememberMe(true, user?.uid ?: "", email)
                } else {
                    Log.d("AuthFlow", "loginUser: Clearing RememberMe")
                    repository.clearRememberMe()
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Login failed"
                Log.e("AuthFlow", "loginUser: Failed - $errorMessage")
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    private fun registerUser(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = repository.register(fullName, email, phone, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isRegistered = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Registration failed"
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    private fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = repository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isForgotPasswordSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to send reset email"
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    /**
     * Checks if the user session is authenticated in Firebase.
     */
    fun isUserAuthenticated(): Boolean {
        return repository.isUserAuthenticated()
    }

    /**
     * Signs out the user and clears all local remember me settings.
     */
    fun logout(onComplete: () -> Unit = {}) {
        Log.d("AuthFlow", "logout: Clearing session")
        viewModelScope.launch {
            repository.clearRememberMe()
            repository.logout()
            onComplete()
        }
    }

    private fun resetUiState() {
        _uiState.value = AuthUiState()
    }
}

package com.example.financeflow.viewmodel.auth

/**
 * Represents the UI state for the authentication flows (Login, Register, Forgot Password).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isRegistered: Boolean = false,
    val isForgotPasswordSuccess: Boolean = false
)

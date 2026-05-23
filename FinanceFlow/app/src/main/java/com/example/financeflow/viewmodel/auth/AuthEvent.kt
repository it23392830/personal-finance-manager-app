package com.example.financeflow.viewmodel.auth

/**
 * Sealed interface representing UI interactions/actions triggered by the user
 * on the auth screens.
 */
sealed interface AuthEvent {
    /**
     * Triggered when the user clicks the sign-in button.
     */
    data class Login(
        val email: String,
        val password: String,
        val rememberMe: Boolean
    ) : AuthEvent

    /**
     * Triggered when the user clicks the registration button.
     */
    data class Register(
        val fullName: String,
        val email: String,
        val phone: String,
        val password: String
    ) : AuthEvent

    /**
     * Triggered when the user enters their email to reset their password.
     */
    data class SendPasswordReset(
        val email: String
    ) : AuthEvent

    /**
     * Resets the authentication UI states (clears error and success states).
     */
    object ResetState : AuthEvent
}

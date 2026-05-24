package com.example.financeflow.repository.auth

import kotlinx.coroutines.flow.Flow

/**
 * Repository dealing with Firebase Authentication, Cloud Firestore (storing user details),
 * and local UserPreferences (Remember Me).
 */
interface AuthRepository {

    /**
     * Signs in a user using Firebase Authentication.
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    /**
     * Registers a new user inside Firebase Authentication and saves user data in Cloud Firestore.
     */
    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit>

    /**
     * Sends a password reset email via Firebase.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Checks if the user is authenticated in Firebase Auth.
     */
    fun isUserAuthenticated(): Boolean

    /**
     * Signs out the user from Firebase Auth.
     */
    fun logout()

    /**
     * Retrives the local 'Remember Me' preference state.
     */
    fun getRememberMe(): Flow<Boolean>

    /**
     * Updates the local 'Remember Me' preference state.
     */
    suspend fun setRememberMe(remember: Boolean)

    /**
     * Clears local 'Remember Me' preference state.
     */
    suspend fun clearRememberMe()
}
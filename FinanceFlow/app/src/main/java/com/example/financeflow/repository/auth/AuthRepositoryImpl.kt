package com.example.financeflow.repository.auth

import com.example.financeflow.data.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Concrete implementation of the AuthRepository.
 * Handles authentication tasks via Firebase Auth, stores supplementary profiles in Firestore,
 * and maintains Remember Me states in DataStore.
 */
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return try {
            // Create Firebase Auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: ""

            // Store details in Cloud Firestore
            val userData = hashMapOf(
                "fullName" to fullName,
                "email" to email,
                "phone" to phone,
                "profileImage" to "",
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .set(userData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getRememberMe(): Flow<Boolean> {
        return preferencesManager.rememberMe
    }

    override suspend fun setRememberMe(remember: Boolean, userId: String, email: String) {
        preferencesManager.setRememberMe(remember, userId, email)
    }

    override suspend fun clearRememberMe() {
        preferencesManager.clear()
    }
}
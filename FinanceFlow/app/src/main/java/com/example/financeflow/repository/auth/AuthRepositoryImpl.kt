package com.example.financeflow.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val userId = result.user?.uid ?: ""

            val userData = hashMapOf(
                "email" to email,
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

    override fun logout() {

        auth.signOut()
    }
}
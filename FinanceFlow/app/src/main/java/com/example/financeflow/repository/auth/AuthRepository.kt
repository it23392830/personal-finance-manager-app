package com.example.financeflow.repository.auth

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun register(
        email: String,
        password: String
    ): Result<Unit>

    fun logout()
}
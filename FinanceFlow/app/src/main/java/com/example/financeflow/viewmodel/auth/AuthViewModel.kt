package com.example.financeflow.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            repository.login(email, password)
        }
    }

    fun register(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            repository.register(email, password)
        }
    }

    fun logout() {

        repository.logout()
    }
}
package com.example.financeflow.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _greetingState = MutableStateFlow("")
    val greetingState: StateFlow<String> = _greetingState.asStateFlow()

    init {
        updateGreeting()
    }

    private fun updateGreeting() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { profile ->
                val firstName = profile.fullName.trim().split("\\s+".toRegex()).firstOrNull() ?: ""
                val greeting = getGreeting()
                
                val displayGreeting = if (firstName.isNotEmpty()) {
                    "$greeting, $firstName!!"
                } else {
                    "$greeting!!"
                }
                _greetingState.value = displayGreeting
            }
        }
    }

    fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 5..11 -> "Good Morning ☀️"
            in 12..16 -> "Good Afternoon 🌤️"
            in 17..20 -> "Good Evening 🌙"
            else -> "Good Night 🌙"
        }
    }
}

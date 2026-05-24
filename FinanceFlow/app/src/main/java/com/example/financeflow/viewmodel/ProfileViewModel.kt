package com.example.financeflow.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.UserProfile
import com.example.financeflow.repository.ProfileRepository
import com.example.financeflow.repository.ProfileResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for ProfileScreen and its dialogs.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadProfile()
    }

    /** Loads profile and starts realtime updates. */
    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true
            when (val result = repository.getUserProfile()) {
                is ProfileResult.Success -> _profile.value = result.data
                is ProfileResult.Error -> _error.value = result.message
            }
            _loading.value = false
        }

        viewModelScope.launch {
            runCatching {
                repository.observeUserProfile().collect { profile ->
                    _profile.value = profile
                }
            }.onFailure { error ->
                _error.value = error.message ?: "Unable to observe profile"
            }
        }
    }

    /** Updates full name, email, currency, and tracker settings. */
    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            _loading.value = true
            when (val result = repository.updateProfile(profile)) {
                is ProfileResult.Success -> _toastMessage.value = "Profile updated"
                is ProfileResult.Error -> _error.value = result.message
            }
            _loading.value = false
        }
    }

    /** Updates notification switch settings immediately. */
    fun updateNotificationSettings(
        pushNotifications: Boolean,
        dailyReminder: Boolean,
        weeklyReport: Boolean
    ) {
        viewModelScope.launch {
            when (val result = repository.updateNotificationSettings(pushNotifications, dailyReminder, weeklyReport)) {
                is ProfileResult.Success -> _toastMessage.value = "Notification settings updated"
                is ProfileResult.Error -> _error.value = result.message
            }
        }
    }

    /** Validates, re-authenticates, and changes the FirebaseAuth password. */
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            when {
                currentPassword.isBlank() -> {
                    _error.value = "Enter your current password"
                }
                newPassword.length <= 6 -> {
                    _error.value = "Password must be longer than 6 characters"
                }
                newPassword != confirmPassword -> {
                    _error.value = "Passwords do not match"
                }
                else -> {
                    _loading.value = true
                    when (val result = repository.changePassword(currentPassword, newPassword)) {
                        is ProfileResult.Success -> _toastMessage.value = "Password changed"
                        is ProfileResult.Error -> _error.value = result.message
                    }
                    _loading.value = false
                }
            }
        }
    }

    /** Deletes the user's FirebaseAuth account and Firestore profile. */
    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            when (val result = repository.deleteAccount()) {
                is ProfileResult.Success -> {
                    _toastMessage.value = "Account deleted"
                    onDeleted()
                }
                is ProfileResult.Error -> _error.value = result.message
            }
            _loading.value = false
        }
    }

    /** Uploads a selected profile image to Firebase Storage. */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _loading.value = true
            when (val result = repository.uploadProfileImage(imageUri)) {
                is ProfileResult.Success -> _toastMessage.value = "Profile image updated"
                is ProfileResult.Error -> _error.value = result.message
            }
            _loading.value = false
        }
    }

    /** Clears visible one-time messages after the UI shows them. */
    fun clearMessages() {
        _error.value = null
        _toastMessage.value = null
    }
}

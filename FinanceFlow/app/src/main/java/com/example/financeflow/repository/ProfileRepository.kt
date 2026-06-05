package com.example.financeflow.repository

import android.net.Uri
import com.example.financeflow.data.remote.ProfileFirestoreService
import com.example.financeflow.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for Profile module repository operations.
 */
sealed class ProfileResult<out T> {
    data class Success<T>(val data: T) : ProfileResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : ProfileResult<Nothing>()
}

/**
 * Repository boundary for Profile features.
 */
interface ProfileRepository {
    /** Loads the profile once. */
    suspend fun getUserProfile(): ProfileResult<UserProfile>

    /** Streams profile updates in realtime. */
    fun observeUserProfile(): Flow<UserProfile>

    /** Updates profile fields and settings. */
    suspend fun updateProfile(profile: UserProfile): ProfileResult<Unit>

    /** Updates only notification switch fields. */
    suspend fun updateNotificationSettings(
        pushNotifications: Boolean,
        dailyReminder: Boolean,
        weeklyReport: Boolean
    ): ProfileResult<Unit>

    /** Re-authenticates and changes the FirebaseAuth password. */
    suspend fun changePassword(currentPassword: String, newPassword: String): ProfileResult<Unit>

    /** Deletes Storage profile image, Firestore document, and FirebaseAuth account. */
    suspend fun deleteAccount(): ProfileResult<Unit>

    /** Uploads a profile image and returns its download URL. */
    suspend fun uploadProfileImage(imageUri: Uri): ProfileResult<String>
}

/**
 * Implementation of [ProfileRepository].
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val service: ProfileFirestoreService
) : ProfileRepository {
    override suspend fun getUserProfile(): ProfileResult<UserProfile> = runProfileRequest {
        service.getUserProfile()
    }

    override fun observeUserProfile(): Flow<UserProfile> = service.observeUserProfile()

    override suspend fun updateProfile(profile: UserProfile): ProfileResult<Unit> = runProfileRequest {
        service.updateProfile(profile)
    }

    override suspend fun updateNotificationSettings(
        pushNotifications: Boolean,
        dailyReminder: Boolean,
        weeklyReport: Boolean
    ): ProfileResult<Unit> = runProfileRequest {
        service.updateNotificationSettings(pushNotifications, dailyReminder, weeklyReport)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): ProfileResult<Unit> = runProfileRequest {
        service.changePassword(currentPassword, newPassword)
    }

    override suspend fun deleteAccount(): ProfileResult<Unit> = runProfileRequest {
        service.deleteAccount()
    }

    override suspend fun uploadProfileImage(imageUri: Uri): ProfileResult<String> = runProfileRequest {
        service.uploadProfileImage(imageUri)
    }

    /** Converts Firebase exceptions into UI-safe results. */
    private suspend fun <T> runProfileRequest(block: suspend () -> T): ProfileResult<T> {
        return try {
            ProfileResult.Success(block())
        } catch (error: Throwable) {
            ProfileResult.Error(error.message ?: "Something went wrong", error)
        }
    }
}

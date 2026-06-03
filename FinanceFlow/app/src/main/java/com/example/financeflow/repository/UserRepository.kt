package com.example.financeflow.repository

import com.example.financeflow.data.remote.ProfileFirestoreService
import com.example.financeflow.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val profileService: ProfileFirestoreService
) {
    /**
     * Fetches the user's profile from Firestore as a Flow.
     */
    fun getUserProfile(): Flow<UserProfile> = profileService.observeUserProfile()
}

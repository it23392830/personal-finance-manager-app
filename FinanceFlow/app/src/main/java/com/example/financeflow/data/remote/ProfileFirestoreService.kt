package com.example.financeflow.data.remote

import android.net.Uri
import com.example.financeflow.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

/**
 * Firestore, FirebaseAuth, and FirebaseStorage API for Profile features.
 */
@Singleton
class ProfileFirestoreService(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) {
    /** Returns the current user's uid or throws if the user is not signed in. */
    private fun currentUserId(): String {
        return auth.currentUser?.uid ?: error("User not logged in")
    }

    /** Returns the current user's profile document reference. */
    private fun userDocument() = firestore.collection("users").document(currentUserId())

    /** Loads the profile once, creating a default document when missing. */
    suspend fun getUserProfile(): UserProfile {
        val uid = currentUserId()
        val document = userDocument().get().await()
        if (!document.exists()) {
            val defaultProfile = defaultProfile(uid)
            userDocument().set(defaultProfile).await()
            return defaultProfile
        }

        return document.toObject(UserProfile::class.java)?.copy(userId = uid) ?: defaultProfile(uid)
    }

    /** Streams profile changes in realtime from Firestore. */
    fun observeUserProfile(): Flow<UserProfile> = callbackFlow {
        val uid = currentUserId()
        val listener = userDocument().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val profile = snapshot?.toObject(UserProfile::class.java)?.copy(userId = uid) ?: defaultProfile(uid)
            trySend(profile)
        }

        awaitClose { listener.remove() }
    }

    /** Updates basic profile and preference fields in Firestore. */
    suspend fun updateProfile(profile: UserProfile) {
        userDocument().set(profile.copy(userId = currentUserId(), updatedAt = System.currentTimeMillis())).await()
    }

    /** Updates notification switch settings immediately. */
    suspend fun updateNotificationSettings(
        pushNotifications: Boolean,
        dailyReminder: Boolean,
        weeklyReport: Boolean
    ) {
        userDocument().update(
            mapOf(
                "pushNotifications" to pushNotifications,
                "dailyReminder" to dailyReminder,
                "weeklyReport" to weeklyReport,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    /** Re-authenticates the current user and changes the FirebaseAuth password. */
    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: error("User not logged in")
        val email = user.email ?: error("No email found for this account")
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    /** Deletes profile image, Firestore user document, and FirebaseAuth account. */
    suspend fun deleteAccount() {
        val profile = getUserProfile()
        if (profile.profileImage.isNotBlank()) {
            runCatching { storage.getReferenceFromUrl(profile.profileImage).delete().await() }
        }
        userDocument().delete().await()
        auth.currentUser?.delete()?.await() ?: error("User not logged in")
    }

    /** Uploads a selected profile image and stores its download URL on the profile document. */
    suspend fun uploadProfileImage(imageUri: Uri): String {
        val uid = currentUserId()
        val reference = storage.reference.child("profile_images/$uid/profile.jpg")
        reference.putFile(imageUri).await()
        val downloadUrl = reference.downloadUrl.await().toString()
        userDocument().update(
            mapOf(
                "profileImage" to downloadUrl,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
        return downloadUrl
    }

    /** Builds a profile from FirebaseAuth user data when Firestore has none. */
    private fun defaultProfile(uid: String): UserProfile {
        val firebaseUser = auth.currentUser
        val now = System.currentTimeMillis()
        return UserProfile(
            userId = uid,
            fullName = firebaseUser?.displayName.orEmpty(),
            email = firebaseUser?.email.orEmpty(),
            createdAt = now,
            updatedAt = now
        )
    }
}

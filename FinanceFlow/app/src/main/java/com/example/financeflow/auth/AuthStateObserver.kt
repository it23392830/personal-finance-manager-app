package com.example.financeflow.auth

import android.util.Log
import com.example.financeflow.repository.income.IncomeRepository
import com.example.financeflow.workers.NotificationScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes FirebaseAuth state changes and triggers data sync when a user signs in.
 */
@Singleton
class AuthStateObserver @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: IncomeRepository,
    private val notificationScheduler: NotificationScheduler
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                notificationScheduler.scheduleDailyReminders()
                notificationScheduler.scheduleDebugNotificationTest()

                // User signed in: sync remote incomes for this user into Room
                scope.launch {
                    try {
                        repository.syncFromFirestore()
                        Log.d("AuthStateObserver", "syncFromFirestore completed for ${user.uid}")
                    } catch (e: Exception) {
                        Log.w("AuthStateObserver", "syncFromFirestore failed: ${e.message}")
                    }
                }
            } else {
                // User signed out - no action for now
                Log.d("AuthStateObserver", "user signed out")
            }
        }

        auth.addAuthStateListener(listener)
    }
}

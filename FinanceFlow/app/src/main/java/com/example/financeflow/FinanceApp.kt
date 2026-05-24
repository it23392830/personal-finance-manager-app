package com.example.financeflow

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.financeflow.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FinanceApp : android.app.Application(), Configuration.Provider {
	@Inject
	lateinit var authStateObserver: com.example.financeflow.auth.AuthStateObserver

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	@Inject
	lateinit var notificationHelper: NotificationHelper

	/** Provides Hilt-created dependencies to WorkManager workers. */
	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()

	override fun onCreate() {
		super.onCreate()
		// Ensure the AuthStateObserver is created and its init block registers listener
		// (Hilt will inject the dependency before onCreate runs)
		notificationHelper.createNotificationChannel()
	}
}

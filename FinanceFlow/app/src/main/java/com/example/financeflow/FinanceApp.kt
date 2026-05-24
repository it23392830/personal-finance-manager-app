package com.example.financeflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FinanceApp : Application() {
	@Inject
	lateinit var authStateObserver: com.example.financeflow.auth.AuthStateObserver

	override fun onCreate() {
		super.onCreate()
		// Ensure the AuthStateObserver is created and its init block registers listener
		// (Hilt will inject the dependency before onCreate runs)
	}
}
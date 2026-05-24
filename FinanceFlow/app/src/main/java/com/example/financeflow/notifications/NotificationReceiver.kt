package com.example.financeflow.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action !in supportedActions) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            NotificationReceiverEntryPoint::class.java
        )

        entryPoint.notificationScheduler().scheduleDailyReminders()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationReceiverEntryPoint {
        fun notificationScheduler(): NotificationScheduler
    }

    companion object {
        private val supportedActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )
    }
}

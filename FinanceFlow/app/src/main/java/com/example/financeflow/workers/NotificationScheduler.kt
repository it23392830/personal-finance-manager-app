package com.example.financeflow.workers

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules recurring notification workers at their requested local times.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val MORNING_WORK_NAME = "daily_morning_finance_reminder"
        private const val NIGHT_WORK_NAME = "daily_night_finance_reminder"
        private const val DEBUG_WORK_NAME = "debug_finance_notification_test"
    }

    /** Schedules the daily morning reminder for 9:00 AM. */
    fun scheduleMorningReminder() {
        val request = PeriodicWorkRequestBuilder<MorningReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(hour = 9, minute = 0), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MORNING_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /** Schedules the daily conditional night reminder for 9:00 PM. */
    fun scheduleNightReminder() {
        val request = PeriodicWorkRequestBuilder<NightReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(hour = 21, minute = 30), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NIGHT_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /** Schedules every finance notification worker used by the app. */
    fun scheduleDailyReminders() {
        scheduleMorningReminder()
        scheduleNightReminder()
    }

    /** Schedules one debug notification shortly after login for manual testing. */
    fun scheduleDebugNotificationTest() {
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (!isDebuggable) return

        val request = OneTimeWorkRequestBuilder<DebugNotificationWorker>()
            .setInitialDelay(20, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            DEBUG_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /** Calculates how long WorkManager should wait before the first run. */
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val nextRun = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return nextRun.timeInMillis - now.timeInMillis
    }
}

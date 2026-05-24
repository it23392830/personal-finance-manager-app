package com.example.financeflow.notifications

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.work.ExistingWorkPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.financeflow.workers.DebugNotificationWorker
import com.example.financeflow.workers.MorningReminderWorker
import com.example.financeflow.workers.NightReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    companion object {
        private const val MORNING_WORK_NAME = "daily_morning_finance_reminder"
        private const val NIGHT_WORK_NAME = "daily_night_finance_reminder"
        private const val DEBUG_WORK_NAME = "debug_finance_notification_test"
    }

    fun scheduleDailyReminders() {
        scheduleMorningReminder()
        scheduleNightReminder()
    }

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

    fun scheduleNightReminder() {
        val request = PeriodicWorkRequestBuilder<NightReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(hour = 21, minute = 0), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NIGHT_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelDailyReminders() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(MORNING_WORK_NAME)
        workManager.cancelUniqueWork(NIGHT_WORK_NAME)
    }

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

    /**
     * Uses local wall-clock time so reminders match the user's device timezone.
     */
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now(zoneId)
        var nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1)
        }

        return Duration.between(now, nextRun).toMillis()
    }
}

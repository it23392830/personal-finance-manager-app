package com.example.financeflow.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.financeflow.model.FinanceNotification
import com.example.financeflow.model.FinanceNotificationType
import com.example.financeflow.repository.notification.NotificationRepository
import com.example.financeflow.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

/**
 * WorkManager task that creates the daily 9:00 AM reminder for logged-in users.
 */
@HiltWorker
class MorningReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper,
    private val auth: FirebaseAuth
) : CoroutineWorker(appContext, workerParams) {

    /** Saves the morning reminder and shows it in the Android notification bar. */
    override suspend fun doWork(): Result {
        if (auth.currentUser == null) return Result.success()

        val title = "Expense Streak Reminder"
        val message = "Don't forget to log your expenses today 🔥"
        val notification = FinanceNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = FinanceNotificationType.MORNING,
            isRead = false
        )

        return runCatching {
            notificationRepository.insertNotification(notification)
            notificationHelper.showNotification(title, message, notification.id.hashCode())
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}

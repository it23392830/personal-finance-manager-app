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
 * Debug-only worker for testing the full notification pipeline immediately.
 *
 * It saves the notification into Room/Firestore and shows the Android status bar
 * notification without waiting for the daily schedule.
 */
@HiltWorker
class DebugNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper,
    private val auth: FirebaseAuth
) : CoroutineWorker(appContext, workerParams) {

    /** Creates one test notification for the currently logged-in user. */
    override suspend fun doWork(): Result {
        if (auth.currentUser == null) return Result.success()

        val title = "Still missing today's update \uD83C\uDF19"
        val message = "You haven't added today's income or expenses yet."
        val notification = FinanceNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = FinanceNotificationType.MISSED,
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

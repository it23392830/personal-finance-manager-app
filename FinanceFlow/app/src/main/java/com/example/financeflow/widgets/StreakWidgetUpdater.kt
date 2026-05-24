package com.example.financeflow.widgets

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.financeflow.data.model.Streak
import com.example.financeflow.data.remote.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun enqueueRefresh() {
        enqueueRefresh(context)
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "financeflow_streak_widget_refresh"

        fun enqueueRefresh(context: Context) {
            val request = OneTimeWorkRequestBuilder<StreakWidgetRefreshWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}

@HiltWorker
class StreakWidgetRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return runCatching {
            val viewState = if (auth.currentUser == null) {
                StreakWidgetViewState(
                    heroIcon = "🔥",
                    heroLabel = "BROKEN",
                    title = "Start Again",
                    subtitle = "Add expense today",
                    currentValue = "0 Days",
                    freezeValue = "0",
                    bestValue = "0 Days",
                    statusValue = "BROKEN"
                )
            } else {
                firestoreService.getStreak().toWidgetViewState()
            }

            StreakWidgetProvider.updateAllWidgets(applicationContext, viewState)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    private fun Streak.toWidgetViewState(): StreakWidgetViewState {
        return when (streakStatus) {
            Streak.STATUS_FROZEN -> StreakWidgetViewState(
                heroIcon = "❄",
                heroLabel = "FROZEN",
                title = "Frozen",
                subtitle = "Log today's expense",
                currentValue = "0 Days",
                freezeValue = missedDays.toString(),
                bestValue = "${bestStreak} Days",
                statusValue = "FROZEN"
            )

            Streak.STATUS_ACTIVE -> StreakWidgetViewState(
                heroIcon = "🔥",
                heroLabel = "ACTIVE",
                title = "${currentStreak} Day Streak",
                subtitle = "Keep going!",
                currentValue = "${currentStreak} Days",
                freezeValue = missedDays.toString(),
                bestValue = "${bestStreak} Days",
                statusValue = "ACTIVE"
            )

            else -> StreakWidgetViewState(
                heroIcon = "🔥",
                heroLabel = "BROKEN",
                title = "Start Again",
                subtitle = "Add expense today",
                currentValue = "0 Days",
                freezeValue = missedDays.toString(),
                bestValue = "${bestStreak} Days",
                statusValue = "BROKEN"
            )
        }
    }
}

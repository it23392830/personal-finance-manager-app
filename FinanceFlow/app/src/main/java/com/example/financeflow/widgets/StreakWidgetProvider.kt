package com.example.financeflow.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.financeflow.MainActivity
import com.example.financeflow.R

data class StreakWidgetViewState(
    val heroIcon: String,
    val heroLabel: String,
    val title: String,
    val subtitle: String,
    val currentValue: String,
    val freezeValue: String,
    val bestValue: String,
    val statusValue: String
)

class StreakWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        StreakWidgetUpdater.enqueueRefresh(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        StreakWidgetUpdater.enqueueRefresh(context)
    }

    companion object {
        fun updateAllWidgets(
            context: Context,
            state: StreakWidgetViewState
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, StreakWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)

            widgetIds.forEach { widgetId ->
                appWidgetManager.updateAppWidget(
                    widgetId,
                    buildRemoteViews(context, state, widgetId)
                )
            }
        }

        private fun buildRemoteViews(
            context: Context,
            state: StreakWidgetViewState,
            widgetId: Int
        ): RemoteViews {
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_OPEN_STREAK_WIDGET, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                widgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            return RemoteViews(context.packageName, R.layout.financeflow_streak_widget).apply {
                setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)
                setOnClickPendingIntent(R.id.widget_open_hint, openAppPendingIntent)
                setTextViewText(R.id.widget_title, state.title)
                setTextViewText(R.id.widget_motivation, state.subtitle)
                setTextViewText(R.id.widget_hero_icon, state.heroIcon)
                setTextViewText(R.id.widget_hero_label, state.heroLabel)
                setTextViewText(R.id.widget_current_value, state.currentValue)
                setTextViewText(R.id.widget_freeze_value, state.freezeValue)
                setTextViewText(R.id.widget_best_value, state.bestValue)
                setTextViewText(R.id.widget_status_value, state.statusValue)
            }
        }
    }
}

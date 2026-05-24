package com.example.financeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.financeflow.navigation.AppNavGraph
import com.example.financeflow.ui.theme.FinanceFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val openStreakFromWidget = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openStreakFromWidget.value = intent?.getBooleanExtra(EXTRA_OPEN_STREAK_WIDGET, false) == true
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            FinanceFlowTheme(darkTheme = isDarkTheme) {
                AppNavGraph(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    openStreakOnLaunch = openStreakFromWidget.value,
                    onStreakLaunchHandled = { openStreakFromWidget.value = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openStreakFromWidget.value = intent.getBooleanExtra(EXTRA_OPEN_STREAK_WIDGET, false)
    }
    companion object {
        const val EXTRA_OPEN_STREAK_WIDGET = "extra_open_streak_widget"
    }
}

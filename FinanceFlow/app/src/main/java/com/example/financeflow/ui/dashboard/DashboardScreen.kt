package com.example.financeflow.ui.dashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.components.Home.BottomNavigationBar
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.DailyReportScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.insights.MonthlyReportScreen
import com.example.financeflow.ui.insights.WeeklyReportScreen
import com.example.financeflow.ui.notifications.NotificationScreen
import com.example.financeflow.ui.profile.LogoutScreen
import com.example.financeflow.ui.profile.ProfileScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.GoalDetailsScreen
import com.example.financeflow.ui.savings.SavingsScreen
import com.example.financeflow.ui.streak.Streak.StreakScreen
import com.example.financeflow.viewmodel.notification.NotificationViewModel

@Composable
fun DashboardScreen(
    rootNavController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    openStreakOnLaunch: Boolean = false,
    onStreakLaunchHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val unreadNotificationCount by notificationViewModel.unreadCount.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(openStreakOnLaunch) {
        if (openStreakOnLaunch) {
            navController.navigate(Routes.STREAK) {
                launchSingleTop = true
            }
            onStreakLaunchHandled()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavigationBar(
                isDarkTheme = isDarkTheme,
                currentDestination = currentDestination,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    isDarkTheme = isDarkTheme,
                    onAddIncomeClick = { rootNavController.navigate(Routes.ADD_INCOME) },
                    onAddExpenseClick = { navController.navigate(Routes.EXPENSES) },
                    onIncomeClick = { navController.navigate(Routes.INCOME) },
                    onGoalsClick = { navController.navigate(Routes.GOALS) },
                    onExpensesClick = { navController.navigate(Routes.EXPENSES) },
                    onSavingsClick = { navController.navigate(Routes.SAVINGS) },
                    onGoalCardClick = { navController.navigate(Routes.GOALS) },
                    onViewInsightsClick = { navController.navigate(Routes.INSIGHTS) },
                    onStreakClick = { navController.navigate(Routes.STREAK) },
                    onThemeClick = onThemeToggle,
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onNotificationClick = { navController.navigate(Routes.NOTIFICATIONS) },
                    unreadNotificationCount = unreadNotificationCount
                )
            }

            composable(Routes.INCOME) {
                IncomeScreen(isDarkTheme = isDarkTheme, navController = rootNavController)
            }

            composable(Routes.EXPENSES) {
                ExpensesScreen(
                    isDarkTheme = isDarkTheme,
                    onAddExpenseClick = { navController.navigate(Routes.EXPENSES) }
                )
            }

            composable(Routes.SAVINGS) {
                SavingsScreen(isDarkTheme = isDarkTheme, navController = rootNavController)
            }

            composable(Routes.GOALS) {
                GoalsScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateToDetail = { goalId ->
                        rootNavController.navigate("goal_detail/$goalId")
                    }
                )
            }

            composable(Routes.INSIGHTS) {
                InsightsScreen(
                    isDarkTheme = isDarkTheme,
                    onViewReports = { navController.navigate(Routes.DAILY_REPORT) }
                )
            }

            composable(Routes.STREAK) {
                StreakScreen(isDarkTheme = isDarkTheme)
            }

            composable(Routes.NOTIFICATIONS) {
                NotificationScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = notificationViewModel
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogout = { navController.navigate(Routes.LOGOUT) }
                )
            }

            composable(Routes.LOGOUT) {
                LogoutScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onNavigateBack = { navController.popBackStack() },
                    onAccountDeleted = {
                        rootNavController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.GOAL_DETAILS) {
                GoalDetailsScreen(
                    isDarkTheme = isDarkTheme,
                    onAddContribution = { navController.navigate(Routes.ADD_SAVING) }
                )
            }

            composable(Routes.ADD_SAVING) {
                AddSavingScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DAILY_REPORT) {
                DailyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Weekly" -> navController.navigate(Routes.WEEKLY_REPORT) {
                                popUpTo(Routes.DAILY_REPORT) { inclusive = true }
                            }
                            "Monthly" -> navController.navigate(Routes.MONTHLY_REPORT) {
                                popUpTo(Routes.DAILY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.WEEKLY_REPORT) {
                WeeklyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily" -> navController.navigate(Routes.DAILY_REPORT) {
                                popUpTo(Routes.WEEKLY_REPORT) { inclusive = true }
                            }
                            "Monthly" -> navController.navigate(Routes.MONTHLY_REPORT) {
                                popUpTo(Routes.WEEKLY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.MONTHLY_REPORT) {
                MonthlyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily" -> navController.navigate(Routes.DAILY_REPORT) {
                                popUpTo(Routes.MONTHLY_REPORT) { inclusive = true }
                            }
                            "Weekly" -> navController.navigate(Routes.WEEKLY_REPORT) {
                                popUpTo(Routes.MONTHLY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

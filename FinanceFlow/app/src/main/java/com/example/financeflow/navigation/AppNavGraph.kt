package com.example.financeflow.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financeflow.ui.auth.ForgotPasswordScreen
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.auth.SplashScreen
import com.example.financeflow.ui.auth.WelcomeScreen
import com.example.financeflow.ui.components.Home.BottomNavigationBar
import com.example.financeflow.ui.dashboard.DashboardScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.income.AddIncomeScreen
import com.example.financeflow.ui.income.DeleteIncomeScreen
import com.example.financeflow.ui.income.EditIncomeScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.DailyReportScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.insights.MonthlyReportScreen
import com.example.financeflow.ui.insights.WeeklyReportScreen
import com.example.financeflow.ui.profile.ProfileScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.GoalDetailsScreen
import com.example.financeflow.ui.savings.SavingsScreen

// ─── Routes that should show the bottom navigation bar ───────────────────────
private val bottomNavRoutes = setOf(
    Routes.HOME,
    Routes.INCOME,
    Routes.EXPENSES,
    Routes.SAVINGS,
    Routes.GOALS,
    Routes.INSIGHTS
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentDestination?.route in bottomNavRoutes) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(top = topPadding)
        ) {

            // ── Auth flow ─────────────────────────────────────────────────────

            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onNext = {
                        navController.navigate(Routes.WELCOME) {
                            launchSingleTop = true
                        }
                    },
                    onForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD) {
                            launchSingleTop = true
                        }
                    },
                    onRegister = {
                        navController.navigate(Routes.REGISTER) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNext = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onVerify = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ── Main app ──────────────────────────────────────────────────────

            composable(Routes.HOME) {
                HomeScreen(
                    onAddIncomeClick  = { navController.navigate(Routes.INCOME) },
                    onAddExpenseClick = { navController.navigate(Routes.EXPENSES) },
                    onIncomeClick     = { navController.navigate(Routes.INCOME) },
                    onGoalsClick      = { navController.navigate(Routes.GOALS) },
                    onExpensesClick   = { navController.navigate(Routes.EXPENSES) },
                    onSavingsClick    = { navController.navigate(Routes.SAVINGS) },
                    onGoalCardClick   = { navController.navigate(Routes.GOALS) }
                )
            }

            composable(Routes.INCOME) { IncomeScreen(navController) }

            composable(Routes.ADD_INCOME) {
                AddIncomeScreen(
                    onAddIncome = { _, _, _, _, _, _ -> navController.popBackStack() },
                    onNavigateUp = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_INCOME,
                arguments = listOf(navArgument("incomeId") { type = NavType.StringType })
            ) {
                EditIncomeScreen(
                    onCancel = { navController.popBackStack() },
                    onSaveChanges = { _, _, _, _, _, _ -> navController.popBackStack() }
                )
            }

            composable(
                route = Routes.DELETE_INCOME,
                arguments = listOf(navArgument("incomeId") { type = NavType.StringType })
            ) {
                DeleteIncomeScreen(
                    onCancel = { navController.popBackStack() },
                    onConfirmDelete = { navController.popBackStack() }
                )
            }

            composable(Routes.EXPENSES)  { ExpensesScreen() }
            composable(Routes.SAVINGS)   { SavingsScreen(navController) }
            composable(Routes.GOALS)     { GoalsScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Routes.GOAL_DETAILS) {
                GoalDetailsScreen(
                    onAddContribution = { navController.navigate(Routes.ADD_SAVING) }
                )
            }
            composable(Routes.ADD_SAVING) {
                AddSavingScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Routes.INSIGHTS) {
                InsightsScreen(
                    onViewReports = { navController.navigate(Routes.DAILY_REPORT) }
                )
            }

            composable(Routes.DASHBOARD) { DashboardScreen(navController) }

            // ── Report screens ────────────────────────────────────────────────

            composable(Routes.DAILY_REPORT) {
                DailyReportScreen(
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Weekly"  -> navController.navigate(Routes.WEEKLY_REPORT) {
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
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily"   -> navController.navigate(Routes.DAILY_REPORT) {
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
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily"  -> navController.navigate(Routes.DAILY_REPORT) {
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

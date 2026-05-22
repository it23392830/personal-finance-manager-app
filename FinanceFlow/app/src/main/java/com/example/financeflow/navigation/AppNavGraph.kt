package com.example.financeflow.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financeflow.ui.auth.ForgotPasswordScreen
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.auth.SplashScreen
import com.example.financeflow.ui.auth.WelcomeScreen
import com.example.financeflow.ui.dashboard.DashboardScreen
import com.example.financeflow.ui.goals.GoalDetailScreen
import com.example.financeflow.ui.income.AddIncomeScreen
import com.example.financeflow.ui.income.DeleteIncomeScreen
import com.example.financeflow.ui.income.EditIncomeScreen
import com.example.financeflow.ui.profile.ProfileScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.GoalDetailsScreen

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = Modifier.padding(top = topPadding)
    ) {
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
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                rootNavController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

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

        composable(
            route = "goal_detail/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            GoalDetailScreen(
                goalId = goalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.GOAL_DETAILS) {
            GoalDetailsScreen(
                onAddContribution = { navController.navigate(Routes.ADD_SAVING) }
            )
        }

        composable(Routes.ADD_SAVING) {
            AddSavingScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

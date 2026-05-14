package com.example.financeflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.savings.SavingsScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.insights.InsightsScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen()
        }

        composable(Routes.REGISTER) {
            RegisterScreen()
        }

        composable(Routes.HOME) {
            HomeScreen()
        }

        composable(Routes.INCOME) {
            IncomeScreen()
        }

        composable(Routes.EXPENSES) {
            ExpensesScreen()
        }

        composable(Routes.SAVINGS) {
            SavingsScreen()
        }

        composable(Routes.GOALS) {
            GoalsScreen()
        }

        composable(Routes.INSIGHTS) {
            InsightsScreen()
        }
    }
}

@Composable
fun GoalsScreen() {
    TODO("Not yet implemented")
}

@Composable
fun SavingsScreen() {
    TODO("Not yet implemented")
}
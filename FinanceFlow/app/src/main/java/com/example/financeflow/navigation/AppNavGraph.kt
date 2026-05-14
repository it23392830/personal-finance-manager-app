package com.example.financeflow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.components.BottomNavigationBar   // ← only new import
import com.example.financeflow.ui.dashboard.DashboardScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.savings.SavingsScreen

// Routes where the bottom nav is visible
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
        bottomBar = {
            // Show nav bar only on main tabs — hidden on Login / Register / Dashboard
            if (currentDestination?.route in bottomNavRoutes) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onItemClick        = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.LOGIN,
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── All routes unchanged ──────────────────────
            composable(Routes.LOGIN)     { LoginScreen(navController) }
            composable(Routes.REGISTER)  { RegisterScreen() }
            composable(Routes.HOME)      { HomeScreen() }
            composable(Routes.INCOME)    { IncomeScreen() }
            composable(Routes.EXPENSES)  { ExpensesScreen() }
            composable(Routes.SAVINGS)   { SavingsScreen() }
            composable(Routes.GOALS)     { GoalsScreen() }
            composable(Routes.INSIGHTS)  { InsightsScreen() }
            composable(Routes.DASHBOARD) { DashboardScreen() }
        }
    }
}

// ─────────────────────────────────────────────
//  Stub screens (remove when real screens exist)
// ─────────────────────────────────────────────
@Composable
fun GoalsScreen() {
    TODO("Not yet implemented")
}

@Composable
fun SavingsScreen() {
    TODO("Not yet implemented")
}
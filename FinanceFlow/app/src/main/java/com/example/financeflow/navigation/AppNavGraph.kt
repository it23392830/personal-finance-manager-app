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
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.components.BottomNavigationBar
import com.example.financeflow.ui.dashboard.DashboardScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.income.*

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
    ) { innerPadding ->
        val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(top = topPadding)
        ) {
            composable(Routes.LOGIN)     { LoginScreen(navController) }
            composable(Routes.REGISTER)  { RegisterScreen() }
            
            composable(Routes.HOME) { 
                HomeScreen(
                    onAddIncomeClick = { navController.navigate(Routes.INCOME) },
                    onAddExpenseClick = { navController.navigate(Routes.EXPENSES) },
                    onIncomeClick = { navController.navigate(Routes.INCOME) },
                    onGoalsClick = { navController.navigate(Routes.GOALS) },
                    onExpensesClick = { navController.navigate(Routes.EXPENSES) },
                    onSavingsClick = { navController.navigate(Routes.SAVINGS) },
                    onGoalCardClick = { navController.navigate(Routes.GOALS) }
                ) 
            }
            
            composable(Routes.INCOME)    { IncomeScreen(navController) }
            
            composable(Routes.ADD_INCOME) {
                AddIncomeScreen(
                    onAddIncome = { _, _, _, _, _, _ ->
                        navController.popBackStack()
                    },
                    onNavigateUp = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_INCOME,
                arguments = listOf(navArgument("incomeId") { type = NavType.StringType })
            ) { _ ->
                EditIncomeScreen(
                    onCancel = { navController.popBackStack() },
                    onSaveChanges = { _, _, _, _, _, _ ->
                        // In a real app, you would call ViewModel to save changes
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.DELETE_INCOME,
                arguments = listOf(navArgument("incomeId") { type = NavType.StringType })
            ) { _ ->
                DeleteIncomeScreen(
                    onCancel = { navController.popBackStack() },
                    onConfirmDelete = {
                        // In a real app, you would call ViewModel to delete
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.EXPENSES)  { ExpensesScreen() }
            composable(Routes.SAVINGS)   { SavingsScreen() }
            composable(Routes.GOALS)     { GoalsScreen() }
            composable(Routes.INSIGHTS)  { InsightsScreen() }
            composable(Routes.DASHBOARD) { DashboardScreen(navController) }
        }
    }
}

// ─────────────────────────────────────────────
//  Stub screens
// ─────────────────────────────────────────────
@Composable
fun GoalsScreen() {
}

@Composable
fun SavingsScreen() {
}

@Composable
fun InsightsScreen() {
}

@Composable
fun ExpensesScreen() {
}

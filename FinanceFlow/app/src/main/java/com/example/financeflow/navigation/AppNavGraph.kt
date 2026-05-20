package com.example.financeflow.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.auth.RegisterScreen
import com.example.financeflow.ui.components.BottomNavigationBar
import com.example.financeflow.ui.dashboard.DashboardScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.SavingsScreen

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
        // Set insets to 0 so Scaffold doesn't automatically push content up
        // We will handle the top padding (status bar) manually.
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
        // Use systemBars top padding to avoid content being under the camera punch hole
        val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(top = topPadding)
        ) {
            composable(Routes.LOGIN)     { LoginScreen(navController) }
            composable(Routes.REGISTER)  { RegisterScreen() }
            composable(Routes.HOME)      { HomeScreen() }
            composable(Routes.INCOME)    { IncomeScreen() }
            composable(Routes.EXPENSES)  { ExpensesScreen() }
            composable(Routes.SAVINGS)   { SavingsScreen(navController) }
            composable(Routes.GOALS)     { GoalsScreen() }
            composable(Routes.INSIGHTS)  { InsightsScreen() }
            composable(Routes.DASHBOARD) { DashboardScreen() }
            composable(Routes.ADD_SAVING) { 
                AddSavingScreen(onNavigateBack = { navController.popBackStack() }) 
            }
        }
    }
}

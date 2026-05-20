package com.example.financeflow.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.financeflow.navigation.BottomNavItem
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.dashboard.HomeScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.GoalDetailsScreen
import com.example.financeflow.ui.savings.SavingsScreen

@Composable
fun DashboardScreen() {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Income,
        BottomNavItem.Expenses,
        BottomNavItem.Savings,
        BottomNavItem.Goals,
        BottomNavItem.Insights
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry.value?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.HOME)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.title)
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.INCOME) { IncomeScreen() }
            composable(Routes.EXPENSES) { ExpensesScreen() }
            composable(Routes.SAVINGS) { SavingsScreen(navController) }
            composable(Routes.GOALS) { GoalsScreen() }
            composable(Routes.INSIGHTS) { InsightsScreen() }
            composable(Routes.GOAL_DETAILS) {
                GoalDetailsScreen(
                    onAddContribution = {
                        navController.navigate(Routes.ADD_SAVING)
                    }
                )
            }
            composable(Routes.ADD_SAVING) {
                AddSavingScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

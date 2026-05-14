package com.example.financeflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeflow.ui.screens.auth.LoginScreen
import com.example.financeflow.ui.screens.auth.RegisterScreen
import com.example.financeflow.ui.screens.home.HomeScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen()
        }

        composable(Routes.Register.route) {
            RegisterScreen()
        }

        composable(Routes.Home.route) {
            HomeScreen()
        }
    }
}
package com.example.financeflow.navigation

sealed class Routes(val route: String) {

    data object Login : Routes("login")

    data object Register : Routes("register")

    data object Home : Routes("home")
}
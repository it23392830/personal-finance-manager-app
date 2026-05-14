package com.example.financeflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        Routes.HOME,
        "Home",
        Icons.Default.Home
    )

    object Income : BottomNavItem(
        Routes.INCOME,
        "Income",
        Icons.Default.AttachMoney
    )

    object Expenses : BottomNavItem(
        Routes.EXPENSES,
        "Expenses",
        Icons.Default.AccountBalance
    )

    object Savings : BottomNavItem(
        Routes.SAVINGS,
        "Savings",
        Icons.Default.Star
    )

    object Goals : BottomNavItem(
        Routes.GOALS,
        "Goals",
        Icons.Default.Flag
    )

    object Insights : BottomNavItem(
        Routes.INSIGHTS,
        "Insights",
        Icons.Default.PieChart
    )
}
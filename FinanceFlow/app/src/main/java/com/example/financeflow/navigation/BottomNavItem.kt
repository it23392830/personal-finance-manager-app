package com.example.financeflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * BottomNavItem
 *
 * Updated to match the specific icon and title requirements:
 * - Income uses the Trends/Graph icon.
 * - Expenses uses the Wallet icon.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        Routes.HOME,
        "Home",
        Icons.Outlined.Home
    )

    object Income : BottomNavItem(
        Routes.INCOME,
        "Income",
        Icons.AutoMirrored.Outlined.TrendingUp
    )

    object Expenses : BottomNavItem(
        Routes.EXPENSES,
        "Expenses",
        Icons.Outlined.AccountBalanceWallet
    )

    object Savings : BottomNavItem(
        Routes.SAVINGS,
        "Savings",
        Icons.Outlined.Savings
    )

    object Goals : BottomNavItem(
        Routes.GOALS,
        "Goals",
        Icons.Outlined.TrackChanges
    )

    object Insights : BottomNavItem(
        Routes.INSIGHTS,
        "Insights",
        Icons.Outlined.BarChart
    )
}

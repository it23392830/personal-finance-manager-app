package com.example.financeflow.ui.dashboard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import com.example.financeflow.navigation.Routes
import com.example.financeflow.ui.chat.ChatScreen
import com.example.financeflow.ui.components.Home.BottomNavigationBar
import com.example.financeflow.ui.expenses.ExpensesScreen
import com.example.financeflow.ui.goals.GoalsScreen
import com.example.financeflow.ui.income.IncomeScreen
import com.example.financeflow.ui.insights.DailyReportScreen
import com.example.financeflow.ui.insights.InsightsScreen
import com.example.financeflow.ui.insights.MonthlyReportScreen
import com.example.financeflow.ui.insights.WeeklyReportScreen
import com.example.financeflow.ui.notifications.NotificationScreen
import com.example.financeflow.ui.profile.LogoutScreen
import com.example.financeflow.ui.profile.ProfileScreen
import com.example.financeflow.ui.components.savings.GoalDetailsScreen
import com.example.financeflow.ui.savings.AddSavingScreen
import com.example.financeflow.ui.savings.SavingsScreen
import com.example.financeflow.ui.streak.Streak.StreakScreen
import com.example.financeflow.presentation.viewmodel.StreakViewModel
import com.example.financeflow.viewmodel.notification.NotificationViewModel

@Composable
fun DashboardScreen(
    rootNavController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    openStreakOnLaunch: Boolean = false,
    onStreakLaunchHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val streakViewModel: StreakViewModel = hiltViewModel()
    val unreadNotificationCount by notificationViewModel.unreadCount.collectAsState()
    val streakUiState by streakViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = currentRoute in listOf(
        Routes.HOME,
        Routes.INCOME,
        Routes.EXPENSES,
        Routes.SAVINGS,
        Routes.GOALS,
        Routes.INSIGHTS
    )
    val showChatFab = showBottomBar && currentRoute != Routes.CHAT
    val density = LocalDensity.current
    val fabBaseEndPadding = 24.dp
    val fabBaseBottomPadding = 110.dp
    val fabSize = 56.dp
    var fabOffsetX by rememberSaveable { mutableStateOf(0f) }
    var fabOffsetY by rememberSaveable { mutableStateOf(0f) }
    var fabDragged by remember { mutableStateOf(false) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val animatedOffsetX by animateDpAsState(targetValue = fabOffsetX.dp, label = "fab_offset_x")
    val animatedOffsetY by animateDpAsState(targetValue = fabOffsetY.dp, label = "fab_offset_y")

    LaunchedEffect(openStreakOnLaunch) {
        if (openStreakOnLaunch) {
            navController.navigate(Routes.STREAK) {
                launchSingleTop = true
            }
            onStreakLaunchHandled()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    isDarkTheme = isDarkTheme,
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it }
        ) {
            val maxLeft = with(density) { (containerSize.width.toDp() - fabBaseEndPadding - fabSize).coerceAtLeast(0.dp) }
            val maxUp = with(density) { (containerSize.height.toDp() - fabBaseBottomPadding - fabSize).coerceAtLeast(0.dp) }
            val minX = -maxLeft.value
            val minY = -maxUp.value

            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            ) {
            composable(Routes.HOME) {
                HomeScreen(
                    isDarkTheme = isDarkTheme,
                    streakDays = streakUiState.currentStreak,
                    onAddIncomeClick = { 
                        rootNavController.navigate(Routes.ADD_INCOME) {
                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAddExpenseClick = { 
                        navController.navigate(Routes.EXPENSES) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onIncomeClick = { 
                        navController.navigate(Routes.INCOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onGoalsClick = { 
                        navController.navigate(Routes.GOALS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onExpensesClick = { 
                        navController.navigate(Routes.EXPENSES) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSavingsClick = { 
                        navController.navigate(Routes.SAVINGS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onGoalCardClick = { 
                        navController.navigate(Routes.GOALS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onViewInsightsClick = { 
                        navController.navigate(Routes.INSIGHTS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onStreakClick = { 
                        navController.navigate(Routes.STREAK) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onThemeClick = onThemeToggle,
                    onProfileClick = { 
                        navController.navigate(Routes.PROFILE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNotificationClick = { 
                        navController.navigate(Routes.NOTIFICATIONS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    unreadNotificationCount = unreadNotificationCount
                )
            }

            composable(Routes.INCOME) {
                IncomeScreen(isDarkTheme = isDarkTheme, navController = rootNavController)
            }

            composable(Routes.EXPENSES) {
                ExpensesScreen(
                    isDarkTheme = isDarkTheme,
                    onAddExpenseClick = { rootNavController.navigate(Routes.ADD_EXPENSE) }
                )
            }

            composable(Routes.SAVINGS) {
                SavingsScreen(isDarkTheme = isDarkTheme, navController = rootNavController)
            }

            composable(Routes.GOALS) {
                GoalsScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateToDetail = { goalId ->
                        rootNavController.navigate("goal_detail/$goalId")
                    }
                )
            }

            composable(Routes.INSIGHTS) {
                InsightsScreen(
                    isDarkTheme = isDarkTheme,
                    onViewReports = { navController.navigate(Routes.DAILY_REPORT) }
                )
            }

            composable(Routes.CHAT) {
                ChatScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Routes.STREAK) {
                StreakScreen(isDarkTheme = isDarkTheme)
            }

            composable(Routes.NOTIFICATIONS) {
                NotificationScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = notificationViewModel
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogout = { 
                        navController.navigate(Routes.LOGOUT) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.LOGOUT) {
                LogoutScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onNavigateBack = { navController.popBackStack() },
                    onAccountDeleted = {
                        rootNavController.navigate(Routes.LOGIN) {
                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                inclusive = true
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.GOAL_DETAILS) {
                GoalDetailsScreen(
                    isDarkTheme = isDarkTheme,
                    onAddContribution = { 
                        navController.navigate(Routes.ADD_SAVING) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.ADD_SAVING) {
                AddSavingScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DAILY_REPORT) {
                DailyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Weekly" -> navController.navigate(Routes.WEEKLY_REPORT) {
                                popUpTo(Routes.DAILY_REPORT) { inclusive = true }
                            }
                            "Monthly" -> navController.navigate(Routes.MONTHLY_REPORT) {
                                popUpTo(Routes.DAILY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.WEEKLY_REPORT) {
                WeeklyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily" -> navController.navigate(Routes.DAILY_REPORT) {
                                popUpTo(Routes.WEEKLY_REPORT) { inclusive = true }
                            }
                            "Monthly" -> navController.navigate(Routes.MONTHLY_REPORT) {
                                popUpTo(Routes.WEEKLY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.MONTHLY_REPORT) {
                MonthlyReportScreen(
                    isDarkTheme = isDarkTheme,
                    onNavigateUp = { navController.popBackStack() },
                    onClose = { navController.popBackStack(Routes.INSIGHTS, false) },
                    onTabSelected = { tab ->
                        when (tab) {
                            "Daily" -> navController.navigate(Routes.DAILY_REPORT) {
                                popUpTo(Routes.MONTHLY_REPORT) { inclusive = true }
                            }
                            "Weekly" -> navController.navigate(Routes.WEEKLY_REPORT) {
                                popUpTo(Routes.MONTHLY_REPORT) { inclusive = true }
                            }
                        }
                    }
                )
            }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (showChatFab) {
                    FloatingActionButton(
                        onClick = {
                            if (fabDragged) {
                                fabDragged = false
                                return@FloatingActionButton
                            }
                            navController.navigate(Routes.CHAT) {
                                launchSingleTop = true
                            }
                        },
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = fabBaseEndPadding, bottom = fabBaseBottomPadding)
                            .offset(x = animatedOffsetX, y = animatedOffsetY)
                            .pointerInput(containerSize) {
                                detectDragGestures(
                                    onDragStart = { fabDragged = false },
                                    onDragEnd = { },
                                    onDragCancel = { }
                                ) { change, dragAmount ->
                                    change.consume()
                                    val deltaX = with(density) { dragAmount.x.toDp().value }
                                    val deltaY = with(density) { dragAmount.y.toDp().value }
                                    if (deltaX != 0f || deltaY != 0f) {
                                        fabDragged = true
                                    }
                                    fabOffsetX = (fabOffsetX + deltaX).coerceIn(minX, 0f)
                                    fabOffsetY = (fabOffsetY + deltaY).coerceIn(minY, 0f)
                                }
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Open chat"
                        )
                    }
                }
            }
        }
    }
}

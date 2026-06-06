package com.example.financeflow.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import com.example.financeflow.ui.components.Income.IncomeSummaryCard
import com.example.financeflow.ui.components.Home.BottomNavigationBar
import com.example.financeflow.ui.components.Home.QuickActionRow
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.ui.chat.ChatScreen
import com.example.financeflow.ui.profile.ProfileScreen
import com.example.financeflow.viewmodel.auth.AuthViewModel
import com.example.financeflow.viewmodel.ProfileViewModel
import com.example.financeflow.repository.ProfileResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.whenever

class NavigationUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loginScreen_showsWelcomeText() {
        val mockRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.auth.AuthRepository::class.java)
        org.mockito.Mockito.`when`(mockRepo.getRememberMe()).thenReturn(flowOf(false))
        val authVm = AuthViewModel(mockRepo)

        composeTestRule.setContent {
            LoginScreen(onNext = {}, viewModel = authVm)
        }

        composeTestRule.onNodeWithText("Welcome back").assertIsDisplayed()
    }

    @Test
    fun addIncomeButton_triggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            IncomeSummaryCard(totalAmount = 0.0, currencyCode = "LKR", onAddIncomeClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("+ Add Income").performClick()
        assert(clicked)
    }

    @Test
    fun addExpenseButton_triggersCallback() {
        var expenseClicked = false
        composeTestRule.setContent {
            QuickActionRow(onAddIncomeClick = {}, onAddExpenseClick = { expenseClicked = true })
        }

        composeTestRule.onNodeWithText("+ Add Expense").performClick()
        assert(expenseClicked)
    }

    @Test
    fun bottomNavigation_containsAllTabs_and_clicksInvoke() {
        val clickedItems = mutableListOf<String>()
        composeTestRule.setContent {
            BottomNavigationBar(isDarkTheme = false, currentDestination = null) { item ->
                clickedItems.add(item.title)
            }
        }

        listOf("Home", "Income", "Expenses", "Savings", "Goals", "Insights").forEach { label ->
            composeTestRule.onNodeWithContentDescription(label).performClick()
        }

        // ensure at least the set of clicked items contains expected labels
        assert(clickedItems.containsAll(listOf("Home", "Income", "Expenses", "Savings", "Goals", "Insights")))
    }

    @Test
    fun backButton_returnsToPreviousScreen() {
        var returned = false
        composeTestRule.setContent {
            ChatScreen(onNavigateBack = { returned = true })
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(returned)
    }

    @Test
    fun chatFloatingButton_opensChatScreen() {
        var showChat = false
        composeTestRule.setContent {
            androidx.compose.material3.Scaffold {
                FloatingActionButton(onClick = { showChat = true }) {
                    Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = "Open chat")
                }
                if (showChat) {
                    ChatScreen(onNavigateBack = {})
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Open chat").performClick()
        composeTestRule.onNodeWithText("Finance Assistant").assertIsDisplayed()
    }

    @Test
    fun logout_invokesNavigateToLogout_fromProfile() {
        var loggedOut = false

        val mockProfileRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.ProfileRepository::class.java)
        runBlocking {
            whenever(mockProfileRepo.getUserProfile()).thenReturn(ProfileResult.Success(com.example.financeflow.model.UserProfile()))
        }
        whenever(mockProfileRepo.observeUserProfile()).thenReturn(kotlinx.coroutines.flow.flowOf(com.example.financeflow.model.UserProfile()))
        val profileVm = ProfileViewModel(mockProfileRepo)

        val mockAuthRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.auth.AuthRepository::class.java)
        org.mockito.Mockito.`when`(mockAuthRepo.getRememberMe()).thenReturn(flowOf(false))
        val authVm = AuthViewModel(mockAuthRepo)

        composeTestRule.setContent {
            ProfileScreen(onNavigateToLogout = { loggedOut = true }, profileViewModel = profileVm, authViewModel = authVm)
        }

        // Find the Log Out button text and click it
        composeTestRule.onNodeWithText("Log Out").performScrollTo().performClick()
        assert(loggedOut)
    }
}

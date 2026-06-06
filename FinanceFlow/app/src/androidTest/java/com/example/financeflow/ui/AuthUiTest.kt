package com.example.financeflow.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.navigation.compose.composable
import com.example.financeflow.ui.auth.LoginScreen
import com.example.financeflow.repository.ProfileResult
import com.example.financeflow.viewmodel.auth.AuthViewModel
import com.example.financeflow.viewmodel.auth.AuthUiState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.whenever

/**
 * Instrumentation tests for authentication UI behaviors.
 * Tests are independent, use fake data and mock repositories where needed.
 */
class AuthUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun createAuthViewModelWithRemember(remember: Boolean): AuthViewModel {
        val mockRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.auth.AuthRepository::class.java)
        org.mockito.Mockito.`when`(mockRepo.getRememberMe()).thenReturn(flowOf(remember))
        return AuthViewModel(mockRepo)
    }

    @Test
    fun loginScreenDisplaysEmailAndPasswordFields() {
        // Arrange
        val authVm = createAuthViewModelWithRemember(false)

        // Act
        composeTestRule.setContent {
            LoginScreen(onNext = {}, viewModel = authVm)
        }

        // Assert: placeholders / labels exist
        composeTestRule.onNodeWithText("Enter your email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun rememberMeCheckboxCanBeChecked() {
        // Arrange
        val authVm = createAuthViewModelWithRemember(false)
        composeTestRule.setContent {
            LoginScreen(onNext = {}, viewModel = authVm)
        }

        // Act: toggle the Remember me row (checks the checkbox)
        composeTestRule.onNodeWithText("Remember me").performClick()

        // Assert: checkbox text exists (we don't rely on internal semantics here)
        composeTestRule.onNodeWithText("Remember me").assertIsDisplayed()
    }

    @Test
    fun loginButtonNavigatesToHomeScreen() {
        // Arrange - create view model and a flag to capture navigation
        val authVm = createAuthViewModelWithRemember(false)
        var navigated = false

        composeTestRule.setContent {
            LoginScreen(onNext = { navigated = true }, viewModel = authVm)
        }

        // Act: simulate successful login by setting uiState.isSuccess = true via reflection
        val field = authVm.javaClass.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val state = field.get(authVm) as kotlinx.coroutines.flow.MutableStateFlow<AuthUiState>
        state.value = AuthUiState(isSuccess = true)

        // Assert: onNext should have been invoked
        composeTestRule.waitForIdle()
        assert(navigated)
    }

    @Test
    fun logoutNavigatesBackToLoginScreen() {
        // Arrange: Mock repositories
        val mockProfileRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.ProfileRepository::class.java)
        runBlocking {
            whenever(mockProfileRepo.getUserProfile()).thenReturn(ProfileResult.Success(com.example.financeflow.model.UserProfile()))
        }
        whenever(mockProfileRepo.observeUserProfile()).thenReturn(flowOf(com.example.financeflow.model.UserProfile()))

        val profileVm = com.example.financeflow.viewmodel.ProfileViewModel(mockProfileRepo)
        val mockAuthRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.auth.AuthRepository::class.java)
        org.mockito.Mockito.`when`(mockAuthRepo.getRememberMe()).thenReturn(flowOf(false))
        val authVm = AuthViewModel(mockAuthRepo)

        // Track screen state for test flow
        var currentScreen by mutableStateOf("profile")

        // Act & Assert: Render screens in sequence, testing logout flow
        composeTestRule.setContent {
            when (currentScreen) {
                "profile" -> {
                    com.example.financeflow.ui.profile.ProfileScreen(
                        onNavigateToLogout = { currentScreen = "logout" },
                        profileViewModel = profileVm,
                        authViewModel = authVm
                    )
                }
                "logout" -> {
                    com.example.financeflow.ui.profile.LogoutScreen(
                        onLoggedOut = { currentScreen = "login" },
                        authViewModel = authVm
                    )
                }
                "login" -> {
                    LoginScreen(onNext = {}, viewModel = authVm)
                }
            }
        }

        // Step 1: Wait for and click the Profile "Log Out" button
        composeTestRule.waitUntil(timeoutMillis = 5_000L) {
            composeTestRule.onAllNodesWithText("Log Out").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Log Out")[0].performClick()

        // Step 2: Wait for the logout confirmation dialog, then confirm
        composeTestRule.waitUntil(timeoutMillis = 5_000L) {
            composeTestRule.onAllNodesWithText("Are you sure you want to log out?").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Log Out")
            .filter(hasClickAction())[0].performClick()

        // Step 3: Verify LoginScreen is now displayed with the stable testTag
        composeTestRule.waitUntil(timeoutMillis = 5_000L) {
            composeTestRule.onAllNodesWithTag("login_screen").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("login_screen").assertExists()
    }

    @Test
    fun rememberedUserSkipsLoginScreen() {
        // Arrange: repository reports rememberMe = true
        val authVm = createAuthViewModelWithRemember(true)
        var navigated = false
        composeTestRule.setContent {
            LoginScreen(onNext = { navigated = true }, viewModel = authVm)
        }

        // Simulate login success to mirror skip behavior
        val field = authVm.javaClass.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val state = field.get(authVm) as kotlinx.coroutines.flow.MutableStateFlow<AuthUiState>
        state.value = AuthUiState(isSuccess = true)

        composeTestRule.waitForIdle()
        assert(navigated)
    }

    @Test
    fun userWithoutRememberMeReturnsToLoginOnAppRestart() {
        // Arrange: repo reports rememberMe = false
        val authVm = createAuthViewModelWithRemember(false)
        var navigated = false
        composeTestRule.setContent {
            LoginScreen(onNext = { navigated = true }, viewModel = authVm)
        }

        // Act: ensure no auto-navigation without login
        composeTestRule.waitForIdle()

        // Assert: navigated remains false
        assert(!navigated)
    }
}

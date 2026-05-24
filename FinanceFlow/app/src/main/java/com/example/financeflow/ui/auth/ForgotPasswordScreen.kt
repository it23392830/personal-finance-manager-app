package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeflow.viewmodel.auth.AuthEvent
import com.example.financeflow.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PrimaryPurple = Color(0xFF7C4DFF)
private val TextDark      = Color(0xFF1A1A1A)
private val TextHint      = Color(0xFFAAAAAA)

/**
 * ForgotPasswordScreen
 *
 * Provides functionality for triggering a Firebase password reset email.
 * Replaces the static OTP verification layout with a functional, validated email form.
 */
@Composable
fun ForgotPasswordScreen(
    onVerify: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Monitor success state of reset link dispatch
    LaunchedEffect(uiState.isForgotPasswordSuccess) {
        if (uiState.isForgotPasswordSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Password reset email sent successfully!")
                // Wait briefly for the user to read the message, then route back to login
                delay(1500L)
                onVerify()
                viewModel.onEvent(AuthEvent.ResetState)
            }
        }
    }

    // Monitor errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.onEvent(AuthEvent.ResetState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(80.dp))

                // Headline
                Text(
                    text = "Forgot Password",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )

                Spacer(Modifier.height(20.dp))

                // Body description
                Text(
                    text = "Please enter your registered email address to receive a password reset link.",
                    fontSize = 14.sp,
                    color = TextHint,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(40.dp))

                // Email Address field
                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter your email",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Mail,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(Modifier.height(32.dp))

                // Verify Button triggers password reset
                AuthPrimaryButton(
                    text = "Verify",
                    enabled = !uiState.isLoading,
                    onClick = {
                        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        if (email.isBlank() || !isEmailValid) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter a valid email address.")
                            }
                        } else {
                            viewModel.onEvent(AuthEvent.SendPasswordReset(email.trim()))
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Back to Login helper link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Back to Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryPurple,
                        modifier = Modifier.clickable { onVerify() }
                    )
                }
            }

            // Spinner Overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen()
    }
}
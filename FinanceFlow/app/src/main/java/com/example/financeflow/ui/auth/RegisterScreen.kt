package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.financeflow.viewmodel.auth.AuthEvent
import com.example.financeflow.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.launch

private val PrimaryPurple = Color(0xFF7C4DFF)
private val TextHint = Color(0xFFAAAAAA)
private val TextDark = Color(0xFF1A1A1A)
private val RedDot = Color(0xFFE53935)
private val LinkColor = Color(0xFF4CAF50)

/**
 * RegisterScreen
 *
 * Implements form validation, registers user via Firebase Authentication,
 * pushes demographic records to Firestore under the 'users' table, and routes back to Login.
 */
@Composable
fun RegisterScreen(
    onNext: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsChecked by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("money.json")
    )
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever
    )

    // Monitor registration result
    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            onNext() // Navigate back to LoginScreen
            viewModel.onEvent(AuthEvent.ResetState)
        }
    }

    // Monitor error messages
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                LottieAnimation(
                    composition = composition.value,
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .size(220.dp)
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Get Started",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(RedDot, shape = RoundedCornerShape(50))
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "by creating an account.",
                    fontSize = 13.sp,
                    color = TextHint
                )

                Spacer(Modifier.height(24.dp))

                // Full Name Input
                AuthTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Full name",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Email Input
                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Valid email",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Mail,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Phone Input
                AuthTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Phone number",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Password Input
                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Strong password",
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = TextHint,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Terms and Conditions Checked Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsChecked,
                        onCheckedChange = { termsChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryPurple,
                            uncheckedColor = TextHint
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    val termsText = buildAnnotatedString {
                        append("By checking the box you agree to our ")
                        withStyle(SpanStyle(color = LinkColor, fontWeight = FontWeight.SemiBold)) {
                            append("Terms")
                        }
                        append(" and ")
                        withStyle(SpanStyle(color = LinkColor, fontWeight = FontWeight.SemiBold)) {
                            append("Conditions")
                        }
                    }
                    Text(text = termsText, fontSize = 12.sp, color = TextHint)
                }

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(24.dp))

                // Next Button triggers registration
                AuthPrimaryButton(
                    text = "Next",
                    enabled = !uiState.isLoading,
                    onClick = {
                        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        
                        if (fullName.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Full name is required.")
                            }
                        } else if (email.isBlank() || !isEmailValid) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("A valid email address is required.")
                            }
                        } else if (phone.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Phone number is required.")
                            }
                        } else if (password.length < 8) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Password must be at least 8 characters.")
                            }
                        } else if (!termsChecked) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("You must agree to our Terms and Conditions.")
                            }
                        } else {
                            viewModel.onEvent(
                                AuthEvent.Register(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    password = password
                                )
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Login Link
                val loginText = buildAnnotatedString {
                    append("Already a member? ")
                    withStyle(SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.Bold)) {
                        append("Login in")
                    }
                }
                Text(
                    text = loginText,
                    fontSize = 13.sp,
                    color = TextDark,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Spacer(Modifier.height(32.dp))
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
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}

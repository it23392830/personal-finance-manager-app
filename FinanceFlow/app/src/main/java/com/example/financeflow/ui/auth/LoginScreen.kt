package com.example.financeflow.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.R

// ─── Colors ───────────────────────────────────────────────────────────────────
private val PrimaryPurple = Color(0xFF7C4DFF)
private val TextHint      = Color(0xFFAAAAAA)
private val TextDark      = Color(0xFF1A1A1A)
private val RedDot        = Color(0xFFE53935)

/**
 * LoginScreen
 *
 * "Welcome back – sign in to access your account"
 * Uses the money/piggy-road illustration (group_436) at the top.
 *
 * Drawable required:
 *   res/drawable/group_436.png  ← curving road with piggy bank & flying cash
 *
 * @param onNext           Navigate to WelcomeScreen after successful login.
 * @param onForgotPassword Navigate to ForgotPasswordScreen.
 * @param onRegister       Navigate to RegisterScreen.
 */
@Composable
fun LoginScreen(
    onNext: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            // ── Road / piggy illustration ─────────────────────────────────────
            // Asset: res/drawable/group_436.png
            Image(
                painter = painterResource(id = R.drawable.group_436),
                contentDescription = "Money and piggy bank illustration",
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .aspectRatio(1.05f),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(8.dp))

            // ── "Welcome back" headline with red dot ──────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Welcome back",
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
                text = "sign in to access your account",
                fontSize = 13.sp,
                color = TextHint
            )

            Spacer(Modifier.height(28.dp))

            // ── Email field ───────────────────────────────────────────────────
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

            Spacer(Modifier.height(14.dp))

            // ── Password field ────────────────────────────────────────────────
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide" else "Show",
                        tint = TextHint,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                }
            )

            Spacer(Modifier.height(12.dp))

            // ── Remember me + Forgot password ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryPurple,
                            uncheckedColor = TextHint
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = "Remember me", fontSize = 13.sp, color = TextHint)
                }

                Text(
                    text = "Forget password ?",
                    fontSize = 13.sp,
                    color = PrimaryPurple,
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            // ── Next button ───────────────────────────────────────────────────
            AuthPrimaryButton(text = "Next", onClick = onNext)

            Spacer(Modifier.height(16.dp))

            // ── Register link ─────────────────────────────────────────────────
            val registerText = buildAnnotatedString {
                append("New member ? ")
                withStyle(SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.Bold)) {
                    append("Register now")
                }
            }
            Text(
                text = registerText,
                fontSize = 13.sp,
                color = TextDark,
                modifier = Modifier.clickable { onRegister() }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}
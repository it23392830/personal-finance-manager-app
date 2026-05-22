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
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

private val PrimaryPurple = Color(0xFF7C4DFF)
private val TextHint = Color(0xFFAAAAAA)
private val TextDark = Color(0xFF1A1A1A)
private val RedDot = Color(0xFFE53935)
private val LinkColor = Color(0xFF4CAF50)

@Composable
fun RegisterScreen(
    onNext: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsChecked by remember { mutableStateOf(false) }

    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("money.json")
    )
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever
    )
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
                text = "by creating a account.",
                fontSize = 13.sp,
                color = TextHint
            )

            Spacer(Modifier.height(24.dp))

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

            AuthPrimaryButton(text = "Next", onClick = onNext)

            Spacer(Modifier.height(16.dp))

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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}

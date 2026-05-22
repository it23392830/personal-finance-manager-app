package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Colors ───────────────────────────────────────────────────────────────────
private val PrimaryPurple = Color(0xFF7C4DFF)
private val TextDark      = Color(0xFF1A1A1A)
private val TextHint      = Color(0xFFAAAAAA)
private val OtpBoxBg      = Color(0xFFF5F5F5)
private val OtpBorder     = Color(0xFFDDDDDD)

/**
 * ForgotPasswordScreen
 *
 * "Almost there" – displays a 6-digit OTP input and a countdown timer
 * to resend the code. Tapping "Verify" calls [onVerify].
 *
 * @param email    The email address shown in the body text.
 * @param onVerify Called when the user taps the Verify button.
 */
@Composable
fun ForgotPasswordScreen(
    email: String = "contact.uiuxexperts@gmail.com",
    onVerify: () -> Unit = {}
) {
    // ── OTP state: 6 individual digit strings ─────────────────────────────────
    val otpValues = remember { mutableStateListOf("6", "3", "5", "2", "2", "9") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // ── Resend countdown (30 s) ───────────────────────────────────────────────
    var secondsLeft by remember { mutableIntStateOf(30) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1_000L)
            secondsLeft--
        }
    }

    val countdownText = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(Modifier.height(80.dp))

            // ── Headline ──────────────────────────────────────────────────────
            Text(
                text = "Almost there",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Spacer(Modifier.height(20.dp))

            // ── Body text ─────────────────────────────────────────────────────
            val bodyText = buildAnnotatedString {
                append("Please enter the 6-digit code sent to your\nemail ")
                withStyle(SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.SemiBold)) {
                    append(email)
                }
                append(" for verification.")
            }
            Text(text = bodyText, fontSize = 14.sp, color = TextHint, lineHeight = 20.sp)

            Spacer(Modifier.height(40.dp))

            // ── OTP boxes ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                otpValues.forEachIndexed { index, digit ->
                    OtpBox(
                        value = digit,
                        focusRequester = focusRequesters[index],
                        onValueChange = { newVal ->
                            if (newVal.length <= 1 && newVal.all { it.isDigit() }) {
                                otpValues[index] = newVal
                                // Auto-advance focus
                                if (newVal.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Verify button ─────────────────────────────────────────────────
            AuthPrimaryButton(text = "Verify", onClick = onVerify)

            Spacer(Modifier.height(24.dp))

            // ── Resend section ────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Didn't receive any code? Resend Again",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Request a new code in $countdownText",
                    fontSize = 12.sp,
                    color = TextHint
                )
            }
        }
    }
}

/**
 * OtpBox
 *
 * A single character input box for the OTP row.
 */
@Composable
private fun OtpBox(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .background(OtpBoxBg, RoundedCornerShape(10.dp))
            .border(1.dp, OtpBorder, RoundedCornerShape(10.dp))
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            ),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    inner()
                }
            }
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen()
    }
}
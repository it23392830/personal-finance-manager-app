package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Shared colors ────────────────────────────────────────────────────────────
private val PrimaryPurple = Color(0xFF7C4DFF)
private val FieldBg       = Color(0xFFF2F2F2)
private val TextHint      = Color(0xFFAAAAAA)
private val TextDark      = Color(0xFF1A1A1A)

/**
 * AuthTextField
 *
 * Styled input field shared across Login, Register, and ForgotPassword screens.
 * Renders a flat, rounded text field with a gray background matching the Figma design.
 *
 * @param value               Current text value.
 * @param onValueChange       Callback on value change.
 * @param placeholder         Hint text when field is empty.
 * @param trailingIcon        Optional trailing icon composable.
 * @param visualTransformation Use [PasswordVisualTransformation] for password fields.
 * @param keyboardOptions     Keyboard type / IME options.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(text = placeholder, color = TextHint, fontSize = 14.sp)
        },
        trailingIcon = trailingIcon?.let { { it() } },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(color = TextDark, fontSize = 14.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        )
    )
}

/**
 * AuthPrimaryButton
 *
 * Full-width purple button with a chevron ">" used across auth screens.
 *
 * @param text    Button label (e.g. "Next", "Verify").
 * @param onClick Click callback.
 * @param enabled Whether the button is interactive.
 */
@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryPurple,
            contentColor = Color.White,
            disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(text = "›", fontSize = 22.sp, fontWeight = FontWeight.Light)
        }
    }
}
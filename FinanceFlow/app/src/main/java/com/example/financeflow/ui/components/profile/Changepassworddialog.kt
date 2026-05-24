package com.example.financeflow.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val DialogActionGreen = Color(0xFF3DBD7D)

private data class ChangePasswordPalette(
    val card: Color,
    val field: Color,
    val fieldBorder: Color,
    val text: Color,
    val muted: Color
)

private fun changePasswordPalette(isDarkTheme: Boolean): ChangePasswordPalette =
    if (isDarkTheme) {
        ChangePasswordPalette(
            card = Color(0xFF241F30),
            field = Color(0xFF2F293A),
            fieldBorder = Color(0xFF5C4F72),
            text = Color(0xFFF4EEFF),
            muted = Color(0xFFB8AEC8)
        )
    } else {
        ChangePasswordPalette(
            card = Color(0xFFFFFFFF),
            field = Color(0xFFE8D5FF),
            fieldBorder = Color(0xFFB39DDB),
            text = Color(0xFF1A1A1A),
            muted = Color(0xFF888888)
        )
    }

@Composable
fun ChangePasswordDialog(
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val palette = changePasswordPalette(isDarkTheme)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(25.dp))
                    .background(palette.card, RoundedCornerShape(25.dp))
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Change Password",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.text
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = palette.muted
                            )
                        }
                    }

                    PasswordField(
                        label = "New Password",
                        value = newPassword,
                        visible = newPasswordVisible,
                        onChange = { newPassword = it },
                        onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                        palette = palette
                    )

                    PasswordField(
                        label = "Confirm New Password",
                        value = confirmPassword,
                        visible = confirmPasswordVisible,
                        onChange = { confirmPassword = it },
                        onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                        palette = palette
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.75f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DialogActionGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Change Password",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    palette: ChangePasswordPalette
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.text
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            placeholder = {
                Text(text = "..........", color = palette.muted.copy(alpha = 0.6f))
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (visible) "Hide" else "Show",
                        tint = palette.muted
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = palette.field,
                unfocusedContainerColor = palette.field,
                focusedBorderColor = palette.fieldBorder,
                unfocusedBorderColor = palette.fieldBorder,
                cursorColor = palette.text,
                focusedTextColor = palette.text,
                unfocusedTextColor = palette.text
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewChangePasswordDialog() {
    MaterialTheme {
        ChangePasswordDialog()
    }
}

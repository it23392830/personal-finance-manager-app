package com.example.financeflow.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private val CardWhite      = Color(0xFFFFFFFF)
private val GreenBtn       = Color(0xFF3DBD7D)
private val PurpleField    = Color(0xFFE8D5FF)
private val PurpleFieldBorder = Color(0xFFB39DDB)
private val DarkText       = Color(0xFF1A1A1A)
private val LabelGray      = Color(0xFF888888)

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit = {}) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                    .background(CardWhite, RoundedCornerShape(25.dp))
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
                            color = DarkText
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = LabelGray
                            )
                        }
                    }

                    PurplePasswordField(
                        label = "New Password",
                        value = newPassword,
                        visible = newPasswordVisible,
                        onChange = { newPassword = it },
                        onToggleVisibility = { newPasswordVisible = !newPasswordVisible }
                    )

                    PurplePasswordField(
                        label = "Confirm New Password",
                        value = confirmPassword,
                        visible = confirmPasswordVisible,
                        onChange = { confirmPassword = it },
                        onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.75f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenBtn,
                            contentColor = CardWhite
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
private fun PurplePasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onChange: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkText
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            placeholder = {
                Text(text = "••••••••••", color = LabelGray.copy(alpha = 0.6f))
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (visible) "Hide" else "Show",
                        tint = LabelGray
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PurpleField,
                unfocusedContainerColor = PurpleField,
                focusedBorderColor = PurpleFieldBorder,
                unfocusedBorderColor = PurpleFieldBorder,
                cursorColor = DarkText,
                focusedTextColor = DarkText,
                unfocusedTextColor = DarkText
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "ChangePasswordDialog")
@Composable
private fun PreviewChangePasswordDialog() {
    MaterialTheme {
        ChangePasswordDialog()
    }
}

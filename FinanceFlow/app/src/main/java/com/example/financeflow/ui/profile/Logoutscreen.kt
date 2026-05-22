package com.example.financeflow.ui.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LogoutBgPurple = Color(0xFFEDE2FF)
private val LogoutCardWhite = Color(0xFFFFFFFF)
private val LogoutPurpleBtn = Color(0xFF9B72CF)
private val LogoutPurpleDark = Color(0xFF6A3FA0)
private val LogoutRedBtn = Color(0xFFE53935)
private val LogoutPurpleField = Color(0xFFE8D5FF)
private val LogoutPurpleFieldBorder = Color(0xFFB39DDB)
private val LogoutInfoCardBg = Color(0xFFD8C4F5)
private val LogoutDarkText = Color(0xFF1A1A1A)
private val LogoutBodyText = Color(0xFF3D3D3D)
private val LogoutLabelGray = Color(0xFF888888)

@Composable
fun LogoutScreen(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val palette = logoutPalette(isDarkTheme)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.screenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LogoutHeaderCard(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            palette = palette
        )

        Text(
            text = "Are You Sure You Want To Log Out Your Account?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = palette.primaryTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        LogoutInfoCard(palette = palette)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Please Enter Your Password To Confirm Deletion Of Your Account.",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primaryTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                placeholder = {
                    Text(text = "..........", color = palette.secondaryTextColor.copy(alpha = 0.6f))
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = palette.secondaryTextColor
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = palette.fieldBackground,
                    unfocusedContainerColor = palette.fieldBackground,
                    focusedBorderColor = palette.fieldBorderColor,
                    unfocusedBorderColor = palette.fieldBorderColor,
                    cursorColor = palette.primaryTextColor,
                    focusedTextColor = palette.primaryTextColor,
                    unfocusedTextColor = palette.primaryTextColor
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoutRedBtn,
                    contentColor = LogoutCardWhite
                )
            ) {
                Text(
                    text = "Yes, Delete Account",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoutPurpleBtn,
                    contentColor = LogoutCardWhite
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun LogoutHeaderCard(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    palette: LogoutPalette
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.headerCardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log Out",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.titleColor,
                modifier = Modifier.width(80.dp)
            )

            Text(
                text = "Kavindu Silva",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = if (isDarkTheme) "Dark Mode" else "Light Mode",
                tint = palette.secondaryTextColor,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onThemeToggle() }
            )
        }
    }
}

@Composable
private fun LogoutInfoCard(palette: LogoutPalette) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp))
            .background(palette.infoCardColor, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "This action will permanently delete all of your data, and you will not be able to recover it. Please keep the following in mind before proceeding:",
                fontSize = 13.sp,
                color = palette.bodyTextColor,
                lineHeight = 19.sp
            )

            val bullets = listOf(
                "All your expenses, income and associated transactions will be eliminated.",
                "You will not be able to access your account or any related information.",
                "This action cannot be undone."
            )

            bullets.forEach { point ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\u2022  ",
                        fontSize = 13.sp,
                        color = palette.bodyTextColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = point,
                        fontSize = 13.sp,
                        color = palette.bodyTextColor,
                        lineHeight = 19.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private data class LogoutPalette(
    val screenBackground: Color,
    val headerCardColor: Color,
    val titleColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val fieldBackground: Color,
    val fieldBorderColor: Color,
    val infoCardColor: Color,
    val bodyTextColor: Color
)

private fun logoutPalette(isDarkTheme: Boolean): LogoutPalette {
    return if (isDarkTheme) {
        LogoutPalette(
            screenBackground = Color(0xFF16131D),
            headerCardColor = Color(0xFF241F30),
            titleColor = Color(0xFFE2D5FF),
            primaryTextColor = Color(0xFFF4EEFF),
            secondaryTextColor = Color(0xFFB8AEC8),
            fieldBackground = Color(0xFF2F293A),
            fieldBorderColor = Color(0xFF5C4F72),
            infoCardColor = Color(0xFF312A40),
            bodyTextColor = Color(0xFFE2DAEF)
        )
    } else {
        LogoutPalette(
            screenBackground = LogoutBgPurple,
            headerCardColor = Color(0xFFFFFDE8),
            titleColor = LogoutPurpleDark,
            primaryTextColor = LogoutDarkText,
            secondaryTextColor = LogoutLabelGray,
            fieldBackground = LogoutPurpleField,
            fieldBorderColor = LogoutPurpleFieldBorder,
            infoCardColor = LogoutInfoCardBg,
            bodyTextColor = LogoutBodyText
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "LogoutScreen Light")
@Composable
private fun PreviewLogoutScreen() {
    MaterialTheme {
        LogoutScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "LogoutScreen Dark")
@Composable
private fun PreviewLogoutScreenDark() {
    MaterialTheme {
        LogoutScreen(isDarkTheme = true)
    }
}

package com.example.financeflow.ui.profile

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.MainActivity
import com.example.financeflow.viewmodel.auth.AuthViewModel

@Composable
fun LogoutScreen(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(true) }
    var shouldNavigateBack by remember { mutableStateOf(true) }

    LaunchedEffect(showDialog) {
        if (!showDialog && shouldNavigateBack) {
            onNavigateBack()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        shouldNavigateBack = false
                        authViewModel.logout {
                            val restartIntent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            context.startActivity(restartIntent)
                        }
                    }
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    shouldNavigateBack = true
                    showDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "LogoutDialog Light")
@Composable
private fun PreviewLogoutScreen() {
    LogoutScreen()
}

@Preview(showBackground = true, showSystemUi = true, name = "LogoutDialog Dark")
@Composable
private fun PreviewLogoutScreenDark() {
    LogoutScreen(isDarkTheme = true)
}

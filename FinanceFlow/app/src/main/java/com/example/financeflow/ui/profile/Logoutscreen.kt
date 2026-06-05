package com.example.financeflow.ui.profile

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.viewmodel.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogoutScreen(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(true) }

    LaunchedEffect(showDialog) {
        if (!showDialog) {
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
                        try {
                            // Sign out synchronously so the user is fully signed out
                            // before navigation clears the back stack (and cancels the ViewModel scope).
                            FirebaseAuth.getInstance().signOut()
                            // Clear DataStore preferences asynchronously — safe to run after navigation.
                            authViewModel.logoutClearPrefsOnly()
                            onLoggedOut()
                        } catch (e: Exception) {
                            Log.e("APP_CRASH", "Logout failed: ${e.stackTraceToString()}")
                            onLoggedOut()
                        }
                    }
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
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

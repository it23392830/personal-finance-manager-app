package com.example.financeflow.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val DeleteRedBtn = Color(0xFFE53935)
private val DeletePurpleBtn = Color(0xFF9B72CF)

@Composable
fun DeleteAccountDialog(
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val cardColor = if (isDarkTheme) Color(0xFF241F30) else Color(0xFFFFFFFF)
    val titleColor = if (isDarkTheme) Color(0xFFF4EEFF) else Color(0xFF1A1A1A)
    val bodyColor = if (isDarkTheme) Color(0xFFCDBFDD) else Color(0xFF555555)

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
                    .background(cardColor, RoundedCornerShape(25.dp))
                    .padding(28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Delete Account",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Are You Sure You Want To Log Out?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "By deleting your account, you agree that you understand the consequences of this action and that you agree to permanently delete your account and all associated data.",
                        fontSize = 13.sp,
                        color = bodyColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeleteRedBtn,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Yes, Delete Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeletePurpleBtn,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Cancel", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewDeleteAccountDialogDarkAware() {
    MaterialTheme {
        DeleteAccountDialog()
    }
}

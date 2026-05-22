package com.example.financeflow.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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


// Color tokens

private val CardWhite  = Color(0xFFFFFFFF)
private val RedBtn     = Color(0xFFE53935)
private val PurpleBtn  = Color(0xFF9B72CF)
private val DarkText   = Color(0xFF1A1A1A)
private val BodyGray   = Color(0xFF555555)


// DeleteAccountDialog
//
// Confirmation popup that appears when the user taps "Yes, Delete Account"
// on the LogoutScreen.
//
// Layout (matches Image 3 / Figma):
//   • Dark scrim overlay (45 % black)
//   • Centered white card (25dp corners, 24dp padding, 10dp shadow)
//   • Title: "Delete Account"
//   • Sub-title: "Are You Sure You Want To Log Out?"
//   • Body description paragraph
//   • "Yes, Delete Account" red button → Toast + close
//   • "Cancel" purple button → close
//
// Parameters:
//   onDismiss – collapses the dialog (Cancel or after deletion)

@Composable
fun DeleteAccountDialog(onDismiss: () -> Unit = {}) {

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // ── Full-screen dark scrim ─────────────────────────────────────────
        Box(
            modifier        = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            // ── White popup card ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(25.dp))
                    .background(CardWhite, RoundedCornerShape(25.dp))
                    .padding(28.dp)
            ) {
                Column(
                    horizontalAlignment   = Alignment.CenterHorizontally,
                    verticalArrangement   = Arrangement.spacedBy(16.dp)
                ) {

                    // ── Title ──────────────────────────────────────────────
                    Text(
                        text       = "Delete Account",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = DarkText,
                        textAlign  = TextAlign.Center
                    )

                    // ── Sub-title ──────────────────────────────────────────
                    Text(
                        text       = "Are You Sure You Want To Log Out?",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = DarkText,
                        textAlign  = TextAlign.Center
                    )

                    // ── Description paragraph ──────────────────────────────
                    Text(
                        text = "By deleting your account, you agree that you " +
                               "understand the consequences of this action and " +
                               "that you agree to permanently delete your account " +
                               "and all associated data.",
                        fontSize  = 13.sp,
                        color     = BodyGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Yes, Delete Account — red ──────────────────────────
                    Button(
                        onClick  = {
                            // Toast confirms deletion — no backend logic
                            Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedBtn,
                            contentColor   = CardWhite
                        )
                    ) {
                        Text(
                            text       = "Yes, Delete Account",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // ── Cancel — purple ────────────────────────────────────
                    Button(
                        onClick  = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleBtn,
                            contentColor   = CardWhite
                        )
                    ) {
                        Text(
                            text       = "Cancel",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// Previews
 
@Preview(showBackground = true, showSystemUi = true, name = "DeleteAccountDialog")
@Composable
fun PreviewDeleteAccountDialog() {
    MaterialTheme {
        DeleteAccountDialog()
    }
}
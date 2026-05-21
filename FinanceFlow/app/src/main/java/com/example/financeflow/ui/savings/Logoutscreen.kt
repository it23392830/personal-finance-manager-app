package com.example.financeflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Color tokens
private val BgPurple      = Color(0xFFEDE2FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val PurpleBtn     = Color(0xFF9B72CF)
private val PurpleDark    = Color(0xFF6A3FA0)
private val RedBtn        = Color(0xFFE53935)
private val PurpleField   = Color(0xFFE8D5FF)
private val PurpleFieldBorder = Color(0xFFB39DDB)
private val InfoCardBg    = Color(0xFFD8C4F5)   // slightly deeper lavender info box
private val DarkText      = Color(0xFF1A1A1A)
private val BodyText      = Color(0xFF3D3D3D)
private val LabelGray     = Color(0xFF888888)

// LogoutScreen
//
// Full-page screen shown when the user taps "Log Out" on ProfileScreen.
// Uses Column + verticalScroll (content fits on one screen; scroll guards
// small devices and OS keyboards pushing content up).
//
// Layout (matches Image 2 / Figma exactly):
//   1. Header card        — "Log Out" title (purple), dark-mode icon, user name
//   2. Warning headline   — "Are You Sure You Want To Log Out Your Account?"
//   3. Purple info card   — permanent data-loss warning + bullet points
//   4. Password confirm   — label + purple rounded field with eye toggle
//   5. Action buttons     — "Yes, Delete Account" (red) | "Cancel" (purple)
//   6. [Overlay]          — DeleteAccountDialog shown above content when triggered
//
// Parameters:
//   onNavigateBack – wired to navController.popBackStack() in AppNavGraph
@Composable
fun LogoutScreen(onNavigateBack: () -> Unit = {}) {

    // ── Local state  
    var password              by remember { mutableStateOf("") }
    var passwordVisible       by remember { mutableStateOf(false) }
    var showDeleteDialog      by remember { mutableStateOf(false) }

    // ── Main scrollable layout  
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPurple)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ── 1. Header card   
        LogoutHeaderCard()

        // ── 2. Warning headline  
        Text(
            text       = "Are You Sure You Want To Log Out Your Account?",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = DarkText,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.fillMaxWidth()
        )

        // ── 3. Purple info card  
        LogoutInfoCard()

        // ── 4. Password confirmation section  
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text       = "Please Enter Your Password To Confirm Deletion Of Your Account.",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = DarkText,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )

            // Purple rounded password field
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                singleLine    = true,
                placeholder   = {
                    Text(text = "••••••••••", color = LabelGray.copy(alpha = 0.6f))
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector        = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint               = LabelGray
                        )
                    }
                },
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = PurpleField,
                    unfocusedContainerColor = PurpleField,
                    focusedBorderColor      = PurpleFieldBorder,
                    unfocusedBorderColor    = PurpleFieldBorder,
                    cursorColor             = DarkText,
                    focusedTextColor        = DarkText,
                    unfocusedTextColor      = DarkText
                )
            )
        }

        // ── 5. Action buttons  ─
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Yes, Delete Account → opens DeleteAccountDialog
            Button(
                onClick  = { showDeleteDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedBtn,
                    contentColor   = CardWhite
                )
            ) {
                Text(
                    text       = "Yes, Delete Account",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            }

            // Cancel → navigate back to ProfileScreen
            Button(
                onClick  = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleBtn,
                    contentColor   = CardWhite
                )
            ) {
                Text(
                    text       = "Cancel",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // ── DeleteAccountDialog overlay  
    // Rendered outside Column so it floats above all content as a true overlay.
    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// LogoutHeaderCard
//
// Top cream/white card (matches Figma Image 2 header):
//   LEFT  → "Log Out" title in purple
//   CENTER → "Kavindu Silva" large bold
//   RIGHT → DarkMode / sun icon
@Composable
private fun LogoutHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE8)) // warm cream
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: "Log Out" label — purple
            Text(
                text       = "Log Out",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = PurpleDark,
                modifier   = Modifier.width(80.dp)
            )

            // Center: user name (large, bold)
            Text(
                text       = "Kavindu Silva",
                fontSize   = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = PurpleDark,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.weight(1f)
            )

            // Right: brightness / dark-mode icon
            Icon(
                imageVector        = Icons.Default.DarkMode,
                contentDescription = "Dark Mode",
                tint               = LabelGray,
                modifier           = Modifier
                    .size(26.dp)
                    .clickable { /* UI stub */ }
            )
        }
    }
}

// LogoutInfoCard
//
// Purple rounded card containing the permanent-deletion warning text
// and three bullet points exactly as shown in Figma Image 2.
@Composable
private fun LogoutInfoCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp))
            .background(InfoCardBg, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Opening paragraph
            Text(
                text = "This action will permanently delete all of your data, " +
                       "and you will not be able to recover it. Please keep the " +
                       "following in mind before proceeding:",
                fontSize   = 13.sp,
                color      = BodyText,
                lineHeight = 19.sp
            )

            // Bullet points
            val bullets = listOf(
                "All your expenses, income and associated transactions will be eliminated.",
                "You will not be able to access your account or any related information.",
                "This action cannot be undone."
            )
            bullets.forEach { point ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text     = "•  ",
                        fontSize = 13.sp,
                        color    = BodyText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text       = point,
                        fontSize   = 13.sp,
                        color      = BodyText,
                        lineHeight = 19.sp,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Previews
@Preview(showBackground = true, showSystemUi = true, name = "LogoutScreen – Full")
@Composable
fun PreviewLogoutScreen() {
    MaterialTheme {
        LogoutScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Logout Header Card")
@Composable
fun PreviewLogoutHeaderCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LogoutHeaderCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Logout Info Card")
@Composable
fun PreviewLogoutInfoCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LogoutInfoCard()
        }
    }
}
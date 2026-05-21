package com.example.financeflow.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.R
import kotlinx.coroutines.delay

// ─── Colors ───────────────────────────────────────────────────────────────────
private val TextDark = Color(0xFF1A1A1A)

/**
 * WelcomeScreen
 *
 * Displays the "Welcome to Home" heading and the cats-on-WELCOME illustration
 * (group_35). Automatically navigates to HomeScreen after 1 second.
 *
 * Drawable required:
 *   res/drawable/group_35.png  ← two black cats sitting on the WELCOME wordmark
 *
 * @param onNavigateToHome  Called after the 1-second delay.
 */
@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit = {}) {

    // ── Auto-navigate after 1 second ──────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(1_000L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(Modifier.height(40.dp))

            // ── Top-left title ────────────────────────────────────────────────
            Text(
                text = "Welcome to Home",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(40.dp))

            // ── Cats-on-WELCOME illustration ──────────────────────────────────
            // Asset: res/drawable/group_35.png
            // The image is wide (landscape), so we fill the width and let it
            // scale proportionally, centred horizontally.
            Image(
                painter = painterResource(id = R.drawable.group_35),
                contentDescription = "Two cats on the WELCOME wordmark",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}
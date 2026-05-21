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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.R
import kotlinx.coroutines.delay

// ─── Theme colors ─────────────────────────────────────────────────────────────
private val PrimaryPurple = Color(0xFF7C4DFF)

/**
 * SplashScreen
 *
 * Displays the Penny-Pilot brand with the piggy-bank illustration (image_37).
 * Automatically navigates to LoginScreen after 1 second.
 *
 * Drawable required:
 *   res/drawable/image_37.png  ← the person + piggy bank flat illustration
 *
 * @param onNavigateToLogin  Called after the 1-second splash delay.
 */
@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit = {}) {

    // ── Auto-navigate after 1 second ──────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(1_000L)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Piggy bank illustration ───────────────────────────────────────
            // Asset: res/drawable/image_37.png
            Image(
                painter = painterResource(id = R.drawable.image_37),
                contentDescription = "Penny-Pilot piggy bank illustration",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1.3f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text = "Penny-Pilot",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text = "where your income meets goals....",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = PrimaryPurple.copy(alpha = 0.65f)
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}
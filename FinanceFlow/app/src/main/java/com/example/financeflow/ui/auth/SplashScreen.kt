package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

private val PrimaryPurple = Color(0xFF7C4DFF)

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit = {}) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("saving.json")
    )
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(Unit) {
        delay(2_200L)
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
            LottieAnimation(
                composition = composition.value,
                progress = { progress.value },
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .size(320.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Penny-Pilot",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "where your income meets goals....",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = PrimaryPurple.copy(alpha = 0.65f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}

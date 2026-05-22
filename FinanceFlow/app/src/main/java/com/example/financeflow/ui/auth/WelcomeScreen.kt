package com.example.financeflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit = {}) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("welcome.json")
    )
    val pawsComposition = rememberLottieComposition(
        LottieCompositionSpec.Asset("paws-pet.json")
    )
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever,
        speed = 2f
    )
    val pawsProgress = animateLottieCompositionAsState(
        composition = pawsComposition.value,
        iterations = LottieConstants.IterateForever,
        speed = 1.2f
    )

    LaunchedEffect(Unit) {
        delay(2_200L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LottieAnimation(
                composition = composition.value,
                progress = { progress.value },
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .size(260.dp)
            )

            Box(modifier = Modifier.height(8.dp))

            LottieAnimation(
                composition = pawsComposition.value,
                progress = { pawsProgress.value },
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .size(92.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}

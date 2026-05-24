package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.financeflow.ui.screens.streak.StreakVisualState

private val StreakOrange = Color(0xFFFF8C42)
private val StreakOrangeLight = Color(0xFFFFD1A8)
private val StreakBlue = Color(0xFF7DB7FF)
private val StreakBlueLight = Color(0xFFDCEBFF)
private val StreakText = Color(0xFF342A44)
private val StreakSubtle = Color(0xFF7D748E)

@Composable
fun StreakTopBar(
    streakCount: Int,
    visualState: StreakVisualState,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val animationAsset = when (visualState) {
        StreakVisualState.Active -> "fire_animation.json"
        StreakVisualState.Frozen -> "fire_unlit.json"
        StreakVisualState.Zero -> "fire_unlit.json"
    }
    val composition = rememberLottieComposition(LottieCompositionSpec.Asset(animationAsset))
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = if (isDarkTheme) Color(0xFF1F2937) else Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (visualState == StreakVisualState.Active) {
                                        listOf(StreakOrangeLight, Color.White)
                                    } else {
                                        listOf(StreakBlueLight, Color.White)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (visualState == StreakVisualState.Active) "🔥" else "❄",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "$streakCount Day Streak",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkTheme) Color(0xFFF8FAFC) else StreakText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (visualState) {
                        StreakVisualState.Active -> "Your consistency is looking amazing"
                        StreakVisualState.Frozen -> "Freeze mode is keeping your streak safe"
                        StreakVisualState.Zero -> "A new streak is ready whenever you are"
                    },
                    fontSize = 13.sp,
                    color = if (isDarkTheme) Color(0xFFCBD5E1) else StreakSubtle
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (visualState == StreakVisualState.Active) {
                            Color(0xFFFFF2E8)
                        } else {
                            Color(0xFFF0F7FF)
                        }
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition.value,
                    progress = { progress.value },
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakTopBarPreview() {
    MaterialTheme {
        StreakTopBar(
            streakCount = 7,
            visualState = StreakVisualState.Active,
            isDarkTheme = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

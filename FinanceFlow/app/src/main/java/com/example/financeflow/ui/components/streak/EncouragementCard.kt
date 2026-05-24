package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.streak.Streak.StreakVisualState

@Composable
fun EncouragementCard(
    visualState: StreakVisualState,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gradient = when (visualState) {
        StreakVisualState.Active -> if (isDarkTheme) {
            listOf(Color(0xFF3F2A1D), Color(0xFF7C2D12))
        } else {
            listOf(Color(0xFFFFF4E8), Color(0xFFFFE5D1))
        }
        StreakVisualState.Frozen -> if (isDarkTheme) {
            listOf(Color(0xFF0F2746), Color(0xFF1D4ED8))
        } else {
            listOf(Color(0xFFF0F8FF), Color(0xFFDCEBFF))
        }
        StreakVisualState.Zero -> if (isDarkTheme) {
            listOf(Color(0xFF312E81), Color(0xFF4338CA))
        } else {
            listOf(Color(0xFFF7F3FF), Color(0xFFEAE2FF))
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Little Motivation",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF8A7B9B)
            )
            Text(
                text = when (visualState) {
                    StreakVisualState.Active -> "Keep going! You're on fire 🔥"
                    StreakVisualState.Frozen -> "Your streak is frozen ❄"
                    StreakVisualState.Zero -> "Log today's expenses to start a streak!"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF382D49)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EncouragementCardPreview() {
    MaterialTheme {
        EncouragementCard(
            visualState = StreakVisualState.Active,
            isDarkTheme = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

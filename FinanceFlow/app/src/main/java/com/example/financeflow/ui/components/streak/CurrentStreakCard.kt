package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun CurrentStreakCard(
    streakCount: Int,
    visualState: StreakVisualState,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gradient = when (visualState) {
        StreakVisualState.Active -> if (isDarkTheme) {
            listOf(Color(0xFF7C3A14), Color(0xFFB45309))
        } else {
            listOf(Color(0xFFFFB870), Color(0xFFFF8E53))
        }
        StreakVisualState.Frozen -> if (isDarkTheme) {
            listOf(Color(0xFF1E3A5F), Color(0xFF3B82F6))
        } else {
            listOf(Color(0xFFAED2FF), Color(0xFF7FB7FF))
        }
        StreakVisualState.Zero -> if (isDarkTheme) {
            listOf(Color(0xFF334155), Color(0xFF475569))
        } else {
            listOf(Color(0xFFD9E7FF), Color(0xFFB8CCF0))
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (visualState == StreakVisualState.Active) "🔥 Current Streak" else "❄ Current Streak",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.92f)
            )
            Text(
                text = if (visualState == StreakVisualState.Zero) "0 Days" else "$streakCount Days",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (visualState) {
                    StreakVisualState.Active -> "You logged expenses consistently"
                    StreakVisualState.Frozen -> "Your streak is safe under freeze mode"
                    StreakVisualState.Zero -> "A fresh streak can start today"
                },
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.88f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentStreakCardPreview() {
    MaterialTheme {
        CurrentStreakCard(
            streakCount = 7,
            visualState = StreakVisualState.Active,
            isDarkTheme = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

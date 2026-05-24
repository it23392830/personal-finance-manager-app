package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.screens.streak.StreakVisualState

@Composable
fun FreezeStatusCard(
    visualState: StreakVisualState,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (isDarkTheme) Color(0xFF172554) else Color(0xFFEFF7FF),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = if (isDarkTheme) Color(0xFF1D4ED8) else Color(0xFFD8EBFF)
            ) {
                BoxCenter {
                    Text(text = "❄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Streak Freeze Status",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFDBEAFE) else Color(0xFF36507A)
                )
                Text(
                    text = when (visualState) {
                        StreakVisualState.Active -> "Your streak is protected"
                        StreakVisualState.Frozen -> "You missed logging expenses"
                        StreakVisualState.Zero -> "Start your streak today"
                    },
                    fontSize = 13.sp,
                    color = if (isDarkTheme) Color(0xFFBFDBFE) else Color(0xFF6C84A8)
                )
            }
        }
    }
}

@Composable
private fun BoxCenter(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun FreezeStatusCardPreview() {
    MaterialTheme {
        FreezeStatusCard(
            visualState = StreakVisualState.Frozen,
            isDarkTheme = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

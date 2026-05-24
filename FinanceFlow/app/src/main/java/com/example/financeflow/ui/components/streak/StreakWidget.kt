package com.example.financeflow.ui.components.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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

@Composable
fun StreakWidget(
    icon: String,
    title: String,
    value: String,
    onClick: () -> Unit = {},
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(148.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        if (isDarkTheme) {
                            listOf(Color(0xFF1F2937), Color(0xFF111827))
                        } else {
                            listOf(Color(0xFFFFFFFF), Color(0xFFFFF9F4))
                        }
                    )
                )
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 22.sp)
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF8A7D9E)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF342A44)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakWidgetPreview() {
    MaterialTheme {
        StreakWidget(
            icon = "🔥",
            title = "Current Streak",
            value = "7 Days",
            onClick = {},
            isDarkTheme = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

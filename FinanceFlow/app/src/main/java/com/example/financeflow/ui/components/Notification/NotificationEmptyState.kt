package com.example.financeflow.ui.notifications.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.notifications.NotificationFilter

@Composable
fun NotificationEmptyState(
    filter: NotificationFilter,
    modifier: Modifier = Modifier
) {
    val title = when (filter) {
        NotificationFilter.ALL -> "All caught up!"
        NotificationFilter.UNREAD -> "No unread notifications"
        NotificationFilter.ALERTS -> "No alerts"
    }
    
    val subtitle = when (filter) {
        NotificationFilter.ALL -> "You have no notifications right now"
        NotificationFilter.UNREAD -> "All notifications have been read"
        NotificationFilter.ALERTS -> "No spending alerts at the moment"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔔", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

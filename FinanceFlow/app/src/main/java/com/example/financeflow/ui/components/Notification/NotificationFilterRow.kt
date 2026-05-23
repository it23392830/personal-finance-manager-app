package com.example.financeflow.ui.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.notifications.NotificationFilter

@Composable
fun NotificationFilterRow(
    selectedFilter: NotificationFilter,
    unreadCount: Int,
    onFilterSelected: (NotificationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterPill(
                label = "All",
                isSelected = selectedFilter == NotificationFilter.ALL,
                onClick = { onFilterSelected(NotificationFilter.ALL) }
            )
            FilterPill(
                label = "Unread",
                isSelected = selectedFilter == NotificationFilter.UNREAD,
                count = if (unreadCount > 0) unreadCount else null,
                onClick = { onFilterSelected(NotificationFilter.UNREAD) }
            )
            FilterPill(
                label = "Alerts",
                isSelected = selectedFilter == NotificationFilter.ALERTS,
                onClick = { onFilterSelected(NotificationFilter.ALERTS) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    count: Int? = null
) {
    val bgColor = if (isSelected) Color(0xFF8B5CF6) else Color(0xFFF3F4F6)
    val textColor = if (isSelected) Color.White else Color(0xFF6B7280)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(99.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = textColor,
                fontWeight = fontWeight,
                fontSize = 14.sp
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFFEF4444), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

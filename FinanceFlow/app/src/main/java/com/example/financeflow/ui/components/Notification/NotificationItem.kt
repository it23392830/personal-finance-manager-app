package com.example.financeflow.ui.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.notifications.NotificationType
import com.example.financeflow.ui.notifications.NotificationUiItem
import com.example.financeflow.ui.notifications.notificationBgColor
import com.example.financeflow.ui.notifications.notificationColor

@Composable
fun NotificationItem(
    item: NotificationUiItem,
    onRead: (Int) -> Unit,
    onDismiss: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (item.isRead) Color(0xFFFAFAFA) else Color(0xFFFFFFFF)
    
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRead(item.id) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left icon circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(notificationBgColor(item.type), RoundedCornerShape(12.dp))
                        .border(1.dp, notificationColor(item.type).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.timeAgo,
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.body,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(if (item.isRead) 0.75f else 1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Type badge
                    Surface(
                        color = notificationBgColor(item.type),
                        shape = RoundedCornerShape(99.dp),
                        border = Row { }.let { null } // Just for structure
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, notificationColor(item.type).copy(alpha = 0.3f), RoundedCornerShape(99.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.type.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() },
                                color = notificationColor(item.type),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Dismiss button
        IconButton(
            onClick = { onDismiss(item.id) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(14.dp)
            )
        }

        // Unread dot
        if (!item.isRead) {
            Box(
                modifier = Modifier
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(8.dp)
                    .background(Color(0xFF8B5CF6), CircleShape)
            )
        }
    }
}

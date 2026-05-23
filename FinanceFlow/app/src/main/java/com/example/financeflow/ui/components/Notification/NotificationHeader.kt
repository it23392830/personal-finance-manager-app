package com.example.financeflow.ui.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationHeader(
    unreadCount: Int,
    onBackClick: () -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5CF6))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        
        Text(
            text = "Notifications",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        if (unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text(
                    text = "$unreadCount unread",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (unreadCount > 0) {
            TextButton(onClick = onMarkAllRead) {
                Text(
                    text = "Mark all ✓",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

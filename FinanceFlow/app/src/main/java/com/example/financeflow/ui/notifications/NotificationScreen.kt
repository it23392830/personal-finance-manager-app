package com.example.financeflow.ui.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.FinanceNotification
import com.example.financeflow.model.FinanceNotificationType
import com.example.financeflow.viewmodel.notification.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NotificationFilter(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    REMINDERS("Reminders"),
    MISSED_ACTIVITY("Missed Activity")
}

data class NotificationUiItem(
    val id: String,
    val type: String,
    val emoji: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean
)

private data class NotificationPalette(
    val screenBg: Color,
    val topCardBg: Color,
    val topCardBorder: Color,
    val cardBg: Color,
    val unreadCardBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val chipBg: Color,
    val chipSelectedBg: Color,
    val chipSelectedText: Color,
    val chipText: Color,
    val unreadDot: Color,
    val badgeBg: Color,
    val deleteBg: Color,
    val markReadBg: Color,
    val iconBg: Color
)

private fun notificationPalette(isDarkTheme: Boolean): NotificationPalette {
    return if (isDarkTheme) {
        NotificationPalette(
            screenBg = Color(0xFF121216),
            topCardBg = Color(0xFF1E1E27),
            topCardBorder = Color(0xFF343447),
            cardBg = Color(0xFF1D1D26),
            unreadCardBg = Color(0xFF262436),
            textPrimary = Color(0xFFF3F2FF),
            textSecondary = Color(0xFFB9B7CC),
            chipBg = Color(0xFF2B2B39),
            chipSelectedBg = Color(0xFF8B5CF6),
            chipSelectedText = Color.White,
            chipText = Color(0xFFD7D5EA),
            unreadDot = Color(0xFFFFB020),
            badgeBg = Color(0xFFFF5E4D),
            deleteBg = Color(0xFFB03A3A),
            markReadBg = Color(0xFF2F8F5B),
            iconBg = Color(0xFF2C2A3B)
        )
    } else {
        NotificationPalette(
            screenBg = Color(0xFFF7F3FF),
            topCardBg = Color.White,
            topCardBorder = Color(0xFFECE7F7),
            cardBg = Color.White,
            unreadCardBg = Color(0xFFF7F2FF),
            textPrimary = Color(0xFF1B1730),
            textSecondary = Color(0xFF6D6885),
            chipBg = Color(0xFFF1ECFB),
            chipSelectedBg = Color(0xFF8B5CF6),
            chipSelectedText = Color.White,
            chipText = Color(0xFF5B5475),
            unreadDot = Color(0xFFFF7A3D),
            badgeBg = Color(0xFFFF5E4D),
            deleteBg = Color(0xFFFF6B6B),
            markReadBg = Color(0xFF4CAF50),
            iconBg = Color(0xFFEDE4FF)
        )
    }
}

private fun isReminderType(type: String): Boolean {
    return type == FinanceNotificationType.MORNING ||
        type == FinanceNotificationType.STREAK ||
        type == FinanceNotificationType.SAVINGS
}

private fun isMissedActivityType(type: String): Boolean {
    return type == FinanceNotificationType.MISSED
}

private fun notificationEmoji(type: String): String {
    return when (type) {
        FinanceNotificationType.MORNING -> "\u2600\ufe0f"
        FinanceNotificationType.MISSED -> "\ud83c\udf19"
        FinanceNotificationType.STREAK -> "\ud83d\udd25"
        FinanceNotificationType.SAVINGS -> "\ud83d\udcb0"
        else -> "\ud83d\udd14"
    }
}

private fun FinanceNotification.toUiItem(): NotificationUiItem {
    val formatter = SimpleDateFormat("h:mm a", Locale.US)
    return NotificationUiItem(
        id = id,
        type = type,
        emoji = notificationEmoji(type),
        title = title,
        message = message,
        time = formatter.format(Date(timestamp)),
        isRead = isRead
    )
}

@Composable
fun NotificationScreen(
    isDarkTheme: Boolean = false,
    onNavigateBack: () -> Unit = {},
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val palette = notificationPalette(isDarkTheme)
    val notificationModels by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val notifications = remember(notificationModels) { notificationModels.map { it.toUiItem() } }
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    val filteredNotifications = when (selectedFilter) {
        NotificationFilter.ALL -> notifications
        NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
        NotificationFilter.REMINDERS -> notifications.filter { isReminderType(it.type) }
        NotificationFilter.MISSED_ACTIVITY -> notifications.filter { isMissedActivityType(it.type) }
    }

    Scaffold(containerColor = palette.screenBg) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            NotificationTopSection(
                palette = palette,
                unreadCount = unreadCount,
                onNavigateBack = onNavigateBack
            )

            NotificationFilterChips(
                selectedFilter = selectedFilter,
                unreadCount = unreadCount,
                palette = palette,
                onSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredNotifications.isEmpty()) {
                NotificationEmptyState(palette = palette)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = filteredNotifications,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        var visible by remember(item.id) { mutableStateOf(false) }
                        LaunchedEffect(item.id) { visible = true }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(durationMillis = 320, delayMillis = index * 55)) +
                                scaleIn(initialScale = 0.96f, animationSpec = tween(durationMillis = 320, delayMillis = index * 55))
                        ) {
                            NotificationCard(
                                item = item,
                                palette = palette,
                                onDelete = viewModel::deleteNotification,
                                onMarkRead = viewModel::markAsRead
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTopSection(
    palette: NotificationPalette,
    unreadCount: Int,
    onNavigateBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.topCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.topCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notifications",
                    color = palette.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Stay on track with your daily finance habits",
                    color = palette.textSecondary,
                    fontSize = 12.sp
                )
            }

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .background(palette.badgeBg, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationFilterChips(
    selectedFilter: NotificationFilter,
    unreadCount: Int,
    palette: NotificationPalette,
    onSelected: (NotificationFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(NotificationFilter.entries.size) { index ->
            val filter = NotificationFilter.entries[index]
            val selected = selectedFilter == filter

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selected) palette.chipSelectedBg else palette.chipBg,
                tonalElevation = if (selected) 0.dp else 1.dp,
                shadowElevation = if (selected) 0.dp else 1.dp,
                onClick = { onSelected(filter) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = filter.label,
                        color = if (selected) palette.chipSelectedText else palette.chipText,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    if (filter == NotificationFilter.UNREAD && unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    color = if (selected) Color.White.copy(alpha = 0.2f) else palette.badgeBg,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    item: NotificationUiItem,
    palette: NotificationPalette,
    onDelete: (String) -> Unit,
    onMarkRead: (String) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            when (target) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onMarkRead(item.id)
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(item.id)
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val (bgColor, icon, label, align) = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    Quad(palette.markReadBg, Icons.Default.Check, "Mark read", Alignment.CenterStart)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    Quad(palette.deleteBg, Icons.Default.Delete, "Delete", Alignment.CenterEnd)
                }
                SwipeToDismissBoxValue.Settled -> {
                    Quad(Color.Transparent, Icons.Default.Check, "", Alignment.CenterStart)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp),
                contentAlignment = align
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (item.isRead) 0.74f else 1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (item.isRead) palette.cardBg else palette.unreadCardBg
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (item.isRead) palette.topCardBorder else palette.chipSelectedBg.copy(alpha = 0.25f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(palette.iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = palette.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.message,
                        color = palette.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!item.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(palette.unreadDot, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = item.time,
                            color = palette.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open",
                    tint = palette.textSecondary,
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationEmptyState(palette: NotificationPalette) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(palette.chipBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\ud83d\udd14", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No notifications yet",
                color = palette.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "We'll remind you about your finances here",
                color = palette.textSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

package com.example.financeflow.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financeflow.ui.notifications.components.NotificationEmptyState
import com.example.financeflow.ui.notifications.components.NotificationFilterRow
import com.example.financeflow.ui.notifications.components.NotificationHeader
import com.example.financeflow.ui.notifications.components.NotificationItem

// ── Enums ─────────────────────────────────────────────────────────────────────

enum class NotificationType {
    EXPENSE_ALERT,      // red
    INCOME,             // green
    GOAL,               // purple
    REMINDER,           // amber
    STREAK,             // orange
    RECURRING,          // blue
    BUDGET_WARNING,     // red-orange
    SYSTEM              // grey
}

// ── Data Class ────────────────────────────────────────────────────────────────

data class NotificationUiItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeAgo: String,        // "Just now", "2 min ago", "1 hr ago" etc.
    val isRead: Boolean,
    val emoji: String           // leading emoji for visual identity
)

// ── Filter Tabs ───────────────────────────────────────────────────────────────

enum class NotificationFilter { ALL, UNREAD, ALERTS }

// ── Hardcoded Notifications ───────────────────────────────────────────────────

val HARDCODED_NOTIFICATIONS = listOf(
    NotificationUiItem(
        id = 1, type = NotificationType.BUDGET_WARNING,
        title = "Spending Limit Warning",
        body = "You have used 85% of your discretionary budget. Only LKR 12,345 remaining for May.",
        timeAgo = "Just now", isRead = false, emoji = "⚠️"
    ),
    NotificationUiItem(
        id = 2, type = NotificationType.EXPENSE_ALERT,
        title = "Food expenses increased by 22%",
        body = "Your food spending this week is LKR 8,240 — 22% higher than last week's average.",
        timeAgo = "15 min ago", isRead = false, emoji = "🍕"
    ),
    NotificationUiItem(
        id = 3, type = NotificationType.STREAK,
        title = "🔥 3 Day Streak — Keep it up!",
        body = "You've logged expenses for 3 days in a row. Log today to reach 4!",
        timeAgo = "1 hr ago", isRead = false, emoji = "⚡"
    ),
    NotificationUiItem(
        id = 4, type = NotificationType.RECURRING,
        title = "Recurring payment due soon",
        body = "Electricity Bill (LKR 4,500) is due in 4 days on May 25.",
        timeAgo = "2 hrs ago", isRead = true, emoji = "🔁"
    ),
    NotificationUiItem(
        id = 5, type = NotificationType.GOAL,
        title = "MacBook Pro Goal Update",
        body = "You're 2.3% closer to your MacBook Pro M4 goal. LKR 490,000 remaining.",
        timeAgo = "3 hrs ago", isRead = true, emoji = "🎯"
    ),
    NotificationUiItem(
        id = 6, type = NotificationType.INCOME,
        title = "Income recorded",
        body = "LKR 45,000 from Freelance was added to your May 2026 income.",
        timeAgo = "Yesterday", isRead = true, emoji = "💰"
    ),
    NotificationUiItem(
        id = 7, type = NotificationType.REMINDER,
        title = "Daily expense reminder",
        body = "You haven't logged any expenses today. Don't forget to record your spending!",
        timeAgo = "Yesterday", isRead = true, emoji = "🔔"
    ),
    NotificationUiItem(
        id = 8, type = NotificationType.EXPENSE_ALERT,
        title = "Large expense detected",
        body = "A single expense of LKR 34,000 (Monthly Rent) was recorded — your largest this month.",
        timeAgo = "2 days ago", isRead = true, emoji = "🏠"
    ),
    NotificationUiItem(
        id = 9, type = NotificationType.RECURRING,
        title = "Spotify Premium may be missed",
        body = "Spotify Premium (LKR 990) was due on May 10 and hasn't been logged yet.",
        timeAgo = "3 days ago", isRead = true, emoji = "🎵"
    ),
    NotificationUiItem(
        id = 10, type = NotificationType.GOAL,
        title = "Goal Impact Alert",
        body = "Your last expense delayed your MacBook Pro goal by 2 days. Reduce dining to recover.",
        timeAgo = "3 days ago", isRead = true, emoji = "⚡"
    ),
    NotificationUiItem(
        id = 11, type = NotificationType.SYSTEM,
        title = "May 2026 Summary Ready",
        body = "Your monthly expense report for May 2026 is ready. Total spent: LKR 53,150.",
        timeAgo = "4 days ago", isRead = true, emoji = "📊"
    ),
    NotificationUiItem(
        id = 12, type = NotificationType.INCOME,
        title = "AdSense income added",
        body = "+LKR 5,200 from AdSense (USD) converted and added to May income.",
        timeAgo = "5 days ago", isRead = true, emoji = "📈"
    ),
)

// ── Color helper ──────────────────────────────────────────────────────────────

fun notificationColor(type: NotificationType): Color = when (type) {
    NotificationType.EXPENSE_ALERT    -> Color(0xFFEF4444)
    NotificationType.BUDGET_WARNING   -> Color(0xFFF97316)
    NotificationType.INCOME           -> Color(0xFF22C55E)
    NotificationType.GOAL             -> Color(0xFF8B5CF6)
    NotificationType.REMINDER         -> Color(0xFFF59E0B)
    NotificationType.STREAK           -> Color(0xFFF97316)
    NotificationType.RECURRING        -> Color(0xFF3B82F6)
    NotificationType.SYSTEM           -> Color(0xFF6B7280)
}

fun notificationBgColor(type: NotificationType): Color = when (type) {
    NotificationType.EXPENSE_ALERT    -> Color(0xFFFEF2F2)
    NotificationType.BUDGET_WARNING   -> Color(0xFFFFF7ED)
    NotificationType.INCOME           -> Color(0xFFECFDF5)
    NotificationType.GOAL             -> Color(0xFFF5F3FF)
    NotificationType.REMINDER         -> Color(0xFFFFFBEB)
    NotificationType.STREAK           -> Color(0xFFFFF7ED)
    NotificationType.RECURRING        -> Color(0xFFEFF6FF)
    NotificationType.SYSTEM           -> Color(0xFFF9FAFB)
}

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit = {}
) {
    // ── Local state ────────────────────────────────────────────
    var notifications by remember { mutableStateOf(HARDCODED_NOTIFICATIONS) }
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    // ── Derived ────────────────────────────────────────────────
    val unreadCount = notifications.count { !it.isRead }

    val filtered = when (selectedFilter) {
        NotificationFilter.ALL    -> notifications
        NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
        NotificationFilter.ALERTS -> notifications.filter {
            it.type == NotificationType.EXPENSE_ALERT ||
            it.type == NotificationType.BUDGET_WARNING
        }
    }

    // ── Scaffold ───────────────────────────────────────────────
    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // 1. Header
            NotificationHeader(
                unreadCount   = unreadCount,
                onBackClick   = onNavigateBack,
                onMarkAllRead = {
                    notifications = notifications.map { it.copy(isRead = true) }
                }
            )

            // 2. Filter row
            NotificationFilterRow(
                selectedFilter   = selectedFilter,
                unreadCount      = unreadCount,
                onFilterSelected = { selectedFilter = it }
            )

            // 3. Notification list or empty state
            if (filtered.isEmpty()) {
                NotificationEmptyState(filter = selectedFilter)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtered.forEach { item ->
                        NotificationItem(
                            item    = item,
                            onRead  = { id ->
                                notifications = notifications.map {
                                    if (it.id == id) it.copy(isRead = true) else it
                                }
                            },
                            onDismiss = { id ->
                                notifications = notifications.filter { it.id != id }
                            }
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

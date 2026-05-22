package com.example.financeflow.ui.dashboard

import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.weight

// ─────────────────────────────────────────────
//  Design Tokens
// ─────────────────────────────────────────────
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardColor       = Color(0xFFFFFFFF)
private val PrimaryPurple   = Color(0xFF8B5CF6)

private
// ─────────────────────────────────────────────
//  Design Tokens
// ─────────────────────────────────────────────
private val BackgroundColor = androidx.compose.ui.graphics.Color(0xFFF8FAFC)
private val CardColor = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
private val PrimaryPurple = androidx.compose.ui.graphics.Color(0xFF8B5CF6)
private val StreakFire = androidx.compose.ui.graphics.Color(0xFFF59E0B)
private val SuccessGreen = androidx.compose.ui.graphics.Color(0xFF22C55E)
private val TextPrimary = androidx.compose.ui.graphics.Color(0xFF1F2937)
private val TextMuted = androidx.compose.ui.graphics.Color(0xFF6B7280)
private val BorderColor = androidx.compose.ui.graphics.Color(0xFFE5E7EB)

/**
 * StreakScreen - Pure UI Layer
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun StreakScreen() {
    androidx.compose.material3.Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    androidx.compose.material3.Text(
                        text = "My Streak",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = {}) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp)
        ) {
            // ── 2. Current Streak Card ────────────────────────────────────────
            item {
                CurrentStreakCard()
            }

            // ── 3. Weekly Streak Calendar ─────────────────────────────────────
            item {
                WeeklyStreakCalendar()
            }

            // ── 4. Streak Stats Row ───────────────────────────────────────────
            item {
                StreakStatsRow()
            }

            // ── 5. How to Keep Your Streak Section ────────────────────────────
            item {
                HowToKeepStreakSection()
            }

            // ── 6. Recent Activity Section ────────────────────────────────────
            item {
                RecentActivitySection()
            }

            // ── 7. Motivational Banner ────────────────────────────────────────
            item {
                MotivationalBanner()
            }

            item { androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp)) }
        }
    }
}

@androidx.compose.runtime.Composable
private fun CurrentStreakCard() {
    androidx.compose.material3.Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(androidx.compose.ui.graphics.Color(0xFFFBBF24), StreakFire)
                    )
                )
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                androidx.compose.material3.Text(
                    text = "3 🔥",
                    style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 64.sp
                    )
                )
                androidx.compose.material3.Text(
                    text = "Day Streak",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = "Keep it up! Log expenses daily to grow your streak",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun WeeklyStreakCalendar() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val loggedStatus = listOf(true, true, true, false, false, false, false)
    val today = "Wed"

    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            androidx.compose.material3.Text(
                text = "Weekly Activity",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TextPrimary
                )
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    val isLogged = loggedStatus[index]
                    val isToday = day == today

                    androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        androidx.compose.material3.Text(
                            text = day,
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                color = if (isToday) PrimaryPurple else TextMuted,
                                fontWeight = if (isToday) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.foundation.layout.Box(
                            modifier = androidx.compose.ui.Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isToday) PrimaryPurple.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent)
                                .then(
                                    if (isToday) androidx.compose.ui.Modifier.border(
                                        2.dp,
                                        PrimaryPurple,
                                        CircleShape
                                    ) else androidx.compose.ui.Modifier
                                ),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            if (isLogged) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = androidx.compose.ui.Modifier.size(20.dp)
                                )
                            } else {
                                androidx.compose.foundation.layout.Box(
                                    modifier = androidx.compose.ui.Modifier.size(10.dp).clip(CircleShape)
                                        .background(BorderColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun StreakStatsRow() {
    androidx.compose.foundation.layout.Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        StatCard(emoji = "🔥", label = "Current", value = "3 days", modifier = androidx.compose.ui.Modifier.weight(1f))
        StatCard(emoji = "🏆", label = "Best", value = "12 days", modifier = androidx.compose.ui.Modifier.weight(1f))
        StatCard(emoji = "📅", label = "This Month", value = "18/31", modifier = androidx.compose.ui.Modifier.weight(1f))
    }
}

@androidx.compose.runtime.Composable
private fun StatCard(emoji: String, label: String, value: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = modifier.shadow(2.dp, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.padding(12.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.Text(text = emoji, fontSize = 20.sp)
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
            androidx.compose.material3.Text(
                text = value,
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TextPrimary
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            androidx.compose.material3.Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun HowToKeepStreakSection() {
    androidx.compose.foundation.layout.Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
        androidx.compose.material3.Text(
            text = "How to Keep Your Streak",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )
        )
        androidx.compose.material3.Card(
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardColor),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                TipRow("Log at least 1 expense per day")
                TipRow("Daily reminder set for 8:00 PM")
                TipRow("Even logging LKR 0 counts!")
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun TipRow(text: String) {
    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.size(6.dp).clip(CircleShape).background(PrimaryPurple))
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
        androidx.compose.material3.Text(text = text, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(color = TextMuted))
    }
}

@androidx.compose.runtime.Composable
private fun RecentActivitySection() {
    androidx.compose.foundation.layout.Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
        androidx.compose.material3.Text(
            text = "Recent Activity",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )
        )
        androidx.compose.material3.Card(
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardColor),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column {
                ActivityRow("Today · 3 expenses · LKR 1,270", true)
                androidx.compose.material3.HorizontalDivider(
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )
                ActivityRow("Yesterday · 2 expenses · LKR 890", true)
                androidx.compose.material3.HorizontalDivider(
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )
                ActivityRow("Mon · 0 expenses · Missed", false)
                androidx.compose.material3.HorizontalDivider(
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )
                ActivityRow("Sun · 4 expenses · LKR 2,100", true)
                androidx.compose.material3.HorizontalDivider(
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )
                ActivityRow("Sat · 1 expense · LKR 300", true)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ActivityRow(text: String, isSuccess: Boolean) {
    androidx.compose.foundation.layout.Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                color = if (isSuccess) TextPrimary else TextMuted,
                fontWeight = if (isSuccess) androidx.compose.ui.text.font.FontWeight.Medium else androidx.compose.ui.text.font.FontWeight.Normal
            ),
            modifier = androidx.compose.ui.Modifier.weight(1f)
        )
        androidx.compose.material3.Text(text = if (isSuccess) "✅" else "❌", fontSize = 18.sp)
    }
}

@androidx.compose.runtime.Composable
private fun MotivationalBanner() {
    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PrimaryPurple),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "You're 3 days away from your best streak! 🚀",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
private fun StreakScreenPreview() {
    StreakScreen()
}

val StreakFire      = Color(0xFFF59E0B)
private val SuccessGreen    = Color(0xFF22C55E)
private val TextPrimary     = Color(0xFF1F2937)
private val TextMuted       = Color(0xFF6B7280)
private val BorderColor     = Color(0xFFE5E7EB)

/**
 * StreakScreen - Pure UI Layer
 * 
 * Features:
 * 1. Header with back arrow and title "My Streak"
 * 2. Current Streak Card with amber gradient and "3 🔥"
 * 3. Weekly Streak Calendar with status indicators
 * 4. Streak Stats Row (Current, Best, Month)
 * 5. How to Keep Your Streak bullet tips
 * 6. Recent Activity list (last 5 days)
 * 7. Motivational Banner at bottom
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen() {
    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // ── 1. Header Section ─────────────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        text = "My Streak",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) { // Non-functional UI element only
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── 2. Current Streak Card ────────────────────────────────────────
            item {
                CurrentStreakCard()
            }

            // ── 3. Weekly Streak Calendar ─────────────────────────────────────
            item {
                WeeklyStreakCalendar()
            }

            // ── 4. Streak Stats Row ───────────────────────────────────────────
            item {
                StreakStatsRow()
            }

            // ── 5. How to Keep Your Streak Section ────────────────────────────
            item {
                HowToKeepStreakSection()
            }

            // ── 6. Recent Activity Section ────────────────────────────────────
            item {
                RecentActivitySection()
            }

            // ── 7. Motivational Banner ────────────────────────────────────────
            item {
                MotivationalBanner()
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun CurrentStreakCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFBBF24), StreakFire) // Amber to Orange
                    )
                )
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "3 🔥",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 64.sp
                    )
                )
                Text(
                    text = "Day Streak",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keep it up! Log expenses daily to grow your streak",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun WeeklyStreakCalendar() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    // Mock data: Wed is Today, Mon-Wed logged, Thu-Sun Missed
    val loggedStatus = listOf(true, true, true, false, false, false, false)
    val today = "Wed"

    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    val isLogged = loggedStatus[index]
                    val isToday = day == today
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isToday) PrimaryPurple else TextMuted,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> PrimaryPurple.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                )
                                .then(
                                    if (isToday) Modifier.border(2.dp, PrimaryPurple, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isLogged -> Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                else -> Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BorderColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(emoji = "🔥", label = "Current", value = "3 days", modifier = Modifier.weight(1f))
        StatCard(emoji = "🏆", label = "Best", value = "12 days", modifier = Modifier.weight(1f))
        StatCard(emoji = "📅", label = "This Month", value = "18/31 days logged", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.shadow(2.dp, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HowToKeepStreakSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "How to Keep Your Streak",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TipRow("Log at least 1 expense per day")
                TipRow("Daily reminder set for 8:00 PM")
                TipRow("Even logging LKR 0 counts!")
            }
        }
    }
}

@Composable
private fun TipRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(PrimaryPurple)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
        )
    }
}

@Composable
private fun RecentActivitySection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                val activities = listOf(
                    "Today · 3 expenses · LKR 1,270 ✅" to true,
                    "Yesterday · 2 expenses · LKR 890 ✅" to true,
                    "Mon · 0 expenses · Missed ❌" to false,
                    "Sun · 4 expenses · LKR 2,100 ✅" to true,
                    "Sat · 1 expense · LKR 300 ✅" to true
                )

                activities.forEachIndexed { index, (content, isSuccess) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSuccess) TextPrimary else TextMuted,
                                fontWeight = if (isSuccess) FontWeight.Medium else FontWeight.Normal
                            )
                        )
                    }
                    if (index < activities.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = BorderColor,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MotivationalBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You're 3 days away from your best streak! 🚀",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakScreenPreview() {
    StreakScreen()
}

package com.example.financeflow.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design Tokens
// CardWhite and OrangeAccent are provided by SavingsCard.kt in the same package
private val TextPrimary      = Color(0xFF1A1A2E)
private val TextSecondary    = Color(0xFF6B7280)
private val PrimaryPurple    = Color(0xFF7C4DFF)
private val ProgressTrack    = Color(0xFFEDE7FF)
private val ProgressFillStart= Color(0xFF7C4DFF)
private val ProgressFillEnd  = Color(0xFFB39DDB)
private val DaysChipBg       = Color(0xFFF3EDFF)
private val SavingsInfoBg    = Color(0xFFFFFDE7)
private val SavingsInfoBorder= Color(0xFFFFECB3)
private val WarningAmber     = Color(0xFFFF9800)

// Data models

/** Original Goal Data Model */
data class GoalProgressData(
    val goalTitle: String          = "MacBook Pro M4 Goal",
    val currentAmount: Long        = 11_200L,
    val targetAmount: Long         = 490_000L,
    val daysRemaining: Int         = 267,
    val dailySavingsNeeded: Long   = 1_794L,
    val currentDailyRate: Long     = 1_774L,
    val currencySymbol: String     = "LKR"
) {
    val progressFraction: Float
        get() = (currentAmount.toFloat() / targetAmount).coerceIn(0f, 1f)

    val progressPercent: Float
        get() = progressFraction * 100f

    val isOnTrack: Boolean
        get() = currentDailyRate >= dailySavingsNeeded
}

/** New Saving Goal Data Model */
data class SavingGoal(
    val name: String,
    val savedAmount: String,
    val targetAmount: String,
    val progressPercent: Float,   // 0f – 1f
    val progressLabel: String     // e.g. "40.1% complete"
)

// Dummy Data

fun goalProgressSample() = GoalProgressData()

val dummyGoals = listOf(
    SavingGoal(
        name = "MacBook Pro M4",
        savedAmount = "LKR 196,400",
        targetAmount = "Target: LKR 490,000",
        progressPercent = 0.401f,
        progressLabel = "40.1% complete"
    ),
    SavingGoal(
        name = "Emergency Fund",
        savedAmount = "LKR 14,000",
        targetAmount = "Target: LKR 30,000",
        progressPercent = 0.283f,
        progressLabel = "28.3% complete"
    ),
    SavingGoal(
        name = "Vacation",
        savedAmount = "LKR 20,000",
        targetAmount = "Target: LKR 150,000",
        progressPercent = 0.133f,
        progressLabel = "13.3% complete"
    )
)

// Composables (Original)

@Composable
fun GoalProgressCard(
    data: GoalProgressData = goalProgressSample(),
    modifier: Modifier = Modifier
) {
    // Animate progress bar on first composition
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue    = if (animationPlayed) data.progressFraction else 0f,
        animationSpec  = tween(durationMillis = 900),
        label          = "goalProgress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 4.dp,
                shape        = RoundedCornerShape(20.dp),
                ambientColor = PrimaryPurple.copy(alpha = 0.08f),
                spotColor    = PrimaryPurple.copy(alpha = 0.14f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Header row
            GoalHeaderRow(
                title         = data.goalTitle,
                daysRemaining = data.daysRemaining
            )

            // Amount row
            GoalAmountRow(
                current        = data.currentAmount,
                target         = data.targetAmount,
                currencySymbol = data.currencySymbol,
                percent        = data.progressPercent
            )

            // Progress bar
            GoalProgressBar(progress = animatedProgress)

            // Savings info panel
            DailySavingsPanel(
                needed         = data.dailySavingsNeeded,
                currentRate    = data.currentDailyRate,
                currencySymbol = data.currencySymbol,
                isOnTrack      = data.isOnTrack
            )
        }
    }
}

@Composable
private fun GoalHeaderRow(title: String, daysRemaining: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                fontSize   = 16.sp
            )
        )

        // Days remaining chip
        Surface(
            shape = RoundedCornerShape(50),
            color = DaysChipBg
        ) {
            Text(
                text     = "$daysRemaining days left",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style    = MaterialTheme.typography.labelSmall.copy(
                    color      = PrimaryPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 11.sp
                )
            )
        }
    }
}

@Composable
private fun GoalAmountRow(
    current: Long,
    target: Long,
    currencySymbol: String,
    percent: Float
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Bottom
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text  = "Current",
                style = MaterialTheme.typography.labelSmall.copy(
                    color    = TextSecondary,
                    fontSize = 11.sp
                )
            )
            Text(
                text  = "$currencySymbol ${"%,d".format(current)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextPrimary,
                    fontSize   = 22.sp
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text  = "Target",
                style = MaterialTheme.typography.labelSmall.copy(
                    color    = TextSecondary,
                    fontSize = 11.sp
                )
            )
            Text(
                text  = "$currencySymbol ${"%,d".format(target)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryPurple,
                    fontSize   = 16.sp
                )
            )
        }
    }
}

@Composable
private fun GoalProgressBar(progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Percentage label
        Text(
            text  = "${"%.1f".format(progress * 100)}% complete",
            style = MaterialTheme.typography.labelSmall.copy(
                color      = TextSecondary,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium
            )
        )

        // Custom gradient progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(ProgressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(ProgressFillStart, ProgressFillEnd)
                        )
                    )
            )
        }
    }
}

@Composable
private fun DailySavingsPanel(
    needed: Long,
    currentRate: Long,
    currencySymbol: String,
    isOnTrack: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SavingsInfoBg)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "Daily savings needed",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text  = "$currencySymbol ${"%,d".format(needed)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary,
                        fontSize   = 18.sp
                    )
                )
                Text(
                    text  = "Current rate: $currencySymbol ${"%,d".format(currentRate)}/day",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color    = if (isOnTrack) Color(0xFF2DBD6E) else WarningAmber,
                        fontSize = 11.sp
                    )
                )
            }

            // On-track indicator
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isOnTrack)
                    Color(0xFF2DBD6E).copy(alpha = 0.12f)
                else
                    WarningAmber.copy(alpha = 0.12f)
            ) {
                Text(
                    text     = if (isOnTrack) "✓ On Track" else "⚠ Behind",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style    = MaterialTheme.typography.labelSmall.copy(
                        color      = if (isOnTrack) Color(0xFF2DBD6E) else WarningAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 11.sp
                    )
                )
            }
        }
    }
}

// Composables (New)

@Composable
fun SavingsByGoalCard(goals: List<SavingGoal> = dummyGoals) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Section title
            Text(
                text = "Savings by Goal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Goal rows
            goals.forEachIndexed { index, goal ->
                GoalProgressItem(goal = goal)
                // Divider between items but not after last
                if (index < goals.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFF0F0F0),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun GoalProgressItem(
    goal: SavingGoal,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    // Each goal row owns its own menu state so cards open independently.
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Top row: name, amount, menu icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = goal.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = goal.savedAmount,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box {
                // The menu icon acts as the anchor for the floating action card.
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // The popup opens directly below the three-dot icon.
                ActionMenuCard(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onEdit = onEditClick,
                    onDelete = onDeleteClick
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Orange progress bar
        LinearProgressIndicator(
            progress = { goal.progressPercent },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = OrangeAccent,
            trackColor = Color(0xFFF0E6D0),
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom row: progress label left, target right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goal.progressLabel,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = goal.targetAmount,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

// Previews

@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun GoalProgressCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GoalProgressCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewSavingsByGoalCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SavingsByGoalCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewGoalProgressItem() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GoalProgressItem(goal = dummyGoals[0])
        }
    }
}

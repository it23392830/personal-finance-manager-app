package com.example.financeflow.ui.components.Home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.financeflow.ui.components.savings.ActionMenuCard
import com.example.financeflow.ui.components.CardWhite
import com.example.financeflow.ui.components.OrangeAccent

private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val PrimaryPurple = Color(0xFF7C4DFF)
private val ProgressTrack = Color(0xFFEDE7FF)
private val ProgressFillStart = Color(0xFF7C4DFF)
private val ProgressFillEnd = Color(0xFFB39DDB)
private val DaysChipBg = Color(0xFFF3EDFF)
private val SavingsInfoBg = Color(0xFFFFFDE7)
private val WarningAmber = Color(0xFFFF9800)

data class GoalProgressData(
    val goalTitle: String = "MacBook Pro M4 Goal",
    val currentAmount: Long = 11_200L,
    val targetAmount: Long = 490_000L,
    val daysRemaining: Int = 267,
    val dailySavingsNeeded: Long = 1_794L,
    val currentDailyRate: Long = 1_774L,
    val currencySymbol: String = "LKR"
) {
    val progressFraction: Float
        get() = (currentAmount.toFloat() / targetAmount).coerceIn(0f, 1f)

    val progressPercent: Float
        get() = progressFraction * 100f

    val isOnTrack: Boolean
        get() = currentDailyRate >= dailySavingsNeeded
}

data class SavingGoal(
    val name: String,
    val savedAmount: String,
    val targetAmount: String,
    val progressPercent: Float,
    val progressLabel: String
)

fun goalProgressSample() = GoalProgressData()

val defaultGoals: List<SavingGoal> = listOf(
    SavingGoal("MacBook Pro M4", "LKR 196,400", "Target: LKR 490,000", 0.401f, "40.1% complete"),
    SavingGoal("Emergency Fund", "LKR 14,000", "Target: LKR 30,000", 0.283f, "28.3% complete"),
    SavingGoal("Vacation", "LKR 20,000", "Target: LKR 150,000", 0.133f, "13.3% complete")
)

val dummyGoals: List<SavingGoal> = defaultGoals

@Composable
fun GoalProgressCard(
    data: GoalProgressData = goalProgressSample(),
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) data.progressFraction else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "goalProgress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = PrimaryPurple.copy(alpha = 0.08f),
                spotColor = PrimaryPurple.copy(alpha = 0.14f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GoalHeaderRow(title = data.goalTitle, daysRemaining = data.daysRemaining)
            GoalAmountRow(
                current = data.currentAmount,
                target = data.targetAmount,
                currencySymbol = data.currencySymbol,
                percent = data.progressPercent
            )
            GoalProgressBar(progress = animatedProgress)
            DailySavingsPanel(
                needed = data.dailySavingsNeeded,
                currentRate = data.currentDailyRate,
                currencySymbol = data.currencySymbol,
                isOnTrack = data.isOnTrack
            )
        }
    }
}

@Composable
private fun GoalHeaderRow(title: String, daysRemaining: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 16.sp
            )
        )
        Surface(shape = RoundedCornerShape(50), color = DaysChipBg) {
            Text(
                text = "$daysRemaining days left",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PrimaryPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Current",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
            Text(
                text = "$currencySymbol ${"%,d".format(current)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 22.sp
                )
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Target",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
            Text(
                text = "$currencySymbol ${"%,d".format(target)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun GoalProgressBar(progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "${"%.1f".format(progress * 100)}% complete",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        )
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Daily savings needed",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "$currencySymbol ${"%,d".format(needed)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Current rate: $currencySymbol ${"%,d".format(currentRate)}/day",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isOnTrack) Color(0xFF2DBD6E) else WarningAmber,
                        fontSize = 11.sp
                    )
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isOnTrack) Color(0xFF2DBD6E).copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f)
            ) {
                Text(
                    text = if (isOnTrack) "On Track" else "Behind",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isOnTrack) Color(0xFF2DBD6E) else WarningAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SavingsByGoalCard(
    goals: List<SavingGoal> = defaultGoals,
    onEditClick: (SavingGoal) -> Unit = {},
    onDeleteClick: (SavingGoal) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Savings by Goal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (goals.isEmpty()) {
                Text(
                    text = "No goals yet. Add one to get started!",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                goals.forEachIndexed { index, goal ->
                    GoalProgressItem(
                        goal = goal,
                        onEditClick = { onEditClick(goal) },
                        onDeleteClick = { onDeleteClick(goal) }
                    )
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
}

@Composable
fun GoalProgressItem(
    goal: SavingGoal,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
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
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                ActionMenuCard(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onEditClick = {
                        menuExpanded = false
                        onEditClick()
                    },
                    onDeleteClick = {
                        menuExpanded = false
                        onDeleteClick()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = goal.progressLabel, fontSize = 11.sp, color = Color.Gray)
            Text(text = goal.targetAmount, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

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
            GoalProgressItem(goal = defaultGoals[0])
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Empty Goal List")
@Composable
fun PreviewEmptyGoalCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SavingsByGoalCard(goals = emptyList())
        }
    }
}

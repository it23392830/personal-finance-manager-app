package com.example.financeflow.ui.components.savings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.financeflow.ui.theme.CardWhite
import com.example.financeflow.ui.theme.OrangeAccent

// Design Tokens - Light Mode
private val LightTextPrimary      = Color(0xFF1A1A2E)
private val LightTextSecondary    = Color(0xFF6B7280)
private val LightCardBg           = Color.White
private val LightProgressTrack    = Color(0xFFEDE7FF)
private val LightDaysChipBg       = Color(0xFFF3EDFF)
private val LightSavingsInfoBg    = Color(0xFFFFFDE7)

// Design Tokens - Dark Mode
private val DarkTextPrimary       = Color(0xFFE8E8E8)
private val DarkTextSecondary     = Color(0xFFB0B0B0)
private val DarkCardBg            = Color(0xFF2A2A3E)
private val DarkProgressTrack     = Color(0xFF3A3A4E)
private val DarkDaysChipBg        = Color(0xFF3E3E2A)
private val DarkSavingsInfoBg     = Color(0xFF3E3E2A)

private val PrimaryPurple    = Color(0xFF7C4DFF)
private val ProgressFillStart= Color(0xFF7C4DFF)
private val ProgressFillEnd  = Color(0xFFB39DDB)
private val WarningAmber     = Color(0xFFFF9800)

data class GoalProgressCardColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBg: Color,
    val progressTrack: Color,
    val daysChipBg: Color,
    val savingsInfoBg: Color
)

private fun getGoalProgressCardColors(isDarkTheme: Boolean): GoalProgressCardColors =
    if (isDarkTheme) {
        GoalProgressCardColors(
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            cardBg = DarkCardBg,
            progressTrack = DarkProgressTrack,
            daysChipBg = DarkDaysChipBg,
            savingsInfoBg = DarkSavingsInfoBg
        )
    } else {
        GoalProgressCardColors(
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            cardBg = LightCardBg,
            progressTrack = LightProgressTrack,
            daysChipBg = LightDaysChipBg,
            savingsInfoBg = LightSavingsInfoBg
        )
    }

// Data models

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

data class SavingGoal(
    val name: String,
    val savedAmount: String,
    val targetAmount: String,
    val progressPercent: Float,   // 0f – 1f
    val progressLabel: String     // e.g. "40.1% complete"
)

fun goalProgressSample() = GoalProgressData()

val defaultGoals: List<SavingGoal> = listOf(
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

val dummyGoals: List<SavingGoal> = defaultGoals

@Composable
fun GoalProgressCard(
    isDarkTheme: Boolean = false,
    data: GoalProgressData = goalProgressSample(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = getGoalProgressCardColors(isDarkTheme)
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
            .background(colors.cardBg)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GoalHeaderRow(
                isDarkTheme   = isDarkTheme,
                title         = data.goalTitle,
                daysRemaining = data.daysRemaining
            )
            GoalAmountRow(
                isDarkTheme    = isDarkTheme,
                current        = data.currentAmount,
                target         = data.targetAmount,
                currencySymbol = data.currencySymbol,
                percent        = data.progressPercent
            )
            GoalProgressBar(isDarkTheme = isDarkTheme, progress = animatedProgress)
            DailySavingsPanel(
                isDarkTheme    = isDarkTheme,
                needed         = data.dailySavingsNeeded,
                currentRate    = data.currentDailyRate,
                currencySymbol = data.currencySymbol,
                isOnTrack      = data.isOnTrack
            )
        }
    }
}

@Composable
private fun GoalHeaderRow(isDarkTheme: Boolean = false, title: String, daysRemaining: Int) {
    val colors = getGoalProgressCardColors(isDarkTheme)
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color      = colors.textPrimary,
                fontSize   = 16.sp
            )
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = colors.daysChipBg
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
    isDarkTheme: Boolean = false,
    current: Long,
    target: Long,
    currencySymbol: String,
    percent: Float
) {
    val colors = getGoalProgressCardColors(isDarkTheme)
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Bottom
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text  = "Current",
                style = MaterialTheme.typography.labelSmall.copy(
                    color    = colors.textSecondary,
                    fontSize = 11.sp
                )
            )
            Text(
                text  = "$currencySymbol ${"%,d".format(current)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = colors.textPrimary,
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
                    color    = colors.textSecondary,
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
private fun GoalProgressBar(isDarkTheme: Boolean = false, progress: Float) {
    val colors = getGoalProgressCardColors(isDarkTheme)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text  = "${"%.1f".format(progress * 100)}% complete",
            style = MaterialTheme.typography.labelSmall.copy(
                color      = colors.textSecondary,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.progressTrack)
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
    isDarkTheme: Boolean = false,
    needed: Long,
    currentRate: Long,
    currencySymbol: String,
    isOnTrack: Boolean
) {
    val colors = getGoalProgressCardColors(isDarkTheme)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.savingsInfoBg)
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
                        color    = colors.textSecondary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text  = "$currencySymbol ${"%,d".format(needed)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = colors.textPrimary,
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

@Composable
fun GoalProgressListCard(
    isDarkTheme: Boolean = false,
    goals: List<SavingGoal> = defaultGoals,
    onEditClick: (SavingGoal) -> Unit = {},
    onDeleteClick: (SavingGoal) -> Unit = {}
) {
    val colors = getGoalProgressCardColors(isDarkTheme)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Savings by Goal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (goals.isEmpty()) {
                Text(
                    text = "No goals yet. Add one to get started!",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                goals.forEachIndexed { index, goal ->
                    GoalProgressItem(
                        goal = goal,
                        isDarkTheme = isDarkTheme,
                        onEditClick = { onEditClick(goal) },
                        onDeleteClick = { onDeleteClick(goal) }
                    )
                    if (index < goals.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = colors.progressTrack,
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
    isDarkTheme: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val colors = getGoalProgressCardColors(isDarkTheme)

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
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = goal.savedAmount,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
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
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                ActionMenuCard(
                    isDarkTheme = isDarkTheme,
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
            trackColor = colors.progressTrack,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goal.progressLabel,
                fontSize = 11.sp,
                color = colors.textSecondary
            )
            Text(
                text = goal.targetAmount,
                fontSize = 11.sp,
                color = colors.textSecondary
            )
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
fun PreviewGoalProgressListCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GoalProgressListCard()
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

package com.example.financeflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import kotlin.math.min

// ──────────────────────────────────────────────
// Data model
// ──────────────────────────────────────────────

data class GoalProgressData(
    val goalName: String,
    val targetAmount: String,
    val savedAmount: String,
    val progressFraction: Float,          // 0f..1f
    val daysLeft: Int? = null,
    val icon: ImageVector = Icons.Rounded.Savings,
    val accentColor: Color = Color(0xFF9B72E8),
    val secondaryColor: Color = Color(0xFFD4AEFF),
    val category: String = "Goal"
)

// ──────────────────────────────────────────────
// Public composable — horizontal card layout
// ──────────────────────────────────────────────

@Composable
fun GoalProgressCard(
    data: GoalProgressData,
    modifier: Modifier = Modifier,
    animateProgress: Boolean = true
) {
    val cardShape = RoundedCornerShape(22.dp)
    val clampedFraction = data.progressFraction.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animateProgress) clampedFraction else clampedFraction,
        animationSpec = tween(durationMillis = 900, easing = EaseOutCubic),
        label = "goal_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                ambientColor = data.accentColor.copy(alpha = 0.22f),
                spotColor = data.accentColor.copy(alpha = 0.14f)
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Circular progress indicator ──────────
            GoalCircularProgress(
                progress = animatedProgress,
                accent = data.accentColor,
                secondary = data.secondaryColor,
                icon = data.icon,
                size = 68.dp
            )

            // ── Text section ─────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GoalHeader(
                    name = data.goalName,
                    category = data.category,
                    accent = data.accentColor
                )

                GoalAmountRow(
                    saved = data.savedAmount,
                    target = data.targetAmount
                )

                GoalLinearBar(
                    progress = animatedProgress,
                    accent = data.accentColor,
                    secondary = data.secondaryColor
                )

                GoalFooter(
                    progressFraction = clampedFraction,
                    daysLeft = data.daysLeft,
                    accent = data.accentColor
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Compact horizontal variant (for lists)
// ──────────────────────────────────────────────

@Composable
fun GoalProgressCardCompact(
    data: GoalProgressData,
    modifier: Modifier = Modifier
) {
    val clampedFraction = data.progressFraction.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedFraction,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "compact_progress"
    )

    val cardShape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(data.accentColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = data.accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.goalName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF1D1530),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${"%.0f".format(clampedFraction * 100)}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = data.accentColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            GoalLinearBar(
                progress = animatedProgress,
                accent = data.accentColor,
                secondary = data.secondaryColor,
                height = 6.dp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.savedAmount,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6B6880)
                )
                Text(
                    text = "of ${data.targetAmount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFADABBB)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Sub-composables
// ──────────────────────────────────────────────

@Composable
private fun GoalCircularProgress(
    progress: Float,
    accent: Color,
    secondary: Color,
    icon: ImageVector,
    size: Dp
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.toPx() / 2) - strokeWidth / 2

            // Track
            drawArc(
                color = secondary.copy(alpha = 0.20f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(accent, secondary, accent)
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(size * 0.38f)
        )
    }
}

@Composable
private fun GoalHeader(
    name: String,
    category: String,
    accent: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1D1530),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(
            text = category,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(accent.copy(alpha = 0.12f))
                .padding(horizontal = 7.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = accent
        )
    }
}

@Composable
private fun GoalAmountRow(
    saved: String,
    target: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = saved,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF1D1530)
        )
        Text(
            text = "/ $target",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFADABBB)
        )
    }
}

@Composable
private fun GoalLinearBar(
    progress: Float,
    accent: Color,
    secondary: Color,
    height: Dp = 7.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(secondary.copy(alpha = 0.18f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(accent, secondary)
                    )
                )
        )
    }
}

@Composable
private fun GoalFooter(
    progressFraction: Float,
    daysLeft: Int?,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${"%.0f".format(progressFraction * 100)}% achieved",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = accent
        )
        daysLeft?.let {
            Text(
                text = "$it days left",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFADABBB)
            )
        }
    }
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3EEFF)
@Composable
fun GoalProgressCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GoalProgressCard(
                data = GoalProgressData(
                    goalName = "Vacation Fund",
                    targetAmount = "$5,000",
                    savedAmount = "$3,200",
                    progressFraction = 0.64f,
                    daysLeft = 42,
                    icon = Icons.Rounded.BeachAccess,
                    accentColor = Color(0xFF9B72E8),
                    secondaryColor = Color(0xFFD4AEFF),
                    category = "Travel"
                )
            )
            GoalProgressCard(
                data = GoalProgressData(
                    goalName = "Emergency Fund",
                    targetAmount = "$10,000",
                    savedAmount = "$8,750",
                    progressFraction = 0.875f,
                    daysLeft = 15,
                    icon = Icons.Rounded.Security,
                    accentColor = Color(0xFF4EC9C9),
                    secondaryColor = Color(0xFF88E0D8),
                    category = "Safety"
                )
            )
        }
    }
}

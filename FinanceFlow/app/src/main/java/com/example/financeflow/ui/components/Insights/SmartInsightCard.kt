package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────
private val TextDark  = Color(0xFF1E1B2E)
private val TextMuted = Color(0xFF6B7280)

/** Type of a Smart Insight, which drives its border / icon color */
enum class InsightType {
    POSITIVE,  // green – good habit
    WARNING,   // orange – watch out
    NEUTRAL    // blue – informational
}

private fun InsightType.borderColor() = when (this) {
    InsightType.POSITIVE -> Color(0xFF22C55E)
    InsightType.WARNING  -> Color(0xFFF59E0B)
    InsightType.NEUTRAL  -> Color(0xFF60A5FA)
}

private fun InsightType.bgColor() = when (this) {
    InsightType.POSITIVE -> Color(0xFFEFFFF4)
    InsightType.WARNING  -> Color(0xFFFFFBEB)
    InsightType.NEUTRAL  -> Color(0xFFEFF6FF)
}

private fun InsightType.iconColor() = when (this) {
    InsightType.POSITIVE -> Color(0xFF22C55E)
    InsightType.WARNING  -> Color(0xFFF59E0B)
    InsightType.NEUTRAL  -> Color(0xFF60A5FA)
}

/**
 * SmartInsightCard
 *
 * Displays a single smart insight with a coloured border, icon, title,
 * body text and an optional action label.
 *
 * @param type       Visual variant (POSITIVE / WARNING / NEUTRAL).
 * @param icon       Leading icon.
 * @param title      Bold headline of the insight.
 * @param body       Descriptive body text.
 * @param actionText Optional clickable action string at the bottom.
 */
@Composable
fun SmartInsightCard(
    type: InsightType,
    icon: ImageVector,
    title: String,
    body: String,
    actionText: String? = null,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getInsightsColors(isDarkTheme)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, type.borderColor(), RoundedCornerShape(14.dp))
            .background(type.bgColor())
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            // Leading icon badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(type.iconColor().copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = type.iconColor(),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text column
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.TextDark
                )
                Text(
                    text = body,
                    fontSize = 12.sp,
                    color = colors.TextMuted,
                    lineHeight = 17.sp
                )
                if (actionText != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = actionText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = type.iconColor()
                    )
                }
            }
        }
    }
}

// ─── Sample data helpers ──────────────────────────────────────────────────────

/** Three smart insight cards matching the Figma design */
@Composable
fun SmartInsightsSection(isDarkTheme: Boolean = false) {
    val colors = getInsightsColors(isDarkTheme)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(
            text = "Smart Insights",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colors.TextDark
        )

        SmartInsightCard(
            type       = InsightType.POSITIVE,
            icon       = Icons.Default.CheckCircle,
            title      = "Strong Savings Habit",
            body       = "You've allocated 28% of income to savings this month – exceeding the recommended 20% target.",
            actionText = "Keep It Up !",
            isDarkTheme = isDarkTheme
        )

        SmartInsightCard(
            type       = InsightType.WARNING,
            icon       = Icons.Default.Warning,
            title      = "Optional Budget Usage High",
            body       = "You've used 83% of your optional budget. Consider reducing discretionary spending.",
            actionText = "Review optional expenses",
            isDarkTheme = isDarkTheme
        )

        SmartInsightCard(
            type       = InsightType.NEUTRAL,
            icon       = Icons.Default.TrendingDown,
            title      = "Must Expenses Stable",
            body       = "Your essential expenses have remained consistent at LKR 52,000/month for 4 months.",
            actionText = "Good control!",
            isDarkTheme = isDarkTheme
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun SmartInsightCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SmartInsightsSection()
    }
}

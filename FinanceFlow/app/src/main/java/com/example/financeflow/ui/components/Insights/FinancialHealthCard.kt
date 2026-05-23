package com.example.financeflow.ui.components.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────
private val PrimaryPurple = Color(0xFF8B5CF6)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextDark      = Color(0xFF1E1B2E)
private val TextMuted     = Color(0xFF9CA3AF)
private val IncomeGreen   = Color(0xFF22C55E)
private val ScoreBg       = Color(0xFFF3ECFF)

/**
 * FinancialHealthCard
 *
 * Displays a financial health score (0-100) with a linear progress bar
 * and three sub-metric chips: Savings Rate, Consistency, Goal Progress.
 *
 * @param score         Overall score integer (e.g. 23).
 * @param label         Text label under the score bar (e.g. "Good – Keep Improving!").
 * @param savingsRate   Savings rate percentage string (e.g. "28.8%").
 * @param consistency   Consistency score string (e.g. "75/100").
 * @param goalProgress  Goal progress decimal string (e.g. "2.2").
 */
@Composable
fun FinancialHealthCard(
    isDarkTheme: Boolean = false,
    score: Int = 23,
    label: String = "Good – Keep Improving!",
    savingsRate: String = "28.8%",
    consistency: String = "75/100",
    goalProgress: String = "2.2"
) {
    val colors = getInsightsColors(isDarkTheme)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Financial Health Score",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.TextDark
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.DayDetailBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = colors.PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Big score number
            Text(
                text = score.toString(),
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.PrimaryPurple
            )

            Spacer(Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = colors.PrimaryPurple,
                trackColor = colors.DayDetailBg,
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(6.dp))

            Text(text = label, fontSize = 12.sp, color = colors.TextMuted)

            Spacer(Modifier.height(16.dp))

            // Sub-metric chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HealthMetricChip(
                    icon = Icons.Default.Star,
                    label = "Savings Rate",
                    value = savingsRate,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                HealthMetricChip(
                    icon = Icons.Default.Favorite,
                    label = "Consistency",
                    value = consistency,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                HealthMetricChip(
                    icon = Icons.Default.FlashOn,
                    label = "Goal Progress",
                    value = goalProgress,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Individual metric chip inside the health card */
@Composable
private fun HealthMetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getInsightsColors(isDarkTheme)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.DayDetailBg)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null,
            tint = colors.PrimaryPurple, modifier = Modifier.size(18.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextDark)
        Text(text = label, fontSize = 10.sp, color = colors.TextMuted)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
fun FinancialHealthCardPreview() {
    FinancialHealthCard()
}

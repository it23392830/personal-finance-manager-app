package com.example.financeflow.ui.savings

import com.example.financeflow.ui.components.savings.getSavingsColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.savings.ContributionHistoryList
import com.example.financeflow.ui.components.savings.dummyContributions

// Uses shared savings theme tokens via getSavingsColors

// GoalDetailsScreen
//
// Root composable for the Goal Details page.
// Sections (top to bottom):
//   1. Header card  — title, subtitle, icon buttons
//   2. Goal info card — goal name, current amount, saved amount, progress bar
//   3. Add Contribution button card
//   4. Contribution History section label + card list
//
// All data is hardcoded dummy data.
// No ViewModel / Repository / Navigation / Firebase.

@Composable
fun GoalDetailsScreen(
    isDarkTheme: Boolean = false,
    onAddContribution: () -> Unit = {}
) {
    val colors = getSavingsColors(isDarkTheme)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── 1. Header card
        item { GoalDetailsHeaderCard(isDarkTheme = isDarkTheme) }

        // ── 2. Goal information card
        item {
            GoalInfoCard(
                isDarkTheme = isDarkTheme,
                goalName = "Vacation",
                currentAmount = "LKR 19,620.00",
                savedAmount = "LKR 6,53500.31",
                progress = 0.40f
            )
        }

        // ── 3. Add Contribution button card
        item { AddContributionCard(isDarkTheme = isDarkTheme, onAddContribution = onAddContribution) }

        // ── 4. Contribution History
        item {
            Text(
                text = "Contribution History",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item { ContributionHistoryList(isDarkTheme = isDarkTheme, entries = dummyContributions) }
    }
}

// GoalDetailsHeaderCard
//
// Top card matching the shared Savings Overview header design.
@Composable
private fun GoalDetailsHeaderCard(isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Savings Overview",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accent
                )
                Text(
                    text = "Track your saving habits & allocations",
                    fontSize = 12.sp,
                    color = colors.muted
                )
            }
        }
    }
}

// GoalInfoCard
//
// Displays:
//   - "Goal" label + goal name (bold, large)
//   - "Current Amount" label + amount (bold)
//   - "Amount Saved" label + saved amount (green #00D68F)
//   - Orange rounded progress bar on gray track
//
// Parameters:
//   goalName      – goal title, e.g. "Vacation"
//   currentAmount – e.g. "LKR 19,620.00"
//   savedAmount   – e.g. "LKR 6,53500.31"
//   progress      – 0f..1f fraction for the progress bar
@Composable
fun GoalInfoCard(
    isDarkTheme: Boolean = false,
    goalName: String      = "Vacation",
    currentAmount: String = "LKR 19,620.00",
    savedAmount: String   = "LKR 6,53500.31",
    progress: Float       = 0.40f
) {
    val colors2 = getSavingsColors(isDarkTheme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors2.cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // ── Goal label + name
            Text(
                text = "Goal",
                fontSize = 12.sp,
                color = colors2.muted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = goalName,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors2.textPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Current amount row
            GoalAmountRow(
                label  = "Current Amount",
                value  = currentAmount,
                valueColor = colors2.textPrimary,
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Amount Saved row (green)
            GoalAmountRow(
                label  = "Amount Saved",
                value  = savedAmount,
                valueColor = colors2.success,
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Orange rounded progress bar
            LinearProgressIndicator(
                progress    = { progress },
                modifier    = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color       = colors2.accent,
                trackColor  = colors2.progressTrack,
                strokeCap   = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Progress percentage label
            Text(
                text  = "${(progress * 100).toInt()}% saved",
                fontSize = 11.sp,
                color = colors2.muted
            )
        }
    }
}

// GoalAmountRow — helper: label on top, value below

@Composable
private fun GoalAmountRow(
    label: String,
    value: String,
    valueColor: Color,
    isDarkTheme: Boolean = false
) {
    val colors = getSavingsColors(isDarkTheme)
    Column {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = colors.muted
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text       = value,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            color      = valueColor
        )
    }
}

// AddContributionCard
//
// White rounded card containing a single centered orange CTA button.

@Composable
fun AddContributionCard(isDarkTheme: Boolean = false, onAddContribution: () -> Unit = {}) {
    val colors = getSavingsColors(isDarkTheme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onAddContribution,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "+ Add Contribution",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true, showSystemUi = true, name = "GoalDetailsScreen – Full")
@Composable
fun PreviewGoalDetailsScreen() {
    MaterialTheme {
        GoalDetailsScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewGoalDetailsHeaderCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GoalDetailsHeaderCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewGoalInfoCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GoalInfoCard()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF)
@Composable
fun PreviewAddContributionCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AddContributionCard()
        }
    }
}

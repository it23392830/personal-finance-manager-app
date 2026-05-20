package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Design tokens ─────────────────────────────────────────────────────────────
private val GreenCard = Color(0xFFA8E6B0)
private val TextDark  = Color(0xFF1F2937)

/**
 * A pastel-green banner reminding the user that their salary is due soon.
 *
 * @param daysUntilSalary   Number of days until the next salary date.
 * @param salaryDayOfMonth  Day of the month the salary is typically received (default 25).
 * @param modifier          Optional [Modifier].
 */
@Composable
fun SalaryReminderCard(
    daysUntilSalary: Int,
    salaryDayOfMonth: Int = 25,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar emoji icon
            Text(
                text = "📅",
                fontSize = 36.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Salary Due Soon",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDark
                )
                Text(
                    text = "Your monthly salary is typically received on the ${salaryDayOfMonth}th.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark.copy(alpha = 0.75f)
                )
                Text(
                    text = "That's in $daysUntilSalary days",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextDark
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF)
@Composable
private fun SalaryReminderCardPreview() {
    SalaryReminderCard(
        daysUntilSalary = 20,
        modifier = Modifier.padding(16.dp)
    )
}
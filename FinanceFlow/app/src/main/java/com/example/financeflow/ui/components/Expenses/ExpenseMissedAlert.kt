package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseMissedAlert(
    isDarkTheme: Boolean = false,
    missedItems: List<RecurringUiItem>,
    onMarkPaid: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (missedItems.isEmpty()) return
    val colors = getExpensesColors(isDarkTheme)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.ExpenseBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.ExpenseBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = colors.ExpenseRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Missed Payments",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.ExpenseRed
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                missedItems.forEach { item ->
                    val category = getCat(item.categoryId)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.CardBg, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(category.emoji, fontSize = 18.sp)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.TextPrimary
                            )
                            Text(
                                "${fmtLKR(item.amount)} • Was due ${item.nextDue}",
                                fontSize = 11.sp,
                                color = colors.TextMuted
                            )
                        }

                        Button(
                            onClick = { onMarkPaid(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.ExpenseRed),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseMissedAlertPreview() {
    FinanceFlowTheme {
        ExpenseMissedAlert(
            missedItems = HARDCODED_RECURRING.filter { it.missed },
            onMarkPaid = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

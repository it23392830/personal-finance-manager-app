package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.model.Income
import com.example.financeflow.model.IncomeSource
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background

// ── Design tokens ─────────────────────────────────────────────────────────────
private val LightTextDark  = Color(0xFF1F2937)
private val LightTextMuted = Color(0xFF6B7280)
private val LightCurrencyBg = Color(0xFFF3F4F6)

private val DarkTextDark   = Color(0xFFE8E8E8)
private val DarkTextMuted  = Color(0xFFB0B0B0)
private val DarkCurrencyBg = Color(0xFF3A3A4E)
private val GreenBadge = Color(0xFF22C55E)
private val RedDelete  = Color(0xFFEF4444)

private data class TransactionCardColors(
    val textDark: Color,
    val textMuted: Color,
    val currencyBg: Color
)

private fun getTransactionCardColors(isDarkTheme: Boolean): TransactionCardColors =
    if (isDarkTheme) {
        TransactionCardColors(DarkTextDark, DarkTextMuted, DarkCurrencyBg)
    } else {
        TransactionCardColors(LightTextDark, LightTextMuted, LightCurrencyBg)
    }

/**
 * A single row in the "Recent Transactions" list.
 *
 * Shows title, date, currency badge, amount, and a 3-dot overflow menu with
 * **Edit** and **Delete** actions.
 *
 * @param income       The [Income] entry to display.
 * @param onEditClick  Called when the user taps "Edit".
 * @param onDeleteClick Called when the user taps "Delete".
 * @param modifier     Optional [Modifier].
 */
@Composable
fun TransactionCard(
    isDarkTheme: Boolean = false,
    income: Income,
    expanded: Boolean = false,
    onToggleExpand: (String) -> Unit = {},
    onEditClick: (Income) -> Unit,
    onDeleteClick: (Income) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getTransactionCardColors(isDarkTheme)
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Title + date ──────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transactionTitle(income),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textDark
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "📅 ${formatDate(income.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
                CurrencyBadge(currency = income.currency)
            }
        }

        // ── Amount ────────────────────────────────────────────────────────────
        Text(
            text = formatAmount(income.currency, income.amount),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textDark
        )

        Spacer(modifier = Modifier.width(8.dp))

        // ── Expand / Collapse arrow ───────────────────────────────────────────
        IconButton(onClick = { onToggleExpand(income.id) }) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = colors.textMuted
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // ── 3-dot overflow menu ───────────────────────────────────────────────
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = colors.textMuted
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                // Edit option
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Edit",
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onEditClick(income)
                    },
                    modifier = Modifier.background(
                        color = GreenBadge,
                        shape = RoundedCornerShape(8.dp)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Delete option
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Delete",
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onDeleteClick(income)
                    },
                    modifier = Modifier.background(
                        color = RedDelete,
                        shape = RoundedCornerShape(8.dp)
                    )
                )
            }
        }
    }

    // Expanded details section
    val detailsBg = Color(0xFFF3E8FF)
    AnimatedVisibility(
        visible = expanded,
        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .background(detailsBg, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            // Date (dd/MM/yyyy)
            val shortDate = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(income.date.toDate())
            Text(text = "Date:\n$shortDate", color = colors.textDark)
            Spacer(modifier = Modifier.height(6.dp))
            // Description
            if (income.description.isNotBlank()) {
                Text(text = "Description:\n${income.description}", color = colors.textDark)
                Spacer(modifier = Modifier.height(6.dp))
            }
            // Additional Notes - not part of model; show createdAt as proxy if available
            Text(text = "Currency: ${income.currency}", color = colors.textMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Income Source: ${try { IncomeSource.valueOf(income.source).label } catch (e: Exception) { income.source }}", color = colors.textMuted)
        }
    }
}

// ── Small currency badge ──────────────────────────────────────────────────────

@Composable
fun CurrencyBadge(currency: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = LightCurrencyBg
    ) {
        Text(
            text = currency,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            ),
            color = LightTextMuted
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Derives a human-readable title from the income entry:
 * uses description if present, otherwise falls back to the source name.
 */
private fun transactionTitle(income: Income): String {
    // Keep the displayed title as the income source label.
    // Do not use the description as the title so editing description
    // won't change the name shown in the recent transactions list.
    return try {
        IncomeSource.valueOf(income.source).label
    } catch (e: Exception) {
        // Fallback to source string or description if source is invalid
        if (income.source.isNotBlank()) income.source else income.description
    }
}

private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

private fun formatDate(timestamp: Timestamp): String =
    dateFormatter.format(timestamp.toDate())

// ── Preview ───────────────────────────────────────────────────────────────────

private val fakeTransactions = listOf(
    Income(id = "1", source = "SALARY",    description = "Salary",                      amount = 135_000.0, currency = "LKR"),
    Income(id = "2", source = "FREELANCE", description = "Freelance - React Project",   amount =  45_000.0, currency = "LKR"),
    Income(id = "3", source = "ADSENSE",   description = "Google AdSense",              amount =      23.5, currency = "USD"),
    Income(id = "4", source = "CRYPTO",    description = "Crypto Trading",              amount =   2_300.0, currency = "LKR"),
    Income(id = "5", source = "FREELANCE", description = "Freelance - WordPress Site",  amount =   8_000.0, currency = "LKR"),
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TransactionCardPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        fakeTransactions.forEach { income ->
            TransactionCard(
                isDarkTheme = false,
                income = income,
                expanded = false,
                onToggleExpand = {},
                onEditClick = {},
                onDeleteClick = {}
            )
            HorizontalDivider(color = Color(0xFFF3F4F6))
        }
    }
}
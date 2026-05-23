package com.example.financeflow.ui.income

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Theme Colors (shared; declare only if not in a shared theme file) ────────
private val BgPurple      = Color(0xFFF3ECFF)
private val PrimaryPurple  = Color(0xFF8B5CF6)
private val IncomeGreen    = Color(0xFF22C55E)
private val CardWhite      = Color(0xFFFFFFFF)
private val TextDark       = Color(0xFF1E1B2E)
private val TextMuted      = Color(0xFF9CA3AF)
private val FieldBg        = Color(0xFFF9F6FF)
private val DividerColor   = Color(0xFFE9E2FF)
private val CancelPurple   = Color(0xFF7C3AED)

// ─── Sample data for edit prefill ────────────────────────────────────────────
@Suppress("DEPRECATION")
private val sampleIncomeSources = listOf(
    "Salary"     to Icons.Default.Work,
    "Freelance"  to Icons.Default.Code,
    "AdSense"    to Icons.Default.AttachMoney,
    "Crypto"     to Icons.Default.CurrencyBitcoin,
    "Investment" to Icons.Default.TrendingUp,
    "Other"      to Icons.Default.Category
)

private val sampleCurrencies = listOf(
    "LKR (Sri Lankan Rupee)",
    "USD (US Dollar)",
    "EUR (Euro)",
    "GBP (British Pound)",
    "AUD (Australian Dollar)"
)

/**
 * EditIncomeScreen
 *
 * Displays a pre-filled form for editing an existing income record.
 * In a real app the IncomeViewModel would supply the current values;
 * here they default to sample data so the Preview renders correctly.
 *
 * @param initialSource      Pre-selected income source.
 * @param initialAmount      Pre-filled amount string.
 * @param initialCurrency    Pre-selected currency.
 * @param initialDescription Pre-filled description.
 * @param initialDate        Pre-filled date string (dd/MM/yyyy).
 * @param initialNotes       Pre-filled notes.
 * @param onSaveChanges      Callback with updated fields.
 * @param onCancel           Back / cancel navigation callback.
 */
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIncomeScreen(
    isDarkTheme: Boolean = false,
    initialSource: String      = "Salary",
    initialAmount: String      = "135,000.00",
    initialCurrency: String    = "LKR (Sri Lankan Rupee)",
    initialDescription: String = "Monthly Salary – ABC Corp",
    initialDate: String        = "05/05/2026",
    initialNotes: String       = "",
    onSaveChanges: (source: String, amount: String, currency: String,
                    description: String, date: String, notes: String) -> Unit = { _, _, _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    // ── Local mutable state (prefilled with initial values) ───────────────────
    var amount           by remember { mutableStateOf(initialAmount) }
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }
    var selectedSource   by remember { mutableStateOf(initialSource) }
    var description      by remember { mutableStateOf(initialDescription) }
    var date             by remember { mutableStateOf(initialDate) }
    var notes            by remember { mutableStateOf(initialNotes) }

    var currencyExpanded by remember { mutableStateOf(false) }
    var sourceExpanded   by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // ── Root scaffold ─────────────────────────────────────────────────────────
    Scaffold(
        containerColor = BgPurple,
        topBar = {
            IncomeTopBar(title = "Edit Income", onNavigateUp = onCancel)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Form Card ─────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = PrimaryPurple.copy(alpha = 0.12f),
                            spotColor   = PrimaryPurple.copy(alpha = 0.18f)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        // Amount
                        IncomeFieldLabel("Amount")
                        IncomeAmountField(value = amount, onValueChange = { amount = it })

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Currency
                        IncomeFieldLabel("Currency")
                        IncomeDropdownField(
                            selectedValue    = selectedCurrency,
                            options          = sampleCurrencies,
                            expanded         = currencyExpanded,
                            onExpandChange   = { currencyExpanded = it },
                            onOptionSelected = { selectedCurrency = it; currencyExpanded = false },
                            leadingIcon      = Icons.Default.CurrencyExchange
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Income Source
                        IncomeFieldLabel("Income Source")
                        IncomeSourceDropdown(
                            selectedSource   = selectedSource,
                            sourceOptions    = sampleIncomeSources,
                            expanded         = sourceExpanded,
                            onExpandChange   = { sourceExpanded = it },
                            onOptionSelected = { selectedSource = it; sourceExpanded = false }
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Description
                        IncomeFieldLabel("Description (Optional)")
                        IncomeTextField(
                            value         = description,
                            onValueChange = { description = it },
                            placeholder   = "e.g., React Project for ABC Co.",
                            leadingIcon   = Icons.Default.Description
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Date
                        IncomeFieldLabel("Date")
                        IncomeDateField(value = date, onValueChange = { date = it })

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Notes
                        IncomeFieldLabel("Notes (Optional)")
                        IncomeTextField(
                            value         = notes,
                            onValueChange = { notes = it },
                            placeholder   = "Any additional notes…",
                            leadingIcon   = Icons.Default.Notes,
                            singleLine    = false,
                            minLines      = 3
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Action Buttons Row ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Save Changes – green
                Button(
                    onClick = {
                        onSaveChanges(selectedSource, amount, selectedCurrency,
                            description, date, notes)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IncomeGreen,
                        contentColor   = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Cancel – purple outline
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CancelPurple
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CancelPurple)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun EditIncomeScreenPreview() {
    MaterialTheme {
        EditIncomeScreen()
    }
}
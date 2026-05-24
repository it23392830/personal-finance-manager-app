package com.example.financeflow.ui.income

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.theme.FinanceFlowTheme

/**
 * EditIncomeScreen
 *
 * Displays a pre-filled form for editing an existing income record.
 * In a real app the IncomeViewModel would supply the current values;
 * here they default to sample data so the Preview renders correctly.
 *
 * @param incomeId           The ID of the income record being edited.
 * @param isDarkTheme        Whether the UI should render in dark mode.
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
    incomeId: String = "",
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
    val colors = getIncomeFormColors(isDarkTheme)
    // ── Local mutable state (prefilled with initial values) ───────────────────
    var amount           by remember { mutableStateOf(initialAmount) }
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }
    var selectedSource   by remember { mutableStateOf(initialSource) }
    var description      by remember { mutableStateOf(initialDescription) }
    var date             by remember { mutableStateOf(initialDate) }
    var notes            by remember { mutableStateOf(initialNotes) }

    // ── Dropdown expanded states ─────────────────────────────────────────────
    var sourceExpanded   by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── 1. Header card ──────────────────────────────────────────────────
        EditIncomeHeaderCard(colors)

        // ── 2. Main form card ───────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── Field 1: Amount ─────────────────────────────────────────
                IncomeFieldLabel("Amount", isDarkTheme)
                IncomeAmountField(amount, { amount = it }, isDarkTheme)

                // ── Field 2: Currency ───────────────────────────────────────
                IncomeFieldLabel("Currency", isDarkTheme)
                IncomeDropdownField(
                    selectedValue = selectedCurrency,
                    options = listOf("LKR (Sri Lankan Rupee)", "USD (US Dollar)", "EUR (Euro)", "GBP (British Pound)"),
                    expanded = currencyExpanded,
                    onExpandChange = { currencyExpanded = it },
                    onOptionSelected = { selectedCurrency = it; currencyExpanded = false },
                    leadingIcon = Icons.Default.CurrencyExchange,
                    isDarkTheme = isDarkTheme
                )

                // ── Field 3: Source ─────────────────────────────────────────
                IncomeFieldLabel("Source", isDarkTheme)
                IncomeSourceDropdown(
                    selectedSource = selectedSource,
                    sourceOptions = listOf(
                        "Salary" to Icons.Default.Work,
                        "Freelance" to Icons.Default.Code,
                        "Investment" to Icons.Default.TrendingUp,
                        "Other" to Icons.Default.Category
                    ),
                    expanded = sourceExpanded,
                    onExpandChange = { sourceExpanded = it },
                    onOptionSelected = { selectedSource = it; sourceExpanded = false },
                    isDarkTheme = isDarkTheme
                )

                // ── Field 4: Description ────────────────────────────────────
                IncomeFieldLabel("Description", isDarkTheme)
                IncomeTextField(description, { description = it }, "Description", Icons.Default.Description, isDarkTheme = isDarkTheme)

                // ── Field 5: Date ───────────────────────────────────────────
                IncomeFieldLabel("Date", isDarkTheme)
                IncomeDateField(date, { date = it }, isDarkTheme)

                // ── Field 6: Notes ───────────────────────────────────────────
                IncomeFieldLabel("Notes (Optional)", isDarkTheme)
                IncomeTextField(notes, { notes = it }, "Notes", Icons.Default.Notes, singleLine = false, minLines = 2, isDarkTheme = isDarkTheme)
            }
        }

        // ── 3. Action buttons ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Save Changes button
            Button(
                onClick = {
                    onSaveChanges(selectedSource, amount, selectedCurrency, description, date, notes)
                },
                modifier = Modifier.weight(1f).height(50.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.success, contentColor = Color.White)
            ) {
                Text("Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.secondary, contentColor = Color.White)
            ) {
                Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bottom breathing room
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun EditIncomeHeaderCard(colors: IncomeFormColors) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.fieldBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "Edit Income",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = colors.primary
                )
                Text(
                    text     = "Modify your transaction details",
                    fontSize = 12.sp,
                    color    = colors.textMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun EditIncomeScreenPreview() {
    FinanceFlowTheme {
        EditIncomeScreen()
    }
}

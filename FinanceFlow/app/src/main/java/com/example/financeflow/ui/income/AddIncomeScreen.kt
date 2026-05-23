package com.example.financeflow.ui.income

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.financeflow.viewmodel.income.IncomeViewModel
import com.example.financeflow.model.Income
import com.example.financeflow.model.IncomeSource
import com.google.firebase.Timestamp

// ─── Theme Colors ────────────────────────────────────────────────────────────
private val BgPurple    = Color(0xFFF3ECFF)
private val PrimaryPurple = Color(0xFF8B5CF6)
private val IncomeGreen = Color(0xFF22C55E)
private val CardWhite   = Color(0xFFFFFFFF)
private val TextDark    = Color(0xFF1E1B2E)
private val TextMuted   = Color(0xFF9CA3AF)
private val FieldBg     = Color(0xFFF9F6FF)
private val DividerColor = Color(0xFFE9E2FF)

// ─── Income Source Options ────────────────────────────────────────────────────
@Suppress("DEPRECATION")
private val incomeSourceOptions = listOf(
    "Salary"      to Icons.Default.Work,
    "Freelance"   to Icons.Default.Code,
    "AdSense"     to Icons.Default.AttachMoney,
    "Crypto"      to Icons.Default.CurrencyBitcoin,
    "Investment"  to Icons.Default.TrendingUp,
    "Other"       to Icons.Default.Category
)

// ─── Currency Options ─────────────────────────────────────────────────────────
private val currencyOptions = listOf(
    "LKR (Sri Lankan Rupee)",
    "USD (US Dollar)",
    "EUR (Euro)",
    "GBP (British Pound)",
    "AUD (Australian Dollar)"
)

/**
 * AddIncomeScreen
 *
 * Allows the user to enter a new income record.
 * Uses fake/sample data for preview; real data flows via ViewModel in production.
 *
 * @param onAddIncome  Callback fired with the new entry fields when "+ Add Income" is tapped.
 * @param onNavigateUp Back-navigation callback.
 */
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    onAddIncome: (source: String, amount: String, currency: String,
                  description: String, date: String, notes: String) -> Unit = { _, _, _, _, _, _ -> },
    onNavigateUp: () -> Unit = {}
) {
    // ── Local UI state ────────────────────────────────────────────────────────
    var amount          by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(currencyOptions[0]) }
    var selectedSource  by remember { mutableStateOf(incomeSourceOptions[0].first) }
    var description     by remember { mutableStateOf("") }
    var date            by remember { mutableStateOf("05/05/2026") }
    var notes           by remember { mutableStateOf("") }

    var currencyExpanded by remember { mutableStateOf(false) }
    var sourceExpanded   by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val viewModel: IncomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    // ── Root scaffold ─────────────────────────────────────────────────────────
    Scaffold(
        containerColor = BgPurple,
        topBar = {
            IncomeTopBar(title = "Add Income", onNavigateUp = onNavigateUp)
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
                            spotColor = PrimaryPurple.copy(alpha = 0.18f)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        // Amount
                        IncomeFieldLabel("Amount")
                        IncomeAmountField(
                            value = amount,
                            onValueChange = { amount = it }
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Currency
                        IncomeFieldLabel("Currency")
                        IncomeDropdownField(
                            selectedValue = selectedCurrency,
                            options = currencyOptions,
                            expanded = currencyExpanded,
                            onExpandChange = { currencyExpanded = it },
                            onOptionSelected = { selectedCurrency = it; currencyExpanded = false },
                            leadingIcon = Icons.Default.CurrencyExchange
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Income Source
                        IncomeFieldLabel("Income Source")
                        IncomeSourceDropdown(
                            selectedSource = selectedSource,
                            sourceOptions = incomeSourceOptions,
                            expanded = sourceExpanded,
                            onExpandChange = { sourceExpanded = it },
                            onOptionSelected = { selectedSource = it; sourceExpanded = false }
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Description
                        IncomeFieldLabel("Description (Optional)")
                        IncomeTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "e.g., React Project for ABC Co.",
                            leadingIcon = Icons.Default.Description
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Date
                        IncomeFieldLabel("Date")
                        IncomeDateField(
                            value = date,
                            onValueChange = { date = it }
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Notes
                        IncomeFieldLabel("Notes (Optional)")
                        IncomeTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = "Any additional notes…",
                            leadingIcon = Icons.Default.Notes,
                            singleLine = false,
                            minLines = 3
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Add Income Button ─────────────────────────────────────────────
            Button(
                onClick = {
                    // Create domain Income and persist via ViewModel, then call callbacks
                    val amountVal = amount.replace(",", "").toDoubleOrNull() ?: 0.0
                    val currencyCode = selectedCurrency.split(" ").firstOrNull() ?: "LKR"
                    val sourceEnum = when (selectedSource.lowercase()) {
                        "salary" -> IncomeSource.SALARY
                        "freelance" -> IncomeSource.FREELANCE
                        "adsense" -> IncomeSource.ADSENSE
                        "crypto" -> IncomeSource.CRYPTO
                        "investment" -> IncomeSource.INVESTMENT
                        "rental" -> IncomeSource.RENTAL
                        else -> IncomeSource.OTHER
                    }

                    val income = Income(
                        source = sourceEnum.name,
                        amount = amountVal,
                        currency = currencyCode,
                        description = description,
                        date = Timestamp.now()
                    )

                    scope.launch {
                        viewModel.addIncome(income)
                        onAddIncome(selectedSource, amount, selectedCurrency, description, date, notes)
                        onNavigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IncomeGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ Add Income",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Reusable Composables ─────────────────────────────────────────────────────

/** Top app bar shared across Income screens */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeTopBar(title: String, onNavigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = PrimaryPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = PrimaryPurple
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPurple),
        modifier = Modifier.shadow(0.dp)
    )
}

/** Small bold label above each field */
@Composable
fun IncomeFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextDark.copy(alpha = 0.7f)
    )
}

/** Styled numeric Amount field with up/down arrows */
@Composable
fun IncomeAmountField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("0.00", color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        trailingIcon = {
            Column {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            val current = value.toDoubleOrNull() ?: 0.0
                            onValueChange(String.format("%.2f", current + 500))
                        }
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            val current = value.toDoubleOrNull() ?: 0.0
                            onValueChange(String.format("%.2f", maxOf(0.0, current - 500)))
                        }
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = DividerColor,
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        )
    )
}

/** Generic text field for description / notes */
@Composable
fun IncomeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
        },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = DividerColor,
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        )
    )
}

/** Date field with calendar icon */
@Composable
fun IncomeDateField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Pick date",
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = DividerColor,
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        )
    )
}

/** Generic dropdown for list of strings (e.g. currencies) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeDropdownField(
    selectedValue: String,
    options: List<String>,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    leadingIcon: ImageVector
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = TextDark) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

/** Income Source dropdown with icons next to each option */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeSourceDropdown(
    selectedSource: String,
    sourceOptions: List<Pair<String, ImageVector>>,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val selectedIcon = sourceOptions.find { it.first == selectedSource }?.second
        ?: Icons.Default.Category

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = selectedSource,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(selectedIcon, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            sourceOptions.forEach { (label, icon) ->
                DropdownMenuItem(
                    text = { Text(label, color = TextDark) },
                    leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryPurple) },
                    onClick = { onOptionSelected(label) }
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun AddIncomeScreenPreview() {
    MaterialTheme {
        AddIncomeScreen()
    }
}

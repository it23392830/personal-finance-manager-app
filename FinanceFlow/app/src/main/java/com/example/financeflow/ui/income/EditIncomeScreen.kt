package com.example.financeflow.ui.income

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.theme.FinanceFlowTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.viewmodel.income.IncomeViewModel
import com.example.financeflow.model.Income
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp

/**
 * EditIncomeScreen
 *
 * Similar layout to AddIncomeScreen but pre-fills values for an existing income entry.
 * This version keeps the UI unchanged but adds:
 * - default date = today's date (dd/MM/yyyy)
 * - quick income source chips
 * - Add New Source dialog
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditIncomeScreen(
    incomeId: String = "",
    isDarkTheme: Boolean = false,
    onSaveChanges: (source: String, amount: String, currency: String, description: String, date: String, notes: String) -> Unit = { _, _, _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    val colors = getIncomeFormColors(isDarkTheme)
    val viewModel: IncomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // defaults
    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())

    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("LKR (Sri Lankan Rupee)") }
    var selectedSource by remember { mutableStateOf("Salary") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(todayStr) }
    var notes by remember { mutableStateOf("") }

    var sourceExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var showAddSourceDialog by remember { mutableStateOf(false) }
    var newSourceName by remember { mutableStateOf("") }

    // Load income if id provided
    LaunchedEffect(incomeId) {
        if (incomeId.isNotBlank()) {
            val inc = viewModel.getIncomeById(incomeId)
            inc?.let {
                selectedSource = it.source
                amount = String.format(Locale.ENGLISH, "%.2f", it.amount)
                selectedCurrency = it.currency
                description = it.description
                date = it.date.toDate().let { d -> SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(d) }
                notes = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        EditIncomeHeaderCard(colors = getIncomeFormColors(isDarkTheme))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = getIncomeFormColors(isDarkTheme).cardBg)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                // Amount
                IncomeFieldLabel("Amount", isDarkTheme)
                IncomeAmountField(value = amount, onValueChange = { amount = it }, isDarkTheme = isDarkTheme)

                // Currency
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

                // Quick Income Sources
                IncomeFieldLabel("Quick Income Sources", isDarkTheme)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sources = uiState.incomeSources.ifEmpty { listOf("Salary", "Freelance", "AdSense", "Crypto", "Business") }
                    sources.take(5).forEach { src ->
                        AssistChip(onClick = { selectedSource = src }, label = { Text(src) })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showAddSourceDialog = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+ Add New Source")
                }

                if (showAddSourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddSourceDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                if (newSourceName.isNotBlank()) {
                                    viewModel.addIncomeSource(newSourceName.trim())
                                    newSourceName = ""
                                }
                                showAddSourceDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = { TextButton(onClick = { showAddSourceDialog = false }) { Text("Cancel") } },
                        title = { Text("Add New Source") },
                        text = {
                            OutlinedTextField(value = newSourceName, onValueChange = { newSourceName = it }, placeholder = { Text("Enter source name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        }
                    )
                }

                // Income Source dropdown
                IncomeFieldLabel("Income Source", isDarkTheme)
                val dynamicSourceOptions = (uiState.incomeSources.takeIf { it.isNotEmpty() } ?: listOf("Salary", "Freelance", "AdSense", "Crypto", "Business")).map { label ->
                    val icon = when (label.lowercase()) {
                        "salary" -> Icons.Default.Work
                        "freelance" -> Icons.Default.Code
                        "adsense" -> Icons.Default.AttachMoney
                        "crypto" -> Icons.Default.CurrencyBitcoin
                        "investment" -> Icons.Default.TrendingUp
                        "rental" -> Icons.Default.Home
                        "business" -> Icons.Default.Business
                        else -> Icons.Default.Category
                    }
                    label to icon
                }

                IncomeSourceDropdown(
                    selectedSource = selectedSource,
                    sourceOptions = dynamicSourceOptions,
                    expanded = sourceExpanded,
                    onExpandChange = { sourceExpanded = it },
                    onOptionSelected = { selectedSource = it; sourceExpanded = false },
                    isDarkTheme = isDarkTheme
                )

                // Description
                IncomeFieldLabel("Description (Optional)", isDarkTheme)
                IncomeTextField(value = description, onValueChange = { description = it }, placeholder = "e.g., React Project for ABC Co.", leadingIcon = Icons.Default.Description, isDarkTheme = isDarkTheme)

                // Date
                IncomeFieldLabel("Date", isDarkTheme)
                IncomeDateField(value = date, onValueChange = { date = it }, isDarkTheme = isDarkTheme)

                // Notes
                IncomeFieldLabel("Notes (Optional)", isDarkTheme)
                IncomeTextField(value = notes, onValueChange = { notes = it }, placeholder = "Any additional notes…", leadingIcon = Icons.Default.Notes, singleLine = false, minLines = 3, isDarkTheme = isDarkTheme)

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        // build Income and update via ViewModel
                        val amountVal = amount.replace(",", "").toDoubleOrNull() ?: 0.0
                        val parsedDate = try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date) ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }
                        
                        val income = Income(
                            id = incomeId,
                            source = selectedSource,
                            amount = amountVal,
                            currency = selectedCurrency.split(" ").firstOrNull() ?: selectedCurrency,
                            description = description,
                            date = Timestamp(parsedDate)
                        )
                        scope.launch {
                            viewModel.updateIncome(income)
                            onSaveChanges(selectedSource, amount, selectedCurrency, description, date, notes)
                        }
                    }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = getIncomeFormColors(isDarkTheme).success, contentColor = Color.White)) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save Changes", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = getIncomeFormColors(isDarkTheme).secondary), border = androidx.compose.foundation.BorderStroke(2.dp, getIncomeFormColors(isDarkTheme).secondary)) {
                        Text(text = "Cancel", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

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
                    text = "Edit Income",
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = "Modify your transaction details",
                    fontSize = 12.sp,
                    color = colors.textMuted
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

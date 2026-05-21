package com.example.financeflow.ui.expenses

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.viewmodel.ExpenseViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedSubCategory by remember { mutableStateOf("Dining") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var expenseType by remember { mutableStateOf("ESSENTIAL") }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringFrequency by remember { mutableStateOf("Monthly") }
    
    val categories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health")
    val subCategories = mapOf(
        "Food" to listOf("Groceries", "Dining", "Coffee", "Snacks"),
        "Transport" to listOf("Fuel", "Public Transport", "Taxi", "Maintenance"),
        "Bills" to listOf("Rent", "Electricity", "Water", "Internet", "Mobile")
    )
    val paymentMethods = listOf("Cash", "Card", "Bank Transfer", "Digital Wallet")
    val frequencies = listOf("Daily", "Weekly", "Monthly", "Yearly")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Expense Type Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Type: ", style = MaterialTheme.typography.bodyLarge)
                FilterChip(
                    selected = expenseType == "ESSENTIAL",
                    onClick = { expenseType = "ESSENTIAL" },
                    label = { Text("Essential") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = expenseType == "DISCRETIONARY",
                    onClick = { expenseType = "DISCRETIONARY" },
                    label = { Text("Discretionary") }
                )
            }

            // Category Selection
            Text("Category", style = MaterialTheme.typography.titleMedium)
            ScrollableRow {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { 
                            selectedCategory = cat
                            selectedSubCategory = subCategories[cat]?.firstOrNull() ?: ""
                        },
                        label = { Text(cat) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Sub-Category Selection
            val currentSubs = subCategories[selectedCategory] ?: emptyList()
            if (currentSubs.isNotEmpty()) {
                Text("Sub-Category", style = MaterialTheme.typography.titleMedium)
                ScrollableRow {
                    currentSubs.forEach { sub ->
                        FilterChip(
                            selected = selectedSubCategory == sub,
                            onClick = { selectedSubCategory = sub },
                            label = { Text(sub) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            // Payment Method
            Text("Payment Method", style = MaterialTheme.typography.titleMedium)
            ScrollableRow {
                paymentMethods.forEach { method ->
                    FilterChip(
                        selected = selectedPaymentMethod == method,
                        onClick = { selectedPaymentMethod = method },
                        label = { Text(method) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Recurring Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recurring Expense", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
            }

            if (isRecurring) {
                Text("Frequency", style = MaterialTheme.typography.titleMedium)
                ScrollableRow {
                    frequencies.forEach { freq ->
                        FilterChip(
                            selected = recurringFrequency == freq,
                            onClick = { recurringFrequency = freq },
                            label = { Text(freq) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amountVal = amount.toDoubleOrNull() ?: 0.0
                    if (amountVal > 0) {
                        viewModel.addExpense(
                            amount = amountVal,
                            category = selectedCategory,
                            subCategory = selectedSubCategory,
                            paymentMethod = selectedPaymentMethod,
                            note = note,
                            expenseType = expenseType,
                            isRecurring = isRecurring,
                            recurringFrequency = if (isRecurring) recurringFrequency else ""
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Expense")
            }
        }
    }
}

@Composable
fun ScrollableRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

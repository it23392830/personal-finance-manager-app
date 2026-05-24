package com.example.financeflow.ui.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.Expense
import com.example.financeflow.ui.components.Expenses.*
import com.example.financeflow.ui.expenses.PaymentMethod
import com.example.financeflow.ui.expenses.getCatDisplayLabel
import com.example.financeflow.ui.expenses.getCat
import com.example.financeflow.ui.expenses.CAT_TREE
import com.example.financeflow.viewmodel.expense.ExpenseViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    isDarkTheme: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val colors = getExpensesColors(isDarkTheme)
    
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("food_dining") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CARD) }
    var date by remember { mutableStateOf(currentDate) }
    var notes by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.AppBg,
                    titleContentColor = colors.TextPrimary
                )
            )
        },
        containerColor = colors.AppBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // 1. Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                prefix = { Text("Rs. ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // 2. Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getCatDisplayLabel(category),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(colors.CardBg)
                ) {
                    CAT_TREE.forEach { (parent, children) ->
                        DropdownMenuItem(
                            text = { Text("${parent.emoji} ${parent.label}", fontWeight = FontWeight.Bold) },
                            onClick = { category = parent.id; expanded = false }
                        )
                        children.forEach { child ->
                            DropdownMenuItem(
                                text = { Text("   ↳ ${child.emoji} ${child.label}", fontSize = 14.sp) },
                                onClick = { category = child.id; expanded = false }
                            )
                        }
                    }
                }
            }

            // 3. Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text(getCat(category).label) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // 4. Payment Method Grid
            Text("Payment Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextMuted)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val methods = PaymentMethod.values().toList()
                methods.chunked(2).forEach { rowMethods ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowMethods.forEach { method ->
                            PaymentMethodButton(
                                method = method,
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 5. Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Date", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextMuted)
                    Text(date, fontSize = 15.sp, color = colors.TextPrimary)
                }
                IconButton(onClick = { /* Simplified: In a real app, show date picker */ }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = colors.Primary)
                }
            }

            // 6. Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )

            // 7. Fixed Expense Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Fixed Expense", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.TextPrimary)
                    Text("Save as a fixed monthly payment", fontSize = 11.sp, color = colors.TextMuted)
                }
                Switch(
                    checked = isRecurring,
                    onCheckedChange = { isRecurring = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.SuccessGreen
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            // Add Expense Button
            Button(
                onClick = {
                    val amtValue = amount.toDoubleOrNull() ?: 0.0
                    val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateObj = try { sdfInput.parse(date) } catch (_: Exception) { Date() }
                    
                    val expense = Expense(
                        amount = amtValue,
                        currency = "LKR",
                        category = category,
                        description = description,
                        notes = notes,
                        isFixed = isRecurring,
                        isPaid = false,
                        date = Timestamp(dateObj)
                    )
                    viewModel.addExpense(expense)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.HeaderRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Expense", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PaymentMethodButton(
    method: PaymentMethod,
    selected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(isDarkTheme)

    Surface(
        onClick = onClick,
        color = if (selected) colors.PrimaryLight else colors.CardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) colors.PrimaryBorder else colors.Border),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                method.label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) colors.PrimaryText else colors.TextPrimary
            )
        }
    }
}

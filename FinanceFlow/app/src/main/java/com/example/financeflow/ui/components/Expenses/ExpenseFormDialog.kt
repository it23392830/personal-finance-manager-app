package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.financeflow.ui.expenses.*
import com.example.financeflow.ui.theme.FinanceFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormDialog(
    isDarkTheme: Boolean = false,
    isEditMode: Boolean,
    amount: String,            onAmountChange: (String) -> Unit,
    description: String,       onDescriptionChange: (String) -> Unit,
    category: String,          onCategoryChange: (String) -> Unit,
    expenseType: ExpenseType,  onTypeChange: (ExpenseType) -> Unit,
    paymentMethod: PaymentMethod, onPaymentChange: (PaymentMethod) -> Unit,
    date: String,              onDateChange: (String) -> Unit,
    notes: String,             onNotesChange: (String) -> Unit,
    isRecurring: Boolean,      onRecurringChange: (Boolean) -> Unit,
    goalImpactDays: Float?,
    catTree: List<Pair<CategoryDef, List<CategoryDef>>>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = getExpensesColors(isDarkTheme)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            color = colors.CardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isEditMode) "Edit Expense" else "Add New Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.TextPrimary
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Expense Type Toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TypeToggleButton(
                            label = "Essential",
                            emoji = "🛡️",
                            selected = expenseType == ExpenseType.ESSENTIAL,
                            color = colors.MustAmber,
                            bgColor = colors.MustBg,
                            borderColor = colors.MustBorder,
                            onClick = { onTypeChange(ExpenseType.ESSENTIAL) },
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        TypeToggleButton(
                            label = "Discretionary",
                            emoji = "🎯",
                            selected = expenseType == ExpenseType.DISCRETIONARY,
                            color = colors.Primary,
                            bgColor = colors.PrimaryLight,
                            borderColor = colors.PrimaryBorder,
                            onClick = { onTypeChange(ExpenseType.DISCRETIONARY) },
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 2. Amount
                    OutlinedTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        label = { Text("Amount") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // 3. Goal Impact Warning
                    if (goalImpactDays != null && expenseType == ExpenseType.DISCRETIONARY) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.MustBg, RoundedCornerShape(12.dp))
                                .border(1.dp, colors.MustBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                                    Text(
                                        text = "⚡ This expense delays your MacBook Pro goal by ~${"%.1f".format(goalImpactDays)} days",
                                        fontSize = 12.sp,
                                        color = colors.MustText,
                                        fontWeight = FontWeight.Medium
                                    )
                        }
                    }

                    // 4. Category Dropdown
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
                            catTree.forEach { (parent, children) ->
                                DropdownMenuItem(
                                    text = { Text("${parent.emoji} ${parent.label}", fontWeight = FontWeight.Bold) },
                                    onClick = { onCategoryChange(parent.id); expanded = false }
                                )
                                children.forEach { child ->
                                    DropdownMenuItem(
                                        text = { Text("   ↳ ${child.emoji} ${child.label}", fontSize = 14.sp) },
                                        onClick = { onCategoryChange(child.id); expanded = false }
                                    )
                                }
                            }
                        }
                    }

                    // 5. Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Description (Optional)") },
                        placeholder = { Text(getCat(category).label) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // 6. Payment Method Grid
                    Text("Payment Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextMuted)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val methods = PaymentMethod.values().toList()
                        methods.chunked(2).forEach { rowMethods ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowMethods.forEach { method ->
                                    PaymentMethodButton(
                                        method = method,
                                        selected = paymentMethod == method,
                                        onClick = { onPaymentChange(method) },
                                        isDarkTheme = isDarkTheme,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 7. Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Date", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.TextMuted)
                            Text(date, fontSize = 15.sp, color = colors.TextPrimary)
                        }
                        IconButton(onClick = { /* Date picker logic simplified */ }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = colors.Primary)
                        }
                    }

                    // 8. Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    // 9. Recurring
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
                            onCheckedChange = onRecurringChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = colors.SuccessGreen)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = colors.TextMuted)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) colors.Primary else colors.HeaderRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEditMode) "Save Changes" else "Add Expense")
                    }
                }
            }
        }
    }
}

@Composable
fun TypeToggleButton(
    label: String,
    emoji: String,
    selected: Boolean,
    color: Color,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getExpensesColors(isDarkTheme)

    Surface(
        onClick = onClick,
        color = if (selected) bgColor else colors.CardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) borderColor else colors.Border),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) color else colors.TextMuted)
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

@Preview(showBackground = true)
@Composable
fun ExpenseFormDialogPreview() {
    FinanceFlowTheme {
        ExpenseFormDialog(
            isEditMode = false,
            amount = "1200",
            onAmountChange = {},
            description = "Lunch",
            onDescriptionChange = {},
            category = "food_dining",
            onCategoryChange = {},
            expenseType = ExpenseType.DISCRETIONARY,
            onTypeChange = {},
            paymentMethod = PaymentMethod.CARD,
            onPaymentChange = {},
            date = "2026-05-21",
            onDateChange = {},
            notes = "",
            onNotesChange = {},
            isRecurring = false,
            onRecurringChange = {},
            goalImpactDays = 0.5f,
            catTree = CAT_TREE,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

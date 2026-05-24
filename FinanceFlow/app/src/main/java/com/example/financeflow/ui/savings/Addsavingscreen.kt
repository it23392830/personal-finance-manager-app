package com.example.financeflow.ui.savings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.components.savings.SavingsColors
import com.example.financeflow.ui.components.savings.getSavingsColors

private val BgPurple = Color(0xFFEDE2FF)
private val GreenBtn = Color(0xFF3DBD7D)
private val PurpleBtn = Color(0xFF9B72CF)

private val currencyOptions = listOf(
    "LKR (Sri Lankan Rupee)",
    "USD (US Dollar)",
    "EUR (Euro)",
    "GBP (British Pound)"
)

private val goalOptions = listOf(
    "MacBook Pro M4",
    "Emergency Fund",
    "Vacation",
    "Travel Fund",
    "Other"
)

private val rawIncomeData = listOf(
    "Salary (Job)" to "2026-05-01",
    "Freelance (Web Project)" to "2026-05-15",
    "Side Hustle (Sales)" to "2026-05-20",
    "Dividends (JKH)" to "2026-05-25",
    "Bonus (April)" to "2026-04-28"
)

@Composable
fun AddSavingScreen(
    isDarkTheme: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(currencyOptions[0]) }
    var selectedGoal by remember { mutableStateOf("") }
    var selectedIncome by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("05/05/2026") }

    var currencyExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }
    var incomeExpanded by remember { mutableStateOf(false) }

    val todayStr = "2026-05-21"
    val currentMonth = "2026-05"
    val incomeOptions = remember {
        rawIncomeData
            .filter { (_, date) -> date.startsWith(currentMonth) && date <= todayStr }
            .map { it.first }
    }

    val colors = getSavingsColors(isDarkTheme)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AddSavingHeaderCard(isDarkTheme = isDarkTheme)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.formBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FormFieldLabel(text = "Amount", isDarkTheme = isDarkTheme)
                AmountField(value = amount, onChange = { amount = it }, isDarkTheme = isDarkTheme)

                FormFieldLabel(text = "Currency", isDarkTheme = isDarkTheme)
                DropdownField(
                    value = selectedCurrency,
                    expanded = currencyExpanded,
                    options = currencyOptions,
                    onExpand = { currencyExpanded = true },
                    onDismiss = { currencyExpanded = false },
                    onSelect = {
                        selectedCurrency = it
                        currencyExpanded = false
                    },
                    leadingIcon = null,
                    isPlaceholder = false,
                    isDarkTheme = isDarkTheme
                )

                FormFieldLabel(text = "Goal", isDarkTheme = isDarkTheme)
                DropdownField(
                    value = selectedGoal.ifEmpty { "Goal type" },
                    expanded = goalExpanded,
                    options = goalOptions,
                    onExpand = { goalExpanded = true },
                    onDismiss = { goalExpanded = false },
                    onSelect = {
                        selectedGoal = it
                        goalExpanded = false
                    },
                    leadingIcon = Icons.Default.CardGiftcard,
                    isPlaceholder = selectedGoal.isEmpty(),
                    isDarkTheme = isDarkTheme
                )

                FormFieldLabel(text = "Income Source", isDarkTheme = isDarkTheme)
                DropdownField(
                    value = selectedIncome.ifEmpty { "Select Income" },
                    expanded = incomeExpanded,
                    options = incomeOptions,
                    onExpand = { incomeExpanded = true },
                    onDismiss = { incomeExpanded = false },
                    onSelect = {
                        selectedIncome = it
                        incomeExpanded = false
                    },
                    leadingIcon = Icons.Default.Payments,
                    isPlaceholder = selectedIncome.isEmpty(),
                    isDarkTheme = isDarkTheme
                )

                FormFieldLabel(text = "Description (Optional)", isDarkTheme = isDarkTheme)
                DescriptionField(
                    value = description,
                    onChange = { description = it },
                    isDarkTheme = isDarkTheme
                )

                FormFieldLabel(text = "Date", isDarkTheme = isDarkTheme)
                DateField(
                    value = selectedDate,
                    onChange = { selectedDate = it },
                    isDarkTheme = isDarkTheme
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                text = "Save Changes",
                backgroundColor = colors.success,
                modifier = Modifier.weight(1f),
                onClick = { Toast.makeText(context, "Saving Added", Toast.LENGTH_SHORT).show() }
            )
            ActionButton(
                text = "Cancel",
                backgroundColor = colors.accent.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateBack() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddSavingHeaderCard(isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Add Savings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accent
                )
                Text(
                    text = "Track your saving habits & allocations",
                    fontSize = 12.sp,
                    color = colors.muted
                )
            }
        }
    }
}

@Composable
private fun FormFieldLabel(text: String, isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.textPrimary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun AmountField(value: String, onChange: (String) -> Unit, isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(text = "0.00", color = colors.muted) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint = colors.muted
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors(colors)
    )
}

@Composable
private fun DropdownField(
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    isPlaceholder: Boolean = false,
    isDarkTheme: Boolean = false
) {
    val colors = getSavingsColors(isDarkTheme)

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, colors.fieldBorder, RoundedCornerShape(14.dp))
                .background(colors.cardBg)
                .clickable { onExpand() }
                .padding(horizontal = 16.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = colors.muted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        color = if (isPlaceholder) colors.muted else colors.textPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = colors.muted
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 14.sp, color = colors.textPrimary) },
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
private fun DescriptionField(value: String, onChange: (String) -> Unit, isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(
                text = "e.g., React Project for ABC Co.",
                color = colors.muted,
                fontSize = 13.sp
            )
        },
        minLines = 3,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors(colors)
    )
}

@Composable
private fun DateField(value: String, onChange: (String) -> Unit, isDarkTheme: Boolean = false) {
    val colors = getSavingsColors(isDarkTheme)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Pick date",
                tint = colors.muted
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors(colors)
    )
}

@Composable
private fun fieldColors(colors: SavingsColors) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = colors.cardBg,
    unfocusedContainerColor = colors.cardBg,
    focusedBorderColor = colors.accent,
    unfocusedBorderColor = colors.fieldBorder,
    cursorColor = colors.accent,
    focusedTextColor = colors.textPrimary,
    unfocusedTextColor = colors.textPrimary
)

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "AddSavingScreen Full")
@Composable
fun PreviewAddSavingScreen() {
    MaterialTheme {
        AddSavingScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Header Card")
@Composable
fun PreviewAddSavingHeaderCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) { AddSavingHeaderCard() }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7E4A7, name = "Action Buttons")
@Composable
fun PreviewActionButtons() {
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                text = "Save Changes",
                backgroundColor = GreenBtn,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Cancel",
                backgroundColor = PurpleBtn,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

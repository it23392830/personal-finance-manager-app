package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.financeflow.model.Currency
import com.example.financeflow.model.Income
import com.example.financeflow.model.IncomeSource
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

// ── Design tokens ─────────────────────────────────────────────────────────────
private val LightDialogBg    = Color(0xFFA8E6B0)
private val LightGreenButton = Color(0xFF22C55E)
private val LightPurpleButton = Color(0xFF8B5CF6)
private val LightTextDark    = Color(0xFF1F2937)
private val LightTextMuted   = Color(0xFF6B7280)

private val DarkDialogBg     = Color(0xFF214233)
private val DarkGreenButton  = Color(0xFF2DBD6E)
private val DarkPurpleButton = Color(0xFF7C3AED)
private val DarkTextDark     = Color(0xFFE8E8E8)
private val DarkTextMuted    = Color(0xFFB0B0B0)

private data class EditIncomeDialogColors(
    val dialogBg: Color,
    val greenButton: Color,
    val purpleButton: Color,
    val textDark: Color,
    val textMuted: Color,
    val fieldBg: Color,
    val border: Color
)

private fun getEditIncomeDialogColors(isDarkTheme: Boolean): EditIncomeDialogColors =
    if (isDarkTheme) {
        EditIncomeDialogColors(
            dialogBg = DarkDialogBg,
            greenButton = DarkGreenButton,
            purpleButton = DarkPurpleButton,
            textDark = DarkTextDark,
            textMuted = DarkTextMuted,
            fieldBg = Color(0xFF2A2A3E),
            border = Color(0xFF3A3A4E)
        )
    } else {
        EditIncomeDialogColors(
            dialogBg = LightDialogBg,
            greenButton = LightGreenButton,
            purpleButton = LightPurpleButton,
            textDark = LightTextDark,
            textMuted = LightTextMuted,
            fieldBg = Color.White,
            border = Color(0xFFD1D5DB)
        )
    }

/**
 * Dialog for editing an existing income entry.
 *
 * Pre-populates all fields from [existingIncome].
 *
 * @param existingIncome The [Income] to edit.
 * @param onDismiss      Called when the user cancels or taps outside.
 * @param onConfirm      Called with the updated [Income] on "Save Changes".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIncomeDialog(
    isDarkTheme: Boolean = false,
    existingIncome: Income,
    onDismiss: () -> Unit,
    onConfirm: (Income) -> Unit
) {
    val colors = getEditIncomeDialogColors(isDarkTheme)
    // ── Pre-populate form from existing entry ─────────────────────────────────
    var amountText       by remember { mutableStateOf(existingIncome.amount.toString()) }
    var selectedCurrency by remember {
        mutableStateOf(
            Currency.entries.firstOrNull { it.code == existingIncome.currency } ?: Currency.LKR
        )
    }
    var selectedSource   by remember {
        mutableStateOf(
            runCatching { IncomeSource.valueOf(existingIncome.source) }.getOrDefault(IncomeSource.SALARY)
        )
    }
    var description      by remember { mutableStateOf(existingIncome.description) }
    var selectedDate     by remember { mutableStateOf(existingIncome.date.toDate()) }

    var currencyExpanded by remember { mutableStateOf(false) }
    var sourceExpanded   by remember { mutableStateOf(false) }
    var amountError      by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.dialogBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Header ────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Income",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textDark)
                    }
                }

                // ── Amount ────────────────────────────────────────────────────
                EditIncomeDialogLabel("Amount", colors)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) {{ Text("Please enter a valid amount") }} else null,
                    trailingIcon = {
                        Column {
                            Text("▲", fontSize = 8.sp, color = colors.textMuted)
                            Text("▼", fontSize = 8.sp, color = colors.textMuted)
                        }
                    },
                    colors = editIncomeFieldColors(colors),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Currency ──────────────────────────────────────────────────
                EditIncomeDialogLabel("Currency", colors)
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = { currencyExpanded = !currencyExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCurrency.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.textMuted)
                        },
                        colors = editIncomeFieldColors(colors),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = { currencyExpanded = false }
                    ) {
                        Currency.entries.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency.label) },
                                onClick = { selectedCurrency = currency; currencyExpanded = false }
                            )
                        }
                    }
                }

                // ── Income Source ─────────────────────────────────────────────
                EditIncomeDialogLabel("Income Source", colors)
                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = !sourceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSource.label,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Text(sourceEmoji(selectedSource), fontSize = 18.sp) },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.textMuted)
                        },
                        colors = editIncomeFieldColors(colors),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false }
                    ) {
                        IncomeSource.entries.forEach { source ->
                            DropdownMenuItem(
                                text = { Text("${sourceEmoji(source)}  ${source.label}") },
                                onClick = { selectedSource = source; sourceExpanded = false }
                            )
                        }
                    }
                }

                // ── Description ───────────────────────────────────────────────
                EditIncomeDialogLabel("Description (Optional)", colors)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g., React Project for ABC Co.", color = colors.textMuted) },
                    colors = editIncomeFieldColors(colors),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Date ──────────────────────────────────────────────────────
                EditIncomeDialogLabel("Date", colors)
                OutlinedTextField(
                    value = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(selectedDate),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date", tint = colors.textMuted)
                    },
                    colors = editIncomeFieldColors(colors),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ── Action buttons ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save Changes
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                amountError = true
                                return@Button
                            }
                            onConfirm(
                                existingIncome.copy(
                                    amount      = amount,
                                    currency    = selectedCurrency.code,
                                    source      = selectedSource.name,
                                    description = description.trim(),
                                    date        = Timestamp(selectedDate)
                                )
                            )
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.greenButton)
                    ) {
                        Text(
                            "Save Changes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    // Cancel
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.purpleButton)
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun EditIncomeDialogPreview() {
    EditIncomeDialog(
        isDarkTheme = false,
        existingIncome = Income(
            id = "preview",
            amount = 135_000.0,
            currency = "LKR",
            source = "SALARY",
            description = "",
            date = Timestamp(Date())
        ),
        onDismiss = {},
        onConfirm = {}
    )
}

@Composable
private fun EditIncomeDialogLabel(label: String, colors: EditIncomeDialogColors) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = colors.textDark
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun editIncomeFieldColors(colors: EditIncomeDialogColors) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colors.greenButton,
    unfocusedBorderColor = colors.border,
    focusedTextColor = colors.textDark,
    unfocusedTextColor = colors.textDark,
    focusedContainerColor = colors.fieldBg,
    unfocusedContainerColor = colors.fieldBg
)
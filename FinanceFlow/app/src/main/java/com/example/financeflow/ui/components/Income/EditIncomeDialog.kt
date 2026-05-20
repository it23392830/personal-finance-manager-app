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
private val DialogBg      = Color(0xFFA8E6B0)
private val GreenButton   = Color(0xFF22C55E)
private val PurpleButton  = Color(0xFF8B5CF6)
private val TextDark      = Color(0xFF1F2937)
private val TextMuted     = Color(0xFF6B7280)

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
    existingIncome: Income,
    onDismiss: () -> Unit,
    onConfirm: (Income) -> Unit
) {
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
            colors = CardDefaults.cardColors(containerColor = DialogBg)
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
                        color = TextDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark)
                    }
                }

                // ── Amount ────────────────────────────────────────────────────
                IncomeDialogLabel("Amount")
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) {{ Text("Please enter a valid amount") }} else null,
                    trailingIcon = {
                        Column {
                            Text("▲", fontSize = 8.sp, color = TextMuted)
                            Text("▼", fontSize = 8.sp, color = TextMuted)
                        }
                    },
                    colors = incomeFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Currency ──────────────────────────────────────────────────
                IncomeDialogLabel("Currency")
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = { currencyExpanded = !currencyExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCurrency.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = TextMuted)
                        },
                        colors = incomeFieldColors(),
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
                IncomeDialogLabel("Income Source")
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
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = TextMuted)
                        },
                        colors = incomeFieldColors(),
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
                IncomeDialogLabel("Description (Optional)")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g., React Project for ABC Co.", color = TextMuted) },
                    colors = incomeFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Date ──────────────────────────────────────────────────────
                IncomeDialogLabel("Date")
                OutlinedTextField(
                    value = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(selectedDate),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date", tint = TextMuted)
                    },
                    colors = incomeFieldColors(),
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
                        colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
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
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleButton)
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
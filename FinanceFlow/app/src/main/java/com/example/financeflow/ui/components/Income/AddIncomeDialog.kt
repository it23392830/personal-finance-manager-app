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
private val DialogBg    = Color(0xFFA8E6B0)  // Pastel green dialog surface
private val GreenButton = Color(0xFF22C55E)
private val FieldBg     = Color.White
private val TextDark    = Color(0xFF1F2937)
private val TextMuted   = Color(0xFF6B7280)

/**
 * Full-screen-ish dialog for adding a new income entry.
 *
 * @param onDismiss   Called when the user taps the × or taps outside.
 * @param onConfirm   Called with a fully populated [Income] when the user taps "+ Add Income".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Income) -> Unit
) {
    // ── Local form state ──────────────────────────────────────────────────────
    var amountText       by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(Currency.LKR) }
    var selectedSource   by remember { mutableStateOf(IncomeSource.SALARY) }
    var description      by remember { mutableStateOf("") }
    var selectedDate     by remember { mutableStateOf(Date()) }

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
                        text = "Add Income",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
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
                    placeholder = { Text("0.00", color = TextMuted) },
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
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Pick date",
                            tint = TextMuted
                        )
                    },
                    colors = incomeFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ── Add Income button ─────────────────────────────────────────
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            amountError = true
                            return@Button
                        }
                        onConfirm(
                            Income(
                                amount      = amount,
                                currency    = selectedCurrency.code,
                                source      = selectedSource.name,
                                description = description.trim(),
                                date        = Timestamp(selectedDate)
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
                ) {
                    Text(
                        "+ Add Income",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
internal fun IncomeDialogLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = TextDark
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun incomeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF22C55E),
    unfocusedBorderColor = Color(0xFFD1D5DB),
    focusedTextColor     = TextDark,
    unfocusedTextColor   = TextDark,
    focusedContainerColor    = FieldBg,
    unfocusedContainerColor  = FieldBg
)

internal fun sourceEmoji(source: IncomeSource) = when (source) {
    IncomeSource.SALARY     -> "💼"
    IncomeSource.FREELANCE  -> "<>"
    IncomeSource.ADSENSE    -> "$"
    IncomeSource.CRYPTO     -> "₿"
    IncomeSource.INVESTMENT -> "📈"
    IncomeSource.RENTAL     -> "🏠"
    IncomeSource.OTHER      -> "+"
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AddIncomeDialogPreview() {
    AddIncomeDialog(onDismiss = {}, onConfirm = {})
}

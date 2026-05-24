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
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

// ── Design tokens ─────────────────────────────────────────────────────────────
private val LightDialogBg   = Color(0xFFA8E6B0)
private val LightGreenButton = Color(0xFF22C55E)
private val LightFieldBg    = Color.White
private val LightTextDark   = Color(0xFF1F2937)
private val LightTextMuted  = Color(0xFF6B7280)

private val DarkDialogBg    = Color(0xFF214233)
private val DarkGreenButton = Color(0xFF2DBD6E)
private val DarkFieldBg     = Color(0xFF2A2A3E)
private val DarkTextDark    = Color(0xFFE8E8E8)
private val DarkTextMuted   = Color(0xFFB0B0B0)

private data class AddIncomeDialogColors(
    val dialogBg: Color,
    val greenButton: Color,
    val fieldBg: Color,
    val textDark: Color,
    val textMuted: Color,
    val border: Color
)

private fun getAddIncomeDialogColors(isDarkTheme: Boolean): AddIncomeDialogColors =
    if (isDarkTheme) {
        AddIncomeDialogColors(
            dialogBg = DarkDialogBg,
            greenButton = DarkGreenButton,
            fieldBg = DarkFieldBg,
            textDark = DarkTextDark,
            textMuted = DarkTextMuted,
            border = Color(0xFF3A3A4E)
        )
    } else {
        AddIncomeDialogColors(
            dialogBg = LightDialogBg,
            greenButton = LightGreenButton,
            fieldBg = LightFieldBg,
            textDark = LightTextDark,
            textMuted = LightTextMuted,
            border = Color(0xFFD1D5DB)
        )
    }

/**
 * Full-screen-ish dialog for adding a new income entry.
 *
 * @param onDismiss   Called when the user taps the × or taps outside.
 * @param onConfirm   Called with a fully populated [Income] when the user taps "+ Add Income".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Income) -> Unit
) {
    val colors = getAddIncomeDialogColors(isDarkTheme)
    // ── Local form state ──────────────────────────────────────────────────────
    var amountText       by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(Currency.LKR) }
    var selectedSource   by remember { mutableStateOf("Salary") }
    var description      by remember { mutableStateOf("") }
    var selectedDate     by remember { mutableStateOf(Date()) }

    val sourceOptions = listOf("Salary", "Freelance", "AdSense", "Crypto", "Investment", "Rental", "Other")

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
                        text = "Add Income",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.textDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textDark)
                    }
                }

                // ── Amount ────────────────────────────────────────────────────
                AddIncomeDialogLabel("Amount", colors)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    placeholder = { Text("0.00", color = colors.textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) {{ Text("Please enter a valid amount") }} else null,
                    trailingIcon = {
                        Column {
                            Text("▲", fontSize = 8.sp, color = colors.textMuted)
                            Text("▼", fontSize = 8.sp, color = colors.textMuted)
                        }
                    },
                    colors = addIncomeFieldColors(colors),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Currency ──────────────────────────────────────────────────
                AddIncomeDialogLabel("Currency", colors)
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
                        colors = addIncomeFieldColors(colors),
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
                AddIncomeDialogLabel("Income Source", colors)
                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = !sourceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSource,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Text(sourceEmoji(selectedSource), fontSize = 18.sp) },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.textMuted)
                        },
                        colors = addIncomeFieldColors(colors),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false }
                    ) {
                        sourceOptions.forEach { source ->
                            DropdownMenuItem(
                                text = { Text("${sourceEmoji(source)}  $source") },
                                onClick = { selectedSource = source; sourceExpanded = false }
                            )
                        }
                    }
                }

                // ── Description ───────────────────────────────────────────────
                AddIncomeDialogLabel("Description (Optional)", colors)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g., React Project for ABC Co.", color = colors.textMuted) },
                    colors = addIncomeFieldColors(colors),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Date ──────────────────────────────────────────────────────
                AddIncomeDialogLabel("Date", colors)
                OutlinedTextField(
                    value = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(selectedDate),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Pick date",
                            tint = colors.textMuted
                        )
                    },
                    colors = addIncomeFieldColors(colors),
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
                                source      = selectedSource,
                                description = description.trim(),
                                date        = Timestamp(selectedDate)
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.greenButton)
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
private fun AddIncomeDialogLabel(label: String, colors: AddIncomeDialogColors) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = colors.textDark
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun addIncomeFieldColors(colors: AddIncomeDialogColors) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = colors.greenButton,
    unfocusedBorderColor = colors.border,
    focusedTextColor     = colors.textDark,
    unfocusedTextColor   = colors.textDark,
    focusedContainerColor    = colors.fieldBg,
    unfocusedContainerColor  = colors.fieldBg
)

internal fun sourceEmoji(source: String) = when (source.lowercase()) {
    "salary"     -> "💼"
    "freelance"  -> "<>"
    "adsense"    -> "$"
    "crypto"     -> "₿"
    "investment" -> "📈"
    "rental"     -> "🏠"
    else         -> "💰"
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AddIncomeDialogPreview() {
    AddIncomeDialog(isDarkTheme = false, onDismiss = {}, onConfirm = {})
}

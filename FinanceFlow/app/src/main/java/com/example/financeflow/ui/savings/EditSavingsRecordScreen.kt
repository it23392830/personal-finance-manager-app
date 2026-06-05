package com.example.financeflow.ui.savings

import com.example.financeflow.ui.components.savings.getSavingsColors
import com.example.financeflow.ui.components.savings.SavingsColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.financeflow.model.Saving

// Colors are provided by SavingsTheme.getSavingsColors(isDarkTheme)

@Composable
fun EditSavingsRecordScreen(
    isDarkTheme: Boolean = false,
    saving: Saving? = null,
    incomeSources: List<String> = emptyList(),
    goals: List<String> = emptyList(),
    onSave: (Saving) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var month by remember(saving) { mutableStateOf(saving?.month ?: "May 2026") }
    var selectedIncomeSource by remember(saving) { mutableStateOf(saving?.incomeSource ?: "") }
    var selectedGoal by remember(saving) { mutableStateOf(saving?.goalName ?: "") }
    var amountSaved by remember(saving) { mutableStateOf(saving?.amountSaved?.toPlainAmount() ?: "") }
    var date by remember(saving) { mutableStateOf(saving?.date ?: "") }
    var description by remember(saving) { mutableStateOf(saving?.description ?: "") }
    
    var incomeSourceExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 18.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            val colors = getSavingsColors(isDarkTheme)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = colors.formBg)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Savings Record",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close popup",
                                tint = colors.textPrimary
                            )
                        }
                    }

                    EditRecordField(
                        label = "Month Label",
                        value = month,
                        onValueChange = { month = it },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        },
                        colors = colors
                    )

                    EditRecordDropdownField(
                        label = "Income Source",
                        value = selectedIncomeSource.ifEmpty { "Select Income Source" },
                        expanded = incomeSourceExpanded,
                        options = incomeSources,
                        onExpand = { incomeSourceExpanded = true },
                        onDismiss = { incomeSourceExpanded = false },
                        onSelect = { 
                            selectedIncomeSource = it
                            incomeSourceExpanded = false 
                        },
                        colors = colors
                    )

                    EditRecordDropdownField(
                        label = "Goal",
                        value = selectedGoal.ifEmpty { "Select Goal" },
                        expanded = goalExpanded,
                        options = goals,
                        onExpand = { goalExpanded = true },
                        onDismiss = { goalExpanded = false },
                        onSelect = {
                            selectedGoal = it
                            goalExpanded = false
                        },
                        colors = colors
                    )

                    EditRecordField(
                        label = "Amount Saved",
                        value = amountSaved,
                        onValueChange = { amountSaved = it },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        },
                        colors = colors
                    )

                    EditRecordField(
                        label = "Date",
                        value = date,
                        onValueChange = { date = it },
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, null)
                        },
                        colors = colors
                    )

                    EditRecordField(
                        label = "Description (Optional)",
                        value = description,
                        onValueChange = { description = it },
                        colors = colors
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EditRecordActionButton(
                            text = "Save Changes",
                            backgroundColor = colors.success,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSave(
                                    (saving ?: Saving()).copy(
                                        month = month,
                                        incomeSource = selectedIncomeSource,
                                        amountSaved = amountSaved.toMoneyDouble(),
                                        date = date,
                                        description = description,
                                        goalName = selectedGoal
                                    )
                                )
                            }
                        )
                        EditRecordActionButton(
                            text = "Cancel",
                            backgroundColor = colors.accent.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditRecordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: SavingsColors
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.muted
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            readOnly = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.cardBg,
                unfocusedContainerColor = colors.cardBg,
                focusedBorderColor = colors.fieldBorder,
                unfocusedBorderColor = colors.fieldBorder,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
            )
        )
    }
}

@Composable
private fun EditRecordDropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    colors: SavingsColors
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.muted
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = onExpand) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.cardBg,
                    unfocusedContainerColor = colors.cardBg,
                    focusedBorderColor = colors.fieldBorder,
                    unfocusedBorderColor = colors.fieldBorder,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                )
            )
            
            // Invisible click target over the field to trigger dropdown
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onExpand() }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
                modifier = Modifier.fillMaxWidth(0.85f)
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
}

/** Converts user-visible currency text into a Double. */
private fun String.toMoneyDouble(): Double {
    return replace(",", "")
        .replace("LKR", "", ignoreCase = true)
        .trim()
        .toDoubleOrNull() ?: 0.0
}

/** Formats a Firestore amount for editing without a currency prefix. */
private fun Double.toPlainAmount(): String = "%.0f".format(this)

@Composable
private fun EditRecordActionButton(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Edit Savings Record")
@Composable
private fun PreviewEditSavingsRecordScreen() {
    MaterialTheme {
        EditSavingsRecordScreen()
    }
}

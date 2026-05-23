package com.example.financeflow.ui.savings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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


// Color tokens

private val BgPurple      = Color(0xFFEDE2FF)
private val FormYellow    = Color(0xFFF7E4A7)
private val CardWhite     = Color(0xFFFFFFFF)
private val OrangeAccent  = Color(0xFFF5A623)
private val GreenBtn      = Color(0xFF3DBD7D)
private val PurpleBtn     = Color(0xFF9B72CF)
private val FieldBorder   = Color(0xFFD0C4E8)
private val LabelGray     = Color(0xFF888888)
private val DarkText      = Color(0xFF1A1A1A)


// Hardcoded dropdown options

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


// AddSavingScreen
//
// Full-page form for adding a new saving entry.
// Uses Column + verticalScroll() for scrollability.
// All state is local — no ViewModel, no database, no backend.

@Composable
fun AddSavingScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ── Form state ─────────────────────────────────────────────────────────
    var amount          by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(currencyOptions[0]) }
    var selectedGoal    by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var selectedDate    by remember { mutableStateOf("05/05/2026") }

    // ── Dropdown expanded state ─────────────────────────────────────────────
    var currencyExpanded by remember { mutableStateOf(false) }
    var goalExpanded     by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPurple)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── 1. Header card ──────────────────────────────────────────────────
        AddSavingHeaderCard()

        // ── 2. Main form card ───────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = FormYellow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── Field 1: Amount ─────────────────────────────────────────
                FormFieldLabel(text = "Amount")
                AmountField(
                    value    = amount,
                    onChange = { amount = it }
                )

                // ── Field 2: Currency ───────────────────────────────────────
                FormFieldLabel(text = "Currency")
                DropdownField(
                    value        = selectedCurrency,
                    expanded     = currencyExpanded,
                    options      = currencyOptions,
                    onExpand     = { currencyExpanded = true },
                    onDismiss    = { currencyExpanded = false },
                    onSelect     = {
                        selectedCurrency = it
                        currencyExpanded = false
                    },
                    leadingIcon  = null
                )

                // ── Field 3: Goal ───────────────────────────────────────────
                FormFieldLabel(text = "Goal")
                DropdownField(
                    value        = selectedGoal.ifEmpty { "Goal type" },
                    expanded     = goalExpanded,
                    options      = goalOptions,
                    onExpand     = { goalExpanded = true },
                    onDismiss    = { goalExpanded = false },
                    onSelect     = {
                        selectedGoal = it
                        goalExpanded = false
                    },
                    leadingIcon  = Icons.Default.CardGiftcard,
                    isPlaceholder = selectedGoal.isEmpty()
                )

                // ── Field 4: Description ────────────────────────────────────
                FormFieldLabel(text = "Description (Optional)")
                DescriptionField(
                    value    = description,
                    onChange = { description = it }
                )

                // ── Field 5: Date ───────────────────────────────────────────
                FormFieldLabel(text = "Date")
                DateField(
                    value    = selectedDate,
                    onChange = { selectedDate = it }
                )
            }
        }

        // ── 3. Action buttons ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Save Changes — green
            ActionButton(
                text            = "Save Changes",
                backgroundColor = GreenBtn,
                modifier        = Modifier.weight(1f),
                onClick         = {
                    Toast.makeText(context, "Saving Added", Toast.LENGTH_SHORT).show()
                }
            )
            // Cancel — purple
            ActionButton(
                text            = "Cancel",
                backgroundColor = PurpleBtn,
                modifier        = Modifier.weight(1f),
                onClick         = { onNavigateBack() }
            )
        }

        // Bottom breathing room above nav bar
        Spacer(modifier = Modifier.height(8.dp))
    }
}


// AddSavingHeaderCard
// Top white card: title and subtitle only.

@Composable
private fun AddSavingHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "Add Savings",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = OrangeAccent
                )
                Text(
                    text     = "Track your saving habits & allocations",
                    fontSize = 12.sp,
                    color    = LabelGray
                )
            }
        }
    }
}


// FormFieldLabel — small bold label above each field

@Composable
private fun FormFieldLabel(text: String) {
    Text(
        text       = text,
        fontSize   = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color      = DarkText,
        modifier   = Modifier.padding(bottom = 4.dp)
    )
}


// AmountField
// Outlined numeric text field with a dropdown-arrow trailing icon

@Composable
private fun AmountField(
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value            = value,
        onValueChange    = onChange,
        placeholder      = {
            Text(text = "0.00", color = LabelGray)
        },
        trailingIcon     = {
            Icon(
                imageVector     = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint            = LabelGray
            )
        },
        keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine       = true,
        shape            = RoundedCornerShape(14.dp),
        modifier         = Modifier.fillMaxWidth(),
        colors           = fieldColors()
    )
}


// DropdownField
// Tappable box that opens a DropdownMenu with the provided options.
// Optionally shows a leading icon.

@Composable
private fun DropdownField(
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    isPlaceholder: Boolean = false
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // ── Tappable row styled like an outlined field ─────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, FieldBorder, RoundedCornerShape(14.dp))
                .background(CardWhite)
                .clickable { onExpand() }
                .padding(horizontal = 16.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Optional leading icon
                    if (leadingIcon != null) {
                        Icon(
                            imageVector     = leadingIcon,
                            contentDescription = null,
                            tint            = LabelGray,
                            modifier        = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text  = value,
                        fontSize = 14.sp,
                        color = if (isPlaceholder) LabelGray else DarkText
                    )
                }
                Icon(
                    imageVector     = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint            = LabelGray
                )
            }
        }

        // ── Dropdown menu ──────────────────────────────────────────────────
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = onDismiss,
            modifier         = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(text = option, fontSize = 14.sp) },
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}


// DescriptionField
// Multi-line outlined text field for optional description

@Composable
private fun DescriptionField(
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        placeholder   = {
            Text(
                text  = "e.g., React Project for ABC Co.",
                color = LabelGray,
                fontSize = 13.sp
            )
        },
        minLines      = 3,
        shape         = RoundedCornerShape(14.dp),
        modifier      = Modifier.fillMaxWidth(),
        colors        = fieldColors()
    )
}


// DateField
// Outlined text field pre-filled with a date, trailing calendar icon

@Composable
private fun DateField(
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        trailingIcon  = {
            Icon(
                imageVector     = Icons.Default.CalendarMonth,
                contentDescription = "Pick date",
                tint            = LabelGray
            )
        },
        singleLine    = true,
        shape         = RoundedCornerShape(14.dp),
        modifier      = Modifier.fillMaxWidth(),
        colors        = fieldColors()
    )
}


// fieldColors — shared OutlinedTextField color scheme

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = CardWhite,
    unfocusedContainerColor = CardWhite,
    focusedBorderColor      = OrangeAccent,
    unfocusedBorderColor    = FieldBorder,
    cursorColor             = OrangeAccent,
    focusedTextColor        = DarkText,
    unfocusedTextColor      = DarkText
)


// ActionButton — reusable solid-color rounded button

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier
            .height(50.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor   = Color.White
        )
    ) {
        Text(
            text       = text,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// Previews

@Preview(showBackground = true, showSystemUi = true, name = "AddSavingScreen – Full")
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
        Box(modifier = Modifier.padding(16.dp)) {
            AddSavingHeaderCard()
        }
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
                text            = "Save Changes",
                backgroundColor = GreenBtn,
                onClick         = {},
                modifier        = Modifier.weight(1f)
            )
            ActionButton(
                text            = "Cancel",
                backgroundColor = PurpleBtn,
                onClick         = {},
                modifier        = Modifier.weight(1f)
            )
        }
    }
}

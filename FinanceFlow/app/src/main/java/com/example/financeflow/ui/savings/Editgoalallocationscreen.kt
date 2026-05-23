package com.example.financeflow.ui.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Color tokens (local to this file — mirrors the app-wide palette)
private val FormYellow   = Color(0xFFF7E4A7)
private val OrangeBar    = Color(0xFFF5A623)
private val GreenBtn     = Color(0xFF3DBD7D)
private val PurpleBtn    = Color(0xFF9B72CF)
private val FieldBorder  = Color(0xFFD0C4E8)
private val LabelGray    = Color(0xFF888888)
private val DarkText     = Color(0xFF1A1A1A)
private val CardWhite    = Color(0xFFFFFFFF)

// EditGoalAllocationScreen
//
// Full-screen Dialog that renders:
//   • Semi-transparent dark scrim  (Color.Black @ 40 % alpha)
//   • Centred popup card           (#F7E4A7, 25dp corners, 24dp padding)
//   • Form: goal name, allocated amount, target amount
//   • Orange rounded progress bar  (40.1 %)
//   • Save Changes (green) / Cancel (purple) buttons
//
// Parameters:
//   goalName        – pre-filled goal name
//   allocatedAmount – pre-filled allocated amount string (e.g. "LKR 196400")
//   targetAmount    – pre-filled target amount string   (e.g. "LKR 490000")
//   progressPercent – 0f..1f fraction for the bar       (e.g. 0.401f)
//   progressLabel   – human label below bar             (e.g. "40.1% complete")
//   onDismiss       – called when the dialog should close (Save or Cancel)
@Composable
fun EditGoalAllocationScreen(
    isDarkTheme: Boolean = false,
    goalName:        String  = "MacBook Pro M4",
    allocatedAmount: String  = "LKR 196400",
    targetAmount:    String  = "LKR 490000",
    progressPercent: Float   = 0.401f,
    progressLabel:   String  = "40.1% complete",
    onDismiss:       () -> Unit = {}
) {
    // ── Local editable state ───────────────────────────────────────────────
    var name      by remember { mutableStateOf(goalName) }
    var allocated by remember { mutableStateOf(allocatedAmount) }
    var target    by remember { mutableStateOf(targetAmount) }

    // ── Dialog wraps the popup and provides the dark scrim ─────────────────
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Full-screen dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            // ── Popup card ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)           // 92 % of screen width
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(25.dp))
                    .background(FormYellow, RoundedCornerShape(25.dp))
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

                    // ── Header row: title + close icon ─────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = "Edit Goal Allocation",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = DarkText
                        )
                        IconButton(
                            onClick  = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Close,
                                contentDescription = "Close",
                                tint               = DarkText
                            )
                        }
                    }

                    // ── Field 1: Goal Name ─────────────────────────────────
                    EditFormField(
                        label       = "Goal Name",
                        value       = name,
                        onChange    = { name = it },
                        keyboardType = KeyboardType.Text
                    )

                    // ── Field 2: Amount Allocated ──────────────────────────
                    EditFormField(
                        label        = "Amount Allocated (LKR)",
                        value        = allocated,
                        onChange     = { allocated = it },
                        keyboardType = KeyboardType.Number
                    )

                    // ── Field 3: Target Amount ─────────────────────────────
                    EditFormField(
                        label        = "Target Amount (LKR)",
                        value        = target,
                        onChange     = { target = it },
                        keyboardType = KeyboardType.Number
                    )

                    // ── Progress indicator ─────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LinearProgressIndicator(
                            progress   = { progressPercent },
                            modifier   = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color      = OrangeBar,
                            trackColor = Color(0xFFE0D4A0),
                            strokeCap  = StrokeCap.Round
                        )
                        Text(
                            text     = progressLabel,
                            fontSize = 12.sp,
                            color    = LabelGray
                        )
                    }

                    // ── Bottom buttons ─────────────────────────────────────
                    Row(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Save Changes — green, closes dialog
                        EditActionButton(
                            text            = "Save Changes",
                            backgroundColor = GreenBtn,
                            modifier        = Modifier.weight(1f),
                            onClick         = onDismiss
                        )
                        // Cancel — purple, closes dialog
                        EditActionButton(
                            text            = "Cancel",
                            backgroundColor = PurpleBtn,
                            modifier        = Modifier.weight(1f),
                            onClick         = onDismiss
                        )
                    }
                }
            }
        }
    }
}

// EditFormField — labelled OutlinedTextField used inside the popup
@Composable
private fun EditFormField(
    label:        String,
    value:        String,
    onChange:     (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text       = label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = DarkText
        )
        OutlinedTextField(
            value         = value,
            onValueChange = onChange,
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor      = OrangeBar,
                unfocusedBorderColor    = FieldBorder,
                cursorColor             = OrangeBar,
                focusedTextColor        = DarkText,
                unfocusedTextColor      = DarkText
            )
        )
    }
}

// EditActionButton — reusable solid-color rounded button for the popup
@Composable
private fun EditActionButton(
    text:            String,
    backgroundColor: Color,
    onClick:         () -> Unit,
    modifier:        Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(46.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor   = Color.White
        )
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// Previews
@Preview(showBackground = true, showSystemUi = true, name = "EditGoalAllocationScreen")
@Composable
fun PreviewEditGoalAllocationScreen() {
    MaterialTheme {
        EditGoalAllocationScreen()
    }
}
package com.example.financeflow.ui.components.Income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ── Design tokens ─────────────────────────────────────────────────────────────
private val LightRedDelete    = Color(0xFFEF4444)
private val LightPurpleCancel = Color(0xFF8B5CF6)
private val LightTextDark     = Color(0xFF1F2937)
private val LightTextMuted    = Color(0xFF6B7280)

private val DarkRedDelete     = Color(0xFFFF6B6B)
private val DarkPurpleCancel  = Color(0xFF7C3AED)
private val DarkTextDark      = Color(0xFFE8E8E8)
private val DarkTextMuted     = Color(0xFFB0B0B0)

private data class DeleteIncomeDialogColors(
    val redDelete: Color,
    val purpleCancel: Color,
    val textDark: Color,
    val textMuted: Color,
    val cardBg: Color,
    val infoBg: Color,
    val divider: Color,
    val iconBg: Color
)

private fun getDeleteIncomeDialogColors(isDarkTheme: Boolean): DeleteIncomeDialogColors =
    if (isDarkTheme) {
        DeleteIncomeDialogColors(
            redDelete = DarkRedDelete,
            purpleCancel = DarkPurpleCancel,
            textDark = DarkTextDark,
            textMuted = DarkTextMuted,
            cardBg = Color(0xFF2A2A3E),
            infoBg = Color(0xFF1A1A2E),
            divider = Color(0xFF3A3A4E),
            iconBg = Color(0xFF3A1A1A)
        )
    } else {
        DeleteIncomeDialogColors(
            redDelete = LightRedDelete,
            purpleCancel = LightPurpleCancel,
            textDark = LightTextDark,
            textMuted = LightTextMuted,
            cardBg = Color.White,
            infoBg = Color(0xFFF3ECFF),
            divider = Color(0xFFE9E2FF),
            iconBg = Color(0xFFFFE4E4)
        )
    }

/**
 * Confirmation dialog shown before permanently deleting an income entry.
 *
 * @param onDismiss Called when the user taps "Cancel" or taps outside.
 * @param onConfirm Called when the user confirms deletion by tapping "Delete".
 */
@Composable
fun DeleteIncomeDialog(
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = getDeleteIncomeDialogColors(isDarkTheme)
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Title ─────────────────────────────────────────────────────
                Text(
                    text = "Delete Income Entry?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = colors.textDark
                )

                // ── Body text ─────────────────────────────────────────────────
                Text(
                    text = "This action cannot be undone. The income record will be permanently removed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Action buttons ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.redDelete)
                    ) {
                        Text(
                            "Delete",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    // Cancel
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.purpleCancel)
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.SemiBold,
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
private fun DeleteIncomeDialogPreview() {
    DeleteIncomeDialog(isDarkTheme = false, onDismiss = {}, onConfirm = {})
}
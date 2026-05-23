package com.example.financeflow.ui.components.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  Design Tokens
// ─────────────────────────────────────────────
private val AddIncomeGreen  = Color(0xFF2DBD6E)
private val AddExpenseRed   = Color(0xFFFF5252)
private val ButtonTextWhite = Color(0xFFFFFFFF)

// ─────────────────────────────────────────────
//  Public composable
// ─────────────────────────────────────────────
/**
 * QuickActionButton
 *
 * A full-width or half-width pill button with icon + label.
 * Used for primary CTAs like "Add Income" and "Add Expense".
 *
 * @param label         Button label text.
 * @param icon          Leading icon.
 * @param backgroundColor  Fill color of the button.
 * @param contentColor  Icon and text color (default white).
 * @param onClick       Click callback — wire up navigation/dialog later.
 * @param modifier      External layout modifier.
 * @param elevation     Shadow elevation.
 */
@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector? = null,
    backgroundColor: Color,
    contentColor: Color = ButtonTextWhite,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    elevation: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .shadow(
                elevation    = elevation,
                shape        = RoundedCornerShape(50),
                ambientColor = backgroundColor.copy(alpha = 0.25f),
                spotColor    = backgroundColor.copy(alpha = 0.35f)
            )
    ) {
        Button(
            onClick  = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape    = RoundedCornerShape(50),
            colors   = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor   = contentColor
            ),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        modifier           = Modifier.size(20.dp),
                        tint               = contentColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = contentColor
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Convenience row — Add Income + Add Expense
// ─────────────────────────────────────────────
/**
 * QuickActionRow
 *
 * Pre-built side-by-side "Add Income" and "Add Expense" buttons.
 * Callbacks are no-ops until navigation is wired up.
 */
@Composable
fun QuickActionRow(
    isDarkTheme: Boolean = false,
    onAddIncomeClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            label           = "+ Add Income",
            backgroundColor = AddIncomeGreen,
            onClick         = onAddIncomeClick,
            modifier        = Modifier.weight(1f)
        )
        QuickActionButton(
            label           = "+ Add Expense",
            backgroundColor = AddExpenseRed,
            onClick         = onAddExpenseClick,
            modifier        = Modifier.weight(1f)
        )
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFFF5F3FF)
@Composable
private fun QuickActionRowPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuickActionRow()
        }
    }
}
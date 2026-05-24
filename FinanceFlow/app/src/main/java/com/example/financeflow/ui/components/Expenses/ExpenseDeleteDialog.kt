package com.example.financeflow.ui.components.Expenses

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseDeleteDialog(
    isDarkTheme: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = getExpensesColors(isDarkTheme)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.CardBg,
        title = { Text("Delete Expense?", color = colors.TextPrimary) },
        text = { Text("This will permanently remove the expense record. This action cannot be undone.", color = colors.TextMuted) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = colors.ExpenseRed)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.TextMuted)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExpenseDeleteDialogPreview() {
    FinanceFlowTheme {
        ExpenseDeleteDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}

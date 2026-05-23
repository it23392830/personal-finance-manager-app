package com.example.financeflow.ui.components.Expenses

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Expense?") },
        text = { Text("This will permanently remove the expense record. This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseColors.ExpenseRed)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ExpenseColors.TextMuted)
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

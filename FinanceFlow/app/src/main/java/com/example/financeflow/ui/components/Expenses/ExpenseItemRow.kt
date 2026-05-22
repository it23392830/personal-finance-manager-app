package com.example.financeflow.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.*

@Composable
fun ExpenseItemRow(
    item: ExpenseUiItem,
    isMenuOpen: Boolean,
    onMenuToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = getCat(item.categoryId)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(category.bgColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(category.emoji, fontSize = 20.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Title + Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ExpenseColors.TextPrimary
                    )
                    if (item.isRecurring) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp), tint = ExpenseColors.TextMuted)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Surface(
                        color = if (item.type == ExpenseType.ESSENTIAL) ExpenseColors.MustBg else ExpenseColors.PrimaryLight,
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            text = item.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 10.sp,
                            color = if (item.type == ExpenseType.ESSENTIAL) ExpenseColors.MustText else ExpenseColors.PrimaryText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${getCatDisplayLabel(item.categoryId)} • ${item.paymentMethod.label}",
                        fontSize = 11.sp,
                        color = ExpenseColors.TextMuted
                    )
                }
            }

            // Amount
            Text(
                text = "-${fmtLKR(item.amount)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseColors.ExpenseRed
            )

            // Menu
            Box {
                IconButton(onClick = onMenuToggle) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = ExpenseColors.TextMuted)
                }
                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = onMenuToggle,
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = { Text("✏️ Edit") },
                        onClick = { onEdit(); onMenuToggle() }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = ExpenseColors.Border)
                    DropdownMenuItem(
                        text = { Text("🗑️ Delete", color = ExpenseColors.ExpenseRed) },
                        onClick = { onDelete(); onMenuToggle() }
                    )
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = ExpenseColors.Border, modifier = Modifier.padding(horizontal = 12.dp))
    }
}

package com.example.financeflow.ui.components.expenses

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.financeflow.model.Expense
import com.example.financeflow.viewmodel.expense.AddExpenseFormState
import com.example.financeflow.viewmodel.expense.ExpenseUiState
import com.example.financeflow.viewmodel.expense.formatLkr

@Composable
fun ExpenseHeader(
    selectedMonth: String,
    onMonthSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF2F39),
                            ExpenseRed,
                            Color(0xFFE9275D)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Expense Tracker",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Low-friction tracking for busy days",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.16f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Live", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            SelectField(
                value = selectedMonth,
                options = ExpenseMonths,
                onSelected = onMonthSelected,
                modifier = Modifier.fillMaxWidth(),
                height = 38.dp
            )
        }
    }
}

@Composable
fun BudgetCard(uiState: ExpenseUiState) {
    var played by remember { mutableStateOf(false) }
    val usedPercent = if (uiState.optionalBudget <= 0.0) {
        0f
    } else {
        (uiState.totalOptional / uiState.optionalBudget).toFloat().coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = if (played) usedPercent else 0f,
        animationSpec = tween(durationMillis = 850),
        label = "expense_budget_progress"
    )
    LaunchedEffect(usedPercent) { played = true }

    RoundedPanel(
        background = Color.Transparent,
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            LightPink,
                            Color(0xFFFFEFF2),
                            Color(0xFFFFDCE4)
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.78f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Optional Budget Remaining", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "${(usedPercent * 100).toInt()}% used this month",
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = 0.55f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = formatLkr(uiState.optionalRemaining),
                color = Color(0xFFD22F37),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.7f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF3B43), Color(0xFFFF8A65))
                            )
                        )
                )
            }
            Spacer(Modifier.height(42.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
            ) {
                MiniStatCard("Today's Spending", formatLkr(uiState.todaySpending))
                MiniStatCard("Must Expenses", formatLkr(uiState.totalMust))
            }
        }
    }
}

@Composable
fun QuickAddCard(
    onAddExpense: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    RoundedPanel(
        background = Color.White,
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quick Add", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                Button(
                    onClick = onAddExpense,
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("+ Add Expenses", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                ExpenseCategories.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { category ->
                            QuickCategory(category = category, onClick = { onCategoryClick(category) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartSuggestionsCard(
    onSuggestionClick: (String, String) -> Unit
) {
    RoundedPanel(
        background = LightPink,
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Text("Smart Suggestions", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(20.dp))
            SuggestionRow(
                icon = Icons.Outlined.Coffee,
                iconColor = Color(0xFFFF7A1A),
                badge = "Frequently",
                onClick = { onSuggestionClick("Food", "Coffee") }
            )
            Spacer(Modifier.height(20.dp))
            SuggestionRow(
                icon = Icons.Outlined.DirectionsCar,
                iconColor = Color(0xFF2F86E8),
                badge = "Daily",
                onClick = { onSuggestionClick("Transport", "PickMe to Office") }
            )
        }
    }
}

@Composable
fun ExpenseListCard(
    expenses: List<Expense>,
    onDelete: (String) -> Unit
) {
    RoundedPanel(
        background = Color.White,
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Recent Expenses", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(2.dp))
            if (expenses.isEmpty()) {
                EmptyExpenseState()
            } else {
                expenses.take(6).forEach { expense ->
                    ExpenseRow(expense = expense, onDelete = onDelete)
                }
            }
        }
    }
}

@Composable
private fun EmptyExpenseState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LightPink),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = null,
                tint = ExpenseRed,
                modifier = Modifier.size(32.dp)
            )
        }
        Text("No expenses yet", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Quick add a category above to start tracking.",
            fontSize = 11.sp,
            color = Color.Black.copy(alpha = 0.55f)
        )
    }
}

@Composable
fun AddExpenseDialog(
    formState: AddExpenseFormState,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onExpenseTypeChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 690.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Add Expense",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                }

                item {
                    FieldLabel("Expense Type")
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        ExpenseTypeButton(
                            title = "Must",
                            subtitle = "Essential / Fixed",
                            selected = formState.expenseType == "must",
                            icon = Icons.Outlined.Coffee,
                            onClick = { onExpenseTypeChange("must") },
                            modifier = Modifier.weight(1f)
                        )
                        ExpenseTypeButton(
                            title = "optional",
                            subtitle = "Flexible",
                            selected = formState.expenseType == "optional",
                            icon = Icons.Outlined.CreditCard,
                            onClick = { onExpenseTypeChange("optional") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FieldLabel("Amount (LRK)")
                    SoftInput(
                        value = formState.amount,
                        onValueChange = onAmountChange,
                        placeholder = "0.00",
                        keyboardType = KeyboardType.Decimal,
                        errorText = formState.amountError
                    )
                }

                item {
                    FieldLabel("Category")
                    SelectField(
                        value = formState.category,
                        options = ExpenseCategories,
                        onSelected = onCategoryChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    FieldLabel("Description")
                    SoftInput(
                        value = formState.description,
                        onValueChange = onDescriptionChange,
                        placeholder = "",
                        errorText = formState.descriptionError
                    )
                }

                item {
                    FieldLabel("Payment Method")
                    SelectField(
                        value = formState.paymentMethod,
                        options = PaymentMethods,
                        onSelected = onPaymentMethodChange,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcons = mapOf(
                            "Card" to Icons.Outlined.CreditCard,
                            "Cash" to Icons.Outlined.AccountBalanceWallet,
                            "Bank" to Icons.Outlined.AccountBalance
                        )
                    )
                }

                item {
                    FieldLabel("Date")
                    SoftInput(
                        value = formState.date,
                        onValueChange = onDateChange,
                        placeholder = "May 22, 2026",
                        errorText = formState.dateError
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(errorMessage, color = ExpenseRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Button(
                        onClick = onSave,
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Save Expense", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundedPanel(
    background: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = SoftShadow, spotColor = SoftShadow),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
private fun MiniStatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(116.dp)
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = FieldBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
        }
    }
}

@Composable
private fun QuickCategory(category: String, onClick: () -> Unit) {
    val icon = categoryIcon(category)
    val color = categoryColor(category)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(140),
        label = "expense_category_press"
    )
    Column(
        modifier = Modifier
            .width(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(5.dp, RoundedCornerShape(14.dp), ambientColor = color.copy(alpha = 0.22f), spotColor = color.copy(alpha = 0.34f))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(color.copy(alpha = 0.92f), color)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = category, tint = Color.White, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(category, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun SuggestionRow(
    icon: ImageVector,
    iconColor: Color,
    badge: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "suggestion_press"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(FieldBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (badge == "Frequently") "Coffee break" else "Office ride",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Text(
                text = if (badge == "Frequently") "Usually after lunch" else "Common weekday trip",
                fontSize = 10.sp,
                color = Color.Black.copy(alpha = 0.55f)
            )
        }
        SmallBadge(text = badge, tint = Color.Black.copy(alpha = 0.75f))
    }
}

@Composable
private fun ExpenseRow(expense: Expense, onDelete: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(260),
        label = "expense_row_alpha"
    )
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else 18f,
        animationSpec = tween(260),
        label = "expense_row_offset"
    )
    LaunchedEffect(Unit) { visible = true }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer {
                this.alpha = alpha
                translationY = offset
            }
            .shadow(6.dp, RoundedCornerShape(22.dp), ambientColor = SoftShadow, spotColor = SoftShadow)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color.White, Color(0xFFFFFBFB))
                )
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(categoryColor(expense.category)),
            contentAlignment = Alignment.Center
        ) {
            Icon(categoryIcon(expense.category), contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description.ifBlank { expense.category },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(expense.date.ifBlank { "Today." }, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(8.dp))
                SmallBadge(text = expense.paymentMethod.ifBlank { "Card" }, tint = Color.Black)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (expense.amount > 0.0) {
                Text(
                    text = "-${formatLkr(expense.amount)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ExpenseRed,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
            }
            SmallBadge(
                text = expense.expenseType.replaceFirstChar { it.uppercase() },
                tint = if (expense.expenseType == "must") Color(0xFFB89000) else Color(0xFF2D8FE8),
                background = if (expense.expenseType == "must") Color(0xFFFFF6CE) else Color(0xFFEAF5FF)
            )
        }
        IconButton(
            onClick = { if (expense.id.isNotBlank()) onDelete(expense.id) },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "Delete",
                tint = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ExpenseTypeButton(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(58.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFFFFF5CF) else FieldBackground)
            .border(1.dp, Color.Black.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("*", fontSize = 16.sp, color = Color.Black)
        Spacer(Modifier.width(8.dp))
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) Color(0xFFD69A1B) else Color(0xFF2F72D8),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(5.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 10.sp, color = Color.Black.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun SelectField(
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 50.dp,
    leadingIcons: Map<String, ImageVector> = emptyMap()
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .shadow(4.dp, RoundedCornerShape(13.dp))
                .clip(RoundedCornerShape(13.dp))
                .background(FieldBackground)
                .clickable { expanded = !expanded }
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcons[value]?.let {
                Icon(it, contentDescription = null, tint = Color(0xFF2884E8), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = Color.Black)
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = height + 4.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.Black.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.96f))
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F1F1))
                            .clickable {
                                onSelected(option)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leadingIcons[option]?.let {
                            Icon(it, contentDescription = null, tint = Color(0xFF2884E8), modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(option, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        if (option == value) Text("✓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SoftInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Black) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = errorText != null,
        supportingText = errorText?.let {
            {
                Text(
                    text = it,
                    color = ExpenseRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(start = 18.dp, bottom = 8.dp)
    )
}

@Composable
private fun SmallBadge(
    text: String,
    tint: Color,
    background: Color = FieldBackground
) {
    Box(
        modifier = Modifier
            .height(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(background)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 9.sp, color = tint, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun BottomClockCard() {
    RoundedPanel(
        background = Color(0xFFFFD8F3),
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color.Black, modifier = Modifier.size(25.dp))
        }
    }
}

private fun categoryIcon(category: String): ImageVector = when (category) {
    "Food" -> Icons.Outlined.Coffee
    "Shopping" -> Icons.Outlined.ShoppingBag
    "Transport" -> Icons.Outlined.DirectionsCar
    "Rent" -> Icons.Outlined.Home
    "Bills" -> Icons.Outlined.ReceiptLong
    "Health" -> Icons.Outlined.FavoriteBorder
    "Fun" -> Icons.Outlined.CalendarMonth
    else -> Icons.Outlined.MoreHoriz
}

private fun categoryColor(category: String): Color = when (category) {
    "Food" -> Color(0xFFFF7418)
    "Shopping" -> Color(0xFFE82E83)
    "Transport" -> Color(0xFF3485EA)
    "Rent" -> Color(0xFFB842E5)
    "Bills" -> Color(0xFF20B9D1)
    "Health" -> Color(0xFFFF3147)
    "Fun" -> Color(0xFF5F61E8)
    else -> Color(0xFF686B76)
}

private val sampleExpenses = listOf(
    Expense(category = "Food", description = "Lunch at Office", expenseType = "optional", paymentMethod = "Card"),
    Expense(category = "Transport", description = "PickMe to Office", expenseType = "optional", paymentMethod = "Card"),
    Expense(category = "Shopping", description = "Groceries - Kee...", expenseType = "optional", paymentMethod = "Card"),
    Expense(category = "Rent", description = "Monthly Rent...", expenseType = "must", paymentMethod = "Card"),
    Expense(category = "Food", description = "Coffee - Star...", expenseType = "optional", paymentMethod = "Card")
)

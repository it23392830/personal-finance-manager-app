package com.example.financeflow.ui.income

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.financeflow.viewmodel.income.IncomeViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DeleteIncomeScreen
 *
 * Displays a centred warning card asking the user to confirm deletion of
 * an income record. Uses sample/fake data by default for Preview.
 *
 * @param incomeId       The ID of the income record being deleted.
 * @param isDarkTheme    Whether the UI should render in dark mode.
 * @param incomeSource   Label of the income source being deleted.
 * @param incomeAmount   Formatted amount string (e.g. "LKR 135,000.00").
 * @param incomeDate     Formatted date string.
 * @param onConfirmDelete Callback fired when the user taps "Delete".
 * @param onCancel        Back / cancel navigation callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteIncomeScreen(
    incomeId: String = "",
    isDarkTheme: Boolean = false,
    incomeSource: String    = "Salary",
    incomeAmount: String    = "LKR 135,000.00",
    incomeDate: String      = "05/05/2026",
    onConfirmDelete: () -> Unit = {},
    onCancel: () -> Unit    = {}
) {
    val viewModel: IncomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val colors = getIncomeFormColors(isDarkTheme)
    // Controls the spring-in animation of the warning card
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardVisible = true }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            IncomeTopBar(title = "Delete Income", onNavigateUp = onCancel, isDarkTheme = isDarkTheme)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = cardVisible,
                enter   = fadeIn() + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness    = Spring.StiffnessMedium
                    ),
                    initialScale = 0.85f
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = colors.cardBg,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation    = 12.dp,
                            shape        = RoundedCornerShape(24.dp),
                            ambientColor = Color(0xFFEF4444).copy(alpha = 0.10f),
                            spotColor    = Color(0xFFEF4444).copy(alpha = 0.15f)
                        )
                ) {
                    Column(
                        modifier            = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        // ── Warning icon ──────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete warning",
                                tint   = Color(0xFFEF4444),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // ── Headline ──────────────────────────────────────────
                        Text(
                            text       = "Delete Income Entry?",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colors.textPrimary,
                            textAlign  = TextAlign.Center
                        )

                        // ── Record summary card ───────────────────────────────
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = colors.fieldBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier            = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DeleteInfoRow(
                                    icon  = Icons.Default.Work,
                                    label = "Income Source",
                                    value = incomeSource,
                                    isDarkTheme = isDarkTheme
                                )
                                HorizontalDivider(color = colors.divider, thickness = 1.dp)
                                DeleteInfoRow(
                                    icon  = Icons.Default.AttachMoney,
                                    label = "Amount",
                                    value = incomeAmount,
                                    valueColor = colors.success,
                                    isDarkTheme = isDarkTheme
                                )
                                HorizontalDivider(color = colors.divider, thickness = 1.dp)
                                DeleteInfoRow(
                                    icon  = Icons.Default.CalendarMonth,
                                    label = "Date",
                                    value = incomeDate,
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }

                        // ── Warning text ──────────────────────────────────────
                        Text(
                            text      = "Are you sure you want to delete this income record? " +
                                    "This action cannot be undone.",
                            fontSize  = 14.sp,
                            color     = colors.textMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        // ── Action buttons ────────────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // Delete – red filled
                            Button(
                                onClick = {
                                    scope.launch {
                                        if (incomeId.isNotBlank()) viewModel.deleteIncome(incomeId)
                                        onConfirmDelete()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor   = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation  = 1.dp
                                )
                            ) {
                                Icon(
                                    imageVector     = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier        = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text       = "Delete",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp
                                )
                            }

                            // Cancel – Soft Primary
                            Button(
                                onClick = onCancel,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor   = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Text(
                                    text       = "Cancel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A single information row inside the record-summary card.
 */
@Composable
private fun DeleteInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    isDarkTheme: Boolean = false
) {
    val colors = getIncomeFormColors(isDarkTheme)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label side
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text     = label,
                fontSize = 13.sp,
                color    = colors.textMuted
            )
        }

        // Value side
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (valueColor == Color.Unspecified) colors.textPrimary else valueColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun DeleteIncomeScreenPreview() {
    MaterialTheme {
        DeleteIncomeScreen()
    }
}

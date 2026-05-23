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

// ─── Theme Colors ─────────────────────────────────────────────────────────────
private val BgPurple       = Color(0xFFF3ECFF)
private val PrimaryPurple   = Color(0xFF8B5CF6)
private val CardWhite       = Color(0xFFFFFFFF)
private val TextDark        = Color(0xFF1E1B2E)
private val TextMuted       = Color(0xFF6B7280)
private val DeleteRed       = Color(0xFFEF4444)
private val DeleteRedBg     = Color(0xFFFFEEEE)
private val CancelGray      = Color(0xFF9CA3AF)
private val CancelGrayBg    = Color(0xFFF3F4F6)
private val IconBgRed       = Color(0xFFFFE4E4)
private val IncomeGreen     = Color(0xFF22C55E)
private val DividerColor    = Color(0xFFE9E2FF)

/**
 * DeleteIncomeScreen
 *
 * Displays a centred warning card asking the user to confirm deletion of
 * an income record. Uses sample/fake data by default for Preview.
 *
 * @param incomeSource   Label of the income source being deleted.
 * @param incomeAmount   Formatted amount string (e.g. "LKR 135,000.00").
 * @param incomeDate     Formatted date string.
 * @param onConfirmDelete Callback fired when the user taps "Delete".
 * @param onCancel        Back / cancel navigation callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteIncomeScreen(
    isDarkTheme: Boolean = false,
    incomeSource: String    = "Salary",
    incomeAmount: String    = "LKR 135,000.00",
    incomeDate: String      = "05/05/2026",
    onConfirmDelete: () -> Unit = {},
    onCancel: () -> Unit    = {}
) {
    // Controls the spring-in animation of the warning card
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardVisible = true }

    Scaffold(
        containerColor = BgPurple,
        topBar = {
            IncomeTopBar(title = "Delete Income", onNavigateUp = onCancel)
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
                    color = CardWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation    = 12.dp,
                            shape        = RoundedCornerShape(24.dp),
                            ambientColor = DeleteRed.copy(alpha = 0.10f),
                            spotColor    = DeleteRed.copy(alpha = 0.15f)
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
                                .background(IconBgRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete warning",
                                tint   = DeleteRed,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // ── Headline ──────────────────────────────────────────
                        Text(
                            text       = "Delete Income Entry?",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextDark,
                            textAlign  = TextAlign.Center
                        )

                        // ── Record summary card ───────────────────────────────
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BgPurple,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier            = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DeleteInfoRow(
                                    icon  = Icons.Default.Work,
                                    label = "Income Source",
                                    value = incomeSource
                                )
                                HorizontalDivider(color = DividerColor, thickness = 1.dp)
                                DeleteInfoRow(
                                    icon  = Icons.Default.AttachMoney,
                                    label = "Amount",
                                    value = incomeAmount,
                                    valueColor = IncomeGreen
                                )
                                HorizontalDivider(color = DividerColor, thickness = 1.dp)
                                DeleteInfoRow(
                                    icon  = Icons.Default.CalendarMonth,
                                    label = "Date",
                                    value = incomeDate
                                )
                            }
                        }

                        // ── Warning text ──────────────────────────────────────
                        Text(
                            text      = "Are you sure you want to delete this income record? " +
                                    "This action cannot be undone.",
                            fontSize  = 14.sp,
                            color     = TextMuted,
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
                                onClick = onConfirmDelete,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeleteRed,
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

                            // Cancel – gray outlined / soft
                            Button(
                                onClick = onCancel,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryPurple,
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

// ─── Reusable row: icon + label + value ──────────────────────────────────────

/**
 * A single information row inside the record-summary card.
 *
 * @param icon       Leading Material icon.
 * @param label      Grey descriptor text (e.g. "Amount").
 * @param value      Bold value text (e.g. "LKR 135,000.00").
 * @param valueColor Optional colour override for the value text.
 */
@Composable
private fun DeleteInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextDark
) {
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
                tint = PrimaryPurple,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text     = label,
                fontSize = 13.sp,
                color    = TextMuted
            )
        }

        // Value side
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = valueColor
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3ECFF, showSystemUi = true)
@Composable
fun DeleteIncomeScreenPreview() {
    MaterialTheme {
        DeleteIncomeScreen()
    }
}
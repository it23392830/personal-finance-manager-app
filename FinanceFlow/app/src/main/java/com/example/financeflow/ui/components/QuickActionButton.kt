package com.example.financeflow.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ──────────────────────────────────────────────
// Data model
// ──────────────────────────────────────────────

data class QuickActionData(
    val label: String,
    val icon: ImageVector,
    val gradientColors: List<Color> = QuickActionDefaults.purpleGradient,
    val badgeCount: Int = 0                // 0 = no badge
)

object QuickActionDefaults {
    val purpleGradient = listOf(Color(0xFF9B72E8), Color(0xFFB48FE0))
    val tealGradient   = listOf(Color(0xFF4EC9C9), Color(0xFF88E0D8))
    val peachGradient  = listOf(Color(0xFFFF8C67), Color(0xFFFFB48F))
    val roseGradient   = listOf(Color(0xFFFF6B8B), Color(0xFFFF9DB5))
    val mintGradient   = listOf(Color(0xFF4EBA82), Color(0xFF82DDB0))
    val indigoGradient = listOf(Color(0xFF5C7BE0), Color(0xFF90A8F4))
}

// ──────────────────────────────────────────────
// Single button
// ──────────────────────────────────────────────

@Composable
fun QuickActionButton(
    data: QuickActionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 58.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "quick_action_scale"
    )

    val buttonShape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Icon button ──────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(scale)
                .size(buttonSize)
                .shadow(
                    elevation = 10.dp,
                    shape = buttonShape,
                    ambientColor = data.gradientColors.first().copy(alpha = 0.40f),
                    spotColor = data.gradientColors.first().copy(alpha = 0.30f)
                )
                .clip(buttonShape)
                .background(
                    brush = Brush.linearGradient(colors = data.gradientColors)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.label,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )

            // Badge
            if (data.badgeCount > 0) {
                BadgeBox(count = data.badgeCount)
            }
        }

        // ── Label ────────────────────────────────
        Text(
            text = data.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF6B6880),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 72.dp)
        )
    }
}

// ──────────────────────────────────────────────
// Row strip (LazyRow — scroll-safe in LazyColumn)
// ──────────────────────────────────────────────

@Composable
fun QuickActionRow(
    actions: List<QuickActionData>,
    onActionClick: (QuickActionData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    itemSpacing: Dp = 20.dp
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(actions) { action ->
            QuickActionButton(
                data = action,
                onClick = { onActionClick(action) }
            )
        }
    }
}

// ──────────────────────────────────────────────
// Fixed-grid row (for 4–5 actions, no scroll)
// ──────────────────────────────────────────────

@Composable
fun QuickActionGrid(
    actions: List<QuickActionData>,
    onActionClick: (QuickActionData) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            QuickActionButton(
                data = action,
                onClick = { onActionClick(action) }
            )
        }
    }
}

// ──────────────────────────────────────────────
// Sub-composables
// ──────────────────────────────────────────────

@Composable
private fun BadgeBox(count: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 6.dp, end = 6.dp)
            .size(16.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFF4D6D))
    ) {
        Text(
            text = if (count > 9) "9+" else count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun Modifier.align(alignment: Alignment): Modifier = this

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF3EEFF)
@Composable
fun QuickActionButtonPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            QuickActionGrid(
                actions = listOf(
                    QuickActionData(
                        label = "Send",
                        icon = Icons.Rounded.Send,
                        gradientColors = QuickActionDefaults.purpleGradient
                    ),
                    QuickActionData(
                        label = "Receive",
                        icon = Icons.Rounded.CallReceived,
                        gradientColors = QuickActionDefaults.tealGradient
                    ),
                    QuickActionData(
                        label = "Pay",
                        icon = Icons.Rounded.Payments,
                        gradientColors = QuickActionDefaults.peachGradient
                    ),
                    QuickActionData(
                        label = "History",
                        icon = Icons.Rounded.History,
                        gradientColors = QuickActionDefaults.indigoGradient,
                        badgeCount = 3
                    )
                ),
                onActionClick = {}
            )
        }
    }
}

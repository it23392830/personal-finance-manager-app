package com.example.financeflow.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.financeflow.navigation.BottomNavItem

// ─────────────────────────────────────────────
//  Design Tokens — Fintech Palette
// ─────────────────────────────────────────────
private val NavBarBackground   = Color(0xFFFFFFFF)
private val SelectedBubble     = Color(0xFFEDE7FF) // Soft Lavender indicator
private val SelectedIconTint   = Color(0xFF7C4DFF) // Vibrant Purple
private val UnselectedIconTint = Color(0xFFB0B0C3) // Muted Gray-Blue
private val SelectedLabelColor = Color(0xFF7C4DFF)

private val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Income,
    BottomNavItem.Expenses,
    BottomNavItem.Savings,
    BottomNavItem.Goals,
    BottomNavItem.Insights
)

/**
 * BottomNavigationBar
 *
 * Custom floating fintech-style navigation bar.
 * Replaces default Material3 NavigationBar with a slimmed-down Surface
 * to match the high-end Figma design.
 */
@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onItemClick: (BottomNavItem) -> Unit
) {
    // Box provides the floating container with padding from screen edges
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            color = NavBarBackground,
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 16.dp // Soft elevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.route } == true

                    BottomNavTab(
                        item       = item,
                        isSelected = isSelected,
                        onClick    = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

/**
 * BottomNavTab
 *
 * Individual tab with animated circular indicator and scaling icon.
 */
@Composable
private fun RowScope.BottomNavTab(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val iconScale by animateFloatAsState(
        targetValue   = if (isSelected) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "icon_scale"
    )

    val iconTint by animateColorAsState(
        targetValue   = if (isSelected) SelectedIconTint else UnselectedIconTint,
        label         = "icon_tint"
    )

    val bubbleSize by animateDpAsState(
        targetValue   = if (isSelected) 46.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label         = "bubble_size"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication        = null, // Clean look without standard ripple
                onClick           = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp)
        ) {
            // Animated Circular Selection Indicator
            if (bubbleSize > 0.dp) {
                Box(
                    modifier = Modifier
                        .size(bubbleSize)
                        .clip(CircleShape)
                        .background(SelectedBubble)
                )
            }

            Icon(
                imageVector        = item.icon,
                contentDescription = item.title,
                tint               = iconTint,
                modifier           = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
        }

        // Animated label visibility
        if (isSelected) {
            Text(
                text  = item.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SelectedLabelColor
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

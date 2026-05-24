package com.example.financeflow.ui.components.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthSelector(
    selectedMonth: String,
    monthList: List<String>,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    themeColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val selectorBg = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val selectorText = if (isDarkTheme) Color(0xFFF5F5F5) else Color(0xFF1A1A1A)
    val selectorIcon = if (isDarkTheme) Color(0xFFCBCBCB) else Color(0xFF8D8D99)
    val menuBg = if (isDarkTheme) Color(0xFF232323) else Color.White
    val menuBorder = if (isDarkTheme) Color(0xFF383838) else Color(0xFFE7E7EC)

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "month-arrow-rotation"
    )
    val menuAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.92f,
        animationSpec = tween(durationMillis = 220),
        label = "month-menu-alpha"
    )
    val menuScale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.97f,
        animationSpec = tween(durationMillis = 220),
        label = "month-menu-scale"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = selectorBg,
            tonalElevation = if (isDarkTheme) 1.dp else 0.dp,
            shadowElevation = if (isDarkTheme) 2.dp else 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedMonth,
                    color = selectorText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select month",
                    tint = selectorIcon,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .graphicsLayer {
                    alpha = menuAlpha
                    scaleX = menuScale
                    scaleY = menuScale
                }
                .background(menuBg, RoundedCornerShape(18.dp))
        ) {
            monthList.forEach { month ->
                val isSelected = month == selectedMonth
                val itemBg by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) themeColor.copy(alpha = if (isDarkTheme) 0.28f else 0.16f) else Color.Transparent,
                    animationSpec = tween(180),
                    label = "month-item-bg"
                )
                val itemText by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDarkTheme) Color.White else Color(0xFF121212)
                    } else {
                        if (isDarkTheme) Color(0xFFE5E5E5) else Color(0xFF2C2C2C)
                    },
                    animationSpec = tween(180),
                    label = "month-item-text"
                )

                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(itemBg, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = month,
                                color = itemText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    onClick = {
                        onMonthSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}

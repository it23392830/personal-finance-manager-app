package com.example.financeflow.ui.components.Expenses

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.ui.expenses.ExpenseColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun ExpenseTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Overview", "Analytics", "History", "Recurring")
    
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = modifier.fillMaxWidth(),
        containerColor = ExpenseColors.AppBg,
        contentColor = ExpenseColors.HeaderRed,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = ExpenseColors.HeaderRed
            )
        },
        divider = {
            HorizontalDivider(thickness = 0.5.dp, color = ExpenseColors.Border)
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = ExpenseColors.HeaderRed,
                unselectedContentColor = ExpenseColors.TextMuted
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTabRowPreview() {
    FinanceFlowTheme {
        ExpenseTabRow(
            selectedTab = 0,
            onTabSelected = {}
        )
    }
}

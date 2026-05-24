package com.example.financeflow.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeatureMonthHeader(
    title: String,
    subtitle: String,
    selectedMonth: String,
    monthOptions: List<String>,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    headerColor: Color = Color(0xFFE92929),
    titleColor: Color = Color.White,
    subtitleColor: Color = Color.White.copy(alpha = 0.9f),
    actionContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = headerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = subtitleColor,
                        fontSize = 11.sp
                    )
                }

                if (actionContent != null) {
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    actionContent()
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            MonthSelector(
                selectedMonth = selectedMonth,
                monthList = monthOptions,
                onMonthSelected = onMonthSelected,
                themeColor = headerColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

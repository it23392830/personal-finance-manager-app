package com.example.financeflow.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.model.Expense
import com.example.financeflow.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    onAddExpenseClick: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Expenses") },
                actions = {
                    IconButton(onClick = { /* Search logic */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Filter logic */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No expenses recorded yet.", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = onAddExpenseClick, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Add Your First Expense")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val groupedExpenses = expenses.groupBy { 
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.date))
                }

                groupedExpenses.forEach { (date, items) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(items) { expense ->
                        ExpenseItem(expense)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.subCategory,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (expense.note.isNotEmpty()) {
                    Text(
                        text = expense.note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "LKR ${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (expense.expenseType == "ESSENTIAL") 
                        Color(0xFFF59E0B) else Color(0xFF3B82F6),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = expense.paymentMethod,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

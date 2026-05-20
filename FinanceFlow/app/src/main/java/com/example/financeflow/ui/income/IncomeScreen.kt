package com.example.financeflow.ui.income

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.model.*
import com.google.firebase.Timestamp

import androidx.navigation.NavController
import com.example.financeflow.ui.components.Income.AddIncomeDialog
import com.example.financeflow.ui.components.Income.DeleteIncomeDialog
import com.example.financeflow.ui.components.Income.EditIncomeDialog
import com.example.financeflow.ui.components.Income.IncomeCard
import com.example.financeflow.ui.components.Income.IncomeSummaryCard
import com.example.financeflow.ui.components.Income.MonthSelector
import com.example.financeflow.ui.components.Income.MonthYear
import com.example.financeflow.ui.components.Income.SalaryReminderCard
import com.example.financeflow.ui.components.Income.TransactionCard
import com.example.financeflow.ui.components.Income.generateMonthOptions

private val ScreenBg = Color(0xFFF3ECFF)
private val TextDark = Color(0xFF1F2937)
private val TextMuted = Color(0xFF6B7280)
private val GreenText = Color(0xFF22C55E)
private val CardWhite = Color.White

@Composable
fun IncomeScreen(navController: NavController) {

    IncomeScreenContent(
        uiState = previewUiState,

        onMonthSelected = {},

        onAddIncome = {},

        onUpdateIncome = {},

        onDeleteIncome = {},

        onShowAddDialog = {
            navController.navigate(com.example.financeflow.navigation.Routes.ADD_INCOME)
        },

        onDismissAdd = {},

        onShowEditDialog = { income ->
            navController.navigate(
                com.example.financeflow.navigation.Routes.EDIT_INCOME.replace("{incomeId}", income.id)
            )
        },

        onDismissEdit = {},

        onShowDeleteDialog = { income ->
            navController.navigate(
                com.example.financeflow.navigation.Routes.DELETE_INCOME.replace("{incomeId}", income.id)
            )
        },

        onDismissDelete = {}
    )
}

@Composable
fun IncomeScreenContent(
    uiState: IncomeUiState,
    onMonthSelected: (MonthYear) -> Unit,
    onAddIncome: (Income) -> Unit,
    onUpdateIncome: (Income) -> Unit,
    onDeleteIncome: (Income) -> Unit,
    onShowAddDialog: () -> Unit,
    onDismissAdd: () -> Unit,
    onShowEditDialog: (Income) -> Unit,
    onDismissEdit: () -> Unit,
    onShowDeleteDialog: (Income) -> Unit,
    onDismissDelete: () -> Unit
) {

    val monthOptions = remember {
        generateMonthOptions(
            uiState.selectedYear,
            uiState.selectedMonth
        )
    }

    val currentMonthYear =
        MonthYear(
            uiState.selectedYear,
            uiState.selectedMonth
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding =
                PaddingValues(
                    horizontal = 16.dp,
                    vertical = 24.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)

        ) {

            item {

                IncomeHeader()

            }

            item {

                MonthSelector(
                    selected = currentMonthYear,
                    options = monthOptions,
                    onSelected = onMonthSelected
                )

            }

            item {

                IncomeSummaryCard(
                    totalAmount = uiState.totalIncome,
                    currencyCode =
                        uiState.displayCurrency.code,

                    onAddIncomeClick =
                        onShowAddDialog
                )

            }

            item {

                SectionTitle(
                    "Income by Source"
                )

            }

            items(
                uiState.incomeBySource
            ) {

                    source ->

                IncomeCard(
                    data = source
                )

            }

            item {

                SectionTitle(
                    "Recent Transactions"
                )

            }

            item {

                RecentTransactionsCard(
                    transactions =
                        uiState.recentTransactions,

                    onEditClick =
                        onShowEditDialog,

                    onDeleteClick =
                        onShowDeleteDialog
                )

            }

            uiState.daysUntilNextSalary?.let {

                    days ->

                item {

                    SalaryReminderCard(
                        daysUntilSalary =
                            days
                    )

                }

            }

            item {

                Spacer(
                    modifier =
                        Modifier.height(80.dp)
                )

            }

        }

    }

    if (uiState.showAddDialog) {

        AddIncomeDialog(
            onDismiss =
                onDismissAdd,

            onConfirm =
                onAddIncome
        )
    }

    if (
        uiState.showEditDialog &&
        uiState.selectedIncome != null
    ) {

        EditIncomeDialog(

            existingIncome =
                uiState.selectedIncome,

            onDismiss =
                onDismissEdit,

            onConfirm =
                onUpdateIncome

        )

    }

    if (
        uiState.showDeleteDialog &&
        uiState.selectedIncome != null
    ) {

        DeleteIncomeDialog(

            onDismiss =
                onDismissDelete,

            onConfirm = {

                onDeleteIncome(
                    uiState.selectedIncome
                )

            }

        )

    }

}

@Composable
private fun IncomeHeader() {

    Column {

        Text(
            text = "Income Tracking",

            fontWeight =
                FontWeight.Bold,

            fontSize = 26.sp,

            color = GreenText
        )

        Text(
            text =
                "Track your saving habits & allocations",

            color =
                TextMuted
        )

    }

}

@Composable
private fun SectionTitle(
    title: String
) {

    Text(

        text = title,

        style =
            MaterialTheme.typography
                .titleMedium.copy(
                    fontWeight =
                        FontWeight.Bold
                ),

        color =
            TextDark

    )

}

@Composable
private fun RecentTransactionsCard(
    transactions: List<Income>,
    onEditClick: (Income) -> Unit,
    onDeleteClick: (Income) -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardWhite
            )

    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            transactions.forEach {

                    income ->

                TransactionCard(

                    income =
                        income,

                    onEditClick =
                        onEditClick,

                    onDeleteClick =
                        onDeleteClick

                )

            }

        }

    }

}

private val previewTransactions = listOf(

    Income(
        id = "1",
        source = "SALARY",
        description = "Salary",
        amount = 135000.0,
        currency = "LKR",
        date = Timestamp.now()
    ),

    Income(
        id = "2",
        source = "FREELANCE",
        description = "React Project",
        amount = 45000.0,
        currency = "LKR",
        date = Timestamp.now()
    )

)

private val previewUiState = IncomeUiState(

    selectedYear = 2026,

    selectedMonth = 5,

    totalIncome = 215500.0,

    recentTransactions =
        previewTransactions,

    incomeBySource = listOf(

        IncomeBySource(
            IncomeSource.SALARY,
            135000.0,
            1,
            62.6
        ),

        IncomeBySource(
            IncomeSource.FREELANCE,
            73500.0,
            2,
            33.9
        )

    ),

    daysUntilNextSalary = 20

)

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun IncomePreview() {
    IncomeScreen(navController = androidx.navigation.compose.rememberNavController())
}
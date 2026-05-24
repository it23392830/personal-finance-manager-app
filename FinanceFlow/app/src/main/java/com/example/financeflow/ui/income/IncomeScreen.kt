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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.financeflow.navigation.Routes
import com.example.financeflow.viewmodel.income.IncomeViewModel
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

private val LightScreenBg = Color(0xFFF3ECFF)
private val LightTextDark = Color(0xFF1F2937)
private val LightTextMuted = Color(0xFF6B7280)
private val LightGreenText = Color(0xFF22C55E)
private val LightCardBg = Color.White

private val DarkScreenBg = Color(0xFF1A1A2E)
private val DarkTextDark = Color(0xFFE8E8E8)
private val DarkTextMuted = Color(0xFFB0B0B0)
private val DarkGreenText = Color(0xFF2DBD6E)
private val DarkCardBg = Color(0xFF2A2A3E)

private data class IncomeScreenColors(
    val screenBg: Color,
    val cardBg: Color,
    val textDark: Color,
    val textMuted: Color,
    val greenText: Color
)

private fun getIncomeScreenColors(isDarkTheme: Boolean): IncomeScreenColors =
    if (isDarkTheme) {
        IncomeScreenColors(DarkScreenBg, DarkCardBg, DarkTextDark, DarkTextMuted, DarkGreenText)
    } else {
        IncomeScreenColors(LightScreenBg, LightCardBg, LightTextDark, LightTextMuted, LightGreenText)
    }

@Composable
fun IncomeScreen(
    isDarkTheme: Boolean = false,
    navController: NavController,
    viewModel: IncomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    IncomeScreenContent(
        isDarkTheme = isDarkTheme,
        uiState = uiState,

        onMonthSelected = { monthYear ->
            viewModel.setSelectedMonth(monthYear.year, monthYear.month)
        },

        onAddIncome = { income ->
            viewModel.addIncome(income)
            navController.popBackStack()
        },

        onUpdateIncome = { income ->
            viewModel.updateIncome(income)
            navController.popBackStack()
        },

        onDeleteIncome = { income ->
            viewModel.deleteIncome(income.id)
            navController.popBackStack()
        },

        onShowAddDialog = {
            navController.navigate(Routes.ADD_INCOME)
        },

        onDismissAdd = { viewModel.dismissAddDialog() },

        onShowEditDialog = { income ->
            navController.navigate(Routes.EDIT_INCOME.replace("{incomeId}", income.id))
        },

        onDismissEdit = { viewModel.dismissEditDialog() },

        onShowDeleteDialog = { income ->
            navController.navigate(Routes.DELETE_INCOME.replace("{incomeId}", income.id))
        },

        onDismissDelete = { viewModel.dismissDeleteDialog() }
    )
}

@Composable
fun IncomeScreenContent(
    isDarkTheme: Boolean = false,
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
    val colors = getIncomeScreenColors(isDarkTheme)

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
            .background(colors.screenBg)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 120.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)

        ) {

            item {

                IncomeHeader(isDarkTheme = isDarkTheme)

            }

            item {

                MonthSelector(
                    isDarkTheme = isDarkTheme,
                    selected = currentMonthYear,
                    options = monthOptions,
                    onSelected = onMonthSelected
                )

            }

            item {

                IncomeSummaryCard(
                    isDarkTheme = isDarkTheme,
                    totalAmount = uiState.totalIncome,
                    currencyCode =
                        uiState.displayCurrency.code,

                    onAddIncomeClick =
                        onShowAddDialog
                )

            }

            item {

                SectionTitle(
                    isDarkTheme = isDarkTheme,
                    title = "Income by Source"
                )

            }

            items(
                uiState.incomeBySource
            ) {

                    source ->

                IncomeCard(
                    isDarkTheme = isDarkTheme,
                    data = source
                )

            }

            item {

                SectionTitle(
                    isDarkTheme = isDarkTheme,
                    title = "Recent Transactions"
                )

            }

            item {

                RecentTransactionsCard(
                    isDarkTheme = isDarkTheme,
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
                        isDarkTheme = isDarkTheme,
                        daysUntilSalary =
                            days
                    )

                }

            }

        }

    }

    if (uiState.showAddDialog) {

        AddIncomeDialog(
            isDarkTheme = isDarkTheme,
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
            isDarkTheme = isDarkTheme,

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
            isDarkTheme = isDarkTheme,

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
private fun IncomeHeader(isDarkTheme: Boolean = false) {
    val colors = getIncomeScreenColors(isDarkTheme)

    Column {

        Text(
            text = "Income Tracking",

            fontWeight =
                FontWeight.Bold,

            fontSize = 26.sp,

            color = colors.greenText
        )

        Text(
            text =
                "Track your saving habits & allocations",

            color =
                colors.textMuted
        )

    }

}

@Composable
private fun SectionTitle(
    isDarkTheme: Boolean = false,
    title: String
) {
    val colors = getIncomeScreenColors(isDarkTheme)

    Text(

        text = title,

        style =
            MaterialTheme.typography
                .titleMedium.copy(
                    fontWeight =
                        FontWeight.Bold
                ),

        color =
            colors.textDark

    )

}

@Composable
private fun RecentTransactionsCard(
    isDarkTheme: Boolean = false,
    transactions: List<Income>,
    onEditClick: (Income) -> Unit,
    onDeleteClick: (Income) -> Unit
) {
    val colors = getIncomeScreenColors(isDarkTheme)

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                        colors.cardBg
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
                    isDarkTheme = isDarkTheme,

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
    IncomeScreenContent(
        uiState = previewUiState,
        onMonthSelected = {},
        onAddIncome = {},
        onUpdateIncome = {},
        onDeleteIncome = {},
        onShowAddDialog = {},
        onDismissAdd = {},
        onShowEditDialog = {},
        onDismissEdit = {},
        onShowDeleteDialog = {},
        onDismissDelete = {}
    )
}
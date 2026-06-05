package com.example.financeflow.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.Expense
import com.example.financeflow.model.Goal
import com.example.financeflow.model.Income
import com.example.financeflow.model.Saving
import com.example.financeflow.repository.dashboard.CategoryTotal
import com.example.financeflow.repository.dashboard.DashboardRepository
import com.example.financeflow.repository.dashboard.ExpenseTypeBreakdown
import com.example.financeflow.repository.dashboard.GoalProgressSummary
import com.example.financeflow.repository.dashboard.IncomeSourceTotal
import com.example.financeflow.repository.dashboard.MonthlySummary
import com.example.financeflow.ui.expenses.getCat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.max

private val MONTH_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy")

data class ExpenseCategorySummary(
    val label: String,
    val amount: Double
)

data class ExpenseSectionSummary(
    val mustTotal: Double = 0.0,
    val optionalTotal: Double = 0.0,
    val mustItems: List<ExpenseCategorySummary> = emptyList(),
    val optionalItems: List<ExpenseCategorySummary> = emptyList()
)

data class DayActivitySummary(
    val dayOfMonth: Int,
    val incomeCount: Int,
    val expenseCount: Int,
    val savingsCount: Int
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _totalSavings = MutableStateFlow(0.0)
    val totalSavings: StateFlow<Double> = _totalSavings.asStateFlow()

    private val _remainingBalance = MutableStateFlow(0.0)
    val remainingBalance: StateFlow<Double> = _remainingBalance.asStateFlow()

    private val _monthlySummary = MutableStateFlow(MonthlySummary())
    val monthlySummary: StateFlow<MonthlySummary> = _monthlySummary.asStateFlow()

    private val _goalProgress = MutableStateFlow<List<GoalProgressSummary>>(emptyList())
    val goalProgress: StateFlow<List<GoalProgressSummary>> = _goalProgress.asStateFlow()

    private val _expenseCategories = MutableStateFlow<List<ExpenseCategorySummary>>(emptyList())
    val expenseCategories: StateFlow<List<ExpenseCategorySummary>> = _expenseCategories.asStateFlow()

    private val _incomeSources = MutableStateFlow<List<IncomeSourceTotal>>(emptyList())
    val incomeSources: StateFlow<List<IncomeSourceTotal>> = _incomeSources.asStateFlow()

    private val _expenseSectionSummary = MutableStateFlow(ExpenseSectionSummary())
    val expenseSectionSummary: StateFlow<ExpenseSectionSummary> = _expenseSectionSummary.asStateFlow()

    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _savings = MutableStateFlow<List<Saving>>(emptyList())
    val savings: StateFlow<List<Saving>> = _savings.asStateFlow()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        _userName.value = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore('@')
            ?: "User"

        viewModelScope.launch {
            repository.refreshExchangeRates()
        }

        viewModelScope.launch {
            try { repository.getTotalIncome().collectLatest { _totalIncome.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getTotalIncome failed", e) }
        }

        viewModelScope.launch {
            try { repository.getTotalExpenses().collectLatest { _totalExpenses.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getTotalExpenses failed", e) }
        }

        viewModelScope.launch {
            try { repository.getTotalSavings().collectLatest { _totalSavings.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getTotalSavings failed", e) }
        }

        viewModelScope.launch {
            try {
                combine(_totalIncome, _totalExpenses, _totalSavings) { income, expenses, savings ->
                    income - (expenses + savings)
                }.collectLatest { _remainingBalance.value = it }
            } catch (e: Exception) { android.util.Log.e("APP_CRASH", "remainingBalance failed", e) }
        }

        viewModelScope.launch {
            try { repository.getMonthlySummary().collectLatest { _monthlySummary.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getMonthlySummary failed", e) }
        }

        viewModelScope.launch {
            try { repository.getGoalProgress().collectLatest { _goalProgress.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getGoalProgress failed", e) }
        }

        viewModelScope.launch {
            try {
                repository.getExpenseCategoryTotals().collectLatest { totals ->
                    _expenseCategories.value = collapseCategoryTotals(totals)
                }
            } catch (e: Exception) { android.util.Log.e("APP_CRASH", "getExpenseCategoryTotals failed", e) }
        }

        viewModelScope.launch {
            try { repository.getIncomeSources().collectLatest { _incomeSources.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getIncomeSources failed", e) }
        }

        viewModelScope.launch {
            try {
                repository.getExpenseTypeBreakdown().collectLatest { breakdown ->
                    _expenseSectionSummary.value = toExpenseSectionSummary(breakdown)
                }
            } catch (e: Exception) { android.util.Log.e("APP_CRASH", "getExpenseTypeBreakdown failed", e) }
        }

        viewModelScope.launch {
            try { repository.getIncomesFlow().collectLatest { _incomes.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getIncomesFlow failed", e) }
        }

        viewModelScope.launch {
            try { repository.getExpensesFlow().collectLatest { _expenses.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getExpensesFlow failed", e) }
        }

        viewModelScope.launch {
            try { repository.getSavingsFlow().collectLatest { _savings.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getSavingsFlow failed", e) }
        }

        viewModelScope.launch {
            try { repository.getGoalsFlow().collectLatest { _goals.value = it } }
            catch (e: Exception) { android.util.Log.e("APP_CRASH", "getGoalsFlow failed", e) }
        }
    }

    fun getMonthlySummaryFor(month: YearMonth): MonthlySummary {
        return repository.buildMonthlySummary(
            month = month,
            incomes = _incomes.value,
            expenses = _expenses.value,
            savings = _savings.value,
            incomeRates = emptyMap(),
            expenseRates = emptyMap()
        )
    }

    fun getDailyActivityFor(month: YearMonth): List<DayActivitySummary> {
        val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val monthLabel = month.format(MONTH_LABEL_FORMAT)

        val incomeByDay = _incomes.value.filter { it.date.toDate().time in start..end }
            .groupBy { it.date.toDate().toInstant().atZone(ZoneId.systemDefault()).dayOfMonth }
            .mapValues { it.value.size }

        val expenseByDay = _expenses.value.filter { it.date.toDate().time in start..end }
            .groupBy { it.date.toDate().toInstant().atZone(ZoneId.systemDefault()).dayOfMonth }
            .mapValues { it.value.size }

        val savingsByDay = _savings.value.filter { it.month == monthLabel }
            .groupBy { saving ->
                val parts = saving.date.split("-")
                parts.getOrNull(2)?.toIntOrNull() ?: 0
            }
            .mapValues { it.value.size }

        return (1..month.lengthOfMonth()).map { day ->
            DayActivitySummary(
                dayOfMonth = day,
                incomeCount = incomeByDay[day] ?: 0,
                expenseCount = expenseByDay[day] ?: 0,
                savingsCount = savingsByDay[day] ?: 0
            )
        }
    }

    private fun collapseCategoryTotals(totals: List<CategoryTotal>): List<ExpenseCategorySummary> {
        val grouped = totals.groupBy { total ->
            val cat = getCat(total.categoryId)
            cat.parentLabel ?: cat.label
        }
        return grouped.map { (label, items) ->
            ExpenseCategorySummary(label = label, amount = items.sumOf { it.amount })
        }.sortedByDescending { it.amount }
    }

    private fun toExpenseSectionSummary(breakdown: ExpenseTypeBreakdown): ExpenseSectionSummary {
        val mustItems = breakdown.mustItems.map { item ->
            val cat = getCat(item.categoryId)
            ExpenseCategorySummary(label = cat.label, amount = item.amount)
        }
        val optionalItems = breakdown.optionalItems.map { item ->
            val cat = getCat(item.categoryId)
            ExpenseCategorySummary(label = cat.label, amount = item.amount)
        }
        return ExpenseSectionSummary(
            mustTotal = breakdown.mustTotal,
            optionalTotal = breakdown.optionalTotal,
            mustItems = mustItems,
            optionalItems = optionalItems
        )
    }
}

package com.example.financeflow.repository.dashboard

import com.example.financeflow.model.Expense
import com.example.financeflow.model.Goal
import com.example.financeflow.model.Income
import com.example.financeflow.model.Saving
import com.example.financeflow.repository.expense.ExpenseRepository
import com.example.financeflow.repository.goal.GoalRepository
import com.example.financeflow.repository.income.IncomeRepository
import com.example.financeflow.repository.savings.SavingsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

private val DEFAULT_EXCHANGE_RATES = mapOf(
    "LKR" to 1.0,
    "USD" to 300.0,
    "EUR" to 320.0,
    "GBP" to 370.0
)

data class MonthlySummary(
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val savings: Double = 0.0,
    val remainingBalance: Double = 0.0
)

data class GoalProgressSummary(
    val goalId: String,
    val title: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val daysRemaining: Int,
    val dailySavingsNeeded: Double,
    val currentDailyRate: Double,
    val isCompleted: Boolean
)

data class CategoryTotal(
    val categoryId: String,
    val amount: Double
)

data class IncomeSourceTotal(
    val source: String,
    val amount: Double
)

data class ExpenseTypeBreakdown(
    val mustTotal: Double,
    val optionalTotal: Double,
    val mustItems: List<CategoryTotal>,
    val optionalItems: List<CategoryTotal>
)

@Singleton
class DashboardRepository @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val savingsRepository: SavingsRepository,
    private val goalRepository: GoalRepository,
    private val auth: FirebaseAuth
) {
    private val incomeRates = MutableStateFlow(DEFAULT_EXCHANGE_RATES)
    private val expenseRates = MutableStateFlow(DEFAULT_EXCHANGE_RATES)

    suspend fun refreshExchangeRates() {
        runCatching { incomeRepository.getExchangeRates() }.getOrNull()?.let { incomeRates.value = it }
        runCatching { expenseRepository.getExchangeRates() }.getOrNull()?.let { expenseRates.value = it }
    }

    fun getIncomesFlow(): Flow<List<Income>> = incomeRepository.getIncomesFlow()

    fun getExpensesFlow(): Flow<List<Expense>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        return expenseRepository.getAllForUserFlow(uid)
    }

    fun getSavingsFlow(): Flow<List<Saving>> = savingsRepository.getSavingsFlow()

    fun getGoalsFlow(): Flow<List<Goal>> = goalRepository.observeGoals().map { it.getOrNull().orEmpty() }

    fun getTotalIncome(): Flow<Double> = combine(getIncomesFlow(), incomeRates) { list, rates ->
        val now = System.currentTimeMillis()
        list.filter { it.date.toDate().time <= now }
            .sumOf { convertToLkr(it.amount, it.currency, rates) }
    }

    fun getTotalExpenses(): Flow<Double> = combine(getExpensesFlow(), expenseRates) { list, rates ->
        val now = System.currentTimeMillis()
        list.filter { it.date.toDate().time <= now }
            .sumOf { convertToLkr(it.amount, it.currency, rates) }
    }

    fun getTotalSavings(): Flow<Double> = getSavingsFlow().map { list ->
        list.sumOf { it.amountSaved }
    }

    fun getRemainingBalance(): Flow<Double> = combine(
        getTotalIncome(),
        getTotalExpenses(),
        getTotalSavings()
    ) { income, expenses, savings ->
        income - (expenses + savings)
    }

    fun getExpenseCategoryTotals(): Flow<List<CategoryTotal>> = combine(getExpensesFlow(), expenseRates) { list, rates ->
        val month = YearMonth.now()
        val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        list.filter { it.date.toDate().time in start..end }
            .groupBy { it.category }
            .map { (categoryId, items) ->
                val total = items.sumOf { convertToLkr(it.amount, it.currency, rates) }
                CategoryTotal(categoryId = categoryId, amount = total)
            }
            .sortedByDescending { it.amount }
    }

    fun getIncomeSources(): Flow<List<IncomeSourceTotal>> = combine(getIncomesFlow(), incomeRates) { list, rates ->
        val month = YearMonth.now()
        val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        list.filter { it.date.toDate().time in start..end }
            .groupBy { it.source }
            .map { (source, items) ->
                val total = items.sumOf { convertToLkr(it.amount, it.currency, rates) }
                IncomeSourceTotal(source = source, amount = total)
            }
            .sortedByDescending { it.amount }
    }

    fun getExpenseTypeBreakdown(): Flow<ExpenseTypeBreakdown> = combine(getExpensesFlow(), expenseRates) { list, rates ->
        val month = YearMonth.now()
        val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val filtered = list.filter { it.date.toDate().time in start..end }
        
        val mustItems = filtered.filter { it.isFixed }.groupBy { it.category }.map { (categoryId, items) ->
            val total = items.sumOf { convertToLkr(it.amount, it.currency, rates) }
            CategoryTotal(categoryId = categoryId, amount = total)
        }
        val optionalItems = filtered.filter { !it.isFixed }.groupBy { it.category }.map { (categoryId, items) ->
            val total = items.sumOf { convertToLkr(it.amount, it.currency, rates) }
            CategoryTotal(categoryId = categoryId, amount = total)
        }
        ExpenseTypeBreakdown(
            mustTotal = mustItems.sumOf { it.amount },
            optionalTotal = optionalItems.sumOf { it.amount },
            mustItems = mustItems.sortedByDescending { it.amount },
            optionalItems = optionalItems.sortedByDescending { it.amount }
        )
    }

    fun getMonthlySummary(): Flow<MonthlySummary> = combine(
        getIncomesFlow(),
        getExpensesFlow(),
        getSavingsFlow(),
        incomeRates,
        expenseRates
    ) { incomes, expenses, savings, incRates, expRates ->
        buildMonthlySummary(YearMonth.now(), incomes, expenses, savings, incRates, expRates)
    }

    fun getGoalProgress(): Flow<List<GoalProgressSummary>> = getGoalsFlow().map { goals ->
        goals.map { goal ->
            val daysRemaining = goal.daysRemaining.toInt()
            val secondsElapsed = (com.google.firebase.Timestamp.now().seconds - goal.createdAt.seconds).coerceAtLeast(0)
            val daysElapsed = maxOf(1L, secondsElapsed / 86400L)
            val currentDailyRate = goal.currentSavedAmount / daysElapsed
            GoalProgressSummary(
                goalId = goal.id,
                title = goal.title,
                currentAmount = goal.currentSavedAmount,
                targetAmount = goal.targetAmount,
                daysRemaining = daysRemaining,
                dailySavingsNeeded = goal.dailySavingTarget,
                currentDailyRate = currentDailyRate,
                isCompleted = goal.isCompleted
            )
        }
    }

    fun buildMonthlySummary(
        month: YearMonth,
        incomes: List<Income>,
        expenses: List<Expense>,
        savings: List<Saving>,
        incomeRates: Map<String, Double>,
        expenseRates: Map<String, Double>
    ): MonthlySummary {
        val incomeRateMap = if (incomeRates.isEmpty()) DEFAULT_EXCHANGE_RATES else incomeRates
        val expenseRateMap = if (expenseRates.isEmpty()) DEFAULT_EXCHANGE_RATES else expenseRates
        val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val monthLabel = month.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))

        val incomeTotal = incomes.filter { it.date.toDate().time in start..end }
            .sumOf { convertToLkr(it.amount, it.currency, incomeRateMap) }
        val expenseTotal = expenses.filter { it.date.toDate().time in start..end }
            .sumOf { convertToLkr(it.amount, it.currency, expenseRateMap) }
        val savingsTotal = savings.filter { it.month == monthLabel }
            .sumOf { it.amountSaved }
        val remaining = incomeTotal - (expenseTotal + savingsTotal)

        return MonthlySummary(
            income = incomeTotal,
            expenses = expenseTotal,
            savings = savingsTotal,
            remainingBalance = remaining
        )
    }

    private fun convertToLkr(amount: Double, currency: String, rates: Map<String, Double>): Double {
        val rate = rates[currency] ?: rates["USD"] ?: 300.0
        return if (currency == "LKR") amount else amount * rate
    }
}

package com.example.financeflow.repository

import com.example.financeflow.model.Income
import com.example.financeflow.model.Expense
import com.example.financeflow.model.Saving
import com.example.financeflow.repository.dashboard.DashboardRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth
import com.google.firebase.Timestamp
import java.util.Calendar
import org.mockito.kotlin.mock
import com.example.financeflow.repository.income.IncomeRepository
import com.example.financeflow.repository.expense.ExpenseRepository
import com.example.financeflow.repository.savings.SavingsRepository
import com.example.financeflow.repository.goal.GoalRepository
import com.google.firebase.auth.FirebaseAuth

class DashboardRepositoryTest {

    private fun tsFor(year: Int, month: Int, day: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, 12, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Timestamp(java.util.Date(cal.timeInMillis))
    }

    @Test
    fun buildMonthlySummary_calculatesIncomeExpensesSavingsAndRemaining() {
        // Arrange: incomes in USD, expenses in LKR, savings for month label
        val incomes = listOf(
            Income(amount = 100.0, currency = "USD", date = tsFor(2026, 6, 5))
        )
        val expenses = listOf(
            Expense(amount = 5000.0, currency = "LKR", date = tsFor(2026, 6, 6))
        )
        val monthLabel = YearMonth.of(2026, 6).format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))
        val savings = listOf(
            Saving(amountSaved = 2000.0, month = monthLabel)
        )

        val incomeRates = mapOf("LKR" to 1.0, "USD" to 300.0)
        val expenseRates = mapOf("LKR" to 1.0)

        // Create a DashboardRepository with dummy dependencies (not used for this unit test)
        val mockIncomeRepo = mock<IncomeRepository>()
        val mockExpenseRepo = mock<ExpenseRepository>()
        val mockSavingsRepo = mock<SavingsRepository>()
        val mockGoalRepo = mock<GoalRepository>()
        val mockAuth = mock<FirebaseAuth>()

        val repo = DashboardRepository(mockIncomeRepo, mockExpenseRepo, mockSavingsRepo, mockGoalRepo, mockAuth)

        // Act
        val summary = repo.buildMonthlySummary(YearMonth.of(2026, 6), incomes, expenses, savings, incomeRates, expenseRates)

        // USD 100 * 300 = 30000
        assertEquals(30000.0, summary.income, 0.001)
        assertEquals(5000.0, summary.expenses, 0.001)
        assertEquals(2000.0, summary.savings, 0.001)
        assertEquals(30000.0 - (5000.0 + 2000.0), summary.remainingBalance, 0.001)
    }

    @Test
    fun buildMonthlySummary_handlesEmptyRates_usingDefaults() {
        val incomes = listOf(Income(amount = 1.0, currency = "USD", date = tsFor(2026, 6, 1)))
        val expenses = emptyList<Expense>()
        val savings = emptyList<Saving>()

        val mockIncomeRepo = mock<IncomeRepository>()
        val mockExpenseRepo = mock<ExpenseRepository>()
        val mockSavingsRepo = mock<SavingsRepository>()
        val mockGoalRepo = mock<GoalRepository>()
        val mockAuth = mock<FirebaseAuth>()

        val repo = DashboardRepository(mockIncomeRepo, mockExpenseRepo, mockSavingsRepo, mockGoalRepo, mockAuth)

        val summary = repo.buildMonthlySummary(YearMonth.of(2026, 6), incomes, expenses, savings, emptyMap(), emptyMap())

        // Default USD rate is 300.0 (see DashboardRepository DEFAULT_EXCHANGE_RATES)
        assertEquals(300.0, summary.income, 0.001)
    }
}

package com.example.financeflow.repository

import com.example.financeflow.model.Expense
import com.example.financeflow.model.FixedExpense
import com.example.financeflow.model.Saving
import com.example.financeflow.repository.dashboard.DashboardRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth
import com.google.firebase.Timestamp
import java.util.Calendar

/**
 * Unit tests for expense calculations and breakdowns.
 * Follows AAA pattern: Arrange, Act, Assert. Tests are independent and use fake data.
 */
class ExpenseCalculationTest {

    private fun tsFor(year: Int, month: Int, day: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, 12, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Timestamp(java.util.Date(cal.timeInMillis))
    }

    @Test
    fun calculateTotalExpenses_returnsCorrectTotal() {
        // Arrange
        val expenses = listOf(
            Expense(amount = 1000.0, currency = "LKR", category = "Food", date = tsFor(2026,6,2)),
            Expense(amount = 10.0, currency = "USD", category = "Transport", date = tsFor(2026,6,3))
        )
        val repo = DashboardRepository(
            incomeRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.income.IncomeRepository::class.java),
            expenseRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.expense.ExpenseRepository::class.java),
            savingsRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.savings.SavingsRepository::class.java),
            goalRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.goal.GoalRepository::class.java),
            auth = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        )

        // Act
        val summary = repo.buildMonthlySummary(YearMonth.of(2026,6), incomes = emptyList(), expenses = expenses, savings = emptyList(), incomeRates = mapOf("USD" to 300.0), expenseRates = mapOf("USD" to 300.0, "LKR" to 1.0))

        // Assert: 1000 LKR + (10 USD * 300) = 1000 + 3000 = 4000
        assertEquals(4000.0, summary.expenses, 0.001)
    }

    @Test
    fun fixedExpensesIncludedInTotalExpenses() = runBlocking {
        // Arrange
        val expenses = listOf(
            Expense(amount = 2000.0, currency = "LKR", category = "Rent", isFixed = true, date = tsFor(2026,6,1)),
            Expense(amount = 500.0, currency = "LKR", category = "Utilities", isFixed = true, date = tsFor(2026,6,2))
        )

        val mockExpenseRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.expense.ExpenseRepository::class.java)
        val mockAuth = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        val mockUser = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseUser::class.java)
        org.mockito.Mockito.`when`(mockAuth.currentUser).thenReturn(mockUser)
        org.mockito.Mockito.`when`(mockUser.uid).thenReturn("uid1")
        org.mockito.Mockito.`when`(mockExpenseRepo.getAllForUserFlow("uid1")).thenReturn(flowOf(expenses))

        val repo = DashboardRepository(
            incomeRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.income.IncomeRepository::class.java),
            expenseRepository = mockExpenseRepo,
            savingsRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.savings.SavingsRepository::class.java),
            goalRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.goal.GoalRepository::class.java),
            auth = mockAuth
        )

        // Act
        val breakdown = repo.getExpenseTypeBreakdown().first()

        // Assert: mustTotal should equal sum of fixed expenses
        assertEquals(2500.0, breakdown.mustTotal, 0.001)
    }

    @Test
    fun optionalExpensesIncludedInTotalExpenses() = runBlocking {
        // Arrange
        val expenses = listOf(
            Expense(amount = 150.0, currency = "LKR", category = "Dining", isFixed = false, date = tsFor(2026,6,5)),
            Expense(amount = 50.0, currency = "LKR", category = "Cafe", isFixed = false, date = tsFor(2026,6,6))
        )
        val mockExpenseRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.expense.ExpenseRepository::class.java)
        val mockAuth = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        val mockUser = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseUser::class.java)
        org.mockito.Mockito.`when`(mockAuth.currentUser).thenReturn(mockUser)
        org.mockito.Mockito.`when`(mockUser.uid).thenReturn("uid2")
        org.mockito.Mockito.`when`(mockExpenseRepo.getAllForUserFlow("uid2")).thenReturn(flowOf(expenses))

        val repo = DashboardRepository(
            incomeRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.income.IncomeRepository::class.java),
            expenseRepository = mockExpenseRepo,
            savingsRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.savings.SavingsRepository::class.java),
            goalRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.goal.GoalRepository::class.java),
            auth = mockAuth
        )

        // Act
        val breakdown = repo.getExpenseTypeBreakdown().first()

        // Assert: optionalTotal should equal sum of optional expenses
        assertEquals(200.0, breakdown.optionalTotal, 0.001)
    }

    @Test
    fun remainingBalanceCalculatedCorrectly() {
        // Arrange
        val incomes = listOf(com.example.financeflow.model.Income(amount = 500.0, currency = "LKR", date = tsFor(2026,6,2)))
        val expenses = listOf(Expense(amount = 200.0, currency = "LKR", date = tsFor(2026,6,3)))
        val savings = listOf(Saving(amountSaved = 50.0, month = YearMonth.of(2026,6).format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))))

        val repo = DashboardRepository(
            incomeRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.income.IncomeRepository::class.java),
            expenseRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.expense.ExpenseRepository::class.java),
            savingsRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.savings.SavingsRepository::class.java),
            goalRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.goal.GoalRepository::class.java),
            auth = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        )

        // Act
        val summary = repo.buildMonthlySummary(YearMonth.of(2026,6), incomes, expenses, savings, emptyMap(), emptyMap())

        // Assert: remaining = 500 - (200 + 50) = 250
        assertEquals(250.0, summary.remainingBalance, 0.001)
    }

    @Test
    fun expenseCategoryTotalsCalculatedCorrectly() = runBlocking {
        // Arrange
        val expenses = listOf(
            Expense(amount = 100.0, currency = "LKR", category = "Food", date = tsFor(2026,6,1)),
            Expense(amount = 200.0, currency = "LKR", category = "Transport", date = tsFor(2026,6,2)),
            Expense(amount = 50.0, currency = "LKR", category = "Food", date = tsFor(2026,6,3))
        )

        val mockExpenseRepo = org.mockito.Mockito.mock(com.example.financeflow.repository.expense.ExpenseRepository::class.java)
        val mockAuth = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        val mockUser = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseUser::class.java)
        org.mockito.Mockito.`when`(mockAuth.currentUser).thenReturn(mockUser)
        org.mockito.Mockito.`when`(mockUser.uid).thenReturn("uid3")
        org.mockito.Mockito.`when`(mockExpenseRepo.getAllForUserFlow("uid3")).thenReturn(flowOf(expenses))

        val repo = DashboardRepository(
            incomeRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.income.IncomeRepository::class.java),
            expenseRepository = mockExpenseRepo,
            savingsRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.savings.SavingsRepository::class.java),
            goalRepository = org.mockito.Mockito.mock(com.example.financeflow.repository.goal.GoalRepository::class.java),
            auth = mockAuth
        )

        // Act
        val categoryTotals = repo.getExpenseCategoryTotals().first()

        // Assert: Food total = 150, Transport = 200
        val food = categoryTotals.find { it.categoryId == "Food" }!!.amount
        val transport = categoryTotals.find { it.categoryId == "Transport" }!!.amount

        assertEquals(150.0, food, 0.001)
        assertEquals(200.0, transport, 0.001)
    }
}

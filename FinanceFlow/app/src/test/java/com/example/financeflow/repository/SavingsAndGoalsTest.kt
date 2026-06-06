package com.example.financeflow.repository

import com.example.financeflow.model.SavingGoal
import com.example.financeflow.repository.savings.SavingsRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class SavingsAndGoalsTest {

    @Test
    fun calculateSavingRate_returnsPercentage_whenTotalIncomePositive() {
        val repo = SavingsRepository(
            service = org.mockito.Mockito.mock(com.example.financeflow.data.remote.SavingsFirestoreService::class.java),
            savingDao = org.mockito.Mockito.mock(com.example.financeflow.data.local.dao.SavingDao::class.java),
            savingGoalDao = org.mockito.Mockito.mock(com.example.financeflow.data.local.dao.SavingGoalDao::class.java)
        )

        val method = repo.javaClass.getDeclaredMethod("calculateSavingRate", Double::class.javaPrimitiveType, Double::class.javaPrimitiveType)
        method.isAccessible = true
        val result = method.invoke(repo, 500.0, 2000.0) as Double
        assertEquals(25.0, result, 0.0001)
    }

    @Test
    fun calculateGoalProgress_returnsFraction_orExistingProgress() {
        val repo = SavingsRepository(
            service = org.mockito.Mockito.mock(com.example.financeflow.data.remote.SavingsFirestoreService::class.java),
            savingDao = org.mockito.Mockito.mock(com.example.financeflow.data.local.dao.SavingDao::class.java),
            savingGoalDao = org.mockito.Mockito.mock(com.example.financeflow.data.local.dao.SavingGoalDao::class.java)
        )

        val method = repo.javaClass.getDeclaredMethod("calculateGoalProgress", com.example.financeflow.model.SavingGoal::class.java)
        method.isAccessible = true

        val goal = SavingGoal(id = "g1", goalName = "Test", currentAmount = 50.0, targetAmount = 200.0, progress = 0.0f)
        val progress = method.invoke(repo, goal) as Float
        assertEquals(0.25f, progress, 0.0001f)

        val goalNoTarget = SavingGoal(id = "g2", goalName = "T", currentAmount = 1.0, targetAmount = 0.0, progress = 0.5f)
        val progress2 = method.invoke(repo, goalNoTarget) as Float
        assertEquals(0.5f, progress2, 0.0001f)
    }
}

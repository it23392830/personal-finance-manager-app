package com.example.financeflow.model

import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class GoalModelTest {

    private fun tsWithOffsetDays(offsetDays: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis() + offsetDays * 24L * 3600L * 1000L
        return Timestamp(java.util.Date(cal.timeInMillis))
    }

    @Test
    fun progressAndRemaining_calculatedCorrectly() {
        val created = tsWithOffsetDays(-10)
        val deadline = tsWithOffsetDays(20)
        val goal = Goal(
            id = "g1",
            title = "Test",
            targetAmount = 200.0,
            currentSavedAmount = 50.0,
            createdAt = created,
            deadlineDate = deadline
        )

        assertEquals(25.0, goal.progressPercentage, 0.0001)
        assertEquals(150.0, goal.remainingAmount, 0.0001)

        // daysRemaining should be roughly 20 (allow some slack)
        assertTrue(goal.daysRemaining in 19..21)

        // daily target > 0
        assertTrue(goal.dailySavingTarget > 0.0)

        // time progress is 10/30 = 0.333..., savings progress 0.25 => not on track
        assertFalse(goal.isOnTrack)
    }

    @Test
    fun isOnTrack_true_when_progressExceedsTimeProgress() {
        val created = tsWithOffsetDays(-1)
        val deadline = tsWithOffsetDays(9)
        val goal = Goal(
            id = "g2",
            title = "T2",
            targetAmount = 100.0,
            currentSavedAmount = 60.0,
            createdAt = created,
            deadlineDate = deadline
        )

        // elapsed 1/10 = 0.1, progress 60% => on track
        assertTrue(goal.isOnTrack)
    }
}

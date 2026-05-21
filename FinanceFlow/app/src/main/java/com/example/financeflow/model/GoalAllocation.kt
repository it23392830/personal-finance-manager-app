package com.example.financeflow.model

import com.google.firebase.Timestamp

/**
 * Represents a single allocation/deposit the user makes toward a goal.
 * Each time the user adds money toward a goal, a GoalAllocation is created.
 * The goal's currentSavedAmount is kept in sync by summing all allocations.
 *
 * Firestore path: users/{userId}/goals/{goalId}/allocations/{allocationId}
 */
data class GoalAllocation(
    val id: String = "",
    val goalId: String = "",
    val amount: Double = 0.0,
    val monthlyTarget: Double = 0.0,
    val monthYear: String = "",
    val note: String = "",
    val allocatedAt: Timestamp = Timestamp.now()
)

fun GoalAllocation.toMap(): Map<String, Any> = mapOf(
    "goalId" to goalId,
    "amount" to amount,
    "monthlyTarget" to monthlyTarget,
    "monthYear" to monthYear,
    "note" to note,
    "allocatedAt" to allocatedAt
)

fun Map<String, Any>.toGoalAllocation(id: String): GoalAllocation = GoalAllocation(
    id = id,
    goalId = this["goalId"] as? String ?: "",
    amount = (this["amount"] as? Number)?.toDouble() ?: 0.0,
    monthlyTarget = (this["monthlyTarget"] as? Number)?.toDouble() ?: 0.0,
    monthYear = this["monthYear"] as? String ?: "",
    note = this["note"] as? String ?: "",
    allocatedAt = this["allocatedAt"] as? Timestamp ?: Timestamp.now()
)
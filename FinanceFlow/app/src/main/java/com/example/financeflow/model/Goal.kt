package com.example.financeflow.model

data class Goal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val savedAmount: Double = 0.0,
    val currency: String = "LKR",
    val deadline: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
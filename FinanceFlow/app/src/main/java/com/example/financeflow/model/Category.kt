package com.example.financeflow.model

data class Category(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "", // income, expense
    val expenseType: String = "" // must, optional
)
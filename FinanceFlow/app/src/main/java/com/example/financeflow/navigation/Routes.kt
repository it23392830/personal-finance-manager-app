package com.example.financeflow.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"

    const val INCOME = "income"
    const val EXPENSES = "expenses"
    const val SAVINGS = "savings"
    const val GOALS = "goals"
    const val INSIGHTS = "insights"
    const val DASHBOARD = "dashboard"
    const val STREAK = "streak"

    const val ADD_INCOME = "add_income"
    const val EDIT_INCOME = "edit_income/{incomeId}"
    const val DELETE_INCOME = "delete_income/{incomeId}"

    const val ADD_EXPENSE = "add_expense"
    const val EDIT_EXPENSE = "edit_expense/{expenseId}"

    const val DAILY_REPORT = "daily_report"
    const val WEEKLY_REPORT = "weekly_report"
    const val MONTHLY_REPORT = "monthly_report"
}

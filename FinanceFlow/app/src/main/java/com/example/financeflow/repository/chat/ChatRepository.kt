package com.example.financeflow.repository.chat

import com.example.financeflow.data.local.dao.ChatMessageDao
import com.example.financeflow.data.local.entity.ChatMessageEntity
import com.example.financeflow.data.remote.AIService
import com.example.financeflow.model.ChatMessage
import com.example.financeflow.repository.expense.ExpenseRepository
import com.example.financeflow.repository.goal.GoalRepository
import com.example.financeflow.repository.income.IncomeRepository
import com.example.financeflow.repository.savings.SavingsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatMessageDao,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val savingsRepository: SavingsRepository,
    private val goalRepository: GoalRepository,
    private val aiService: AIService,
    private val auth: FirebaseAuth
) {

    private companion object {
        const val TYPING_INDICATOR = "Finance Assistant is typing..."
    }

    fun getMessagesFlow(): Flow<List<ChatMessage>> {
        return chatDao.getMessagesFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun sendMessage(text: String): Result<Unit> {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return Result.success(Unit)

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = trimmed,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(userMessage.toEntity())

        val typingId = UUID.randomUUID().toString()
        val typingMessage = ChatMessage(
            id = typingId,
            text = TYPING_INDICATOR,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(typingMessage.toEntity())

        val summary = buildFinancialSummary()
        val prompt = buildPrompt(summary = summary, question = trimmed)

        val response = aiService.generateResponse(prompt)
        val botText = response.getOrElse { error ->
            when (error.message) {
                AIService.CONFIG_ERROR_MESSAGE -> AIService.CONFIG_ERROR_MESSAGE
                else -> AIService.CONNECTION_ERROR_MESSAGE
            }
        }

        val botMessage = ChatMessage(
            id = typingId,
            text = botText,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(botMessage.toEntity())

        return Result.success(Unit)
    }

    private suspend fun buildFinancialSummary(): String {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return "No authenticated user. Ask the user to sign in."
        }

        val incomes = runCatching { incomeRepository.getIncomesFlow().first() }.getOrDefault(emptyList())
        val expenses = runCatching { expenseRepository.getAllForUserFlow(userId).first() }.getOrDefault(emptyList())
        val savings = runCatching { savingsRepository.getSavingsFlow().first() }.getOrDefault(emptyList())
        val goals = runCatching { goalRepository.observeGoals().first().getOrNull().orEmpty() }.getOrDefault(emptyList())

        val totalIncome = incomes.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val totalSavings = savings.sumOf { it.amountSaved }
        val remainingBalance = totalIncome - (totalExpenses + totalSavings)

        val monthBounds = currentMonthBounds()
        val monthIncome = incomes.filter { it.date.toDate().time in monthBounds }.sumOf { it.amount }
        val monthExpenses = expenses.filter { it.date.toDate().time in monthBounds }.sumOf { it.amount }

        val monthLabel = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US).format(LocalDate.now())
        val monthSavings = savings.filter { it.month == monthLabel }.sumOf { it.amountSaved }

        val topCategory = expenses
            .groupBy { it.category.ifBlank { "Uncategorized" } }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .maxByOrNull { it.value }
            ?.key
            .orEmpty()

        val goalsSummary = if (goals.isEmpty()) {
            "No active goals"
        } else {
            goals.joinToString(separator = "\n") { goal ->
                "${goal.title}: ${formatAmount(goal.currentSavedAmount)} / ${formatAmount(goal.targetAmount)}"
            }
        }

        val totalGoalSaved = goals.sumOf { it.currentSavedAmount }

        return buildString {
            appendLine("Current Financial Data:")
            appendLine("Total Income: ${formatAmount(totalIncome)}")
            appendLine("Total Expenses: ${formatAmount(totalExpenses)}")
            appendLine("Total Savings: ${formatAmount(totalSavings)}")
            appendLine("Remaining Balance: ${formatAmount(remainingBalance)}")
            appendLine("This Month Income: ${formatAmount(monthIncome)}")
            appendLine("This Month Expenses: ${formatAmount(monthExpenses)}")
            appendLine("This Month Savings: ${formatAmount(monthSavings)}")
            appendLine("Top Expense Category: ${if (topCategory.isBlank()) "N/A" else topCategory}")
            appendLine("Total Saved Toward Goals: ${formatAmount(totalGoalSaved)}")
            appendLine("Goals:")
            appendLine(goalsSummary)
        }
    }

    private fun buildPrompt(summary: String, question: String): String {
        return """
You are FinanceFlow's AI assistant. Answer clearly and concisely using the provided data.

$summary

Question:
$question
""".trim()
    }

    private fun currentMonthBounds(): LongRange {
        val zone = ZoneId.systemDefault()
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
        return start..end
    }

    private fun formatAmount(amount: Double): String {
        return "LKR ${String.format(Locale.US, "%.2f", amount)}"
    }

    private fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
        id = id,
        text = text,
        isUser = isUser,
        timestamp = timestamp
    )

    private fun ChatMessage.toEntity(): ChatMessageEntity = ChatMessageEntity(
        id = id,
        text = text,
        isUser = isUser,
        timestamp = timestamp
    )
}

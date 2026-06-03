package com.example.financeflow.viewmodel.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.ExpenseUiState
import com.example.financeflow.repository.expense.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.financeflow.model.Expense
import com.example.financeflow.ui.expenses.ExpenseUiItem
import com.example.financeflow.ui.expenses.CategoryBreakdownItem
import java.util.Calendar
import java.util.Date
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val exchangeRatesMutex = Mutex()
    private var exchangeRates: Map<String, Double> = mapOf("LKR" to 1.0, "USD" to 300.0, "EUR" to 320.0, "GBP" to 370.0)

    private suspend fun loadExchangeRates() {
        try {
            val rates = repository.getExchangeRates()
            exchangeRatesMutex.withLock { exchangeRates = rates }
        } catch (_: Exception) {
        }
    }

    private fun convertToLKR(amount: Double, currency: String): Double {
        val rate = exchangeRates[currency] ?: exchangeRates["USD"] ?: 300.0
        return if (currency == "LKR") amount else amount * rate
    }

    private fun mapToUiItem(e: Expense): ExpenseUiItem {
        val idInt = e.id.hashCode()
        val payment = when (e.paymentMethod.uppercase(Locale.getDefault())) {
            "CARD" -> com.example.financeflow.ui.expenses.PaymentMethod.CARD
            "CASH" -> com.example.financeflow.ui.expenses.PaymentMethod.CASH
            "BANK_TRANSFER" -> com.example.financeflow.ui.expenses.PaymentMethod.BANK_TRANSFER
            "DIGITAL_WALLET" -> com.example.financeflow.ui.expenses.PaymentMethod.DIGITAL_WALLET
            else -> com.example.financeflow.ui.expenses.PaymentMethod.CARD
        }
        val amtLkr = convertToLKR(e.amount, e.currency).toInt()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = try { sdf.format(e.date.toDate()) } catch (_: Exception) { "" }
        return ExpenseUiItem(
            id = idInt,
            categoryId = e.category,
            description = e.description.ifBlank { com.example.financeflow.ui.expenses.getCat(e.category).label },
            amount = amtLkr,
            paymentMethod = payment,
            date = dateStr,
            type = if (e.isFixed) com.example.financeflow.ui.expenses.ExpenseType.ESSENTIAL else com.example.financeflow.ui.expenses.ExpenseType.DISCRETIONARY,
            isRecurring = e.isFixed,
            isPaid = e.isPaid,
            notes = e.notes,
            domainId = e.id
        )
    }

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            launch { loadExchangeRates() }

            // collect expenses for current user and compute derived UI state
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Not authenticated")
                return@launch
            }

            // default month = current
            val cal = Calendar.getInstance()
            val m = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            _uiState.value = _uiState.value.copy(selectedMonth = m)

            // sync once from remote
            launch {
                try { repository.syncFromFirestore() } catch (_: Exception) {}
            }

            // collect all expenses and compute totals and groupings
            repository.getAllForUserFlow(uid).collect { list ->
                currentAllExpenses = list
                recalculateDerivedState()
            }
        }
    }

    private var currentAllExpenses: List<Expense> = emptyList()

    private fun recalculateDerivedState() {
        val list = currentAllExpenses
        
        // compute available months
        val monthsSet = mutableSetOf<Pair<Int, Int>>()
        val creationTime = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.metadata?.creationTimestamp ?: Calendar.getInstance().timeInMillis
        val creationCal = Calendar.getInstance().apply { timeInMillis = creationTime }
        val currentCal = Calendar.getInstance()
        
        val tempCal = creationCal.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        while (tempCal.before(currentCal) || (tempCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) && tempCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH))) {
            monthsSet.add(tempCal.get(Calendar.YEAR) to (tempCal.get(Calendar.MONTH) + 1))
            tempCal.add(Calendar.MONTH, 1)
        }

        val tmpCal = Calendar.getInstance()
        list.forEach { ee ->
            try {
                tmpCal.time = ee.date.toDate()
                monthsSet.add(tmpCal.get(Calendar.YEAR) to (tmpCal.get(Calendar.MONTH) + 1))
            } catch (_: Exception) {}
        }
        val months = monthsSet.toList().sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
        val monthsStr = months.map { (y, m) -> String.format("%04d-%02d", y, m) }

        // compute selected month filtered items
        val sel = _uiState.value.selectedMonth
        val (selYear, selMonth) = if (sel.isBlank()) {
            val c = Calendar.getInstance(); c.get(Calendar.YEAR) to (c.get(Calendar.MONTH) + 1)
        } else {
            val parts = sel.split("-")
            val y = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val m = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
            y to m
        }

        val startCal = Calendar.getInstance().apply { 
            set(Calendar.YEAR, selYear)
            set(Calendar.MONTH, selMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = (startCal.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
        val start = startCal.timeInMillis
        val end = endCal.timeInMillis

        val now = Calendar.getInstance().timeInMillis
        val monthItems = list.filter { e ->
            try {
                val t = e.date.toDate().time
                t in start..end
            } catch (_: Exception) { false }
        }

        val visibleForCalc = monthItems.filter { e ->
            try { e.date.toDate().time <= now } catch (_: Exception) { false }
        }
        
        // total calculation: all non-fixed + only PAID fixed
        val total = visibleForCalc.sumOf { e ->
            if (!e.isFixed || e.isPaid) {
                convertToLKR(e.amount, e.currency)
            } else 0.0
        }

        // today's expenses
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDate.format(Date())
        
        val todayList = list.filter { e ->
            try {
                val dStr = sdfDate.format(e.date.toDate())
                dStr == todayStr
            } catch (_: Exception) { false }
        }.sortedByDescending { it.date.toDate().time }.map { mapToUiItem(it) }

        // Fixed payments (those where isFixed is true)
        val fixedList = list.filter { it.isFixed }

        // recent transactions (limit 10) based on visibleForCalc newest first
        val recent = visibleForCalc.sortedByDescending { it.date.toDate().time }.take(10).map { mapToUiItem(it) }

        // category breakdown
        val grouped = visibleForCalc.filter { !it.isFixed || it.isPaid }.groupBy { it.category }.map { (cat, items) ->
            val sum = items.sumOf { convertToLKR(it.amount, it.currency) }
            val label = com.example.financeflow.ui.expenses.getCat(cat).label
            CategoryBreakdownItem(label = label, amount = sum.toInt(), color = com.example.financeflow.ui.expenses.getCat(cat).bgColor)
        }.sortedByDescending { it.amount }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            availableMonths = monthsStr,
            totalExpense = total,
            todayExpenses = todayList,
            currentMonthTransactions = monthItems.sortedByDescending { it.date.toDate().time }.map { mapToUiItem(it) },
            categoryBreakdown = grouped,
            fixedPayments = fixedList
        )
    }

    fun setSelectedMonth(year: Int, month: Int) {
        val m = String.format("%04d-%02d", year, month)
        _uiState.value = _uiState.value.copy(selectedMonth = m)
        recalculateDerivedState()
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.addExpense(expense)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.updateExpense(expense)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.deleteExpense(id)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // Fixed payments
    fun addFixedExpense(fixed: com.example.financeflow.model.FixedExpense) {
        viewModelScope.launch {
            try {
                repository.addFixedExpense(fixed)
            } catch (_: Exception) {}
        }
    }

    fun toggleFixedPaid(expense: com.example.financeflow.model.Expense, paid: Boolean) {
        viewModelScope.launch {
            try {
                val updated = expense.copy(isPaid = paid)
                repository.updateExpense(updated)
            } catch (_: Exception) {}
        }
    }

    fun deleteFixedExpense(id: String) {
        viewModelScope.launch { try { repository.deleteFixedExpense(id) } catch (_: Exception) {} }
    }

}

package com.example.financeflow.viewmodel.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.ExpenseUiState
import com.example.financeflow.model.Expense
import com.example.financeflow.repository.expense.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.util.Log
import com.example.financeflow.model.FixedExpenseDisplayItem
import com.example.financeflow.model.FixedExpense
import com.example.financeflow.ui.expenses.ExpenseUiItem
import com.example.financeflow.ui.expenses.CategoryBreakdownItem
import java.util.Calendar
import java.util.Date
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
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
        val isFixedVal = e.isFixedExpense || e.isFixed
        return ExpenseUiItem(
            id = idInt,
            categoryId = e.category,
            description = e.description.ifBlank { com.example.financeflow.ui.expenses.getCat(e.category).label },
            amount = amtLkr,
            paymentMethod = payment,
            date = dateStr,
            type = if (isFixedVal) com.example.financeflow.ui.expenses.ExpenseType.ESSENTIAL else com.example.financeflow.ui.expenses.ExpenseType.DISCRETIONARY,
            isRecurring = isFixedVal,
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
                try { 
                    repository.syncFromFirestore() 
                } catch (e: Exception) {
                    Log.e("EXPENSE_SYNC", "Sync expenses failed", e)
                }
                try { 
                    repository.syncFixedFromFirestore() 
                } catch (e: Exception) {
                    Log.e("EXPENSE_SYNC", "Sync fixed expenses failed", e)
                }
                recalculateDerivedState()
            }

            // collect all expenses and compute totals and groupings
            launch {
                try {
                    repository.getAllForUserFlow(uid).collect { list ->
                        currentAllExpenses = list
                        recalculateDerivedState()
                    }
                } catch (e: Exception) {
                    Log.e("APP_CRASH", "getAllForUserFlow failed: ${e.stackTraceToString()}")
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
            }

            // collect fixed templates
            launch {
                try {
                    repository.getAllFixedForUserFlow(uid).collect { list ->
                        Log.d("FIXED_DEBUG", "getAllFixedForUserFlow: Room emitted ${list.size} templates")
                        currentFixedTemplates = list
                        recalculateDerivedState()
                    }
                } catch (e: Exception) {
                    Log.e("APP_CRASH", "getAllFixedForUserFlow failed: ${e.stackTraceToString()}")
                }
            }
        }
    }

    private var currentAllExpenses: List<Expense> = emptyList()
    private var currentFixedTemplates: List<FixedExpense> = emptyList()

    private fun recalculateDerivedState() {
        val list = currentAllExpenses
        val templates = currentFixedTemplates
        
        // compute available months
        val monthsSet = mutableSetOf<Pair<Int, Int>>()
        val creationTime = FirebaseAuth.getInstance().currentUser?.metadata?.creationTimestamp ?: Calendar.getInstance().timeInMillis
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
        
        // total calculation: sum of all records in this month's collection
        // Fixed expenses are only added to 'expenses' collection when applied.
        val total = visibleForCalc.sumOf { e ->
            convertToLKR(e.amount, e.currency)
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

        // Fixed payments: Merge templates with current month's applied status.
        // Accept both isFixed and isFixedExpense so toggle-applied expenses are matched correctly.
        val fixedPayments = templates.map { template ->
            val appliedExpense = list.find {
                (it.isFixedExpense || it.isFixed) && it.templateId == template.id &&
                isSameMonth(it.date.toDate(), startCal.time)
            }
            FixedExpenseDisplayItem(
                template = template,
                isApplied = appliedExpense != null,
                monthExpenseId = appliedExpense?.id
            )
        }.toMutableList()

        // Fallback: also show fixed expenses from the expense list that have no matching template.
        // This restores display of legacy records (saved before the fixedExpenses Firestore
        // collection existed) and any templates accidentally removed by the edit flow.
        val knownTemplateIds = templates.map { it.id }.toSet()
        list.filter { expense ->
            (expense.isFixed || expense.isFixedExpense) &&
            (expense.templateId == null || expense.templateId !in knownTemplateIds)
        }
        .groupBy { it.templateId ?: it.id }
        .forEach { (_, group) ->
            val rep = group.maxByOrNull { it.createdAt.toDate().time } ?: return@forEach
            val syntheticTemplate = FixedExpense(
                id = rep.templateId ?: rep.id,
                userId = rep.userId,
                name = rep.description.ifBlank { rep.category },
                amount = rep.amount,
                category = rep.category,
                isPaid = rep.isPaid,
                createdAt = rep.createdAt
            )
            val appliedThisMonth = try {
                group.find { isSameMonth(it.date.toDate(), startCal.time) }
            } catch (_: Exception) { null }
            fixedPayments.add(
                FixedExpenseDisplayItem(
                    template = syntheticTemplate,
                    isApplied = appliedThisMonth != null,
                    monthExpenseId = appliedThisMonth?.id
                )
            )
        }

        // recent transactions (limit 10) based on visibleForCalc newest first
        val recent = visibleForCalc.sortedByDescending { it.date.toDate().time }.take(10).map { mapToUiItem(it) }

        // category breakdown
        val grouped = visibleForCalc.filter { !it.isFixedExpense || it.isPaid }.groupBy { it.category }.map { (cat, items) ->
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
            fixedPayments = fixedPayments
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

    fun updateExpenseDetails(
        id: String,
        amount: Double,
        description: String,
        category: String,
        paymentMethod: String,
        date: Timestamp,
        notes: String,
        isFixed: Boolean
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val original = currentAllExpenses.find { it.id == id }
                if (original != null) {
                    val oldFixed = original.isFixedExpense
                    val newFixed = isFixed
                    
                    Log.d("FIXED_EDIT", "OldFixed=$oldFixed")
                    Log.d("FIXED_EDIT", "NewFixed=$newFixed")

                    val templateId = original.templateId ?: java.util.UUID.randomUUID().toString()
                    
                    val updated = original.copy(
                        amount = amount,
                        description = description,
                        category = category,
                        paymentMethod = paymentMethod,
                        date = date,
                        notes = notes,
                        isFixed = isFixed,
                        isFixedExpense = isFixed,
                        templateId = if (isFixed) templateId else original.templateId
                    )
                    
                    Log.d("EDIT_EXPENSE", "Before Update = $original")
                    Log.d("EDIT_EXPENSE", "After Update = $updated")
                    Log.d("EDIT_EXPENSE", "DocumentId = ${updated.id}")
                    
                    repository.updateExpense(updated)

                    // Sync Fixed Payment Template
                    if (!oldFixed && newFixed) {
                        // Case A: Create fixed payment
                        val template = FixedExpense(
                            id = templateId,
                            userId = original.userId,
                            name = description,
                            amount = amount,
                            category = category,
                            isPaid = true,
                            createdAt = Timestamp.now()
                        )
                        repository.addFixedExpense(template)
                        Log.d("FIXED_EDIT", "FixedPaymentCreated")
                    } else if (oldFixed && !newFixed) {
                        // Case B: Remove fixed payment
                        original.templateId?.let { tid ->
                            repository.deleteFixedExpense(tid)
                            Log.d("FIXED_EDIT", "FixedPaymentRemoved")
                        }
                    } else if (oldFixed && newFixed) {
                        // Update existing template
                        val template = FixedExpense(
                            id = templateId,
                            userId = original.userId,
                            name = description,
                            amount = amount,
                            category = category,
                            isPaid = true,
                            createdAt = Timestamp.now()
                        )
                        repository.updateFixedExpense(template)
                    }

                } else {
                    Log.e("EDIT_EXPENSE", "Original expense not found for id: $id")
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("EDIT_EXPENSE", "Update failed", e)
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

    // Fixed payments logic
    fun addFixedExpense(fixed: com.example.financeflow.model.FixedExpense) {
        viewModelScope.launch {
            try {
                repository.addFixedExpense(fixed)
            } catch (_: Exception) {}
        }
    }

    fun addFixedExpenseWithRecord(
        name: String,
        amount: Double,
        category: String,
        date: Timestamp,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val templateId = java.util.UUID.randomUUID().toString()
                
                // 1. Create template
                val template = FixedExpense(
                    id = templateId,
                    name = name,
                    amount = amount,
                    category = category,
                    isPaid = true,
                    createdAt = Timestamp.now()
                )
                repository.addFixedExpense(template)
                
                // 2. Create actual record for the date
                val expense = Expense(
                    amount = amount,
                    category = category,
                    description = name,
                    notes = notes,
                    isFixed = true,
                    isFixedExpense = true,
                    isPaid = true,
                    templateId = templateId,
                    date = date
                )
                repository.addExpense(expense)
                
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun toggleFixedApplied(template: FixedExpense, applied: Boolean) {
        viewModelScope.launch {
            try {
                // Find existing expense for this template in the selected month
                val sel = _uiState.value.selectedMonth
                val parts = sel.split("-")
                val selYear = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
                val selMonth = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)

                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selYear)
                    set(Calendar.MONTH, selMonth - 1)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                
                if (applied) {
                    // Create a new expense record for this month
                    val newExpense = Expense(
                        amount = template.amount,
                        category = template.category,
                        description = template.name,
                        isFixed = true,
                        isFixedExpense = true,
                        isPaid = true,
                        templateId = template.id,
                        date = Timestamp(cal.time)
                    )
                    repository.addExpense(newExpense)
                } else {
                    // Find and delete the expense record for this month/template
                    val existing = currentAllExpenses.find { 
                        it.isFixed && it.templateId == template.id &&
                        isSameMonth(it.date.toDate(), cal.time)
                    }
                    existing?.let { repository.deleteExpense(it.id) }
                }
            } catch (_: Exception) {}
        }
    }

    private fun isSameMonth(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance().apply { time = d1 }
        val c2 = Calendar.getInstance().apply { time = d2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
    }

    fun toggleFixedPaid(expense: Expense, paid: Boolean) {
        // ... kept for compatibility if needed, but the main logic is now toggleFixedApplied
    }

    fun deleteFixedExpense(id: String) {
        viewModelScope.launch { try { repository.deleteFixedExpense(id) } catch (_: Exception) {} }
    }

}

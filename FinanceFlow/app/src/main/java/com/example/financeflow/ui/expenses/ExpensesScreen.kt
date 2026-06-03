package com.example.financeflow.ui.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeflow.viewmodel.expense.ExpenseViewModel
import kotlinx.coroutines.flow.collect
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import com.example.financeflow.ui.components.Expenses.*
import com.example.financeflow.ui.components.Expenses.getExpensesColors
import com.example.financeflow.ui.theme.FinanceFlowTheme

// ── Enums ─────────────────────────────────────────────────────────────────────

enum class ExpenseType { ESSENTIAL, DISCRETIONARY }

enum class PaymentMethod(val label: String) {
    CARD("Card"),
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer"),
    DIGITAL_WALLET("Digital Wallet")
}

// ── UI Data Classes ────────────────────────────────────────────────────────────

data class ExpenseUiItem(
    val id: Int,
    val categoryId: String,
    val description: String,
    val amount: Int,
    val paymentMethod: PaymentMethod,
    val date: String,
    val type: ExpenseType,
    val isRecurring: Boolean,
    val isPaid: Boolean = false,
    val notes: String = ""
    , val domainId: String = ""
)

data class RecurringUiItem(
    val id: Int,
    val name: String,
    val amount: Int,
    val categoryId: String,
    val frequency: String,
    val nextDue: String,
    val lastPaid: String,
    val isActive: Boolean,
    val missed: Boolean
)

data class CategoryDef(
    val id: String,
    val label: String,
    val emoji: String,
    val bgColor: Color,
    val parentId: String? = null,
    val parentLabel: String? = null
)

data class SuggestionUiItem(
    val description: String,
    val categoryId: String,
    val amount: Int,
    val paymentMethod: PaymentMethod,
    val count: Int,
    val badge: String
)

data class InsightUiItem(
    val emoji: String,
    val text: String
)

data class PatternStatUiItem(
    val emoji: String,
    val label: String,
    val value: String,
    val sub: String
)

data class CategoryBreakdownItem(
    val label: String,
    val amount: Int,
    val color: Color
)

data class WeeklyTrendItem(
    val label: String,
    val amount: Int
)

// ── Color Tokens ─────────────────────────────────────────────────────────────

object ExpenseColors {
    val AppBg          = Color(0xFFF8FAFC)
    val CardBg         = Color(0xFFFFFFFF)
    val HeaderRed      = Color(0xFFDC2626)
    val Primary        = Color(0xFF8B5CF6)
    val PrimaryLight   = Color(0xFFEEF2FF)
    val PrimaryBorder  = Color(0xFFC7D2FE)
    val PrimaryText    = Color(0xFF4F46E5)
    val Border         = Color(0xFFE5E7EB)
    val SurfaceGrey    = Color(0xFFF3F4F6)
    val TextPrimary    = Color(0xFF1F2937)
    val TextMuted      = Color(0xFF6B7280)
    val MustAmber      = Color(0xFFF59E0B)
    val MustBg         = Color(0xFFFFFBEB)
    val MustBorder     = Color(0xFFFDE68A)
    val MustText       = Color(0xFF92400E)
    val ExpenseRed     = Color(0xFFEF4444)
    val ExpenseBg      = Color(0xFFFEF2F2)
    val SuccessGreen   = Color(0xFF22C55E)
    val BlueCard       = Color(0xFFEFF6FF)
    val TealBg         = Color(0xFFF0FDFA)
    val PurpleBg       = Color(0xFFF5F3FF)
    val AmberWarning   = Color(0xFFFFFBEB)
}

// ── Category Tree & Helpers ──────────────────────────────────────────────────

val CAT_TREE: List<Pair<CategoryDef, List<CategoryDef>>> = listOf(
    CategoryDef("food",          "Food",          "🍕", Color(0xFFF97316)) to listOf(
        CategoryDef("food_coffee",    "Coffee",     "☕", Color(0xFFF59E0B), "food", "Food"),
        CategoryDef("food_dining",    "Dining Out", "🍽️", Color(0xFFF97316), "food", "Food"),
        CategoryDef("food_groceries", "Groceries",  "🛒", Color(0xFF22C55E), "food", "Food"),
    ),
    CategoryDef("shopping",      "Shopping",      "🛍️", Color(0xFFEC4899)) to emptyList(),
    CategoryDef("transport",     "Transport",     "🚗", Color(0xFF3B82F6)) to listOf(
        CategoryDef("transport_ride", "Ride Share", "🚖", Color(0xFF60A5FA), "transport", "Transport"),
    ),
    CategoryDef("rent",          "Housing",       "🏠", Color(0xFF8B5CF6)) to emptyList(),
    CategoryDef("bills",         "Bills",         "📱", Color(0xFF06B6D4)) to emptyList(),
    CategoryDef("health",        "Health",        "❤️", Color(0xFFEF4444)) to listOf(
        CategoryDef("health_gym",     "Gym",        "💪", Color(0xFFF87171), "health", "Health"),
    ),
    CategoryDef("entertainment", "Entertainment", "🎬", Color(0xFF6366F1)) to listOf(
        CategoryDef("entertainment_streaming", "Streaming", "📺",
            Color(0xFF8B5CF6), "entertainment", "Entertainment"),
    ),
    CategoryDef("other",         "Other",         "📦", Color(0xFF6B7280)) to emptyList(),
)

val ALL_CATS: List<CategoryDef> =
    CAT_TREE.flatMap { (parent, children) -> listOf(parent) + children }

val PARENT_CATS: List<CategoryDef> = CAT_TREE.map { it.first }

fun getCat(id: String): CategoryDef =
    ALL_CATS.find { it.id == id } ?: ALL_CATS.last()

fun getCatDisplayLabel(id: String): String {
    val cat = getCat(id)
    return if (cat.parentLabel != null) "${cat.parentLabel} › ${cat.label}"
    else cat.label
}

fun fmtLKR(amount: Int): String = "LKR ${"%,d".format(amount)}"

val TODAY: String get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())

fun relativeDate(dateStr: String, today: String): String {
    return when (dateStr) {
        today -> "Today"
        else  -> {
            val months = listOf("Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec")
            val parts = dateStr.split("-")
            val month = months.getOrNull((parts.getOrNull(1)?.toIntOrNull() ?: 1) - 1) ?: ""
            val day   = parts.getOrNull(2)?.toIntOrNull() ?: 0
            "$month $day"
        }
    }
}

const val DISCRETIONARY_BUDGET = 82300
const val DAILY_SAVINGS_RATE  = 1637

// ── Hardcoded Lists ──────────────────────────────────────────────────────────

val HARDCODED_EXPENSES = listOf(
    ExpenseUiItem(1,  "rent",                    "Monthly Rent",         34000, PaymentMethod.BANK_TRANSFER,  "2026-05-01", ExpenseType.ESSENTIAL,     true),
    ExpenseUiItem(2,  "bills",                   "Electricity Bill",     4500,  PaymentMethod.CARD,           "2026-05-02", ExpenseType.ESSENTIAL,     true),
    ExpenseUiItem(3,  "bills",                   "Internet - Dialog",    3200,  PaymentMethod.CARD,           "2026-05-02", ExpenseType.ESSENTIAL,     true),
    ExpenseUiItem(4,  "entertainment_streaming", "Netflix Subscription", 1490,  PaymentMethod.CARD,           "2026-05-03", ExpenseType.DISCRETIONARY, true),
    ExpenseUiItem(5,  "health_gym",              "Gym Membership",       3500,  PaymentMethod.CARD,           "2026-05-04", ExpenseType.DISCRETIONARY, true),
    ExpenseUiItem(6,  "food_coffee",             "Coffee - Barista",     550,   PaymentMethod.CASH,           "2026-05-05", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(7,  "food_dining",             "Lunch at Office Cafe", 850,   PaymentMethod.CARD,           "2026-05-07", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(8,  "transport_ride",          "PickMe to Office",     420,   PaymentMethod.CASH,           "2026-05-07", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(9,  "food_groceries",          "Groceries - Keells",   3200,  PaymentMethod.CARD,           "2026-05-08", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(10, "food_coffee",             "Coffee - Starbucks",   680,   PaymentMethod.CARD,           "2026-05-09", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(11, "food_coffee",             "Morning Coffee",       620,   PaymentMethod.CASH,           "2026-05-10", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(12, "transport_ride",          "Uber - Late Night",    880,   PaymentMethod.CARD,           "2026-05-11", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(13, "health",                  "Pharmacy - Vitamins",  2100,  PaymentMethod.CARD,           "2026-05-12", ExpenseType.ESSENTIAL,     false),
    ExpenseUiItem(14, "shopping",                "Clothing - H&M",       8500,  PaymentMethod.CARD,           "2026-05-13", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(15, "food_dining",             "Dinner - The Flame",   2800,  PaymentMethod.CARD,           "2026-05-14", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(16, "bills",                   "Mobile Top-Up",        500,   PaymentMethod.DIGITAL_WALLET, "2026-05-15", ExpenseType.ESSENTIAL,     false),
    ExpenseUiItem(17, "food_dining",             "Friday Team Lunch",    1200,  PaymentMethod.CASH,           "2026-05-16", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(18, "entertainment",           "Movie Tickets",        1800,  PaymentMethod.CARD,           "2026-05-17", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(19, "food_coffee",             "Coffee - Barista",     550,   PaymentMethod.CASH,           "2026-05-20", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(20, "transport_ride",          "PickMe to Office",     420,   PaymentMethod.CASH,           "2026-05-21", ExpenseType.DISCRETIONARY, false),
    ExpenseUiItem(21, "food_dining",             "Lunch at Office Cafe", 850,   PaymentMethod.CARD,           "2026-05-21", ExpenseType.DISCRETIONARY, false),
)

val HARDCODED_RECURRING = listOf(
    RecurringUiItem(1, "Monthly Rent",      34000, "rent",                    "Monthly", "2026-06-01", "2026-05-01", true, false),
    RecurringUiItem(2, "Netflix",           1490,  "entertainment_streaming", "Monthly", "2026-06-03", "2026-05-03", true, false),
    RecurringUiItem(3, "Gym Membership",    3500,  "health_gym",              "Monthly", "2026-06-04", "2026-05-04", true, false),
    RecurringUiItem(4, "Electricity Bill",  4500,  "bills",                   "Monthly", "2026-05-25", "2026-04-25", true, false),
    RecurringUiItem(5, "Internet - Dialog", 3200,  "bills",                   "Monthly", "2026-05-25", "2026-04-25", true, false),
    RecurringUiItem(6, "Spotify Premium",   990,   "entertainment_streaming", "Monthly", "2026-05-10", "2026-04-10", true, true),
)

val HARDCODED_SUGGESTIONS = listOf(
    SuggestionUiItem("Morning Coffee",       "food_coffee",    620, PaymentMethod.CASH, 3, "Frequent"),
    SuggestionUiItem("Lunch at Office Cafe", "food_dining",    850, PaymentMethod.CARD, 2, "Frequent"),
    SuggestionUiItem("PickMe to Office",     "transport_ride", 420, PaymentMethod.CASH, 2, "Daily"),
)

val HARDCODED_INSIGHTS = listOf(
    InsightUiItem("☕", "Coffee purchases (4×) are above your monthly average — LKR 2,400 spent"),
    InsightUiItem("🍽️", "Dining out spending (LKR 4,850) is higher than last month's average"),
    InsightUiItem("🛍️", "Shopping increased this month — LKR 8,500 spent on clothing & goods"),
)

val HARDCODED_CATEGORY_BREAKDOWN = listOf(
    CategoryBreakdownItem("Housing",       34000, Color(0xFF8B5CF6)),
    CategoryBreakdownItem("Bills",         8200,  Color(0xFF06B6D4)),
    CategoryBreakdownItem("Shopping",      8500,  Color(0xFFEC4899)),
    CategoryBreakdownItem("Food",          6250,  Color(0xFFF97316)),
    CategoryBreakdownItem("Health",        5600,  Color(0xFFEF4444)),
    CategoryBreakdownItem("Entertainment", 3290,  Color(0xFF6366F1)),
)

val HARDCODED_WEEKLY_TREND = listOf(
    WeeklyTrendItem("Wk 1", 47890),
    WeeklyTrendItem("Wk 2", 18730),
    WeeklyTrendItem("Wk 3", 5570),
    WeeklyTrendItem("Wk 4", 0),
)

val HARDCODED_PATTERN_STATS = listOf(
    PatternStatUiItem("📈", "Most spent category",    "Housing",     "LKR 34,000"),
    PatternStatUiItem("💳", "Top payment method",     "Card",        "Most used this month"),
    PatternStatUiItem("⬆️", "Largest single expense", "LKR 34,000",  "Monthly Rent"),
    PatternStatUiItem("📅", "Highest spending day",   "May 1",       "Peak activity"),
)

// ── ExpensesScreen Composable ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    isDarkTheme: Boolean = false,
    onAddExpenseClick: () -> Unit = {}
) {
    val viewModel: ExpenseViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    // use ViewModel state directly
    val selectedMonth = state.selectedMonth.ifBlank { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(java.util.Date()) }
    val expenses = state.currentMonthTransactions
    val recurringList = state.fixedPayments.map {
        RecurringUiItem(
            id = it.id.hashCode(),
            name = it.description.ifBlank { getCat(it.category).label },
            amount = it.amount.toInt(),
            categoryId = it.category,
            frequency = "Monthly",
            nextDue = "",
            lastPaid = "",
            isActive = true,
            missed = false
        )
    }

    // ── Form dialog ───────────────────────────────────────────
    var showFormDialog  by remember { mutableStateOf(false) }
    var isEditMode      by remember { mutableStateOf(false) }
    var editingDomainId       by remember { mutableStateOf<String?>(null) }
    var formAmount      by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formCategory    by remember { mutableStateOf("food_dining") }
    var formType        by remember { mutableStateOf(ExpenseType.DISCRETIONARY) }
    var formPayment     by remember { mutableStateOf(PaymentMethod.CARD) }
    var formDate        by remember { mutableStateOf(TODAY) }
    var formNotes       by remember { mutableStateOf("") }
    var formIsRecurring by remember { mutableStateOf(false) }

    // ── Delete dialog ─────────────────────────────────────────
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingDomainId  by remember { mutableStateOf<String?>(null) }
    var showPastDialog by remember { mutableStateOf(false) }

    // ── Row context menu ──────────────────────────────────────
    var openMenuId by remember { mutableStateOf<Int?>(null) }

    // ── Derived values ────────────────────────────────────────
    val monthExpenses       = expenses.filter { it.date.startsWith(selectedMonth) }
    val essentialTotal      = monthExpenses.filter { it.type == ExpenseType.ESSENTIAL }.sumOf { it.amount }
    val discretionaryTotal  = monthExpenses.filter { it.type == ExpenseType.DISCRETIONARY }.sumOf { it.amount }
    val totalSpent          = essentialTotal + discretionaryTotal
    val todayTotal          = expenses.filter { it.date == TODAY }.sumOf { it.amount }
    val remaining           = maxOf(0, DISCRETIONARY_BUDGET - discretionaryTotal)
    val budgetUsedPct       = minOf(100f, discretionaryTotal.toFloat() / DISCRETIONARY_BUDGET * 100f)
    val goalImpactDays      = formAmount.toFloatOrNull()
        ?.takeIf { it > 0 && formType == ExpenseType.DISCRETIONARY }
        ?.let { it / DAILY_SAVINGS_RATE }
    val todayExpenses       = expenses.filter { it.date == TODAY }
    val missedRecurring     = recurringList.filter { it.missed && it.isActive }
    val recentCategoryIds   = expenses.map { it.categoryId }.distinct().take(3)

    // ── Helper lambdas (defined here, passed to components) ───

    fun openAddForm(prefillCategoryId: String = "food_dining") {
        onAddExpenseClick()
    }

    fun openEditForm(exp: ExpenseUiItem) {
        formAmount = exp.amount.toString(); formDescription = exp.description
        formCategory = exp.categoryId; formType = exp.type
        formPayment = exp.paymentMethod; formDate = exp.date
        formNotes = exp.notes; formIsRecurring = exp.isRecurring
        isEditMode = true; editingDomainId = exp.domainId
        showFormDialog = true
    }

    fun confirmSave() {
        val amt = formAmount.toDoubleOrNull() ?: return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObj = try { sdf.parse(formDate) } catch (_: Exception) { null }
        val ts = if (dateObj != null) Timestamp(dateObj) else Timestamp.now()
        if (isEditMode) {
            // update existing using stored domain id
            val idStr = editingDomainId ?: ""
            if (formIsRecurring) {
                val fixed = com.example.financeflow.model.FixedExpense(
                    id = idStr,
                    name = formDescription.ifBlank { getCat(formCategory).label },
                    amount = amt,
                    category = formCategory
                )
                viewModel.addFixedExpense(fixed)
            } else {
                val exp = com.example.financeflow.model.Expense(
                    id = idStr,
                    amount = amt,
                    currency = "LKR",
                    category = formCategory,
                    description = formDescription,
                    notes = formNotes,
                    isFixed = formIsRecurring,
                    date = ts
                )
                viewModel.updateExpense(exp)
            }
        } else {
            if (formIsRecurring) {
                val fixed = com.example.financeflow.model.FixedExpense(
                    name = formDescription.ifBlank { getCat(formCategory).label },
                    amount = amt,
                    category = formCategory
                )
                viewModel.addFixedExpense(fixed)
            } else {
                val exp = com.example.financeflow.model.Expense(
                    amount = amt,
                    currency = "LKR",
                    category = formCategory,
                    description = formDescription,
                    notes = formNotes,
                    isFixed = formIsRecurring,
                    date = ts
                )
                viewModel.addExpense(exp)
            }
        }
        showFormDialog = false
    }

    fun confirmDelete() {
        // call viewModel to delete by domain id
        deletingDomainId?.let { idStr -> viewModel.deleteExpense(idStr) }
        showDeleteDialog = false; deletingDomainId = null
    }

    // ── Scaffold ──────────────────────────────────────────────
    val colors = getExpensesColors(isDarkTheme)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.AppBg)
        ) {
            // ── Fixed Header ─────────────────────────────────────────
            ExpenseHeader(
                selectedMonth = selectedMonth,
                onMonthChange = { m ->
                    // parse yyyy-MM -> set in viewmodel
                    val parts = m.split("-")
                    val y = parts.getOrNull(0)?.toIntOrNull() ?: java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                    val mo = parts.getOrNull(1)?.toIntOrNull() ?: (java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1)
                    viewModel.setSelectedMonth(y, mo)
                },
                onAddClick    = { openAddForm() },
                colors = colors,
                availableMonths = state.availableMonths
            )

            // ── Scrollable Content ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Warning banner ──────────────────────────────────
                if (budgetUsedPct >= 85f) {
                    ExpenseWarningBanner(
                        budgetUsedPct = budgetUsedPct,
                        remaining     = remaining,
                        isDarkTheme = isDarkTheme
                    )
                }

                // ── Overview Section ──────────────────────────────
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Total Expenses This Month card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.ExpenseBg)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Total Expenses This Month", fontWeight = FontWeight.Bold, color = colors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "LKR ${"%,.0f".format(state.totalExpense)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.ExpenseRed
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { onAddExpenseClick() }, colors = ButtonDefaults.buttonColors(containerColor = colors.HeaderRed)) {
                                Text("+ Add Expense", color = Color.White)
                            }
                        }
                    }
                    ExpenseQuickAdd(
                        isDarkTheme = isDarkTheme,
                        recentCategoryIds = recentCategoryIds,
                        onCategoryClick   = { catId -> openAddForm(catId) }
                    )
                    // Smart Suggestions removed per requirements
                    ExpenseTodayList(
                        isDarkTheme = isDarkTheme,
                        todayExpenses = state.todayExpenses,
                        openMenuId    = openMenuId,
                        onMenuToggle  = { id -> openMenuId = if (openMenuId == id) null else id },
                        onEdit        = { exp -> openEditForm(exp) },
                        onDelete      = { exp -> deletingDomainId = exp.domainId; showDeleteDialog = true }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // View Past Expenses button
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(onClick = { showPastDialog = true }) { Text("View Past Expenses") }
                }

                Spacer(Modifier.height(24.dp))

                // ── Recurring Section ─────────────────────────────
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Fixed Payments",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.TextPrimary
                        )
                    )
                    // Fixed payments list
                    state.fixedPayments.forEach { fe ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(fe.description.ifBlank { getCat(fe.category).label }, fontWeight = FontWeight.Bold, color = colors.TextPrimary)
                                Text(fmtLKR(fe.amount.toInt()), color = colors.TextMuted)
                            }
                            Switch(checked = fe.isPaid, onCheckedChange = { checked -> viewModel.toggleFixedPaid(fe, checked) })
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { /* open add fixed expense dialog - simplified to reuse add form */ openAddForm() }) { Text("+ Add") }
                    }
                    Spacer(Modifier.height(120.dp))
                }
            }
        }

        // ── Dialogs ───────────────────────────────────────────
        if (showFormDialog) {
            ExpenseFormDialog(
                isDarkTheme       = isDarkTheme,
                isEditMode        = isEditMode,
                amount            = formAmount,      onAmountChange      = { formAmount = it },
                description       = formDescription, onDescriptionChange = { formDescription = it },
                category          = formCategory,    onCategoryChange    = { formCategory = it },
                expenseType       = formType,        onTypeChange        = { formType = it },
                paymentMethod     = formPayment,     onPaymentChange     = { formPayment = it },
                date              = formDate,        onDateChange        = { formDate = it },
                notes             = formNotes,       onNotesChange       = { formNotes = it },
                isRecurring       = formIsRecurring, onRecurringChange   = { formIsRecurring = it },
                goalImpactDays    = goalImpactDays,
                catTree           = CAT_TREE,
                onConfirm         = { confirmSave() },
                onDismiss         = { showFormDialog = false }
            )
        }

        if (showDeleteDialog) {
            ExpenseDeleteDialog(
                isDarkTheme = isDarkTheme,
                onConfirm = { confirmDelete() },
                onDismiss = { showDeleteDialog = false; deletingDomainId = null }
            )
        }

        if (showPastDialog) {
            val dateOptions = state.currentMonthTransactions.map { it.date }.distinct().sortedDescending()
            var selectedPastDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "") }
            Dialog(
                onDismissRequest = { showPastDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colors.AppBg
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.HeaderRed)
                                .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Past Expenses",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showPastDialog = false }) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            if (dateOptions.isNotEmpty()) {
                                Text(
                                    "Select Date",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.TextMuted,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                var expanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedPastDate,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = colors.Primary,
                                            unfocusedBorderColor = colors.Border
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(colors.CardBg)
                                    ) {
                                        dateOptions.forEach { d ->
                                            DropdownMenuItem(
                                                text = { Text(d, fontSize = 14.sp) },
                                                onClick = { selectedPastDate = d; expanded = false }
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(24.dp))

                                val items = state.currentMonthTransactions.filter { it.date == selectedPastDate }
                                if (items.isEmpty()) {
                                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No expenses for this date", color = colors.TextMuted)
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items.forEach { item ->
                                            ExpenseItemRow(
                                                item = item,
                                                isMenuOpen = false,
                                                onMenuToggle = {},
                                                onEdit = {},
                                                onDelete = {},
                                                colors = colors
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No expenses found in this month", color = colors.TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    FinanceFlowTheme {
        ExpensesScreen()
    }
}
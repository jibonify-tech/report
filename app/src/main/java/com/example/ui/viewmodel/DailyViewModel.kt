package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AccountType
import com.example.data.model.BudgetPeriod
import com.example.data.model.CashDenomination
import com.example.data.model.CategoryBudget
import com.example.data.model.DailyNotification
import com.example.data.model.DailySession
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import com.example.data.model.DailyUser
import com.example.data.model.MoneyAccount
import com.example.data.model.OutsideSession
import com.example.data.model.Person
import com.example.data.model.RecurringFrequency
import com.example.data.model.RecurringRule
import com.example.data.model.SavingsGoal
import com.example.data.model.SessionStatus
import com.example.data.model.UserRole
import com.example.data.repository.DailyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class MainDailyTab {
    DASHBOARD,
    TODAY_HUB,
    TRANSACTIONS,
    ACCOUNTS_BUDGETS,
    PEOPLE_DEBT,
    REPORTS
}

enum class DateFilterType {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    ALL
}

enum class SheetType {
    NONE,
    QUICK_EXPENSE,
    QUICK_INCOME,
    TRANSFER,
    LEND,
    BORROW,
    REPAYMENT,
    START_DAY,
    LEAVE_HOME,
    CLOSE_DAY,
    DENOMINATIONS,
    ADD_ACCOUNT,
    EDIT_ACCOUNT,
    ADD_PERSON,
    ADD_BUDGET,
    ADD_SAVINGS_GOAL,
    ADJUST_SAVINGS_GOAL,
    TRANSACTION_DETAIL,
    EXPORT_SUMMARY,
    OUTSIDE_MODE
}

data class TodayTimelineItem(
    val id: String,
    val time: String,
    val title: String,
    val titleBn: String,
    val subtitle: String,
    val amount: Double? = null,
    val type: String, // "START_DAY", "OUTSIDE_START", "TRANSACTION", "CLOSE_DAY"
    val transaction: DailyTransaction? = null
)

data class DaySpending(
    val dayLabel: String,
    val dayLabelBn: String,
    val dateStr: String,
    val amount: Double
)

class DailyViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("jibonify_daily_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application)
    private val repository = DailyRepository(database)

    // Current date helpers
    val todayDateStr: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayFormattedDisplay: String
        get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date())

    // --- Authentication & User Isolation ---
    val allUsers: StateFlow<List<DailyUser>> = repository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSystemAccounts: StateFlow<List<MoneyAccount>> = repository.allAccountsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUserId = MutableStateFlow<String?>(
        prefs.getString("logged_in_user_id", "rahim")
    )
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _inspectingUserId = MutableStateFlow<String?>(null)
    val inspectingUserId: StateFlow<String?> = _inspectingUserId.asStateFlow()

    // The user who is formally logged in
    val loggedInUser: StateFlow<DailyUser?> = combine(_currentUserId, allUsers) { curId, users ->
        if (curId == null) null
        else users.find { it.id == curId } ?: repository.getUserById(curId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Effective user whose financial hisab is currently viewed
    // (If Admin is inspecting another user, effectiveUserId is that user; otherwise currentUserId)
    val effectiveUserId: StateFlow<String> = combine(_currentUserId, _inspectingUserId, allUsers) { curId, inspId, users ->
        val curUser = users.find { it.id == curId }
        if (curUser?.role == UserRole.ADMIN && inspId != null) {
            inspId
        } else {
            curId ?: "rahim"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "rahim")

    // Profile displayed in top bar & header
    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<DailyUser> = effectiveUserId.flatMapLatest { uid ->
        repository.getUserFlow(uid).map { it ?: DailyUser(id = uid) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyUser())

    // Financial accounts scoped exclusively to effective user
    @OptIn(ExperimentalCoroutinesApi::class)
    val accounts: StateFlow<List<MoneyAccount>> = effectiveUserId.flatMapLatest { uid ->
        repository.getAccountsFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeSession: StateFlow<DailySession?> = effectiveUserId.flatMapLatest { uid ->
        repository.getActiveSessionFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeOutsideSession: StateFlow<OutsideSession?> = effectiveUserId.flatMapLatest { uid ->
        repository.getActiveOutsideSessionFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTransactions: StateFlow<List<DailyTransaction>> = effectiveUserId.flatMapLatest { uid ->
        repository.getTransactionsFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val people: StateFlow<List<Person>> = effectiveUserId.flatMapLatest { uid ->
        repository.getPeopleFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val budgets: StateFlow<List<CategoryBudget>> = effectiveUserId.flatMapLatest { uid ->
        repository.getBudgetsFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val savingsGoals: StateFlow<List<SavingsGoal>> = effectiveUserId.flatMapLatest { uid ->
        repository.getSavingsGoalsFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recurringRules: StateFlow<List<RecurringRule>> = effectiveUserId.flatMapLatest { uid ->
        repository.getRecurringRulesFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<DailyNotification>> = effectiveUserId.flatMapLatest { uid ->
        repository.getNotificationsFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cashDenomination: StateFlow<CashDenomination?> = effectiveUserId.flatMapLatest { uid ->
        repository.getDenominationByDateFlow(uid, todayDateStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Calculated Balances (Reactive to Scoped Accounts) ---
    val totalBalance: StateFlow<Double> = accounts
        .map { accs -> accs.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashBalance: StateFlow<Double> = accounts
        .map { accs -> accs.filter { it.type == AccountType.CASH }.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val digitalBalance: StateFlow<Double> = accounts
        .map { accs -> accs.filter { it.type == AccountType.MOBILE_BANKING }.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val bankBalance: StateFlow<Double> = accounts
        .map { accs -> accs.filter { it.type == AccountType.BANK }.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cardBalance: StateFlow<Double> = accounts
        .map { accs -> accs.filter { it.type == AccountType.CARD }.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val receivableTotal: StateFlow<Double> = people
        .map { list -> list.sumOf { it.currentReceivable } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val payableTotal: StateFlow<Double> = people
        .map { list -> list.sumOf { it.currentPayable } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netWorth: StateFlow<Double> = combine(totalBalance, receivableTotal, payableTotal) { tot, rec, pay ->
        tot + rec - pay
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Today Hisab Metrics ---
    val todayTransactions: StateFlow<List<DailyTransaction>> = allTransactions
        .map { trxs -> trxs.filter { it.date == todayDateStr } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayIncome: StateFlow<Double> = todayTransactions
        .map { trxs ->
            trxs.filter {
                it.type == DailyTransactionType.INCOME || it.type == DailyTransactionType.REPAYMENT_RECEIVED
            }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayExpense: StateFlow<Double> = todayTransactions
        .map { trxs ->
            trxs.filter {
                it.type == DailyTransactionType.EXPENSE || it.type == DailyTransactionType.REPAYMENT_PAID
            }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTransfer: StateFlow<Double> = todayTransactions
        .map { trxs -> trxs.filter { it.type == DailyTransactionType.TRANSFER }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayLent: StateFlow<Double> = todayTransactions
        .map { trxs -> trxs.filter { it.type == DailyTransactionType.LEND }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayBorrowed: StateFlow<Double> = todayTransactions
        .map { trxs -> trxs.filter { it.type == DailyTransactionType.BORROW }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayMoneyTakenOutside: StateFlow<Double> = combine(activeOutsideSession, todayTransactions) { outSession, trxs ->
        val transferOutside = trxs.filter { it.isOutsideSession && it.type == DailyTransactionType.TRANSFER }.sumOf { it.amount }
        if (transferOutside > 0) transferOutside else (outSession?.amountTaken ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expectedCash: StateFlow<Double> = accounts
        .map { accs -> accs.filter { it.type == AccountType.CASH }.sumOf { it.currentBalance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val actualCash: StateFlow<Double> = cashDenomination
        .combine(expectedCash) { denom, expCash ->
            if (denom != null && denom.totalAmount > 0) denom.totalAmount else expCash
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashDifference: StateFlow<Double> = combine(actualCash, expectedCash) { actual, expected ->
        actual - expected
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Today Timeline
    val todayTimeline: StateFlow<List<TodayTimelineItem>> = combine(
        activeSession,
        activeOutsideSession,
        todayTransactions
    ) { session, outside, trxs ->
        val items = mutableListOf<TodayTimelineItem>()

        if (session != null) {
            val startTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(session.startTime))
            items.add(
                TodayTimelineItem(
                    id = "session_start_${session.id}",
                    time = startTimeStr,
                    title = "Started Day",
                    titleBn = "দিন শুরু হয়েছে",
                    subtitle = "Opening Balance: ৳${String.format(Locale.US, "%,.0f", session.openingBalance)}",
                    amount = session.openingBalance,
                    type = "START_DAY"
                )
            )

            if (session.status == SessionStatus.CLOSED && session.endTime != null) {
                val endTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(session.endTime))
                items.add(
                    TodayTimelineItem(
                        id = "session_close_${session.id}",
                        time = endTimeStr,
                        title = "Day Closed",
                        titleBn = "দিন সমাপ্ত হয়েছে",
                        subtitle = "Closing Balance: ৳${String.format(Locale.US, "%,.0f", session.closingBalance)}",
                        amount = session.closingBalance,
                        type = "CLOSE_DAY"
                    )
                )
            }
        }

        if (outside != null) {
            val outsideTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(outside.startTime))
            items.add(
                TodayTimelineItem(
                    id = "outside_start_${outside.id}",
                    time = outsideTimeStr,
                    title = "Left Home (${outside.location})",
                    titleBn = "বাইরে বের হয়েছেন (${outside.location})",
                    subtitle = "Carried ৳${String.format(Locale.US, "%,.0f", outside.amountTaken)} • ${outside.purpose}",
                    amount = outside.amountTaken,
                    type = "OUTSIDE_START"
                )
            )
        }

        trxs.forEach { trx ->
            val titleStr = when (trx.type) {
                DailyTransactionType.EXPENSE -> trx.description.ifEmpty { "${trx.category} Expense" }
                DailyTransactionType.INCOME -> trx.description.ifEmpty { "${trx.category} Income" }
                DailyTransactionType.TRANSFER -> "Transfer"
                DailyTransactionType.LEND -> "Lent to ${trx.personName ?: "Friend"}"
                DailyTransactionType.BORROW -> "Borrowed from ${trx.personName ?: "Friend"}"
                DailyTransactionType.REPAYMENT_RECEIVED -> "Received from ${trx.personName ?: "Friend"}"
                DailyTransactionType.REPAYMENT_PAID -> "Repaid to ${trx.personName ?: "Friend"}"
                DailyTransactionType.ADJUSTMENT -> "Balance Adjustment"
            }
            val titleBnStr = when (trx.type) {
                DailyTransactionType.EXPENSE -> trx.description.ifEmpty { "${trx.category} খরচ" }
                DailyTransactionType.INCOME -> trx.description.ifEmpty { "${trx.category} আয়" }
                DailyTransactionType.TRANSFER -> "স্থানান্তর"
                DailyTransactionType.LEND -> "হাওলাত দেওয়া হয়েছে"
                DailyTransactionType.BORROW -> "ধার নেওয়া হয়েছে"
                DailyTransactionType.REPAYMENT_RECEIVED -> "পরিশোধ প্রাপ্তি"
                DailyTransactionType.REPAYMENT_PAID -> "ধার পরিশোধ"
                DailyTransactionType.ADJUSTMENT -> "ব্যালেন্স সমন্বয়"
            }

            items.add(
                TodayTimelineItem(
                    id = "trx_${trx.id}",
                    time = trx.time,
                    title = titleStr,
                    titleBn = titleBnStr,
                    subtitle = "${trx.category} • ৳${String.format(Locale.US, "%,.0f", trx.amount)}",
                    amount = trx.amount,
                    type = "TRANSACTION",
                    transaction = trx
                )
            )
        }

        items.sortedBy { it.time }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search & Filters ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _dateFilter = MutableStateFlow(DateFilterType.ALL)
    val dateFilter: StateFlow<DateFilterType> = _dateFilter.asStateFlow()

    private val _typeFilter = MutableStateFlow<DailyTransactionType?>(null)
    val typeFilter: StateFlow<DailyTransactionType?> = _typeFilter.asStateFlow()

    private val _filterAccountId = MutableStateFlow<String?>(null)
    val filterAccountId: StateFlow<String?> = _filterAccountId.asStateFlow()

    val filteredTransactions: StateFlow<List<DailyTransaction>> = combine(
        allTransactions,
        _searchQuery,
        _dateFilter,
        _typeFilter,
        _filterAccountId
    ) { trxs, query, dFilter, tFilter, accId ->
        var list = trxs

        if (query.isNotBlank()) {
            val q = query.trim().lowercase(Locale.getDefault())
            list = list.filter {
                it.description.lowercase(Locale.getDefault()).contains(q) ||
                        it.category.lowercase(Locale.getDefault()).contains(q) ||
                        (it.personName?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                        it.location.lowercase(Locale.getDefault()).contains(q) ||
                        it.note.lowercase(Locale.getDefault()).contains(q)
            }
        }

        if (tFilter != null) {
            list = list.filter { it.type == tFilter }
        }

        if (accId != null) {
            list = list.filter { it.accountId == accId || it.toAccountId == accId }
        }

        when (dFilter) {
            DateFilterType.TODAY -> list = list.filter { it.date == todayDateStr }
            DateFilterType.YESTERDAY -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yestStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                list = list.filter { it.date == yestStr }
            }
            DateFilterType.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -7)
                val weekAgoStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                list = list.filter { it.date >= weekAgoStr }
            }
            DateFilterType.THIS_MONTH -> {
                val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
                list = list.filter { it.date.startsWith(monthPrefix) }
            }
            DateFilterType.ALL -> Unit
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI State ---
    private val _selectedTab = MutableStateFlow(MainDailyTab.DASHBOARD)
    val selectedTab: StateFlow<MainDailyTab> = _selectedTab.asStateFlow()

    private val _currentLanguage = MutableStateFlow(prefs.getString("app_language", "bn") ?: "bn")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _activeSheet = MutableStateFlow<SheetType?>(null)
    val activeSheet: StateFlow<SheetType?> = _activeSheet.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<DailyTransaction?>(null)
    val selectedTransaction: StateFlow<DailyTransaction?> = _selectedTransaction.asStateFlow()

    private val _selectedGoal = MutableStateFlow<SavingsGoal?>(null)
    val selectedGoal: StateFlow<SavingsGoal?> = _selectedGoal.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // --- Authentication & User Control Actions ---
    fun login(identifier: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(identifier, pass)
            if (user != null) {
                _currentUserId.value = user.id
                prefs.edit().putString("logged_in_user_id", user.id).apply()
                _inspectingUserId.value = null
                showMessage(if (currentLanguage.value == "bn") "স্বাগতম, ${user.name}!" else "Welcome back, ${user.name}!")
                onResult(true, null)
            } else {
                onResult(false, if (currentLanguage.value == "bn") "ফোন/ইমেইল অথবা পাসওয়ার্ড সঠিক নয়!" else "Invalid phone/email or password!")
            }
        }
    }

    fun quickLogin(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                _currentUserId.value = user.id
                prefs.edit().putString("logged_in_user_id", user.id).apply()
                _inspectingUserId.value = null
                showMessage(if (currentLanguage.value == "bn") "লগইন সফল: ${user.name} (${user.role})" else "Logged in as ${user.name} (${user.role})")
            }
        }
    }

    fun register(name: String, phone: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        if (name.isBlank() || phone.isBlank() || pass.isBlank()) {
            onResult(false, if (currentLanguage.value == "bn") "সকল প্রয়োজনীয় তথ্য পূরণ করুন" else "Please fill all required fields")
            return
        }
        viewModelScope.launch {
            try {
                val newUser = repository.registerUser(name, phone, email, pass, UserRole.USER)
                _currentUserId.value = newUser.id
                prefs.edit().putString("logged_in_user_id", newUser.id).apply()
                _inspectingUserId.value = null
                showMessage(if (currentLanguage.value == "bn") "একাউন্ট তৈরি সম্পন্ন হয়েছে! স্বাগতম, ${newUser.name}" else "Account created! Welcome, ${newUser.name}")
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun logout() {
        _currentUserId.value = null
        _inspectingUserId.value = null
        prefs.edit().remove("logged_in_user_id").apply()
        showMessage(if (currentLanguage.value == "bn") "সফলভাবে লগ আউট হয়েছে" else "Logged out successfully")
    }

    fun inspectUser(userId: String?) {
        val cur = loggedInUser.value
        if (cur?.role == UserRole.ADMIN) {
            _inspectingUserId.value = userId
            if (userId != null) {
                val target = allUsers.value.find { it.id == userId }
                showMessage(if (currentLanguage.value == "bn") "বর্তমানে [${target?.name ?: userId}]-এর হিসাব দেখা হচ্ছে" else "Inspecting [${target?.name ?: userId}]'s account")
            } else {
                showMessage(if (currentLanguage.value == "bn") "এডমিন ভিউতে ফিরে এসেছেন" else "Returned to Admin view")
            }
        }
    }

    fun toggleUserStatus(targetUser: DailyUser) {
        val cur = loggedInUser.value
        if (cur?.role == UserRole.ADMIN && targetUser.id != cur.id) {
            viewModelScope.launch {
                val updated = targetUser.copy(isActive = !targetUser.isActive)
                repository.updateUser(updated)
                showMessage(if (currentLanguage.value == "bn") "ইউজার ${if (updated.isActive) "সক্রিয়" else "স্থগিত"} করা হয়েছে" else "User status updated")
            }
        }
    }

    // --- Financial Actions (All strictly user-scoped to effectiveUserId) ---
    fun saveExpense(
        amount: Double,
        category: String,
        accountId: String,
        description: String,
        location: String = "",
        paymentMethod: String = "",
        note: String = "",
        isOutside: Boolean = false
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = DailyTransactionType.EXPENSE,
                amount = amount,
                accountId = accountId,
                category = category,
                date = todayDateStr,
                time = nowTime,
                description = description.ifEmpty { "$category Expense" },
                location = location,
                paymentMethod = paymentMethod,
                note = note,
                isOutsideSession = isOutside
            )
            repository.insertTransaction(trx)
            showMessage("Expense of ৳${String.format(Locale.US, "%,.0f", amount)} added!")
            closeSheet()
        }
    }

    fun saveIncome(
        amount: Double,
        category: String,
        accountId: String,
        description: String,
        personName: String = "",
        note: String = ""
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = DailyTransactionType.INCOME,
                amount = amount,
                accountId = accountId,
                category = category,
                date = todayDateStr,
                time = nowTime,
                description = description,
                personName = if (personName.isNotBlank()) personName else null,
                note = note
            )
            repository.insertTransaction(trx)
            showMessage("Income of ৳${String.format(Locale.US, "%,.0f", amount)} added!")
            closeSheet()
        }
    }

    fun saveTransfer(
        amount: Double,
        fromAccountId: String,
        toAccountId: String,
        note: String = ""
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        if (fromAccountId == toAccountId) {
            showMessage("Source and Destination accounts must be different")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = DailyTransactionType.TRANSFER,
                amount = amount,
                accountId = fromAccountId,
                toAccountId = toAccountId,
                category = "Transfer",
                date = todayDateStr,
                time = nowTime,
                description = "Account Transfer",
                note = note
            )
            repository.insertTransaction(trx)
            showMessage("Transferred ৳${String.format(Locale.US, "%,.0f", amount)} successfully!")
            closeSheet()
        }
    }

    fun saveLend(
        amount: Double,
        accountId: String,
        personId: Long,
        personName: String,
        dueDate: String = "",
        note: String = ""
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = DailyTransactionType.LEND,
                amount = amount,
                accountId = accountId,
                personId = personId,
                personName = personName,
                category = "Lending",
                date = todayDateStr,
                time = nowTime,
                description = "Lent to $personName",
                dueDate = dueDate,
                note = note
            )
            repository.insertTransaction(trx)
            showMessage("Recorded ৳${String.format(Locale.US, "%,.0f", amount)} lent to $personName")
            closeSheet()
        }
    }

    fun saveBorrow(
        amount: Double,
        accountId: String,
        personId: Long,
        personName: String,
        dueDate: String = "",
        note: String = ""
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = DailyTransactionType.BORROW,
                amount = amount,
                accountId = accountId,
                personId = personId,
                personName = personName,
                category = "Borrowing",
                date = todayDateStr,
                time = nowTime,
                description = "Borrowed from $personName",
                dueDate = dueDate,
                note = note
            )
            repository.insertTransaction(trx)
            showMessage("Recorded ৳${String.format(Locale.US, "%,.0f", amount)} borrowed from $personName")
            closeSheet()
        }
    }

    fun saveRepayment(
        amount: Double,
        accountId: String,
        personId: Long,
        personName: String,
        isReceivingFromLend: Boolean,
        note: String = ""
    ) {
        if (amount <= 0) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val trx = DailyTransaction(
                userId = effectiveUserId.value,
                type = if (isReceivingFromLend) DailyTransactionType.REPAYMENT_RECEIVED else DailyTransactionType.REPAYMENT_PAID,
                amount = amount,
                accountId = accountId,
                personId = personId,
                personName = personName,
                category = "Repayment",
                date = todayDateStr,
                time = nowTime,
                description = if (isReceivingFromLend) "Repayment from $personName" else "Repaid to $personName",
                note = note
            )
            repository.insertTransaction(trx)
            showMessage("Repayment of ৳${String.format(Locale.US, "%,.0f", amount)} recorded!")
            closeSheet()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            showMessage("Transaction deleted & balances updated")
            closeSheet()
        }
    }

    fun duplicateTransaction(trx: DailyTransaction) {
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val dup = trx.copy(
                id = 0,
                userId = effectiveUserId.value,
                date = todayDateStr,
                time = nowTime,
                createdAt = System.currentTimeMillis()
            )
            repository.insertTransaction(dup)
            showMessage("Duplicated transaction of ৳${String.format(Locale.US, "%,.0f", trx.amount)} for today!")
            closeSheet()
        }
    }

    fun startDay(budget: Double, note: String) {
        viewModelScope.launch {
            val uid = effectiveUserId.value
            val openBal = totalBalance.value
            repository.startDay(
                userId = uid,
                date = todayDateStr,
                openingBalance = openBal,
                plannedBudget = budget,
                note = note
            )
            showMessage("Day started with opening balance ৳${String.format(Locale.US, "%,.0f", openBal)}")
            closeSheet()
        }
    }

    fun closeDay(actualCashVal: Double, note: String, mood: String, tomorrowReminder: String) {
        viewModelScope.launch {
            val session = activeSession.value
            if (session == null) {
                showMessage("No active session to close!")
                return@launch
            }
            val expCash = expectedCash.value
            val closeBal = totalBalance.value
            val diff = actualCashVal - expCash
            repository.closeDay(
                session = session,
                actualCash = actualCashVal,
                closingBalance = closeBal,
                cashDifference = diff,
                note = note,
                mood = mood,
                tomorrowReminder = tomorrowReminder
            )
            showMessage("Day closed successfully. Hisab reconciled!")
            closeSheet()
        }
    }

    fun startOutsideSession(
        amountTaken: Double,
        sourceAccId: String,
        destAccId: String,
        location: String,
        purpose: String,
        expectedReturnTime: String,
        note: String
    ) {
        viewModelScope.launch {
            val uid = effectiveUserId.value
            repository.startOutsideSession(
                OutsideSession(
                    userId = uid,
                    date = todayDateStr,
                    amountTaken = amountTaken,
                    sourceAccountId = sourceAccId,
                    destinationAccountId = destAccId,
                    location = location,
                    purpose = purpose,
                    expectedReturnTime = expectedReturnTime,
                    notes = note
                )
            )
            showMessage("Outside session started! Outside mode active.")
            closeSheet()
        }
    }

    fun endOutsideSession() {
        viewModelScope.launch {
            val session = activeOutsideSession.value
            if (session != null) {
                repository.endOutsideSession(session)
                showMessage("Returned home! Outside session ended.")
            }
        }
    }

    fun saveDenominations(
        n1000: Int, n500: Int, n200: Int, n100: Int, n50: Int,
        n20: Int, n10: Int, n5: Int, n2: Int, n1: Int, note: String
    ) {
        val total = (n1000 * 1000) + (n500 * 500) + (n200 * 200) + (n100 * 100) +
                (n50 * 50) + (n20 * 20) + (n10 * 10) + (n5 * 5) + (n2 * 2) + (n1 * 1)

        viewModelScope.launch {
            val uid = effectiveUserId.value
            val denom = CashDenomination(
                id = "${uid}_$todayDateStr",
                userId = uid,
                date = todayDateStr,
                n1000 = n1000,
                n500 = n500,
                n200 = n200,
                n100 = n100,
                n50 = n50,
                n20 = n20,
                n10 = n10,
                n5 = n5,
                n2 = n2,
                n1 = n1,
                totalAmount = total.toDouble(),
                note = note
            )
            repository.saveCashDenominations(denom)
            showMessage("Cash notes counted: Total ৳${String.format(Locale.US, "%,.0f", total.toDouble())}")
            closeSheet()
        }
    }

    fun addAccount(
        name: String,
        nameBn: String,
        type: AccountType,
        openingBalance: Double,
        accountNumber: String,
        colorHex: Long,
        iconName: String
    ) {
        if (name.isBlank()) {
            showMessage("Account name cannot be empty")
            return
        }
        viewModelScope.launch {
            val uid = effectiveUserId.value
            val id = "${uid}_acc_" + System.currentTimeMillis()
            val acc = MoneyAccount(
                id = id,
                userId = uid,
                name = name,
                nameBn = if (nameBn.isNotBlank()) nameBn else name,
                type = type,
                openingBalance = openingBalance,
                currentBalance = openingBalance,
                accountNumber = accountNumber,
                colorHex = colorHex,
                iconName = iconName
            )
            repository.insertAccount(acc)
            showMessage("Account '$name' created!")
            closeSheet()
        }
    }

    fun deleteAccount(id: String) {
        viewModelScope.launch {
            repository.deleteAccount(id)
            showMessage("Account deleted!")
            closeSheet()
        }
    }

    fun addPerson(name: String, phone: String, email: String, notes: String) {
        if (name.isBlank()) {
            showMessage("Person name cannot be empty")
            return
        }
        viewModelScope.launch {
            val person = Person(
                userId = effectiveUserId.value,
                name = name,
                phone = phone,
                email = email,
                notes = notes
            )
            repository.insertPerson(person)
            showMessage("Added $name to people list")
            closeSheet()
        }
    }

    fun deletePerson(id: Long) {
        viewModelScope.launch {
            repository.deletePerson(id)
            showMessage("Person removed from list")
            closeSheet()
        }
    }

    fun addBudget(category: String, amount: Double, period: BudgetPeriod) {
        if (amount <= 0) {
            showMessage("Please enter valid budget amount")
            return
        }
        viewModelScope.launch {
            val b = CategoryBudget(
                userId = effectiveUserId.value,
                category = category,
                amount = amount,
                period = period
            )
            repository.insertBudget(b)
            showMessage("Budget set for $category")
            closeSheet()
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
            showMessage("Budget removed")
            closeSheet()
        }
    }

    fun addSavingsGoal(name: String, targetAmount: Double, targetDate: String, note: String) {
        if (name.isBlank() || targetAmount <= 0) {
            showMessage("Please fill goal details properly")
            return
        }
        viewModelScope.launch {
            val goal = SavingsGoal(
                userId = effectiveUserId.value,
                name = name,
                targetAmount = targetAmount,
                targetDate = targetDate,
                note = note
            )
            repository.insertSavingsGoal(goal)
            showMessage("Savings Goal '$name' created!")
            closeSheet()
        }
    }

    fun adjustGoalAmount(goal: SavingsGoal, amount: Double, isDeposit: Boolean, fromAccountId: String) {
        if (amount <= 0) return
        viewModelScope.launch {
            val newAmount = if (isDeposit) goal.currentAmount + amount else (goal.currentAmount - amount).coerceAtLeast(0.0)
            val updated = goal.copy(
                currentAmount = newAmount,
                isCompleted = newAmount >= goal.targetAmount
            )
            repository.updateSavingsGoal(updated)
            if (fromAccountId.isNotEmpty()) {
                val delta = if (isDeposit) -amount else amount
                saveExpense(
                    amount = amount,
                    category = "Savings",
                    accountId = fromAccountId,
                    description = if (isDeposit) "Deposit to ${goal.name}" else "Withdraw from ${goal.name}",
                    note = "Goal: ${goal.name}"
                )
            }
            showMessage(if (isDeposit) "Added ৳${String.format(Locale.US, "%,.0f", amount)} to ${goal.name}" else "Withdrew ৳${String.format(Locale.US, "%,.0f", amount)} from ${goal.name}")
            closeSheet()
        }
    }

    fun deleteSavingsGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(id)
            showMessage("Savings goal removed")
            closeSheet()
        }
    }

    fun executeRecurring(rule: RecurringRule) {
        viewModelScope.launch {
            saveExpense(
                amount = rule.amount,
                category = rule.category,
                accountId = rule.accountId,
                description = "Recurring: ${rule.title}",
                note = "Automated payment rule"
            )
            showMessage("Executed bill payment: ${rule.title}")
        }
    }

    fun deleteRecurringRule(id: Long) {
        viewModelScope.launch {
            repository.deleteRecurringRule(id)
            showMessage("Recurring rule deleted")
            closeSheet()
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead(effectiveUserId.value)
            showMessage("All notifications marked as read")
        }
    }

    fun updateUser(name: String, phone: String, monthlyBudget: Double) {
        viewModelScope.launch {
            val current = user.value
            val updated = current.copy(
                name = name,
                phone = phone,
                monthlyBudget = monthlyBudget
            )
            repository.updateUser(updated)
            showMessage("Profile updated successfully!")
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.setupDefaultAccountsForUser(effectiveUserId.value, user.value.name)
            showMessage("Default accounts restored!")
        }
    }

    // --- Search, Filter & Navigation Handlers ---
    fun selectTab(tab: MainDailyTab) {
        _selectedTab.value = tab
    }

    fun toggleLanguage() {
        val newLang = if (_currentLanguage.value == "bn") "en" else "bn"
        _currentLanguage.value = newLang
        prefs.edit().putString("app_language", newLang).apply()
    }

    fun setDarkMode(isDark: Boolean?) {
        _isDarkMode.value = isDark
    }

    fun openSheet(sheet: SheetType) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = null
        _selectedTransaction.value = null
        _selectedGoal.value = null
    }

    fun selectTransaction(transaction: DailyTransaction) {
        _selectedTransaction.value = transaction
        _activeSheet.value = SheetType.TRANSACTION_DETAIL
    }

    fun selectGoal(goal: SavingsGoal) {
        _selectedGoal.value = goal
        _activeSheet.value = SheetType.ADJUST_SAVINGS_GOAL
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDateFilter(filter: DateFilterType) {
        _dateFilter.value = filter
    }

    fun setTypeFilter(type: DailyTransactionType?) {
        _typeFilter.value = type
    }

    fun setFilterAccountId(accountId: String?) {
        _filterAccountId.value = accountId
    }

    private fun showMessage(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}

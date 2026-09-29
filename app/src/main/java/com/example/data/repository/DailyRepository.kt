package com.example.data.repository

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DailyRepository(private val database: AppDatabase) {

    private val userDao = database.dailyUserDao()
    private val accountDao = database.moneyAccountDao()
    private val sessionDao = database.dailySessionDao()
    private val outsideDao = database.outsideSessionDao()
    private val transactionDao = database.dailyTransactionDao()
    private val personDao = database.personDao()
    private val budgetDao = database.categoryBudgetDao()
    private val goalDao = database.savingsGoalDao()
    private val recurringDao = database.recurringRuleDao()
    private val denominationDao = database.cashDenominationDao()
    private val notificationDao = database.dailyNotificationDao()

    // --- Users ---
    val allUsersFlow: Flow<List<DailyUser>> = userDao.getAllUsersFlow()

    fun getUserFlow(userId: String): Flow<DailyUser?> = userDao.getUserFlow(userId)

    suspend fun getUserById(userId: String): DailyUser? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    suspend fun login(loginInput: String, passwordInput: String): DailyUser? = withContext(Dispatchers.IO) {
        val trimmed = loginInput.trim()
        val user = userDao.getUserByLogin(trimmed) ?: userDao.getUserById(trimmed)
        if (user != null && user.password == passwordInput.trim() && user.isActive) {
            user
        } else {
            null
        }
    }

    suspend fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: UserRole = UserRole.USER
    ): DailyUser = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim()
        val id = cleanPhone.replace(Regex("[^0-9]"), "").ifEmpty {
            cleanEmail.substringBefore("@").ifEmpty { "user_${UUID.randomUUID().toString().take(6)}" }
        }

        val newUser = DailyUser(
            id = id,
            name = name.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            password = password.trim(),
            role = role,
            monthlyBudget = 30000.0,
            city = "Dhaka, Bangladesh"
        )
        userDao.insertUser(newUser)

        // Setup default clean accounts for the new user
        setupDefaultAccountsForUser(id, name.trim())

        newUser
    }

    suspend fun updateUser(user: DailyUser) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
    }

    // --- User-Scoped Data Flows ---
    fun getAccountsFlow(userId: String): Flow<List<MoneyAccount>> =
        accountDao.getAccountsForUserFlow(userId)

    val allAccountsFlow: Flow<List<MoneyAccount>> =
        accountDao.getAllAccountsFlow()

    fun getActiveSessionFlow(userId: String): Flow<DailySession?> =
        sessionDao.getActiveSessionFlow(userId)

    fun getActiveOutsideSessionFlow(userId: String): Flow<OutsideSession?> =
        outsideDao.getActiveOutsideSessionFlow(userId)

    fun getTransactionsFlow(userId: String): Flow<List<DailyTransaction>> =
        transactionDao.getTransactionsForUserFlow(userId)

    val allTransactionsFlow: Flow<List<DailyTransaction>> =
        transactionDao.getAllTransactionsFlow()

    fun getTransactionsByDateFlow(userId: String, date: String): Flow<List<DailyTransaction>> =
        transactionDao.getTransactionsByDateFlow(userId, date)

    fun getPeopleFlow(userId: String): Flow<List<Person>> =
        personDao.getPeopleForUserFlow(userId)

    fun getBudgetsFlow(userId: String): Flow<List<CategoryBudget>> =
        budgetDao.getBudgetsForUserFlow(userId)

    fun getSavingsGoalsFlow(userId: String): Flow<List<SavingsGoal>> =
        goalDao.getSavingsGoalsForUserFlow(userId)

    fun getRecurringRulesFlow(userId: String): Flow<List<RecurringRule>> =
        recurringDao.getRecurringForUserFlow(userId)

    fun getDenominationByDateFlow(userId: String, date: String): Flow<CashDenomination?> =
        denominationDao.getDenominationByDateFlow(userId, date)

    fun getNotificationsFlow(userId: String): Flow<List<DailyNotification>> =
        notificationDao.getNotificationsForUserFlow(userId)

    // --- Transactions & Balance Adjustments ---
    suspend fun insertTransaction(transaction: DailyTransaction): Long = withContext(Dispatchers.IO) {
        val insertedId = transactionDao.insertTransaction(transaction)

        // Apply financial effect
        when (transaction.type) {
            DailyTransactionType.EXPENSE -> {
                accountDao.adjustBalance(transaction.accountId, -transaction.amount)
            }
            DailyTransactionType.INCOME -> {
                accountDao.adjustBalance(transaction.accountId, transaction.amount)
            }
            DailyTransactionType.TRANSFER -> {
                accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                transaction.toAccountId?.let { toAcc ->
                    accountDao.adjustBalance(toAcc, transaction.amount)
                }
            }
            DailyTransactionType.LEND -> {
                accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                transaction.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newLent = person.totalLent + transaction.amount
                        personDao.updatePerson(
                            person.copy(
                                totalLent = newLent,
                                currentReceivable = (newLent - person.totalReceived).coerceAtLeast(0.0),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            DailyTransactionType.BORROW -> {
                accountDao.adjustBalance(transaction.accountId, transaction.amount)
                transaction.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newBorrowed = person.totalBorrowed + transaction.amount
                        personDao.updatePerson(
                            person.copy(
                                totalBorrowed = newBorrowed,
                                currentPayable = (newBorrowed - person.totalRepaid).coerceAtLeast(0.0),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            DailyTransactionType.REPAYMENT_RECEIVED -> {
                accountDao.adjustBalance(transaction.accountId, transaction.amount)
                transaction.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newReceived = person.totalReceived + transaction.amount
                        personDao.updatePerson(
                            person.copy(
                                totalReceived = newReceived,
                                currentReceivable = (person.totalLent - newReceived).coerceAtLeast(0.0),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            DailyTransactionType.REPAYMENT_PAID -> {
                accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                transaction.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newRepaid = person.totalRepaid + transaction.amount
                        personDao.updatePerson(
                            person.copy(
                                totalRepaid = newRepaid,
                                currentPayable = (person.totalBorrowed - newRepaid).coerceAtLeast(0.0),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            DailyTransactionType.ADJUSTMENT -> {
                accountDao.adjustBalance(transaction.accountId, transaction.amount)
            }
        }

        insertedId
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        val existing = transactionDao.getTransactionById(id) ?: return@withContext
        when (existing.type) {
            DailyTransactionType.EXPENSE -> {
                accountDao.adjustBalance(existing.accountId, existing.amount)
            }
            DailyTransactionType.INCOME -> {
                accountDao.adjustBalance(existing.accountId, -existing.amount)
            }
            DailyTransactionType.TRANSFER -> {
                accountDao.adjustBalance(existing.accountId, existing.amount)
                existing.toAccountId?.let { toAcc ->
                    accountDao.adjustBalance(toAcc, -existing.amount)
                }
            }
            DailyTransactionType.LEND -> {
                accountDao.adjustBalance(existing.accountId, existing.amount)
                existing.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newLent = (person.totalLent - existing.amount).coerceAtLeast(0.0)
                        personDao.updatePerson(
                            person.copy(
                                totalLent = newLent,
                                currentReceivable = (newLent - person.totalReceived).coerceAtLeast(0.0)
                            )
                        )
                    }
                }
            }
            DailyTransactionType.BORROW -> {
                accountDao.adjustBalance(existing.accountId, -existing.amount)
                existing.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newBorrowed = (person.totalBorrowed - existing.amount).coerceAtLeast(0.0)
                        personDao.updatePerson(
                            person.copy(
                                totalBorrowed = newBorrowed,
                                currentPayable = (newBorrowed - person.totalRepaid).coerceAtLeast(0.0)
                            )
                        )
                    }
                }
            }
            DailyTransactionType.REPAYMENT_RECEIVED -> {
                accountDao.adjustBalance(existing.accountId, -existing.amount)
                existing.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newReceived = (person.totalReceived - existing.amount).coerceAtLeast(0.0)
                        personDao.updatePerson(
                            person.copy(
                                totalReceived = newReceived,
                                currentReceivable = (person.totalLent - newReceived).coerceAtLeast(0.0)
                            )
                        )
                    }
                }
            }
            DailyTransactionType.REPAYMENT_PAID -> {
                accountDao.adjustBalance(existing.accountId, existing.amount)
                existing.personId?.let { pId ->
                    val person = personDao.getPersonById(pId)
                    if (person != null) {
                        val newRepaid = (person.totalRepaid - existing.amount).coerceAtLeast(0.0)
                        personDao.updatePerson(
                            person.copy(
                                totalRepaid = newRepaid,
                                currentPayable = (person.totalBorrowed - newRepaid).coerceAtLeast(0.0)
                            )
                        )
                    }
                }
            }
            DailyTransactionType.ADJUSTMENT -> {
                accountDao.adjustBalance(existing.accountId, -existing.amount)
            }
        }
        transactionDao.deleteTransactionById(id)
    }

    // --- Accounts ---
    suspend fun insertAccount(account: MoneyAccount) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
    }

    suspend fun deleteAccount(id: String) = withContext(Dispatchers.IO) {
        accountDao.deleteAccountById(id)
    }

    // --- Day Sessions ---
    suspend fun startDay(
        userId: String,
        date: String,
        openingBalance: Double,
        plannedBudget: Double,
        note: String
    ): Long = withContext(Dispatchers.IO) {
        val session = DailySession(
            userId = userId,
            date = date,
            openingBalance = openingBalance,
            plannedBudget = plannedBudget,
            note = note,
            status = SessionStatus.ACTIVE,
            startTime = System.currentTimeMillis()
        )
        sessionDao.insertSession(session)
    }

    suspend fun closeDay(
        session: DailySession,
        actualCash: Double,
        closingBalance: Double,
        cashDifference: Double,
        note: String,
        mood: String,
        tomorrowReminder: String
    ) = withContext(Dispatchers.IO) {
        val updated = session.copy(
            status = SessionStatus.CLOSED,
            endTime = System.currentTimeMillis(),
            actualCash = actualCash,
            closingBalance = closingBalance,
            cashDifference = cashDifference,
            note = note,
            mood = mood,
            tomorrowReminder = tomorrowReminder,
            updatedAt = System.currentTimeMillis()
        )
        sessionDao.updateSession(updated)
    }

    // --- Outside Session ---
    suspend fun startOutsideSession(outsideSession: OutsideSession): Long = withContext(Dispatchers.IO) {
        outsideDao.insertOutsideSession(outsideSession)
    }

    suspend fun endOutsideSession(outsideSession: OutsideSession) = withContext(Dispatchers.IO) {
        val updated = outsideSession.copy(
            isActive = false,
            endTime = System.currentTimeMillis()
        )
        outsideDao.updateOutsideSession(updated)
    }

    // --- People & Debt ---
    suspend fun insertPerson(person: Person): Long = withContext(Dispatchers.IO) {
        personDao.insertPerson(person)
    }

    suspend fun deletePerson(id: Long) = withContext(Dispatchers.IO) {
        personDao.deletePersonById(id)
    }

    // --- Budgets ---
    suspend fun insertBudget(budget: CategoryBudget): Long = withContext(Dispatchers.IO) {
        budgetDao.insertBudget(budget)
    }

    suspend fun deleteBudget(id: Long) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudgetById(id)
    }

    // --- Savings Goals ---
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long = withContext(Dispatchers.IO) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateSavingsGoal(goal: SavingsGoal) = withContext(Dispatchers.IO) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteSavingsGoal(id: Long) = withContext(Dispatchers.IO) {
        goalDao.deleteGoalById(id)
    }

    // --- Recurring ---
    suspend fun insertRecurringRule(rule: RecurringRule): Long = withContext(Dispatchers.IO) {
        recurringDao.insertRecurring(rule)
    }

    suspend fun deleteRecurringRule(id: Long) = withContext(Dispatchers.IO) {
        recurringDao.deleteRecurringById(id)
    }

    // --- Cash Denominations ---
    suspend fun saveCashDenominations(denomination: CashDenomination) = withContext(Dispatchers.IO) {
        denominationDao.insertDenomination(denomination)
    }

    // --- Notifications ---
    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    // --- Seed Default Accounts Helper ---
    suspend fun setupDefaultAccountsForUser(userId: String, userName: String) = withContext(Dispatchers.IO) {
        val prefix = userId
        val defaultAccounts = listOf(
            MoneyAccount(
                id = "${prefix}_pocket_cash",
                userId = userId,
                name = "Pocket Cash",
                nameBn = "পকেট ক্যাশ",
                type = AccountType.CASH,
                openingBalance = 2000.0,
                currentBalance = 2000.0,
                accountNumber = "Pocket / পকেট",
                colorHex = 0xFF10B981,
                iconName = "pocket"
            ),
            MoneyAccount(
                id = "${prefix}_wallet",
                userId = userId,
                name = "Wallet",
                nameBn = "মানিব্যাগ",
                type = AccountType.CASH,
                openingBalance = 3500.0,
                currentBalance = 3500.0,
                accountNumber = "Leather Wallet",
                colorHex = 0xFFF59E0B,
                iconName = "wallet"
            ),
            MoneyAccount(
                id = "${prefix}_home_cash",
                userId = userId,
                name = "Home Cash",
                nameBn = "বাসার ক্যাশ",
                type = AccountType.CASH,
                openingBalance = 7000.0,
                currentBalance = 7000.0,
                accountNumber = "Home Safe Locker",
                colorHex = 0xFF059669,
                iconName = "home"
            ),
            MoneyAccount(
                id = "${prefix}_bkash",
                userId = userId,
                name = "bKash",
                nameBn = "বিকাশ",
                type = AccountType.MOBILE_BANKING,
                openingBalance = 8500.0,
                currentBalance = 8500.0,
                accountNumber = "Personal bKash",
                colorHex = 0xFFE11D48,
                iconName = "bkash"
            ),
            MoneyAccount(
                id = "${prefix}_nagad",
                userId = userId,
                name = "Nagad",
                nameBn = "নগদ",
                type = AccountType.MOBILE_BANKING,
                openingBalance = 4000.0,
                currentBalance = 4000.0,
                accountNumber = "Personal Nagad",
                colorHex = 0xFFEA580C,
                iconName = "nagad"
            ),
            MoneyAccount(
                id = "${prefix}_bank_account",
                userId = userId,
                name = "Bank Account",
                nameBn = "ব্যাংক অ্যাকাউন্ট",
                type = AccountType.BANK,
                openingBalance = 25000.0,
                currentBalance = 25000.0,
                accountNumber = "Savings AC",
                colorHex = 0xFF2563EB,
                iconName = "bank"
            )
        )
        accountDao.insertAccounts(defaultAccounts)

        // Seed sample budget for this user
        budgetDao.insertBudgets(
            listOf(
                CategoryBudget(userId = userId, category = "Food", period = BudgetPeriod.MONTHLY, amount = 8000.0),
                CategoryBudget(userId = userId, category = "Transport", period = BudgetPeriod.MONTHLY, amount = 4000.0),
                CategoryBudget(userId = userId, category = "Shopping", period = BudgetPeriod.MONTHLY, amount = 5000.0),
                CategoryBudget(userId = userId, category = "Bills", period = BudgetPeriod.MONTHLY, amount = 4500.0)
            )
        )

        // Seed sample savings goal
        goalDao.insertGoal(
            SavingsGoal(
                userId = userId,
                name = "Emergency Reserve",
                targetAmount = 50000.0,
                currentAmount = 15000.0,
                targetDate = "2027-01-01",
                note = "Safety emergency fund"
            )
        )

        // Seed sample notification
        notificationDao.insertNotification(
            DailyNotification(
                userId = userId,
                title = "Welcome to Jibonify Daily!",
                titleBn = "জীবনাইফি ডেইলিতে স্বাগতম!",
                message = "Your private financial account has been configured. Start your day and keep your daily hisab safe.",
                messageBn = "আপনার ব্যক্তিগত আর্থিক হিসাব সুরক্ষিতভাবে শুরু হয়েছে। প্রতিদিনের হিসাব রাখুন নিশ্চিন্তে।",
                type = "INFO"
            )
        )
    }

    // --- Initial System Seeding ---
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getAllUsersFlow().firstOrNull() ?: emptyList()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (existingUsers.isEmpty()) {
            // 1. Admin
            val adminUser = DailyUser(
                id = "admin",
                name = "System Admin",
                phone = "01700000000",
                email = "admin@jibonify.com",
                password = "admin",
                role = UserRole.ADMIN,
                occupation = "Administrator",
                city = "Dhaka Central"
            )

            // 2. User Rahim
            val rahimUser = DailyUser(
                id = "rahim",
                name = "Rahim Ahmed",
                phone = "01711111111",
                email = "rahim@jibonify.com",
                password = "1234",
                role = UserRole.USER,
                occupation = "Business & Freelance",
                city = "Dhanmondi, Dhaka"
            )

            // 3. User Karim
            val karimUser = DailyUser(
                id = "karim",
                name = "Karim Uddin",
                phone = "01822222222",
                email = "karim@jibonify.com",
                password = "1234",
                role = UserRole.USER,
                occupation = "Software Engineer",
                city = "Uttara, Dhaka"
            )

            userDao.insertUsers(listOf(adminUser, rahimUser, karimUser))

            // Setup Accounts for Rahim
            setupDefaultAccountsForUser("rahim", "Rahim Ahmed")
            // Rahim starter transaction
            transactionDao.insertTransaction(
                DailyTransaction(
                    userId = "rahim",
                    type = DailyTransactionType.INCOME,
                    amount = 35000.0,
                    accountId = "rahim_bank_account",
                    category = "Salary",
                    date = todayStr,
                    time = "09:00",
                    description = "Monthly Salary Credited",
                    note = "Salary direct transfer"
                )
            )
            transactionDao.insertTransaction(
                DailyTransaction(
                    userId = "rahim",
                    type = DailyTransactionType.EXPENSE,
                    amount = 180.0,
                    accountId = "rahim_pocket_cash",
                    category = "Food",
                    date = todayStr,
                    time = "10:30",
                    description = "Morning Breakfast & Coffee",
                    location = "Dhanmondi",
                    isOutsideSession = true
                )
            )
            sessionDao.insertSession(
                DailySession(
                    userId = "rahim",
                    date = todayStr,
                    openingBalance = 50000.0,
                    plannedBudget = 1500.0,
                    note = "Rahim's active day session",
                    status = SessionStatus.ACTIVE,
                    startTime = System.currentTimeMillis()
                )
            )
            personDao.insertPeople(
                listOf(
                    Person(
                        userId = "rahim",
                        name = "Tanvir Ahmed",
                        phone = "01819-223344",
                        notes = "Colleague",
                        totalLent = 4000.0,
                        totalReceived = 0.0,
                        currentReceivable = 4000.0
                    ),
                    Person(
                        userId = "rahim",
                        name = "Sabbir Hossain",
                        phone = "01711-889900",
                        notes = "Borrowed for laptop bag",
                        totalBorrowed = 1500.0,
                        totalRepaid = 0.0,
                        currentPayable = 1500.0
                    )
                )
            )

            // Setup Accounts for Karim (completely isolated!)
            setupDefaultAccountsForUser("karim", "Karim Uddin")
            transactionDao.insertTransaction(
                DailyTransaction(
                    userId = "karim",
                    type = DailyTransactionType.INCOME,
                    amount = 42000.0,
                    accountId = "karim_bank_account",
                    category = "Freelance",
                    date = todayStr,
                    time = "11:00",
                    description = "Client Milestone Payment",
                    note = "Mobile app project payment"
                )
            )
            transactionDao.insertTransaction(
                DailyTransaction(
                    userId = "karim",
                    type = DailyTransactionType.EXPENSE,
                    amount = 350.0,
                    accountId = "karim_wallet",
                    category = "Transport",
                    date = todayStr,
                    time = "12:15",
                    description = "Uber Ride to Office",
                    location = "Uttara",
                    isOutsideSession = true
                )
            )
            sessionDao.insertSession(
                DailySession(
                    userId = "karim",
                    date = todayStr,
                    openingBalance = 49500.0,
                    plannedBudget = 2000.0,
                    note = "Karim's workday session",
                    status = SessionStatus.ACTIVE,
                    startTime = System.currentTimeMillis()
                )
            )
            personDao.insertPeople(
                listOf(
                    Person(
                        userId = "karim",
                        name = "Habib Bhai",
                        phone = "01999-112233",
                        notes = "Gym Partner",
                        totalLent = 2500.0,
                        totalReceived = 500.0,
                        currentReceivable = 2000.0
                    )
                )
            )

            // Setup Admin Accounts as well
            setupDefaultAccountsForUser("admin", "System Admin")
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DailyTransactionType
import com.example.ui.screens.AccountsBudgetsScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.DailyDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OutsideModeScreen
import com.example.ui.screens.PeopleDebtScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TodayHubScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.components.DailyBottomNav
import com.example.ui.screens.components.DailySpeedDialFab
import com.example.ui.screens.components.DailyTopBar
import com.example.ui.screens.dialogs.AddAccountSheet
import com.example.ui.screens.dialogs.AddBudgetSheet
import com.example.ui.screens.dialogs.AddPersonSheet
import com.example.ui.screens.dialogs.AddSavingsGoalSheet
import com.example.ui.screens.dialogs.AdjustSavingsGoalSheet
import com.example.ui.screens.dialogs.CloseDaySheet
import com.example.ui.screens.dialogs.DenominationsSheet
import com.example.ui.screens.dialogs.ExportSummarySheet
import com.example.ui.screens.dialogs.LeaveHomeSheet
import com.example.ui.screens.dialogs.LendBorrowSheet
import com.example.ui.screens.dialogs.QuickExpenseSheet
import com.example.ui.screens.dialogs.QuickIncomeSheet
import com.example.ui.screens.dialogs.RepaymentSheet
import com.example.ui.screens.dialogs.StartDaySheet
import com.example.ui.screens.dialogs.TransactionDetailSheet
import com.example.ui.screens.dialogs.TransferSheet
import com.example.ui.theme.JibonifyTheme
import com.example.ui.viewmodel.DailyViewModel
import com.example.ui.viewmodel.MainDailyTab
import com.example.ui.viewmodel.SheetType
import java.util.Locale

enum class AppSubScreen {
    LOGIN,
    MAIN,
    OUTSIDE_MODE,
    NOTIFICATIONS,
    SETTINGS,
    ADMIN_PANEL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: DailyViewModel = viewModel()
            val isDarkThemePref by viewModel.isDarkMode.collectAsState()
            val darkTheme = isDarkThemePref ?: isSystemInDarkTheme()

            JibonifyTheme(darkTheme = darkTheme) {
                JibonifyDailyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun JibonifyDailyApp(viewModel: DailyViewModel) {
    var currentSubScreen by remember { mutableStateOf(AppSubScreen.MAIN) }

    // Authentication & multi-user state
    val currentUserId by viewModel.currentUserId.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val inspectingUserId by viewModel.inspectingUserId.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allSystemAccounts by viewModel.allSystemAccounts.collectAsState()

    // Profile & User-Scoped financial entities
    val user by viewModel.user.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val activeOutsideSession by viewModel.activeOutsideSession.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val people by viewModel.people.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()
    val recurringRules by viewModel.recurringRules.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val cashDenomination by viewModel.cashDenomination.collectAsState()

    // Calculated balances
    val totalBalance by viewModel.totalBalance.collectAsState()
    val cashBalance by viewModel.cashBalance.collectAsState()
    val digitalBalance by viewModel.digitalBalance.collectAsState()
    val bankBalance by viewModel.bankBalance.collectAsState()
    val cardBalance by viewModel.cardBalance.collectAsState()
    val receivableTotal by viewModel.receivableTotal.collectAsState()
    val payableTotal by viewModel.payableTotal.collectAsState()
    val netWorth by viewModel.netWorth.collectAsState()

    // Today metrics
    val todayIncome by viewModel.todayIncome.collectAsState()
    val todayExpense by viewModel.todayExpense.collectAsState()
    val todayTransfer by viewModel.todayTransfer.collectAsState()
    val todayLent by viewModel.todayLent.collectAsState()
    val todayBorrowed by viewModel.todayBorrowed.collectAsState()
    val todayMoneyTakenOutside by viewModel.todayMoneyTakenOutside.collectAsState()
    val expectedCash by viewModel.expectedCash.collectAsState()
    val actualCash by viewModel.actualCash.collectAsState()
    val cashDifference by viewModel.cashDifference.collectAsState()
    val todayTimeline by viewModel.todayTimeline.collectAsState()

    // UI state
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val activeSheet by viewModel.activeSheet.collectAsState()
    val selectedTransaction by viewModel.selectedTransaction.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val filterAccountId by viewModel.filterAccountId.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val unreadNotifsCount = notifications.count { !it.isRead }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    // If no user is logged in, show Login & Registration screen
    if (currentUserId == null) {
        LoginScreen(
            language = currentLanguage,
            onLanguageToggle = { viewModel.toggleLanguage() },
            onLogin = { ident, pass, callback ->
                viewModel.login(ident, pass, callback)
            },
            onRegister = { name, phone, email, pass, callback ->
                viewModel.register(name, phone, email, pass, callback)
            },
            onQuickLogin = { uid ->
                viewModel.quickLogin(uid)
            }
        )
        return
    }

    BackHandler(enabled = activeSheet != null || currentSubScreen != AppSubScreen.MAIN || selectedTab != MainDailyTab.DASHBOARD) {
        if (activeSheet != null) {
            viewModel.closeSheet()
        } else if (currentSubScreen != AppSubScreen.MAIN) {
            currentSubScreen = AppSubScreen.MAIN
        } else if (selectedTab != MainDailyTab.DASHBOARD) {
            viewModel.selectTab(MainDailyTab.DASHBOARD)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentSubScreen == AppSubScreen.MAIN) {
                DailySpeedDialFab(
                    language = currentLanguage,
                    onOpenSheet = { sheet ->
                        if (sheet == SheetType.OUTSIDE_MODE) {
                            currentSubScreen = AppSubScreen.OUTSIDE_MODE
                        } else {
                            viewModel.openSheet(sheet)
                        }
                    }
                )
            }
        },
        topBar = {
            if (currentSubScreen == AppSubScreen.MAIN) {
                DailyTopBar(
                    user = user,
                    loggedInUser = loggedInUser,
                    inspectingUserId = inspectingUserId,
                    activeOutsideSession = activeOutsideSession,
                    language = currentLanguage,
                    unreadNotificationsCount = unreadNotifsCount,
                    onSearchClick = {
                        viewModel.selectTab(MainDailyTab.TRANSACTIONS)
                    },
                    onNotificationsClick = {
                        currentSubScreen = AppSubScreen.NOTIFICATIONS
                    },
                    onOutsideModeClick = {
                        currentSubScreen = AppSubScreen.OUTSIDE_MODE
                    },
                    onLanguageToggle = {
                        viewModel.toggleLanguage()
                    },
                    onSettingsClick = {
                        currentSubScreen = AppSubScreen.SETTINGS
                    },
                    onAdminPanelClick = {
                        currentSubScreen = AppSubScreen.ADMIN_PANEL
                    },
                    onLogoutClick = {
                        viewModel.logout()
                        currentSubScreen = AppSubScreen.MAIN
                    }
                )
            }
        },
        bottomBar = {
            if (currentSubScreen == AppSubScreen.MAIN) {
                DailyBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) },
                    language = currentLanguage
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSubScreen) {
                AppSubScreen.MAIN -> {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(120))
                        },
                        label = "main_tabs"
                    ) { tab ->
                        when (tab) {
                            MainDailyTab.DASHBOARD -> {
                                DailyDashboardScreen(
                                    language = currentLanguage,
                                    totalBalance = totalBalance,
                                    cashBalance = cashBalance,
                                    digitalBalance = digitalBalance,
                                    bankBalance = bankBalance,
                                    cardBalance = cardBalance,
                                    receivableTotal = receivableTotal,
                                    payableTotal = payableTotal,
                                    netWorth = netWorth,
                                    accounts = accounts,
                                    activeSession = activeSession,
                                    activeOutsideSession = activeOutsideSession,
                                    todayIncome = todayIncome,
                                    todayExpense = todayExpense,
                                    todayTransfer = todayTransfer,
                                    todayLent = todayLent,
                                    todayBorrowed = todayBorrowed,
                                    todayMoneyTakenOutside = todayMoneyTakenOutside,
                                    expectedCash = expectedCash,
                                    actualCash = actualCash,
                                    cashDifference = cashDifference,
                                    timelineItems = todayTimeline,
                                    onOpenSheet = { sheet ->
                                        if (sheet == SheetType.OUTSIDE_MODE) {
                                            currentSubScreen = AppSubScreen.OUTSIDE_MODE
                                        } else {
                                            viewModel.openSheet(sheet)
                                        }
                                    },
                                    onSelectAccount = { acc ->
                                        viewModel.setFilterAccountId(acc.id)
                                        viewModel.selectTab(MainDailyTab.TRANSACTIONS)
                                    },
                                    onSelectTimelineItem = { trx ->
                                        viewModel.selectTransaction(trx)
                                    },
                                    onNavigateToTodayHub = {
                                        viewModel.selectTab(MainDailyTab.TODAY_HUB)
                                    }
                                )
                            }
                            MainDailyTab.TODAY_HUB -> {
                                TodayHubScreen(
                                    language = currentLanguage,
                                    todayFormatted = viewModel.todayFormattedDisplay,
                                    activeSession = activeSession,
                                    activeOutsideSession = activeOutsideSession,
                                    todayIncome = todayIncome,
                                    todayExpense = todayExpense,
                                    todayTransfer = todayTransfer,
                                    todayLent = todayLent,
                                    todayBorrowed = todayBorrowed,
                                    todayMoneyTakenOutside = todayMoneyTakenOutside,
                                    expectedCash = expectedCash,
                                    actualCash = actualCash,
                                    cashDifference = cashDifference,
                                    cashDenomination = cashDenomination,
                                    timelineItems = todayTimeline,
                                    onOpenSheet = { sheet ->
                                        if (sheet == SheetType.OUTSIDE_MODE) {
                                            currentSubScreen = AppSubScreen.OUTSIDE_MODE
                                        } else {
                                            viewModel.openSheet(sheet)
                                        }
                                    },
                                    onEndOutsideSession = { viewModel.endOutsideSession() },
                                    onSelectTransaction = { trx -> viewModel.selectTransaction(trx) }
                                )
                            }
                            MainDailyTab.TRANSACTIONS -> {
                                TransactionsScreen(
                                    language = currentLanguage,
                                    transactions = filteredTransactions,
                                    accounts = accounts,
                                    searchQuery = searchQuery,
                                    dateFilter = dateFilter,
                                    typeFilter = typeFilter,
                                    filterAccountId = filterAccountId,
                                    onSearchChange = { q -> viewModel.setSearchQuery(q) },
                                    onDateFilterChange = { df -> viewModel.setDateFilter(df) },
                                    onTypeFilterChange = { tf -> viewModel.setTypeFilter(tf) },
                                    onAccountFilterChange = { af -> viewModel.setFilterAccountId(af) },
                                    onSelectTransaction = { trx -> viewModel.selectTransaction(trx) },
                                    onOpenSheet = { sheet -> viewModel.openSheet(sheet) }
                                )
                            }
                            MainDailyTab.ACCOUNTS_BUDGETS -> {
                                AccountsBudgetsScreen(
                                    language = currentLanguage,
                                    accounts = accounts,
                                    budgets = budgets,
                                    savingsGoals = savingsGoals,
                                    recurringRules = recurringRules,
                                    transactions = allTransactions,
                                    onSelectAccount = { acc ->
                                        viewModel.setFilterAccountId(acc.id)
                                        viewModel.selectTab(MainDailyTab.TRANSACTIONS)
                                    },
                                    onDeleteAccount = { id -> viewModel.deleteAccount(id) },
                                    onDeleteBudget = { id -> viewModel.deleteBudget(id) },
                                    onSelectGoal = { g -> viewModel.selectGoal(g) },
                                    onDeleteGoal = { id -> viewModel.deleteSavingsGoal(id) },
                                    onExecuteRecurring = { r -> viewModel.executeRecurring(r) },
                                    onDeleteRecurring = { id -> viewModel.deleteRecurringRule(id) },
                                    onOpenSheet = { sheet -> viewModel.openSheet(sheet) }
                                )
                            }
                            MainDailyTab.PEOPLE_DEBT -> {
                                PeopleDebtScreen(
                                    language = currentLanguage,
                                    people = people,
                                    receivableTotal = receivableTotal,
                                    payableTotal = payableTotal,
                                    onSelectPerson = { p ->
                                        viewModel.setSearchQuery(p.name)
                                        viewModel.selectTab(MainDailyTab.TRANSACTIONS)
                                    },
                                    onDeletePerson = { id -> viewModel.deletePerson(id) },
                                    onOpenSheet = { sheet -> viewModel.openSheet(sheet) }
                                )
                            }
                            MainDailyTab.REPORTS -> {
                                ReportsScreen(
                                    language = currentLanguage,
                                    transactions = allTransactions
                                )
                            }
                        }
                    }
                }

                AppSubScreen.OUTSIDE_MODE -> {
                    val spentOutside = allTransactions.filter { it.isOutsideSession && it.type == DailyTransactionType.EXPENSE }.sumOf { it.amount }
                    val receivedOutside = allTransactions.filter { it.isOutsideSession && it.type == DailyTransactionType.INCOME }.sumOf { it.amount }
                    OutsideModeScreen(
                        language = currentLanguage,
                        outsideSession = activeOutsideSession,
                        moneyCarried = todayMoneyTakenOutside,
                        moneySpentOutside = spentOutside,
                        moneyReceivedOutside = receivedOutside,
                        remainingCash = (todayMoneyTakenOutside - spentOutside + receivedOutside).coerceAtLeast(0.0),
                        onBackClick = { currentSubScreen = AppSubScreen.MAIN },
                        onOpenSheet = { sheet -> viewModel.openSheet(sheet) },
                        onEndJourney = {
                            viewModel.endOutsideSession()
                            currentSubScreen = AppSubScreen.MAIN
                        }
                    )
                }

                AppSubScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        language = currentLanguage,
                        onBackClick = { currentSubScreen = AppSubScreen.MAIN },
                        onMarkRead = { id -> viewModel.markNotificationRead(id) },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() }
                    )
                }

                AppSubScreen.SETTINGS -> {
                    SettingsScreen(
                        language = currentLanguage,
                        user = user,
                        loggedInUser = loggedInUser,
                        isDarkMode = isDarkMode,
                        onLanguageChange = { lang ->
                            if (lang != currentLanguage) viewModel.toggleLanguage()
                        },
                        onDarkModeChange = { dark -> viewModel.setDarkMode(dark) },
                        onUpdateProfile = { name, phone, budget ->
                            viewModel.updateUser(name, phone, budget)
                        },
                        onExportReport = {
                            viewModel.openSheet(SheetType.EXPORT_SUMMARY)
                        },
                        onResetData = {
                            viewModel.resetToSampleData()
                        },
                        onAdminPanelClick = {
                            currentSubScreen = AppSubScreen.ADMIN_PANEL
                        },
                        onLogoutClick = {
                            viewModel.logout()
                            currentSubScreen = AppSubScreen.MAIN
                        },
                        onBackClick = { currentSubScreen = AppSubScreen.MAIN }
                    )
                }

                AppSubScreen.ADMIN_PANEL -> {
                    AdminPanelScreen(
                        language = currentLanguage,
                        currentUser = loggedInUser ?: user,
                        allUsers = allUsers,
                        allAccounts = allSystemAccounts,
                        inspectingUserId = inspectingUserId,
                        onInspectUser = { uid ->
                            viewModel.inspectUser(uid)
                        },
                        onToggleUserStatus = { u ->
                            viewModel.toggleUserStatus(u)
                        },
                        onNavigateToDashboard = {
                            currentSubScreen = AppSubScreen.MAIN
                            viewModel.selectTab(MainDailyTab.DASHBOARD)
                        },
                        onLogoutClick = {
                            viewModel.logout()
                            currentSubScreen = AppSubScreen.MAIN
                        },
                        onBackClick = { currentSubScreen = AppSubScreen.MAIN }
                    )
                }

                AppSubScreen.LOGIN -> Unit
            }
        }
    }

    // Modal Bottom Sheets for actions
    when (activeSheet) {
        SheetType.QUICK_EXPENSE -> {
            QuickExpenseSheet(
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, cat, accId, desc, loc, note ->
                    viewModel.saveExpense(amt, cat, accId, desc, loc, "", note, isOutside = currentSubScreen == AppSubScreen.OUTSIDE_MODE)
                }
            )
        }
        SheetType.QUICK_INCOME -> {
            QuickIncomeSheet(
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, cat, accId, desc, person, note ->
                    viewModel.saveIncome(amt, cat, accId, desc, person, note)
                }
            )
        }
        SheetType.TRANSFER -> {
            TransferSheet(
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, fromId, toId, note ->
                    viewModel.saveTransfer(amt, fromId, toId, note)
                }
            )
        }
        SheetType.LEND -> {
            LendBorrowSheet(
                isLend = true,
                people = people,
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, accId, pId, pName, dueDate, note ->
                    viewModel.saveLend(amt, accId, pId, pName, dueDate, note)
                }
            )
        }
        SheetType.BORROW -> {
            LendBorrowSheet(
                isLend = false,
                people = people,
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, accId, pId, pName, dueDate, note ->
                    viewModel.saveBorrow(amt, accId, pId, pName, dueDate, note)
                }
            )
        }
        SheetType.REPAYMENT -> {
            RepaymentSheet(
                people = people,
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { amt, accId, pId, pName, isRec, note ->
                    viewModel.saveRepayment(amt, accId, pId, pName, isRec, note)
                }
            )
        }
        SheetType.START_DAY -> {
            StartDaySheet(
                dateFormatted = viewModel.todayFormattedDisplay,
                totalBalance = totalBalance,
                cashBalance = cashBalance,
                digitalBalance = digitalBalance,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onStart = { budget, note ->
                    viewModel.startDay(budget, note)
                }
            )
        }
        SheetType.LEAVE_HOME -> {
            LeaveHomeSheet(
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onStartOutside = { amt, src, dst, loc, purp, ret, note ->
                    viewModel.startOutsideSession(amt, src, dst, loc, purp, ret, note)
                    currentSubScreen = AppSubScreen.OUTSIDE_MODE
                }
            )
        }
        SheetType.CLOSE_DAY -> {
            CloseDaySheet(
                activeSession = activeSession,
                expectedCash = expectedCash,
                actualCash = actualCash,
                todayIncome = todayIncome,
                todayExpense = todayExpense,
                todayTransfer = todayTransfer,
                todayLent = todayLent,
                todayBorrowed = todayBorrowed,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onCloseDay = { actual, note, mood, rem ->
                    viewModel.closeDay(actual, note, mood, rem)
                }
            )
        }
        SheetType.DENOMINATIONS -> {
            DenominationsSheet(
                initialDenom = cashDenomination,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { n1k, n500, n200, n100, n50, n20, n10, n5, n2, n1, note ->
                    viewModel.saveDenominations(n1k, n500, n200, n100, n50, n20, n10, n5, n2, n1, note)
                }
            )
        }
        SheetType.ADD_ACCOUNT -> {
            AddAccountSheet(
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { name, nameBn, type, bal, num, col, icon ->
                    viewModel.addAccount(name, nameBn, type, bal, num, col, icon)
                }
            )
        }
        SheetType.ADD_PERSON -> {
            AddPersonSheet(
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { name, phone, email, notes ->
                    viewModel.addPerson(name, phone, email, notes)
                }
            )
        }
        SheetType.ADD_BUDGET -> {
            AddBudgetSheet(
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { cat, amt, per ->
                    viewModel.addBudget(cat, amt, per)
                }
            )
        }
        SheetType.ADD_SAVINGS_GOAL -> {
            AddSavingsGoalSheet(
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onSave = { name, target, date, note ->
                    viewModel.addSavingsGoal(name, target, date, note)
                }
            )
        }
        SheetType.ADJUST_SAVINGS_GOAL -> {
            AdjustSavingsGoalSheet(
                goal = selectedGoal,
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onAdjust = { g, amt, isDep, accId ->
                    viewModel.adjustGoalAmount(g, amt, isDep, accId)
                }
            )
        }
        SheetType.TRANSACTION_DETAIL -> {
            TransactionDetailSheet(
                transaction = selectedTransaction,
                accounts = accounts,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() },
                onDelete = { id -> viewModel.deleteTransaction(id) },
                onDuplicate = { trx -> viewModel.duplicateTransaction(trx) }
            )
        }
        SheetType.EXPORT_SUMMARY -> {
            val summaryText = buildString {
                appendLine("=== JIBONIFY DAILY FINANCIAL REPORT ===")
                appendLine("Date: ${viewModel.todayFormattedDisplay}")
                appendLine("User: ${user.name} (${user.phone})")
                appendLine("---------------------------------------")
                appendLine("TOTAL BALANCE: ৳${String.format(Locale.US, "%,.0f", totalBalance)}")
                appendLine("  • Cash: ৳${String.format(Locale.US, "%,.0f", cashBalance)}")
                appendLine("  • Digital: ৳${String.format(Locale.US, "%,.0f", digitalBalance)}")
                appendLine("  • Bank: ৳${String.format(Locale.US, "%,.0f", bankBalance)}")
                appendLine("  • Card: ৳${String.format(Locale.US, "%,.0f", cardBalance)}")
                appendLine("RECEIVABLE (পাওনা): ৳${String.format(Locale.US, "%,.0f", receivableTotal)}")
                appendLine("PAYABLE (দেনা): ৳${String.format(Locale.US, "%,.0f", payableTotal)}")
                appendLine("NET WORTH: ৳${String.format(Locale.US, "%,.0f", netWorth)}")
                appendLine("---------------------------------------")
                appendLine("TODAY'S HISAB:")
                appendLine("  • Opening: ৳${String.format(Locale.US, "%,.0f", activeSession?.openingBalance ?: 0.0)}")
                appendLine("  • Income: +৳${String.format(Locale.US, "%,.0f", todayIncome)}")
                appendLine("  • Expense: -৳${String.format(Locale.US, "%,.0f", todayExpense)}")
                appendLine("  • Transfer: ৳${String.format(Locale.US, "%,.0f", todayTransfer)}")
                appendLine("  • Lent: ৳${String.format(Locale.US, "%,.0f", todayLent)}")
                appendLine("  • Borrowed: ৳${String.format(Locale.US, "%,.0f", todayBorrowed)}")
                appendLine("  • Expected Cash: ৳${String.format(Locale.US, "%,.0f", expectedCash)}")
                appendLine("  • Actual Cash: ৳${String.format(Locale.US, "%,.0f", actualCash)}")
                appendLine("  • Cash Difference: ৳${String.format(Locale.US, "%,.0f", cashDifference)}")
                appendLine("=======================================")
                appendLine("Generated by Jibonify Daily")
            }

            ExportSummarySheet(
                summaryText = summaryText,
                language = currentLanguage,
                onDismiss = { viewModel.closeSheet() }
            )
        }
        else -> Unit
    }
}

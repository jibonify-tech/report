package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    USER,
    ADMIN
}

@Entity(tableName = "daily_user")
data class DailyUser(
    @PrimaryKey val id: String = "rahim",
    val name: String = "Rahim Ahmed",
    val phone: String = "01711111111",
    val email: String = "rahim@jibonify.com",
    val password: String = "1234",
    val role: UserRole = UserRole.USER,
    val currencySymbol: String = "৳",
    val monthlyBudget: Double = 35000.0,
    val avatarUrl: String = "",
    val occupation: String = "Business & Freelancing",
    val city: String = "Dhaka, Bangladesh",
    val isPro: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AccountType {
    CASH,
    MOBILE_BANKING,
    BANK,
    CARD,
    OTHER
}

@Entity(tableName = "money_accounts")
data class MoneyAccount(
    @PrimaryKey val id: String,
    val userId: String = "rahim",
    val name: String,
    val nameBn: String,
    val type: AccountType,
    val openingBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val accountNumber: String = "",
    val colorHex: Long = 0xFF10B981,
    val iconName: String = "wallet",
    val isActive: Boolean = true,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

enum class SessionStatus {
    ACTIVE,
    CLOSED
}

@Entity(tableName = "daily_sessions")
data class DailySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val date: String, // YYYY-MM-DD
    val openingBalance: Double,
    val openingAccountsJson: String = "", // serialized snapshot
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val plannedBudget: Double = 0.0,
    val closingBalance: Double = 0.0,
    val expectedCash: Double = 0.0,
    val actualCash: Double = 0.0,
    val cashDifference: Double = 0.0,
    val note: String = "",
    val mood: String = "NORMAL", // HAPPY, NORMAL, STRESSED, BUSY
    val tomorrowReminder: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "outside_sessions")
data class OutsideSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val date: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isActive: Boolean = true,
    val amountTaken: Double = 0.0,
    val sourceAccountId: String = "pocket_cash",
    val destinationAccountId: String = "wallet",
    val location: String = "City Center",
    val purpose: String = "Shopping & Daily errands",
    val expectedReturnTime: String = "19:00",
    val notes: String = ""
)

enum class DailyTransactionType {
    EXPENSE,
    INCOME,
    TRANSFER,
    LEND,
    BORROW,
    REPAYMENT_RECEIVED,
    REPAYMENT_PAID,
    ADJUSTMENT
}

@Entity(tableName = "daily_transactions")
data class DailyTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val type: DailyTransactionType,
    val amount: Double,
    val accountId: String, // Source account
    val toAccountId: String? = null, // Destination account for TRANSFER
    val category: String, // Food, Transport, Shopping, Fuel, Salary, etc.
    val personId: Long? = null,
    val personName: String? = null,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = "",
    val location: String = "",
    val paymentMethod: String = "",
    val note: String = "",
    val receiptNote: String = "",
    val dueDate: String = "",
    val isOutsideSession: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val name: String,
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val totalLent: Double = 0.0,
    val totalReceived: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val totalRepaid: Double = 0.0,
    val currentReceivable: Double = 0.0, // totalLent - totalReceived
    val currentPayable: Double = 0.0, // totalBorrowed - totalRepaid
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BudgetPeriod {
    DAILY,
    WEEKLY,
    MONTHLY
}

@Entity(tableName = "budgets")
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val category: String,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val amount: Double,
    val startDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: String = "",
    val note: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

@Entity(tableName = "recurring_rules")
data class RecurringRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val title: String,
    val type: DailyTransactionType = DailyTransactionType.EXPENSE,
    val amount: Double,
    val category: String,
    val accountId: String,
    val frequency: RecurringFrequency = RecurringFrequency.MONTHLY,
    val nextDueDate: String = "",
    val note: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "cash_denominations")
data class CashDenomination(
    @PrimaryKey val id: String, // "${userId}_${date}"
    val userId: String = "rahim",
    val date: String, // YYYY-MM-DD
    val n1000: Int = 0,
    val n500: Int = 0,
    val n200: Int = 0,
    val n100: Int = 0,
    val n50: Int = 0,
    val n20: Int = 0,
    val n10: Int = 0,
    val n5: Int = 0,
    val n2: Int = 0,
    val n1: Int = 0,
    val totalAmount: Double = 0.0,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_notifications")
data class DailyNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "rahim",
    val title: String,
    val titleBn: String,
    val message: String,
    val messageBn: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "INFO", // ALERT, INFO, REMINDER, CLOSING
    val isRead: Boolean = false
)

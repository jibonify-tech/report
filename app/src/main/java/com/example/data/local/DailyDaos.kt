package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CashDenomination
import com.example.data.model.CategoryBudget
import com.example.data.model.DailyNotification
import com.example.data.model.DailySession
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyUser
import com.example.data.model.MoneyAccount
import com.example.data.model.OutsideSession
import com.example.data.model.Person
import com.example.data.model.RecurringRule
import com.example.data.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUserDao {
    @Query("SELECT * FROM daily_user ORDER BY name ASC")
    fun getAllUsersFlow(): Flow<List<DailyUser>>

    @Query("SELECT * FROM daily_user WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String): Flow<DailyUser?>

    @Query("SELECT * FROM daily_user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): DailyUser?

    @Query("SELECT * FROM daily_user WHERE phone = :login OR email = :login OR id = :login LIMIT 1")
    suspend fun getUserByLogin(login: String): DailyUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: DailyUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<DailyUser>)

    @Update
    suspend fun updateUser(user: DailyUser)

    @Query("DELETE FROM daily_user WHERE id = :id")
    suspend fun deleteUser(id: String)
}

@Dao
interface MoneyAccountDao {
    @Query("SELECT * FROM money_accounts WHERE userId = :userId AND isActive = 1 ORDER BY type ASC, currentBalance DESC")
    fun getAccountsForUserFlow(userId: String): Flow<List<MoneyAccount>>

    @Query("SELECT * FROM money_accounts WHERE isActive = 1 ORDER BY type ASC, currentBalance DESC")
    fun getAllAccountsFlow(): Flow<List<MoneyAccount>>

    @Query("SELECT * FROM money_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): MoneyAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: MoneyAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<MoneyAccount>)

    @Update
    suspend fun updateAccount(account: MoneyAccount)

    @Query("UPDATE money_accounts SET currentBalance = currentBalance + :delta, updatedAt = :timestamp WHERE id = :id")
    suspend fun adjustBalance(id: String, delta: Double, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM money_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)
}

@Dao
interface DailySessionDao {
    @Query("SELECT * FROM daily_sessions WHERE userId = :userId AND status = 'ACTIVE' ORDER BY id DESC LIMIT 1")
    fun getActiveSessionFlow(userId: String): Flow<DailySession?>

    @Query("SELECT * FROM daily_sessions WHERE userId = :userId AND date = :date ORDER BY id DESC LIMIT 1")
    suspend fun getSessionByDate(userId: String, date: String): DailySession?

    @Query("SELECT * FROM daily_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getAllSessionsFlow(userId: String): Flow<List<DailySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DailySession): Long

    @Update
    suspend fun updateSession(session: DailySession)
}

@Dao
interface OutsideSessionDao {
    @Query("SELECT * FROM outside_sessions WHERE userId = :userId AND isActive = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveOutsideSessionFlow(userId: String): Flow<OutsideSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutsideSession(session: OutsideSession): Long

    @Update
    suspend fun updateOutsideSession(session: OutsideSession)
}

@Dao
interface DailyTransactionDao {
    @Query("SELECT * FROM daily_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUserFlow(userId: String): Flow<List<DailyTransaction>>

    @Query("SELECT * FROM daily_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<DailyTransaction>>

    @Query("SELECT * FROM daily_transactions WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getTransactionsByDateFlow(userId: String, date: String): Flow<List<DailyTransaction>>

    @Query("SELECT * FROM daily_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): DailyTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: DailyTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<DailyTransaction>)

    @Update
    suspend fun updateTransaction(transaction: DailyTransaction)

    @Query("DELETE FROM daily_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

@Dao
interface PersonDao {
    @Query("SELECT * FROM people WHERE userId = :userId ORDER BY name ASC")
    fun getPeopleForUserFlow(userId: String): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: Long): Person?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(people: List<Person>)

    @Update
    suspend fun updatePerson(person: Person)

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun deletePersonById(id: Long)
}

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY amount DESC")
    fun getBudgetsForUserFlow(userId: String): Flow<List<CategoryBudget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: CategoryBudget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<CategoryBudget>)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Long)
}

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSavingsGoalsForUserFlow(userId: String): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<SavingsGoal>)

    @Update
    suspend fun updateGoal(goal: SavingsGoal)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)
}

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules WHERE userId = :userId AND isActive = 1 ORDER BY nextDueDate ASC")
    fun getRecurringForUserFlow(userId: String): Flow<List<RecurringRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurring(rule: RecurringRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringList(rules: List<RecurringRule>)

    @Update
    suspend fun updateRecurring(rule: RecurringRule)

    @Query("DELETE FROM recurring_rules WHERE id = :id")
    suspend fun deleteRecurringById(id: Long)
}

@Dao
interface CashDenominationDao {
    @Query("SELECT * FROM cash_denominations WHERE userId = :userId AND date = :date LIMIT 1")
    fun getDenominationByDateFlow(userId: String, date: String): Flow<CashDenomination?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDenomination(denomination: CashDenomination)
}

@Dao
interface DailyNotificationDao {
    @Query("SELECT * FROM daily_notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUserFlow(userId: String): Flow<List<DailyNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: DailyNotification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<DailyNotification>)

    @Query("UPDATE daily_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE daily_notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

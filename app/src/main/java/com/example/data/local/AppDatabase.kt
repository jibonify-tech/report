package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        DailyUser::class,
        MoneyAccount::class,
        DailySession::class,
        OutsideSession::class,
        DailyTransaction::class,
        Person::class,
        CategoryBudget::class,
        SavingsGoal::class,
        RecurringRule::class,
        CashDenomination::class,
        DailyNotification::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyUserDao(): DailyUserDao
    abstract fun moneyAccountDao(): MoneyAccountDao
    abstract fun dailySessionDao(): DailySessionDao
    abstract fun outsideSessionDao(): OutsideSessionDao
    abstract fun dailyTransactionDao(): DailyTransactionDao
    abstract fun personDao(): PersonDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun cashDenominationDao(): CashDenominationDao
    abstract fun dailyNotificationDao(): DailyNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jibonify_daily_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

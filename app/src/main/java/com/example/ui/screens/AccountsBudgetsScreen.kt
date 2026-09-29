package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountType
import com.example.data.model.CategoryBudget
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import com.example.data.model.MoneyAccount
import com.example.data.model.RecurringRule
import com.example.data.model.SavingsGoal
import com.example.ui.viewmodel.SheetType
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AccountsBudgetsScreen(
    language: String,
    accounts: List<MoneyAccount>,
    budgets: List<CategoryBudget>,
    savingsGoals: List<SavingsGoal>,
    recurringRules: List<RecurringRule>,
    transactions: List<DailyTransaction>,
    onSelectAccount: (MoneyAccount) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onDeleteBudget: (Long) -> Unit,
    onSelectGoal: (SavingsGoal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onExecuteRecurring: (RecurringRule) -> Unit,
    onDeleteRecurring: (Long) -> Unit,
    onOpenSheet: (SheetType) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        if (language == "bn") "অ্যাকাউন্ট" else "Accounts",
        if (language == "bn") "বাজেট" else "Budgets",
        if (language == "bn") "সঞ্চয় লক্ষ্য" else "Goals",
        if (language == "bn") "নিয়মিত খরচ" else "Recurring"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedSubTab) {
            0 -> AccountsSubTab(
                language = language,
                accounts = accounts,
                onSelectAccount = onSelectAccount,
                onDeleteAccount = onDeleteAccount,
                onAddAccountClick = { onOpenSheet(SheetType.ADD_ACCOUNT) }
            )
            1 -> BudgetsSubTab(
                language = language,
                budgets = budgets,
                transactions = transactions,
                onDeleteBudget = onDeleteBudget,
                onAddBudgetClick = { onOpenSheet(SheetType.ADD_BUDGET) }
            )
            2 -> SavingsGoalsSubTab(
                language = language,
                savingsGoals = savingsGoals,
                onSelectGoal = onSelectGoal,
                onDeleteGoal = onDeleteGoal,
                onAddGoalClick = { onOpenSheet(SheetType.ADD_SAVINGS_GOAL) }
            )
            3 -> RecurringSubTab(
                language = language,
                recurringRules = recurringRules,
                onExecuteRecurring = onExecuteRecurring,
                onDeleteRecurring = onDeleteRecurring
            )
        }
    }
}

@Composable
fun AccountsSubTab(
    language: String,
    accounts: List<MoneyAccount>,
    onSelectAccount: (MoneyAccount) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onAddAccountClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "সমস্ত অ্যাকাউন্ট (${accounts.size})" else "All Accounts (${accounts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onAddAccountClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == "bn") "নতুন অ্যাকাউন্ট" else "Add Account", fontSize = 12.sp)
                }
            }
        }

        items(accounts, key = { it.id }) { acc ->
            val icon = when (acc.type) {
                AccountType.CASH -> Icons.Default.AccountBalanceWallet
                AccountType.MOBILE_BANKING -> Icons.Default.Payments
                AccountType.BANK -> Icons.Default.AccountBalance
                AccountType.CARD -> Icons.Default.CreditCard
                AccountType.OTHER -> Icons.Default.AttachMoney
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAccount(acc) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(acc.colorHex).copy(alpha = 0.15f)
                        ) {
                            Icon(
                                icon,
                                contentDescription = acc.name,
                                tint = Color(acc.colorHex),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (language == "bn") acc.nameBn else acc.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${acc.type.name} • ${acc.accountNumber}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", acc.currentBalance)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetsSubTab(
    language: String,
    budgets: List<CategoryBudget>,
    transactions: List<DailyTransaction>,
    onDeleteBudget: (Long) -> Unit,
    onAddBudgetClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "bn") "ক্যাটেগরি বাজেট ব্যবস্থাপনা" else "Category Budget Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == "bn") "মাসিক ব্যয়ের সীমা ও অগ্রগতি" else "Monthly spending limits & progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddBudgetClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == "bn") "বাজেট যোগ" else "Add Budget", fontSize = 12.sp)
                }
            }
        }

        items(budgets, key = { it.id }) { b ->
            val spent = transactions.filter {
                it.type == DailyTransactionType.EXPENSE && it.category.equals(b.category, ignoreCase = true)
            }.sumOf { it.amount }

            val progress = if (b.amount > 0) (spent / b.amount).toFloat().coerceIn(0f, 1.2f) else 0f
            val percent = (progress * 100).toInt()
            val remaining = (b.amount - spent).coerceAtLeast(0.0)

            val alertColor = when {
                percent >= 100 -> Color(0xFFEF4444)
                percent >= 80 -> Color(0xFFF59E0B)
                else -> Color(0xFF10B981)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = b.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = "${b.period.name} Budget",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = alertColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$percent%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = alertColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(onClick = { onDeleteBudget(b.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress.coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = alertColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Spent: ৳${String.format(Locale.US, "%,.0f", spent)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Limit: ৳${String.format(Locale.US, "%,.0f", b.amount)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsGoalsSubTab(
    language: String,
    savingsGoals: List<SavingsGoal>,
    onSelectGoal: (SavingsGoal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onAddGoalClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "bn") "সঞ্চয় লক্ষ্য (Savings Goals)" else "Savings Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == "bn") "ভবিষ্যতের লক্ষ্য অর্জনে সঞ্চয় করুন" else "Track your progress towards goals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddGoalClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == "bn") "নতুন লক্ষ্য" else "Add Goal", fontSize = 12.sp)
                }
            }
        }

        items(savingsGoals, key = { it.id }) { goal ->
            val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
            val percent = (progress * 100).toInt()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectGoal(goal) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Savings, contentDescription = "Savings", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = goal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (goal.targetDate.isNotEmpty()) {
                                    Text(
                                        text = "Target: ${goal.targetDate}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$percent%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Saved: ৳${String.format(Locale.US, "%,.0f", goal.currentAmount)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Target: ৳${String.format(Locale.US, "%,.0f", goal.targetAmount)}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onSelectGoal(goal) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(if (language == "bn") "+ টাকা জমা / তুলুন" else "Deposit / Withdraw", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecurringSubTab(
    language: String,
    recurringRules: List<RecurringRule>,
    onExecuteRecurring: (RecurringRule) -> Unit,
    onDeleteRecurring: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (language == "bn") "নিয়মিত খরচ ও বিল (Recurring Bills)" else "Recurring Bills & Commitments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (language == "bn") "বাড়ি ভাড়া, ইন্টারনেট বিল, বেতন ইত্যাদি নিয়মিত লেনদেন ১-ট্যাপে রেকর্ড করুন।"
                    else "Rent, WiFi, Subscriptions. Record with 1 tap when due.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(recurringRules, key = { it.id }) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = rule.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "${rule.category} • ${rule.frequency.name} • Due: ${rule.nextDueDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", rule.amount)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { onExecuteRecurring(rule) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Record")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == "bn") "পরিশোধ" else "Pay & Record", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

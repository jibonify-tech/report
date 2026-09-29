package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountType
import com.example.data.model.DailySession
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import com.example.data.model.MoneyAccount
import com.example.data.model.OutsideSession
import com.example.data.model.SessionStatus
import com.example.ui.viewmodel.SheetType
import com.example.ui.viewmodel.TodayTimelineItem
import java.util.Locale

@Composable
fun DailyDashboardScreen(
    language: String,
    totalBalance: Double,
    cashBalance: Double,
    digitalBalance: Double,
    bankBalance: Double,
    cardBalance: Double,
    receivableTotal: Double,
    payableTotal: Double,
    netWorth: Double,
    accounts: List<MoneyAccount>,
    activeSession: DailySession?,
    activeOutsideSession: OutsideSession?,
    todayIncome: Double,
    todayExpense: Double,
    todayTransfer: Double,
    todayLent: Double,
    todayBorrowed: Double,
    todayMoneyTakenOutside: Double,
    expectedCash: Double,
    actualCash: Double,
    cashDifference: Double,
    timelineItems: List<TodayTimelineItem>,
    onOpenSheet: (SheetType) -> Unit,
    onSelectAccount: (MoneyAccount) -> Unit,
    onSelectTimelineItem: (DailyTransaction) -> Unit,
    onNavigateToTodayHub: () -> Unit
) {
    val isBalanced = Math.abs(cashDifference) < 1.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. TOTAL MONEY MASTER CARD
        item {
            TotalMoneyMasterCard(
                language = language,
                totalBalance = totalBalance,
                cashBalance = cashBalance,
                digitalBalance = digitalBalance,
                bankBalance = bankBalance,
                cardBalance = cardBalance,
                receivableTotal = receivableTotal,
                payableTotal = payableTotal,
                netWorth = netWorth
            )
        }

        // 2. DAILY WORKFLOW BANNER (Start Day, Leave Home, Outside, Reconcile, Close Day)
        item {
            DailyWorkflowBanner(
                language = language,
                activeSession = activeSession,
                activeOutsideSession = activeOutsideSession,
                onOpenSheet = onOpenSheet,
                onNavigateToTodayHub = onNavigateToTodayHub
            )
        }

        // 3. MONEY MAP ("Where Is My Money? / টাকা কোথায় আছে?")
        item {
            MoneyMapSection(
                language = language,
                accounts = accounts,
                onSelectAccount = onSelectAccount,
                onAddAccountClick = { onOpenSheet(SheetType.ADD_ACCOUNT) }
            )
        }

        // 4. TODAY'S SUMMARY ("Today's Hisab / আজকের হিসাব")
        item {
            TodayHisabSummaryCard(
                language = language,
                activeSession = activeSession,
                todayIncome = todayIncome,
                todayExpense = todayExpense,
                todayTransfer = todayTransfer,
                todayLent = todayLent,
                todayBorrowed = todayBorrowed,
                todayTakenOutside = todayMoneyTakenOutside,
                expectedCash = expectedCash,
                actualCash = actualCash,
                cashDiff = cashDifference,
                isBalanced = isBalanced,
                onReconcileClick = { onOpenSheet(SheetType.DENOMINATIONS) },
                onCloseDayClick = { onOpenSheet(SheetType.CLOSE_DAY) }
            )
        }

        // 5. QUICK ACTIONS GRID
        item {
            QuickActionsGrid(
                language = language,
                onOpenSheet = onOpenSheet
            )
        }

        // 6. TODAY'S TIMELINE PREVIEW
        item {
            TodayTimelineSection(
                language = language,
                timelineItems = timelineItems,
                onSelectTimelineItem = onSelectTimelineItem,
                onViewAllClick = onNavigateToTodayHub
            )
        }
    }
}

@Composable
fun TotalMoneyMasterCard(
    language: String,
    totalBalance: Double,
    cashBalance: Double,
    digitalBalance: Double,
    bankBalance: Double,
    cardBalance: Double,
    receivableTotal: Double,
    payableTotal: Double,
    netWorth: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == "bn") "মোট বর্তমান ব্যালেন্স" else "Total Available Balance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", totalBalance)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Net Worth badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = if (language == "bn") "নেট ওয়ার্থ" else "Net Worth",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.0f", netWorth)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown: Cash, Digital, Bank, Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BalanceMetricMini(
                        label = if (language == "bn") "ক্যাশ" else "Cash",
                        amount = cashBalance,
                        color = Color(0xFF10B981)
                    )
                    BalanceMetricMini(
                        label = if (language == "bn") "ডিজিটাল" else "Digital",
                        amount = digitalBalance,
                        color = Color(0xFFD11568)
                    )
                    BalanceMetricMini(
                        label = if (language == "bn") "ব্যাংক" else "Bank",
                        amount = bankBalance,
                        color = Color(0xFF2563EB)
                    )
                    BalanceMetricMini(
                        label = if (language == "bn") "কার্ড" else "Card",
                        amount = cardBalance,
                        color = Color(0xFF7C3AED)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Receivable & Payable pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = "Receivable",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (language == "bn") "পাওনা (Receivable)" else "Receivable",
                                    fontSize = 10.sp,
                                    color = Color(0xFF047857),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", receivableTotal)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Payable",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (language == "bn") "দেনা (Payable)" else "Payable",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB91C1C),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", payableTotal)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceMetricMini(label: String, amount: Double, color: Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "৳${String.format(Locale.US, "%,.0f", amount)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DailyWorkflowBanner(
    language: String,
    activeSession: DailySession?,
    activeOutsideSession: OutsideSession?,
    onOpenSheet: (SheetType) -> Unit,
    onNavigateToTodayHub: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "bn") "দৈনন্দিন কাজের ধাপ (Workflow)" else "Daily Workflow",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeSession == null) {
                            if (language == "bn") "দিন এখনো শুরু হয়নি" else "Day not started yet"
                        } else if (activeOutsideSession != null) {
                            if (language == "bn") "সেশন সক্রিয়: বাইরে আছেন (${activeOutsideSession.location})" else "Outside Session Active"
                        } else if (activeSession.status == SessionStatus.CLOSED) {
                            if (language == "bn") "আজকের দিন সমাপ্ত হয়েছে ✓" else "Day has been closed ✓"
                        } else {
                            if (language == "bn") "সেশন সক্রিয়: বাসায় আছেন" else "Active Session at home"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (activeOutsideSession != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.clickable { onOpenSheet(SheetType.OUTSIDE_MODE) }
                    ) {
                        Text(
                            text = if (language == "bn") "বাইরে মোড" else "Outside Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (activeSession == null) {
                    Button(
                        onClick = { onOpenSheet(SheetType.START_DAY) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start Day", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == "bn") "দিন শুরু করুন" else "Start Day", fontSize = 12.sp)
                    }
                } else {
                    if (activeOutsideSession == null) {
                        OutlinedButton(
                            onClick = { onOpenSheet(SheetType.LEAVE_HOME) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = "Leave Home", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == "bn") "বাইরে যাচ্ছি" else "Leave Home", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { onOpenSheet(SheetType.OUTSIDE_MODE) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = "Outside", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == "bn") "বাইরে মোড" else "Outside Mode", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { onOpenSheet(SheetType.DENOMINATIONS) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = "Reconcile", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == "bn") "ক্যাশ মিলান" else "Reconcile", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onOpenSheet(SheetType.CLOSE_DAY) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.LockClock, contentDescription = "Close Day", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == "bn") "দিন সমাপ্ত" else "Close Day", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MoneyMapSection(
    language: String,
    accounts: List<MoneyAccount>,
    onSelectAccount: (MoneyAccount) -> Unit,
    onAddAccountClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (language == "bn") "টাকা কোথায় আছে? (Money Map)" else "Where Is My Money? (Money Map)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (language == "bn") "সকল সক্রিয় অ্যাকাউন্ট ও ব্যালেন্স" else "All active financial accounts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { onAddAccountClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (language == "bn") "অ্যাকাউন্ট" else "Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(accounts) { acc ->
                AccountCardItem(
                    account = acc,
                    language = language,
                    onClick = { onSelectAccount(acc) }
                )
            }
        }
    }
}

@Composable
fun AccountCardItem(
    account: MoneyAccount,
    language: String,
    onClick: () -> Unit
) {
    val icon = when (account.type) {
        AccountType.CASH -> Icons.Default.AccountBalanceWallet
        AccountType.MOBILE_BANKING -> Icons.Default.Payments
        AccountType.BANK -> Icons.Default.AccountBalance
        AccountType.CARD -> Icons.Default.CreditCard
        AccountType.OTHER -> Icons.Default.AttachMoney
    }

    Card(
        modifier = Modifier
            .width(155.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(account.colorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = account.name,
                        tint = Color(account.colorHex),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = account.type.name.take(4),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == "bn") account.nameBn else account.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )

            Text(
                text = "৳ ${String.format(Locale.US, "%,.0f", account.currentBalance)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )

            if (account.accountNumber.isNotEmpty()) {
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TodayHisabSummaryCard(
    language: String,
    activeSession: DailySession?,
    todayIncome: Double,
    todayExpense: Double,
    todayTransfer: Double,
    todayLent: Double,
    todayBorrowed: Double,
    todayTakenOutside: Double,
    expectedCash: Double,
    actualCash: Double,
    cashDiff: Double,
    isBalanced: Boolean,
    onReconcileClick: () -> Unit,
    onCloseDayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
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
                    Text(
                        text = if (language == "bn") "আজকের হিসাব (Today's Hisab)" else "Today's Hisab Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeSession != null) "Opening: ৳${String.format(Locale.US, "%,.0f", activeSession.openingBalance)}"
                        else "Day not started yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                ) {
                    Text(
                        text = if (isBalanced) "✓ Balanced" else if (cashDiff < 0) "⚠ Short" else "⚠ Extra",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TodayMetricItem(
                    label = if (language == "bn") "মোট আয়" else "Income",
                    amount = "+৳${String.format(Locale.US, "%,.0f", todayIncome)}",
                    color = Color(0xFF10B981)
                )
                TodayMetricItem(
                    label = if (language == "bn") "মোট খরচ" else "Expense",
                    amount = "-৳${String.format(Locale.US, "%,.0f", todayExpense)}",
                    color = Color(0xFFEF4444)
                )
                TodayMetricItem(
                    label = if (language == "bn") "স্থানান্তর" else "Transfer",
                    amount = "৳${String.format(Locale.US, "%,.0f", todayTransfer)}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TodayMetricItem(
                    label = if (language == "bn") "হাওলাত দেওয়া" else "Lent",
                    amount = "৳${String.format(Locale.US, "%,.0f", todayLent)}",
                    color = Color(0xFFF59E0B)
                )
                TodayMetricItem(
                    label = if (language == "bn") "ধার নেওয়া" else "Borrowed",
                    amount = "৳${String.format(Locale.US, "%,.0f", todayBorrowed)}",
                    color = Color(0xFF8B5CF6)
                )
                TodayMetricItem(
                    label = if (language == "bn") "বাইরে নেওয়া" else "Taken Outside",
                    amount = "৳${String.format(Locale.US, "%,.0f", todayTakenOutside)}",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cash Comparison Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == "bn") "প্রত্যাশিত ক্যাশ: ৳${String.format(Locale.US, "%,.0f", expectedCash)}"
                            else "Expected: ৳${String.format(Locale.US, "%,.0f", expectedCash)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == "bn") "প্রকৃত ক্যাশ: ৳${String.format(Locale.US, "%,.0f", actualCash)}"
                            else "Actual: ৳${String.format(Locale.US, "%,.0f", actualCash)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = onReconcileClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (language == "bn") "নোট গুনুন" else "Count Cash", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TodayMetricItem(label: String, amount: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun QuickActionsGrid(
    language: String,
    onOpenSheet: (SheetType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (language == "bn") "কুইক অ্যাকশন (Quick Actions)" else "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                label = if (language == "bn") "+ খরচ (Expense)" else "+ Expense",
                icon = Icons.Default.Add,
                color = Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.QUICK_EXPENSE)
            }

            QuickActionButton(
                label = if (language == "bn") "+ আয় (Income)" else "+ Income",
                icon = Icons.Default.Add,
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.QUICK_INCOME)
            }

            QuickActionButton(
                label = if (language == "bn") "স্থানান্তর (Transfer)" else "Transfer",
                icon = Icons.Default.SwapHoriz,
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.TRANSFER)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                label = if (language == "bn") "হাওলাত দিলাম" else "Lend Money",
                icon = Icons.Default.ArrowUpward,
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.LEND)
            }

            QuickActionButton(
                label = if (language == "bn") "ধার নিলাম" else "Borrow Money",
                icon = Icons.Default.ArrowDownward,
                color = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.BORROW)
            }

            QuickActionButton(
                label = if (language == "bn") "নোট গণনা" else "Count Notes",
                icon = Icons.Default.PointOfSale,
                color = Color(0xFF0D9488),
                modifier = Modifier.weight(1f)
            ) {
                onOpenSheet(SheetType.DENOMINATIONS)
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TodayTimelineSection(
    language: String,
    timelineItems: List<TodayTimelineItem>,
    onSelectTimelineItem: (DailyTransaction) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (language == "bn") "আজকের টাইমলাইন (Timeline)" else "Today's Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (language == "bn") "সারাদিনের প্রতিটি ঘটনার ধারাবাহিক চিত্র" else "Chronological order of today's activities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onViewAllClick() }
            ) {
                Text(
                    text = if (language == "bn") "সব দেখুন" else "View All",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (timelineItems.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "আজ এখনো কোনো লেনদেন বা ইভেন্ট রেকর্ড করা হয়নি।"
                    else "No events recorded today yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            timelineItems.take(5).forEach { item ->
                TimelineRowItem(
                    item = item,
                    language = language,
                    onClick = {
                        item.transaction?.let { trx -> onSelectTimelineItem(trx) }
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun TimelineRowItem(
    item: TodayTimelineItem,
    language: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (language == "bn") item.titleBn else item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (item.amount != null) {
                val isExpense = item.transaction?.type == DailyTransactionType.EXPENSE ||
                        item.transaction?.type == DailyTransactionType.LEND ||
                        item.transaction?.type == DailyTransactionType.REPAYMENT_PAID
                val isIncome = item.transaction?.type == DailyTransactionType.INCOME ||
                        item.transaction?.type == DailyTransactionType.REPAYMENT_RECEIVED

                Text(
                    text = "${if (isExpense) "-" else if (isIncome) "+" else ""}৳${String.format(Locale.US, "%,.0f", item.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isExpense) Color(0xFFEF4444) else if (isIncome) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

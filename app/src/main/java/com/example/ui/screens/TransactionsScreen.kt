package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import com.example.data.model.MoneyAccount
import com.example.ui.viewmodel.DateFilterType
import com.example.ui.viewmodel.SheetType
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    language: String,
    transactions: List<DailyTransaction>,
    accounts: List<MoneyAccount>,
    searchQuery: String,
    dateFilter: DateFilterType,
    typeFilter: DailyTransactionType?,
    filterAccountId: String?,
    onSearchChange: (String) -> Unit,
    onDateFilterChange: (DateFilterType) -> Unit,
    onTypeFilterChange: (DailyTransactionType?) -> Unit,
    onAccountFilterChange: (String?) -> Unit,
    onSelectTransaction: (DailyTransaction) -> Unit,
    onOpenSheet: (SheetType) -> Unit
) {
    var expandedAccountMenu by remember { mutableStateOf(false) }

    val totalExpense = transactions.filter {
        it.type == DailyTransactionType.EXPENSE || it.type == DailyTransactionType.LEND || it.type == DailyTransactionType.REPAYMENT_PAID
    }.sumOf { it.amount }

    val totalIncome = transactions.filter {
        it.type == DailyTransactionType.INCOME || it.type == DailyTransactionType.REPAYMENT_RECEIVED
    }.sumOf { it.amount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenSheet(SheetType.QUICK_EXPENSE) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(if (language == "bn") "বর্ণনা, ক্যাটেগরি বা নাম দিয়ে খুঁজুন..." else "Search transactions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Date Filters
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    DateFilterType.TODAY to if (language == "bn") "আজ" else "Today",
                    DateFilterType.YESTERDAY to if (language == "bn") "গতকাল" else "Yesterday",
                    DateFilterType.THIS_WEEK to if (language == "bn") "এই সপ্তাহ" else "This Week",
                    DateFilterType.THIS_MONTH to if (language == "bn") "এই মাস" else "This Month",
                    DateFilterType.ALL to if (language == "bn") "সব" else "All"
                ).forEach { (f, label) ->
                    FilterChip(
                        selected = dateFilter == f,
                        onClick = { onDateFilterChange(f) },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            // Type Filters
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = typeFilter == null,
                    onClick = { onTypeFilterChange(null) },
                    label = { Text(if (language == "bn") "সকল ধরন" else "All Types", fontSize = 11.sp) }
                )
                listOf(
                    DailyTransactionType.EXPENSE to if (language == "bn") "খরচ" else "Expense",
                    DailyTransactionType.INCOME to if (language == "bn") "আয়" else "Income",
                    DailyTransactionType.TRANSFER to if (language == "bn") "স্থানান্তর" else "Transfer",
                    DailyTransactionType.LEND to if (language == "bn") "হাওলাত" else "Lend",
                    DailyTransactionType.BORROW to if (language == "bn") "ধার" else "Borrow"
                ).forEach { (t, label) ->
                    FilterChip(
                        selected = typeFilter == t,
                        onClick = { onTypeFilterChange(t) },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            // Summary of filtered results
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${transactions.size} ${if (language == "bn") "টি লেনদেন" else "transactions"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "+৳${String.format(Locale.US, "%,.0f", totalIncome)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "-৳${String.format(Locale.US, "%,.0f", totalExpense)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            // List of transactions
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == "bn") "কোনো লেনদেন পাওয়া যায়নি।" else "No transactions match your filter.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.id }) { trx ->
                        TransactionCardItem(
                            transaction = trx,
                            language = language,
                            onClick = { onSelectTransaction(trx) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCardItem(
    transaction: DailyTransaction,
    language: String,
    onClick: () -> Unit
) {
    val isExpense = transaction.type == DailyTransactionType.EXPENSE ||
            transaction.type == DailyTransactionType.LEND ||
            transaction.type == DailyTransactionType.REPAYMENT_PAID
    val isIncome = transaction.type == DailyTransactionType.INCOME ||
            transaction.type == DailyTransactionType.REPAYMENT_RECEIVED
    val isTransfer = transaction.type == DailyTransactionType.TRANSFER

    val badgeColor = when {
        isExpense -> Color(0xFFEF4444)
        isIncome -> Color(0xFF10B981)
        isTransfer -> Color(0xFF2563EB)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = when {
                            isExpense -> Icons.Default.ArrowUpward
                            isIncome -> Icons.Default.ArrowDownward
                            else -> Icons.Default.SwapHoriz
                        },
                        contentDescription = transaction.type.name,
                        tint = badgeColor,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (transaction.description.isNotEmpty()) transaction.description else transaction.category,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "${transaction.date} • ${transaction.time} • ${transaction.accountId}" +
                                (if (transaction.personName != null) " • ${transaction.personName}" else ""),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isExpense) "-" else if (isIncome) "+" else ""}৳${String.format(Locale.US, "%,.0f", transaction.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = badgeColor
                )
                Text(
                    text = transaction.category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

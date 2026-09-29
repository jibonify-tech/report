package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import java.util.Locale

@Composable
fun ReportsScreen(
    language: String,
    transactions: List<DailyTransaction>
) {
    var period by remember { mutableStateOf("ALL") } // TODAY, WEEK, MONTH, ALL

    val filtered = when (period) {
        "TODAY" -> transactions.take(10)
        "WEEK" -> transactions.take(25)
        else -> transactions
    }

    val totalIncome = filtered.filter {
        it.type == DailyTransactionType.INCOME || it.type == DailyTransactionType.REPAYMENT_RECEIVED
    }.sumOf { it.amount }

    val totalExpense = filtered.filter {
        it.type == DailyTransactionType.EXPENSE || it.type == DailyTransactionType.LEND || it.type == DailyTransactionType.REPAYMENT_PAID
    }.sumOf { it.amount }

    val netSavings = totalIncome - totalExpense

    // Category breakdown
    val categoryTotals = filtered
        .filter { it.type == DailyTransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "bn") "আর্থিক রিপোর্ট ও বিশ্লেষণ" else "Financial Reports & Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == "bn") "আয়-ব্যয় ও খরচের ক্যাটেগরি ভিত্তিক বিশ্লেষণ" else "Income, expense & category breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = period == "WEEK",
                        onClick = { period = "WEEK" },
                        label = { Text("Week", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = period == "ALL",
                        onClick = { period = "ALL" },
                        label = { Text("All", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Cash Flow Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == "bn") "ক্যাশ ফ্লো সারাংশ (Cash Flow)" else "Cash Flow Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = if (language == "bn") "মোট আয়" else "Total Income", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.0f", totalIncome)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        Column {
                            Text(text = if (language == "bn") "মোট খরচ" else "Total Expense", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.0f", totalExpense)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = if (language == "bn") "নিট সঞ্চয়" else "Net Savings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${if (netSavings >= 0) "+" else ""}৳ ${String.format(Locale.US, "%,.0f", netSavings)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (netSavings >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val incomeRatio = if ((totalIncome + totalExpense) > 0) (totalIncome / (totalIncome + totalExpense)).toFloat() else 0.5f
                    LinearProgressIndicator(
                        progress = { incomeRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFEF4444)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Income: ${(incomeRatio * 100).toInt()}%", fontSize = 11.sp, color = Color(0xFF10B981))
                        Text(text = "Expense: ${((1 - incomeRatio) * 100).toInt()}%", fontSize = 11.sp, color = Color(0xFFEF4444))
                    }
                }
            }
        }

        // Category Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == "bn") "খরচের খাত ভিত্তিক বিশ্লেষণ (Category Breakdown)" else "Top Spending Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (categoryTotals.isEmpty()) {
                        Text(
                            text = if (language == "bn") "কোনো খরচ রেকর্ড পাওয়া যায়নি।" else "No expenses recorded.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    } else {
                        val maxCatAmount = categoryTotals.maxOfOrNull { it.second } ?: 1.0
                        categoryTotals.forEach { (category, amount) ->
                            val ratio = (amount / maxCatAmount).toFloat().coerceIn(0f, 1f)
                            val percentOfTotal = if (totalExpense > 0) ((amount / totalExpense) * 100).toInt() else 0

                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = category, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(
                                        text = "৳ ${String.format(Locale.US, "%,.0f", amount)} ($percentOfTotal%)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

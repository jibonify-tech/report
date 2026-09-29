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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CashDenomination
import com.example.data.model.DailySession
import com.example.data.model.DailyTransaction
import com.example.data.model.OutsideSession
import com.example.data.model.SessionStatus
import com.example.ui.viewmodel.SheetType
import com.example.ui.viewmodel.TodayTimelineItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayHubScreen(
    language: String,
    todayFormatted: String,
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
    cashDenomination: CashDenomination?,
    timelineItems: List<TodayTimelineItem>,
    onOpenSheet: (SheetType) -> Unit,
    onEndOutsideSession: () -> Unit,
    onSelectTransaction: (DailyTransaction) -> Unit
) {
    val isBalanced = Math.abs(cashDifference) < 1.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Session Status Card
        item {
            TodayStatusCard(
                language = language,
                todayFormatted = todayFormatted,
                activeSession = activeSession,
                onStartDayClick = { onOpenSheet(SheetType.START_DAY) },
                onCloseDayClick = { onOpenSheet(SheetType.CLOSE_DAY) }
            )
        }

        // 2. Outside Session Card
        item {
            OutsideSessionCard(
                language = language,
                activeOutsideSession = activeOutsideSession,
                moneyTakenOutside = todayMoneyTakenOutside,
                onStartOutside = { onOpenSheet(SheetType.LEAVE_HOME) },
                onEndOutside = onEndOutsideSession,
                onOpenOutsideMode = { onOpenSheet(SheetType.OUTSIDE_MODE) }
            )
        }

        // 3. Cash Reconciliation & Denominations Card
        item {
            CashReconciliationHubCard(
                language = language,
                expectedCash = expectedCash,
                actualCash = actualCash,
                cashDifference = cashDifference,
                isBalanced = isBalanced,
                cashDenomination = cashDenomination,
                onCountNotesClick = { onOpenSheet(SheetType.DENOMINATIONS) }
            )
        }

        // 4. Chronological Daily Timeline
        item {
            Text(
                text = if (language == "bn") "আজকের পূর্ণাঙ্গ টাইমলাইন (Timeline)" else "Today's Full Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (timelineItems.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == "bn") "আজকের কোনো কার্যক্রম রেকর্ড নেই।" else "No activity recorded today.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(timelineItems.size) { index ->
                val item = timelineItems[index]
                TimelineRowItem(
                    item = item,
                    language = language,
                    onClick = {
                        item.transaction?.let { trx -> onSelectTransaction(trx) }
                    }
                )
            }
        }

        // 5. Close Day Card at bottom
        item {
            if (activeSession != null && activeSession.status == SessionStatus.ACTIVE) {
                Button(
                    onClick = { onOpenSheet(SheetType.CLOSE_DAY) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Close Day")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "bn") "দিন সমাপ্ত করুন (Close My Day)" else "Close My Day",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TodayStatusCard(
    language: String,
    todayFormatted: String,
    activeSession: DailySession?,
    onStartDayClick: () -> Unit,
    onCloseDayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        text = todayFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeSession == null) {
                            if (language == "bn") "দিন এখনো শুরু হয়নি" else "Day not started yet"
                        } else if (activeSession.status == SessionStatus.ACTIVE) {
                            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(activeSession.startTime))
                            if (language == "bn") "সক্রিয় সেশন (শুরু: $timeStr)" else "Active Session (Started: $timeStr)"
                        } else {
                            if (language == "bn") "দিন সমাপ্ত হয়েছে (Closed)" else "Day Closed"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeSession?.status == SessionStatus.ACTIVE) Color(0xFF10B981)
                    else if (activeSession?.status == SessionStatus.CLOSED) MaterialTheme.colorScheme.secondary
                    else Color(0xFFF59E0B)
                ) {
                    Text(
                        text = if (activeSession?.status == SessionStatus.ACTIVE) "ACTIVE"
                        else if (activeSession?.status == SessionStatus.CLOSED) "CLOSED"
                        else "NOT STARTED",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (activeSession != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == "bn") "প্রারম্ভিক ব্যালেন্স" else "Opening Balance",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", activeSession.openingBalance)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = if (language == "bn") "আজকের বাজেট" else "Planned Budget",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", activeSession.plannedBudget)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (activeSession.status == SessionStatus.CLOSED) {
                        Column {
                            Text(
                                text = if (language == "bn") "সমাপনী ব্যালেন্স" else "Closing Balance",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.0f", activeSession.closingBalance)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (activeSession.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: ${activeSession.note}",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Button(
                    onClick = onStartDayClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Day")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == "bn") "আজকের দিন শুরু করুন" else "Start My Day")
                }
            }
        }
    }
}

@Composable
fun OutsideSessionCard(
    language: String,
    activeOutsideSession: OutsideSession?,
    moneyTakenOutside: Double,
    onStartOutside: () -> Unit,
    onEndOutside: () -> Unit,
    onOpenOutsideMode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activeOutsideSession != null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = "Outside",
                        tint = if (activeOutsideSession != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "bn") "বাইরে থাকার হিসাব (Outside Session)" else "Outside Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeOutsideSession != null) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (activeOutsideSession != null) "OUTSIDE" else "HOME",
                        color = if (activeOutsideSession != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (activeOutsideSession != null) {
                Text(
                    text = "${activeOutsideSession.purpose} • ${activeOutsideSession.location}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (language == "bn") "সাথে নেওয়া টাকা: ৳${String.format(Locale.US, "%,.0f", activeOutsideSession.amountTaken)} • ফেরার সম্ভাব্য সময়: ${activeOutsideSession.expectedReturnTime}"
                    else "Cash carried: ৳${String.format(Locale.US, "%,.0f", activeOutsideSession.amountTaken)} • Return: ${activeOutsideSession.expectedReturnTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenOutsideMode,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (language == "bn") "বাইরে মোড চালু" else "Open Outside Mode", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onEndOutside,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (language == "bn") "বাসায় ফিরেছি (End)" else "Return Home", fontSize = 12.sp)
                    }
                }
            } else {
                Text(
                    text = if (language == "bn") "বাইরে বের হওয়ার সময় কত টাকা সাথে নিলেন তা সংরক্ষণ করুন।"
                    else "Record money taken when leaving home for daily commute/shopping.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onStartOutside,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = "Start Outside")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == "bn") "বাইরে যাচ্ছি (Leave Home)" else "Leave Home / Take Cash Outside")
                }
            }
        }
    }
}

@Composable
fun CashReconciliationHubCard(
    language: String,
    expectedCash: Double,
    actualCash: Double,
    cashDifference: Double,
    isBalanced: Boolean,
    cashDenomination: CashDenomination?,
    onCountNotesClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBalanced) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isBalanced) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "Status",
                        tint = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "bn") "ক্যাশ মিলান (Cash Reconciliation)" else "Cash Reconciliation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                ) {
                    Text(
                        text = if (isBalanced) "✓ Balanced" else if (cashDifference < 0) "⚠ Short" else "⚠ Extra",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (language == "bn") "প্রত্যাশিত ক্যাশ" else "Expected Cash",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,.0f", expectedCash)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column {
                    Text(
                        text = if (language == "bn") "প্রকৃত গোনা ক্যাশ" else "Actual Cash",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,.0f", actualCash)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (language == "bn") "পার্থক্য" else "Difference",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (cashDifference > 0) "+" else ""}৳ ${String.format(Locale.US, "%,.0f", cashDifference)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }

            if (cashDenomination != null && cashDenomination.totalAmount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "নোট গণনা: 1000x${cashDenomination.n1000}, 500x${cashDenomination.n500}, 200x${cashDenomination.n200}, 100x${cashDenomination.n100}, 50x${cashDenomination.n50} ... = ৳${String.format(Locale.US, "%,.0f", cashDenomination.totalAmount)}",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCountNotesClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PointOfSale, contentDescription = "Count Notes")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == "bn") "নোট গুনে ক্যাশ আপডেট করুন" else "Count Banknotes & Reconcile")
            }
        }
    }
}

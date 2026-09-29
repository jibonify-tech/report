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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OutsideSession
import com.example.ui.viewmodel.SheetType
import java.util.Locale

@Composable
fun OutsideModeScreen(
    language: String,
    outsideSession: OutsideSession?,
    moneyCarried: Double,
    moneySpentOutside: Double,
    moneyReceivedOutside: Double,
    remainingCash: Double,
    onBackClick: () -> Unit,
    onOpenSheet: (SheetType) -> Unit,
    onEndJourney: () -> Unit
) {
    val location = outsideSession?.location ?: "Outside"
    val purpose = outsideSession?.purpose ?: "Daily Errands"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // High-contrast Header
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onErrorContainer)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = if (language == "bn") "বাইরে মোড (Outside Mode)" else "Outside Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "$purpose • $location",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Outside Money Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (language == "bn") "অবশিষ্ট ক্যাশ টাকা" else "Remaining Cash",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", (moneyCarried - moneySpentOutside + moneyReceivedOutside).coerceAtLeast(0.0))}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (language == "bn") "সাথে নেওয়া টাকা" else "Carried",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", moneyCarried)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Spent", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "bn") "বাইরে খরচ: ৳${String.format(Locale.US, "%,.0f", moneySpentOutside)}"
                                    else "Spent: ৳${String.format(Locale.US, "%,.0f", moneySpentOutside)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFEF4444)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Received", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "bn") "বাইরে প্রাপ্তি: ৳${String.format(Locale.US, "%,.0f", moneyReceivedOutside)}"
                                    else "Received: ৳${String.format(Locale.US, "%,.0f", moneyReceivedOutside)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Big Action Buttons for Outside
            item {
                Text(
                    text = if (language == "bn") "দ্রুত এক ক্লিকে এন্ট্রি" else "Quick 1-Tap Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LargeOutsideButton(
                        label = if (language == "bn") "খাবার খরচ\n(Food)" else "Food Expense",
                        icon = Icons.Default.Fastfood,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.QUICK_EXPENSE)
                    }

                    LargeOutsideButton(
                        label = if (language == "bn") "যাতায়াত / বাস / রিকশা\n(Transport)" else "Transport / Commute",
                        icon = Icons.Default.DirectionsBus,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.QUICK_EXPENSE)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LargeOutsideButton(
                        label = if (language == "bn") "কেনাকাটা\n(Shopping)" else "Shopping",
                        icon = Icons.Default.ShoppingBag,
                        color = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.QUICK_EXPENSE)
                    }

                    LargeOutsideButton(
                        label = if (language == "bn") "অন্যান্য খরচ\n(Other Expense)" else "Other Expense",
                        icon = Icons.Default.Receipt,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.QUICK_EXPENSE)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LargeOutsideButton(
                        label = if (language == "bn") "টাকা পেলাম\n(+ Income)" else "+ Income Received",
                        icon = Icons.Default.ArrowDownward,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.QUICK_INCOME)
                    }

                    LargeOutsideButton(
                        label = if (language == "bn") "টাকা স্থানান্তর\n(Transfer)" else "Money Transfer",
                        icon = Icons.Default.SwapHoriz,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.TRANSFER)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LargeOutsideButton(
                        label = if (language == "bn") "হাওলাত দিলাম\n(Lend)" else "Lend Money",
                        icon = Icons.Default.ArrowUpward,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.LEND)
                    }

                    LargeOutsideButton(
                        label = if (language == "bn") "ধার নিলাম\n(Borrow)" else "Borrow Money",
                        icon = Icons.Default.ArrowDownward,
                        color = Color(0xFF14B8A6),
                        modifier = Modifier.weight(1f)
                    ) {
                        onOpenSheet(SheetType.BORROW)
                    }
                }
            }

            // End Journey / Return Home Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onEndJourney,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Return Home", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "bn") "বাসায় ফিরেছি (End Journey)" else "Return Home / End Journey",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LargeOutsideButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.Person
import com.example.ui.viewmodel.SheetType
import java.util.Locale

@Composable
fun PeopleDebtScreen(
    language: String,
    people: List<Person>,
    receivableTotal: Double,
    payableTotal: Double,
    onSelectPerson: (Person) -> Unit,
    onDeletePerson: (Long) -> Unit,
    onOpenSheet: (SheetType) -> Unit
) {
    var filterType by remember { mutableStateOf("ALL") } // ALL, RECEIVABLE, PAYABLE

    val filteredPeople = people.filter { p ->
        when (filterType) {
            "RECEIVABLE" -> p.currentReceivable > 0
            "PAYABLE" -> p.currentPayable > 0
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Cards: Who Owes Me vs Whom I Owe
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Receivable", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "bn") "কে আমাকে দিবে?" else "Who Owes Me?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", receivableTotal)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF047857)
                        )
                        Text(
                            text = if (language == "bn") "মোট পাওনা (Receivable)" else "Total Receivable",
                            fontSize = 10.sp,
                            color = Color(0xFF047857).copy(alpha = 0.8f)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Payable", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "bn") "আমি কাকে দিব?" else "Whom I Owe?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", payableTotal)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB91C1C)
                        )
                        Text(
                            text = if (language == "bn") "মোট দেনা (Payable)" else "Total Payable",
                            fontSize = 10.sp,
                            color = Color(0xFFB91C1C).copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Action Buttons Row: Add Person, Lend, Borrow, Repay
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenSheet(SheetType.LEND) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(if (language == "bn") "+ হাওলাত" else "+ Lend", fontSize = 12.sp)
                }

                Button(
                    onClick = { onOpenSheet(SheetType.BORROW) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text(if (language == "bn") "+ ধার নিন" else "+ Borrow", fontSize = 12.sp)
                }

                Button(
                    onClick = { onOpenSheet(SheetType.REPAYMENT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (language == "bn") "পরিশোধ" else "Repay", fontSize = 12.sp)
                }
            }
        }

        // Filter Chips & Add Person Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = filterType == "ALL",
                        onClick = { filterType = "ALL" },
                        label = { Text(if (language == "bn") "সবাই" else "All", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = filterType == "RECEIVABLE",
                        onClick = { filterType = "RECEIVABLE" },
                        label = { Text(if (language == "bn") "পাওনাদার" else "Owes Me", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = filterType == "PAYABLE",
                        onClick = { filterType = "PAYABLE" },
                        label = { Text(if (language == "bn") "দেনাদার" else "I Owe", fontSize = 11.sp) }
                    )
                }

                OutlinedButton(
                    onClick = { onOpenSheet(SheetType.ADD_PERSON) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Person", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (language == "bn") "ব্যক্তি" else "Person", fontSize = 11.sp)
                }
            }
        }

        // People List
        if (filteredPeople.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == "bn") "কোনো ব্যক্তি বা দেনা-পাওনার রেকর্ড নেই।" else "No contacts or debt records found.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredPeople, key = { it.id }) { person ->
                PersonCardItem(
                    person = person,
                    language = language,
                    onSelect = { onSelectPerson(person) },
                    onRepayClick = { onOpenSheet(SheetType.REPAYMENT) }
                )
            }
        }
    }
}

@Composable
fun PersonCardItem(
    person: Person,
    language: String,
    onSelect: () -> Unit,
    onRepayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
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
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = person.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(text = person.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (person.phone.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = person.phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Balance Badge
                Column(horizontalAlignment = Alignment.End) {
                    if (person.currentReceivable > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+৳${String.format(Locale.US, "%,.0f", person.currentReceivable)}",
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = if (language == "bn") "পাব (Owes me)" else "Owes me",
                            fontSize = 10.sp,
                            color = Color(0xFF047857)
                        )
                    } else if (person.currentPayable > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "-৳${String.format(Locale.US, "%,.0f", person.currentPayable)}",
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = if (language == "bn") "দিব (I owe)" else "I owe",
                            fontSize = 10.sp,
                            color = Color(0xFFB91C1C)
                        )
                    } else {
                        Text(
                            text = "পরিশোধিত ✓",
                            fontSize = 12.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lent: ৳${String.format(Locale.US, "%,.0f", person.totalLent)} • Repaid: ৳${String.format(Locale.US, "%,.0f", person.totalReceived)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Borrowed: ৳${String.format(Locale.US, "%,.0f", person.totalBorrowed)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

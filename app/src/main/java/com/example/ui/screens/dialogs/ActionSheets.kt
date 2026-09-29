package com.example.ui.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountType
import com.example.data.model.BudgetPeriod
import com.example.data.model.CashDenomination
import com.example.data.model.DailySession
import com.example.data.model.DailyTransaction
import com.example.data.model.DailyTransactionType
import com.example.data.model.MoneyAccount
import com.example.data.model.Person
import com.example.data.model.SavingsGoal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickExpenseSheet(
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, accountId: String, desc: String, loc: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "pocket_cash" }?.id ?: accounts.firstOrNull()?.id ?: "") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val categories = listOf(
        "Food", "Transport", "Shopping", "Fuel", "Mobile", "Internet",
        "Medicine", "Education", "Bills", "Entertainment", "Family", "Business", "Travel", "Personal", "Other"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "খরচ যোগ করুন" else "Quick Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "টাকার পরিমাণ (৳)" else "Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            // Quick Preset Amount Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(20, 50, 100, 500, 1000).forEach { addVal ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = (current + addVal).toInt().toString()
                            }
                    ) {
                        Text(
                            text = "+$addVal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Categories
            Text(
                text = if (language == "bn") "ক্যাটেগরি নির্বাচন করুন" else "Select Category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account selection
            Text(
                text = if (language == "bn") "কোন অ্যাকাউন্ট থেকে খরচ?" else "Paid From Account",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (language == "bn") "বিবরণ (ঐচ্ছিক)" else "Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(if (language == "bn") "স্থান / লোকেশন (ঐচ্ছিক)" else "Location (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "নোট / রসিদের তথ্য" else "Note / Receipt note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && selectedAccountId.isNotBlank()) {
                        onSave(amt, selectedCategory, selectedAccountId, description, location, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "খরচ সংরক্ষণ করুন" else "Save Expense",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickIncomeSheet(
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, accountId: String, desc: String, personName: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Salary") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "bank_account" }?.id ?: accounts.firstOrNull()?.id ?: "") }
    var description by remember { mutableStateOf("") }
    var personName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val categories = listOf(
        "Salary", "Business", "Freelance", "Commission", "Bonus", "Gift", "Refund", "Interest", "Investment", "Other"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "আয় যোগ করুন" else "Add Income",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "আয়ের পরিমাণ (৳)" else "Income Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            // Quick Preset Income Amount Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(500, 1000, 5000, 10000, 25000).forEach { addVal ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = (current + addVal).toInt().toString()
                            }
                    ) {
                        Text(
                            text = "+${if (addVal >= 1000) "${addVal / 1000}k" else "$addVal"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "আয়ের উৎস" else "Income Source",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "কোন অ্যাকাউন্টে জমা হবে?" else "Deposit To Account",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (language == "bn") "বিবরণ" else "Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                label = { Text(if (language == "bn") "ব্যক্তি বা কোম্পানি (ঐচ্ছিক)" else "Payer / Company (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "নোট" else "Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && selectedAccountId.isNotBlank()) {
                        onSave(amt, selectedCategory, selectedAccountId, description, personName, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "আয় সংরক্ষণ করুন" else "Save Income",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransferSheet(
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, fromAccountId: String, toAccountId: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var fromAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "bkash" }?.id ?: accounts.firstOrNull()?.id ?: "") }
    var toAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "pocket_cash" }?.id ?: accounts.getOrNull(1)?.id ?: "") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "টাকা স্থানান্তর (Transfer)" else "Money Transfer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = if (language == "bn") "স্থানান্তর কোনো খরচ নয়। এটি শুধুমাত্র এক অ্যাকাউন্ট থেকে অন্য অ্যাকাউন্টে টাকা সরিয়ে নেয়।"
                else "A transfer is not an expense. It moves money between your accounts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "স্থানান্তরের পরিমাণ (৳)" else "Transfer Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "উৎস অ্যাকাউন্ট (From)" else "From Source Account",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = fromAccountId == acc.id,
                        onClick = { fromAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "গন্তব্য অ্যাকাউন্ট (To)" else "To Destination Account",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = toAccountId == acc.id,
                        onClick = { toAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "নোট (ঐচ্ছিক)" else "Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && fromAccountId.isNotBlank() && toAccountId.isNotBlank() && fromAccountId != toAccountId) {
                        onSave(amt, fromAccountId, toAccountId, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "স্থানান্তর সম্পন্ন করুন" else "Complete Transfer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LendBorrowSheet(
    isLend: Boolean, // true = Give money (Lend); false = Borrow money
    people: List<Person>,
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, accountId: String, personId: Long, personName: String, dueDate: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var selectedPerson by remember { mutableStateOf(people.firstOrNull()) }
    var newPersonName by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var dueDate by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val title = if (isLend) {
        if (language == "bn") "হাওলাত দিলাম (Lend Money)" else "Give Money (Lend)"
    } else {
        if (language == "bn") "ধার নিলাম (Borrow Money)" else "Borrow Money"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = if (isLend) {
                    if (language == "bn") "আপনার অ্যাকাউন্ট থেকে টাকা কমবে এবং পাওনা (Receivable) হিসেবে তৈরি হবে।"
                    else "Decreases your account balance and records a receivable."
                } else {
                    if (language == "bn") "আপনার অ্যাকাউন্টে টাকা জমা হবে এবং দেনা (Payable) হিসেবে তৈরি হবে।"
                    else "Increases your account balance and records a payable debt."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "টাকার পরিমাণ (৳)" else "Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isLend) (if (language == "bn") "কাকে হাওলাত দিচ্ছেন?" else "Lending to Person")
                else (if (language == "bn") "কার কাছ থেকে ধার নিচ্ছেন?" else "Borrowing from Person"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                people.forEach { p ->
                    FilterChip(
                        selected = selectedPerson?.id == p.id,
                        onClick = {
                            selectedPerson = p
                            newPersonName = ""
                        },
                        label = { Text(p.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPersonName,
                onValueChange = {
                    newPersonName = it
                    selectedPerson = null
                },
                label = { Text(if (language == "bn") "অথবা নতুন ব্যক্তির নাম লিখুন" else "Or type new person's name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isLend) (if (language == "bn") "কোন অ্যাকাউন্ট থেকে টাকা দিলেন?" else "Paid from Account")
                else (if (language == "bn") "কোন অ্যাকাউন্টে টাকা গ্রহণ করলেন?" else "Receive in Account"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text(if (language == "bn") "ফেরতের সম্ভাব্য তারিখ (যেমন: 15 Oct 2026)" else "Due Date (e.g. 15 Oct 2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "উদ্দেশ্য / নোট" else "Purpose / Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    val targetPersonName = selectedPerson?.name ?: newPersonName
                    val targetPersonId = selectedPerson?.id ?: 0L
                    if (amt > 0 && targetPersonName.isNotBlank() && selectedAccountId.isNotBlank()) {
                        onSave(amt, selectedAccountId, targetPersonId, targetPersonName, dueDate, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "সংরক্ষণ করুন" else "Save Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RepaymentSheet(
    people: List<Person>,
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, accountId: String, personId: Long, personName: String, isReceiving: Boolean, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isReceiving by remember { mutableStateOf(true) } // true = receive repayment; false = pay borrowed money
    var amountText by remember { mutableStateOf("") }
    var selectedPerson by remember { mutableStateOf(people.firstOrNull()) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "হাওলাত/ধার পরিশোধ (Repayment)" else "Repayment System",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Receive vs Pay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isReceiving) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isReceiving = true }
                ) {
                    Text(
                        text = if (language == "bn") "হাওলাত ফেরত পেলাম\n(Receive Money)" else "Receive Repayment\n(From Lend)",
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isReceiving) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isReceiving) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isReceiving = false }
                ) {
                    Text(
                        text = if (language == "bn") "ধার পরিশোধ করলাম\n(Pay Borrowed)" else "Pay Borrowed Money\n(Repay Debt)",
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (!isReceiving) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "পরিশোধের পরিমাণ (৳)" else "Repayment Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "ব্যক্তি নির্বাচন করুন" else "Select Person",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                people.forEach { p ->
                    val balanceInfo = if (isReceiving) "পাওনা ৳${String.format(Locale.US, "%,.0f", p.currentReceivable)}"
                    else "দেনা ৳${String.format(Locale.US, "%,.0f", p.currentPayable)}"
                    FilterChip(
                        selected = selectedPerson?.id == p.id,
                        onClick = { selectedPerson = p },
                        label = { Text("${p.name} ($balanceInfo)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isReceiving) (if (language == "bn") "টাকা কোন অ্যাকাউন্টে জমা হবে?" else "Deposit into Account")
                else (if (language == "bn") "কোন অ্যাকাউন্ট থেকে পরিশোধ করলেন?" else "Paid from Account"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = {
                            Text("${if (language == "bn") acc.nameBn else acc.name} (৳${String.format(Locale.US, "%,.0f", acc.currentBalance)})")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "নোট" else "Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    val targetPerson = selectedPerson
                    if (amt > 0 && targetPerson != null && selectedAccountId.isNotBlank()) {
                        onSave(amt, selectedAccountId, targetPerson.id, targetPerson.name, isReceiving, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "পরিশোধ নিশ্চিত করুন" else "Confirm Repayment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDaySheet(
    dateFormatted: String,
    totalBalance: Double,
    cashBalance: Double,
    digitalBalance: Double,
    language: String,
    onDismiss: () -> Unit,
    onStart: (budget: Double, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var plannedBudget by remember { mutableStateOf("1500") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "দিন শুরু করুন (Start My Day)" else "Start My Day",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "$dateFormatted • Opening Hisab",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "bn") "প্রারম্ভিক মোট ব্যালেন্স (Opening Balance)" else "Total Opening Balance",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,.0f", totalBalance)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "bn") "হাতে ক্যাশ: ৳${String.format(Locale.US, "%,.0f", cashBalance)}" else "Cash: ৳${String.format(Locale.US, "%,.0f", cashBalance)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == "bn") "ডিজিটাল/ব্যাংক: ৳${String.format(Locale.US, "%,.0f", digitalBalance)}" else "Digital/Bank: ৳${String.format(Locale.US, "%,.0f", digitalBalance)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plannedBudget,
                onValueChange = { plannedBudget = it },
                label = { Text(if (language == "bn") "আজকের বাজেট (৳)" else "Today's Planned Budget (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "আজকের লক্ষ্য / নোট" else "Today's Goals / Note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val b = plannedBudget.toDoubleOrNull() ?: 0.0
                    onStart(b, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "হিসাব শুরু করুন (Start Day)" else "Start Day Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeaveHomeSheet(
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onStartOutside: (amountTaken: Double, sourceAcc: String, destAcc: String, location: String, purpose: String, expectedReturn: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountTaken by remember { mutableStateOf("2000") }
    var sourceAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "home_cash" }?.id ?: accounts.firstOrNull()?.id ?: "") }
    var destinationAccountId by remember { mutableStateOf(accounts.firstOrNull { it.id == "pocket_cash" }?.id ?: accounts.getOrNull(1)?.id ?: "") }
    var location by remember { mutableStateOf("City Market") }
    var purpose by remember { mutableStateOf("Shopping & Errand") }
    var expectedReturnTime by remember { mutableStateOf("19:00") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "বাইরে যাওয়া (Leave Home)" else "Leave Home Session",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = if (language == "bn") "বাইরে যাওয়ার সময় কত টাকা সাথে নিচ্ছেন তা রেকর্ড করুন। এটি আপনার বাইরে খরচ ট্র্যাকিং সহজ করবে।"
                else "Record cash carried outside. Starts an Outside Session for fast tracking.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountTaken,
                onValueChange = { amountTaken = it },
                label = { Text(if (language == "bn") "কত টাকা সাথে নিচ্ছেন (৳)" else "Cash Taken Outside (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "উৎস অ্যাকাউন্ট (টাকা কোথা থেকে নিলেন?)" else "Source Account",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = sourceAccountId == acc.id,
                        onClick = { sourceAccountId = acc.id },
                        label = { Text(if (language == "bn") acc.nameBn else acc.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "গন্তব্য অ্যাকাউন্ট (টাকা কোথায় রাখলেন?)" else "Destination Account (e.g. Pocket)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = destinationAccountId == acc.id,
                        onClick = { destinationAccountId = acc.id },
                        label = { Text(if (language == "bn") acc.nameBn else acc.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(if (language == "bn") "গন্তব্য / স্থান" else "Destination / Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text(if (language == "bn") "উদ্দেশ্য" else "Purpose") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = expectedReturnTime,
                onValueChange = { expectedReturnTime = it },
                label = { Text(if (language == "bn") "ফেরার সম্ভাব্য সময় (যেমন: 19:30)" else "Expected Return Time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountTaken.toDoubleOrNull() ?: 0.0
                    onStartOutside(amt, sourceAccountId, destinationAccountId, location, purpose, expectedReturnTime, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "বাইরে সেশন শুরু করুন (Start Outside)" else "Start Outside Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseDaySheet(
    activeSession: DailySession?,
    expectedCash: Double,
    actualCash: Double,
    todayIncome: Double,
    todayExpense: Double,
    todayTransfer: Double,
    todayLent: Double,
    todayBorrowed: Double,
    language: String,
    onDismiss: () -> Unit,
    onCloseDay: (actualCashVal: Double, note: String, mood: String, tomorrowReminder: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var actualCashInput by remember { mutableStateOf(String.format(Locale.US, "%.0f", actualCash)) }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("HAPPY") }
    var tomorrowReminder by remember { mutableStateOf("") }

    val diff = (actualCashInput.toDoubleOrNull() ?: actualCash) - expectedCash
    val isBalanced = Math.abs(diff) < 1.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "দিন সমাপ্ত করুন (Close My Day)" else "Close My Day",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hisab Reconciliation Box
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isBalanced) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "bn") "প্রত্যাশিত ক্যাশ (Expected):" else "Expected Cash:",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", expectedCash)}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "bn") "প্রকৃত গোনা ক্যাশ (Actual):" else "Actual Physical Cash:",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.0f", actualCashInput.toDoubleOrNull() ?: actualCash)}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == "bn") "স্ট্যাটাস:" else "Status:",
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                        ) {
                            Text(
                                text = if (isBalanced) "✓ Balanced (হিসাব মিলেছে)"
                                else if (diff < 0) "⚠ Short ৳${String.format(Locale.US, "%,.0f", Math.abs(diff))}"
                                else "⚠ Extra ৳${String.format(Locale.US, "%,.0f", diff)}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = if (language == "bn") "আজকের আয়" else "Income", fontSize = 11.sp)
                        Text(
                            text = "+৳${String.format(Locale.US, "%,.0f", todayIncome)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = if (language == "bn") "আজকের খরচ" else "Expense", fontSize = 11.sp)
                        Text(
                            text = "-৳${String.format(Locale.US, "%,.0f", todayExpense)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = if (language == "bn") "হাওলাত দেওয়া" else "Lent", fontSize = 11.sp)
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", todayLent)}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = actualCashInput,
                onValueChange = { actualCashInput = it },
                label = { Text(if (language == "bn") "হাতে থাকা সঠিক ক্যাশ টাকা লিখুন (৳)" else "Enter Actual Physical Cash (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mood
            Text(
                text = if (language == "bn") "আজকের দিনের অনুভূতি (Mood)" else "Today's Mood",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "HAPPY" to "😊 চমৎকার",
                    "NORMAL" to "🙂 স্বাভাবিক",
                    "BUSY" to "🏃 ব্যস্ত",
                    "STRESSED" to "😓 ক্লান্ত"
                ).forEach { (m, label) ->
                    FilterChip(
                        selected = selectedMood == m,
                        onClick = { selectedMood = m },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "সারাদিনের অভিজ্ঞতা বা হিসেবের নোট" else "Closing Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = tomorrowReminder,
                onValueChange = { tomorrowReminder = it },
                label = { Text(if (language == "bn") "আগামীকালের কাজের রিমাইন্ডার" else "Tomorrow's Reminder") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val actual = actualCashInput.toDoubleOrNull() ?: actualCash
                    onCloseDay(actual, note, selectedMood, tomorrowReminder)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (language == "bn") "দিন সমাপ্ত করুন (CLOSE DAY)" else "CLOSE DAY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenominationsSheet(
    initialDenom: CashDenomination?,
    language: String,
    onDismiss: () -> Unit,
    onSave: (n1000: Int, n500: Int, n200: Int, n100: Int, n50: Int, n20: Int, n10: Int, n5: Int, n2: Int, n1: Int, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var n1000 by remember { mutableIntStateOf(initialDenom?.n1000 ?: 0) }
    var n500 by remember { mutableIntStateOf(initialDenom?.n500 ?: 0) }
    var n200 by remember { mutableIntStateOf(initialDenom?.n200 ?: 0) }
    var n100 by remember { mutableIntStateOf(initialDenom?.n100 ?: 0) }
    var n50 by remember { mutableIntStateOf(initialDenom?.n50 ?: 0) }
    var n20 by remember { mutableIntStateOf(initialDenom?.n20 ?: 0) }
    var n10 by remember { mutableIntStateOf(initialDenom?.n10 ?: 0) }
    var n5 by remember { mutableIntStateOf(initialDenom?.n5 ?: 0) }
    var n2 by remember { mutableIntStateOf(initialDenom?.n2 ?: 0) }
    var n1 by remember { mutableIntStateOf(initialDenom?.n1 ?: 0) }
    var note by remember { mutableStateOf(initialDenom?.note ?: "") }

    val total = (n1000 * 1000) + (n500 * 500) + (n200 * 200) + (n100 * 100) +
            (n50 * 50) + (n20 * 20) + (n10 * 10) + (n5 * 5) + (n2 * 2) + (n1 * 1)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "ক্যাশ নোট গণনা (Physical Cash)" else "Cash Denomination Counter",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = if (language == "bn") "আপনার মানিব্যাগ বা ড্রয়ারে থাকা নোটের সংখ্যা দিন, স্বয়ংক্রিয় মোট হিসাব হবে।"
                else "Count physical banknotes in Bangladesh denominations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Master Total Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "bn") "মোট নগদ ক্যাশ টাকা:" else "Total Physical Cash:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,d", total)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Denomination Rows
            DenomCounterRow("৳1000", n1000, 1000, Color(0xFF6B21A8)) { n1000 = it }
            DenomCounterRow("৳500", n500, 500, Color(0xFF0F766E)) { n500 = it }
            DenomCounterRow("৳200", n200, 200, Color(0xFFD97706)) { n200 = it }
            DenomCounterRow("৳100", n100, 100, Color(0xFF1D4ED8)) { n100 = it }
            DenomCounterRow("৳50", n50, 50, Color(0xFF9A3412)) { n50 = it }
            DenomCounterRow("৳20", n20, 20, Color(0xFF15803D)) { n20 = it }
            DenomCounterRow("৳10", n10, 10, Color(0xFFBE185D)) { n10 = it }
            DenomCounterRow("৳5", n5, 5, Color(0xFF475569)) { n5 = it }
            DenomCounterRow("৳2", n2, 2, Color(0xFF475569)) { n2 = it }
            DenomCounterRow("৳1", n1, 1, Color(0xFF475569)) { n1 = it }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "মন্তব্য / নোট" else "Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSave(n1000, n500, n200, n100, n50, n20, n10, n5, n2, n1, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "নোট গণনা সংরক্ষণ করুন" else "Save Note Count",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DenomCounterRow(
    label: String,
    count: Int,
    value: Int,
    noteColor: Color = MaterialTheme.colorScheme.primary,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(105.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = noteColor
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "৳${String.format(Locale.US, "%,d", count * value)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Quick preset chips: +1, +5, +10
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onCountChange(count + 1) }
            ) {
                Text(
                    text = "+1",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onCountChange(count + 5) }
            ) {
                Text(
                    text = "+5",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onCountChange(count + 10) }
            ) {
                Text(
                    text = "+10",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            IconButton(
                onClick = { if (count > 0) onCountChange(count - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
            }

            Text(
                text = count.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier.width(20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(
                onClick = { onCountChange(count + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailSheet(
    transaction: DailyTransaction?,
    accounts: List<MoneyAccount> = emptyList(),
    language: String,
    onDismiss: () -> Unit,
    onDelete: (Long) -> Unit,
    onDuplicate: ((DailyTransaction) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    if (transaction == null) return

    val fromAcc = accounts.find { it.id == transaction.accountId }
    val fromName = if (language == "bn" && !fromAcc?.nameBn.isNullOrBlank()) fromAcc.nameBn else fromAcc?.name ?: transaction.accountId
    val toAcc = accounts.find { it.id == transaction.toAccountId }
    val toName = if (language == "bn" && !toAcc?.nameBn.isNullOrBlank()) toAcc.nameBn else toAcc?.name ?: transaction.toAccountId

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "লেনদেনের বিস্তারিত" else "Transaction Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = transaction.type.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,.0f", transaction.amount)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    if (transaction.description.isNotEmpty()) {
                        Text(
                            text = transaction.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            DetailItemRow(
                label = if (language == "bn") "ক্যাটেগরি" else "Category",
                value = transaction.category
            )
            DetailItemRow(
                label = if (language == "bn") "তারিখ ও সময়" else "Date & Time",
                value = "${transaction.date} • ${transaction.time}"
            )
            DetailItemRow(
                label = if (language == "bn") "অ্যাকাউন্ট" else "Account",
                value = if (transaction.toAccountId != null) "$fromName → $toName" else fromName
            )
            if (!transaction.personName.isNullOrEmpty()) {
                DetailItemRow(
                    label = if (language == "bn") "ব্যক্তি" else "Person",
                    value = transaction.personName
                )
            }
            if (transaction.dueDate.isNotEmpty()) {
                DetailItemRow(
                    label = if (language == "bn") "পরিশোধের মেয়াদ" else "Due Date",
                    value = transaction.dueDate
                )
            }
            if (transaction.location.isNotEmpty()) {
                DetailItemRow(
                    label = if (language == "bn") "স্থান" else "Location",
                    value = transaction.location
                )
            }
            if (transaction.note.isNotEmpty()) {
                DetailItemRow(
                    label = if (language == "bn") "নোট" else "Note",
                    value = transaction.note
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onDuplicate != null) {
                    Button(
                        onClick = { onDuplicate(transaction) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Duplicate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == "bn") "অনুরূপ এন্ট্রি" else "Duplicate")
                    }
                }

                OutlinedButton(
                    onClick = {
                        val text = "Jibonify Daily: ${transaction.type.name} ৳${String.format(Locale.US, "%,.0f", transaction.amount)} (${transaction.category}) on ${transaction.date}"
                        clipboardManager.setText(AnnotatedString(text))
                        copied = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (copied) Icons.Default.Check else Icons.Default.Share, contentDescription = "Copy")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (copied) (if (language == "bn") "কপি হয়েছে" else "Copied") else (if (language == "bn") "শেয়ার / কপি" else "Copy"))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { onDelete(transaction.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (language == "bn") "লেনদেনটি মুছুন (Delete)" else "Delete Transaction")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAccountSheet(
    language: String,
    onDismiss: () -> Unit,
    onSave: (name: String, nameBn: String, type: AccountType, openingBalance: Double, accNumber: String, colorHex: Long, iconName: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var nameBn by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH) }
    var openingBalText by remember { mutableStateOf("0") }
    var accNumber by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "নতুন অ্যাকাউন্ট যোগ করুন" else "Add New Account",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (language == "bn") "অ্যাকাউন্টের নাম (ইংরেজি)" else "Account Name (English)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nameBn,
                onValueChange = { nameBn = it },
                label = { Text(if (language == "bn") "অ্যাকাউন্টের নাম (বাংলা)" else "Account Name (Bangla)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == "bn") "অ্যাকাউন্টের ধরন" else "Account Type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AccountType.values().forEach { t ->
                    FilterChip(
                        selected = selectedType == t,
                        onClick = { selectedType = t },
                        label = { Text(t.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = openingBalText,
                onValueChange = { openingBalText = it },
                label = { Text(if (language == "bn") "বর্তমান / প্রারম্ভিক ব্যালেন্স (৳)" else "Current Balance (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = accNumber,
                onValueChange = { accNumber = it },
                label = { Text(if (language == "bn") "অ্যাকাউন্ট নম্বর / নিকনেম" else "Account Number / Nickname") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val bal = openingBalText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(name, nameBn, selectedType, bal, accNumber, 0xFF10B981, "wallet")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "অ্যাকাউন্ট তৈরি করুন" else "Create Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonSheet(
    language: String,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "নতুন ব্যক্তি যোগ করুন" else "Add New Contact/Person",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (language == "bn") "পূর্ণ নাম" else "Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (language == "bn") "মোবাইল নম্বর" else "Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(if (language == "bn") "ইমেইল (ঐচ্ছিক)" else "Email (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (language == "bn") "নোট / পরিচয়" else "Notes / Relationship") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, phone, email, notes)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "ব্যক্তি সংরক্ষণ করুন" else "Save Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetSheet(
    language: String,
    onDismiss: () -> Unit,
    onSave: (category: String, amount: Double, period: BudgetPeriod) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var category by remember { mutableStateOf("Food") }
    var amountText by remember { mutableStateOf("8000") }
    var period by remember { mutableStateOf(BudgetPeriod.MONTHLY) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "বাজেট নির্ধারণ করুন" else "Set Category Budget",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(if (language == "bn") "ক্যাটেগরি" else "Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "বাজেট সীমা (৳)" else "Budget Limit (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BudgetPeriod.values().forEach { p ->
                    FilterChip(
                        selected = period == p,
                        onClick = { period = p },
                        label = { Text(p.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (category.isNotBlank() && amt > 0) {
                        onSave(category, amt, period)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "বাজেট সংরক্ষণ করুন" else "Save Budget",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsGoalSheet(
    language: String,
    onDismiss: () -> Unit,
    onSave: (name: String, targetAmount: Double, targetDate: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("2026-12-31") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "নতুন সঞ্চয় লক্ষ্য (Savings Goal)" else "New Savings Goal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (language == "bn") "লক্ষ্যের নাম (যেমন: নতুন ফোন, ঈদ কেনাকাটা)" else "Goal Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = targetAmountText,
                onValueChange = { targetAmountText = it },
                label = { Text(if (language == "bn") "টার্গেট টাকার পরিমাণ (৳)" else "Target Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = targetDate,
                onValueChange = { targetDate = it },
                label = { Text(if (language == "bn") "টার্গেট তারিখ (YYYY-MM-DD)" else "Target Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (language == "bn") "নোট" else "Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = targetAmountText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amt > 0) {
                        onSave(name, amt, targetDate, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "লক্ষ্য তৈরি করুন" else "Create Goal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdjustSavingsGoalSheet(
    goal: SavingsGoal?,
    accounts: List<MoneyAccount>,
    language: String,
    onDismiss: () -> Unit,
    onAdjust: (goal: SavingsGoal, amount: Double, isDeposit: Boolean, accountId: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (goal == null) return

    var isDeposit by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Target: ৳${String.format(Locale.US, "%,.0f", goal.targetAmount)} • Current: ৳${String.format(Locale.US, "%,.0f", goal.currentAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isDeposit,
                    onClick = { isDeposit = true },
                    label = { Text(if (language == "bn") "+ জমা করুন (Deposit)" else "+ Add Money") }
                )
                FilterChip(
                    selected = !isDeposit,
                    onClick = { isDeposit = false },
                    label = { Text(if (language == "bn") "- উত্তোলন করুন (Withdraw)" else "- Withdraw Money") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (language == "bn") "টাকার পরিমাণ (৳)" else "Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isDeposit) (if (language == "bn") "কোন অ্যাকাউন্ট থেকে টাকা জমা হবে?" else "From Account")
                else (if (language == "bn") "কোন অ্যাকাউন্টে টাকা স্থানান্তর হবে?" else "Deposit into Account"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = { Text(if (language == "bn") acc.nameBn else acc.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && selectedAccountId.isNotBlank()) {
                        onAdjust(goal, amt, isDeposit, selectedAccountId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "bn") "সম্পন্ন করুন" else "Confirm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSummarySheet(
    summaryText: String,
    language: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "bn") "হিসাব রিপোর্ট এক্সপোর্ট" else "Export Hisab Report",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = summaryText,
                    modifier = Modifier.padding(14.dp),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(summaryText))
                    copied = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(if (copied) Icons.Default.Check else Icons.Default.Share, contentDescription = "Copy")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (copied) (if (language == "bn") "কপি হয়েছে!" else "Copied to Clipboard!")
                else (if (language == "bn") "ক্লিপবোর্ডে কপি করুন" else "Copy Report Summary"))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

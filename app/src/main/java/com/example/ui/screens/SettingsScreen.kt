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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyUser
import com.example.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: String,
    user: DailyUser,
    loggedInUser: DailyUser? = null,
    isDarkMode: Boolean?,
    onLanguageChange: (String) -> Unit,
    onDarkModeChange: (Boolean?) -> Unit,
    onUpdateProfile: (name: String, phone: String, budget: Double) -> Unit,
    onExportReport: () -> Unit,
    onResetData: () -> Unit,
    onAdminPanelClick: (() -> Unit)? = null,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(user.name) }
    var editPhone by remember { mutableStateOf(user.phone) }
    var editBudget by remember { mutableStateOf(user.monthlyBudget.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == "bn") "সেটিংস ও প্রোফাইল" else "Settings & Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.testTag("settings_logout_btn")
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin Hub Shortcut (if user is Admin)
            if (loggedInUser?.role == UserRole.ADMIN && onAdminPanelClick != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAdminPanelClick() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == "bn") "এডমিন কন্ট্রোল প্যানেল" else "Admin Control Hub",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = if (language == "bn") "সকল ইউজার ও একাউন্ট পরিদর্শন করুন"
                                        else "Inspect all users & system accounts",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Text(
                                text = "➜",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // User Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (user.role == UserRole.ADMIN) Color(0xFF6366F1).copy(alpha = 0.2f)
                                            else Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = user.role.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (user.role == UserRole.ADMIN) Color(0xFF6366F1) else Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = user.phone,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = {
                                editName = user.name
                                editPhone = user.phone
                                editBudget = user.monthlyBudget.toString()
                                showEditProfileDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == "bn") "মাসিক বাজেট প্ল্যান:" else "Monthly Budget:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "৳ ${String.format(java.util.Locale.US, "%,.0f", user.monthlyBudget)}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Language Selection
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == "bn") "ভাষা নির্বাচন (Language)" else "Language",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageChange("bn") }
                        ) {
                            RadioButton(selected = language == "bn", onClick = { onLanguageChange("bn") })
                            Text(text = "বাংলা (Bengali)", fontSize = 14.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageChange("en") }
                        ) {
                            RadioButton(selected = language == "en", onClick = { onLanguageChange("en") })
                            Text(text = "English", fontSize = 14.sp)
                        }
                    }
                }
            }

            // Theme Selection
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = "Theme", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == "bn") "থিম ও ডিসপ্লে (Theme)" else "Theme & Display",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDarkModeChange(false) }
                        ) {
                            RadioButton(selected = isDarkMode == false, onClick = { onDarkModeChange(false) })
                            Text(text = if (language == "bn") "লাইট মোড (Light Mode)" else "Light Mode", fontSize = 14.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDarkModeChange(true) }
                        ) {
                            RadioButton(selected = isDarkMode == true, onClick = { onDarkModeChange(true) })
                            Text(text = if (language == "bn") "ডার্ক মোড (Dark Mode)" else "Dark Mode", fontSize = 14.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDarkModeChange(null) }
                        ) {
                            RadioButton(selected = isDarkMode == null, onClick = { onDarkModeChange(null) })
                            Text(text = if (language == "bn") "সিস্টেম ডিফল্ট (System Default)" else "System Default", fontSize = 14.sp)
                        }
                    }
                }
            }

            // Export & Data Management
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (language == "bn") "ডাটা ও এক্সপোর্ট" else "Data & Export",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onExportReport,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Export")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == "bn") "হিসাব রিপোর্ট এক্সপোর্ট / কপি করুন" else "Export Daily Hisab Summary")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == "bn") "ডিফল্ট একাউন্ট রিলোড করুন" else "Restore Default Accounts")
                        }
                    }
                }
            }

            // Security & Account Isolation Rule
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Security",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == "bn")
                                "নিরাপত্তা নীতি: আপনার একাউন্ট ও হিসাব সম্পূর্ণ সুরক্ষিত। কোউ কারো একাউন্ট দেখতে পাবে ণা এডমিন ছাড়া।"
                            else
                                "Privacy Policy: Nobody can see anyone else's account except Admin.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("settings_logout_action_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "bn") "লগ আউট করুন (Log Out)" else "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // About Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAboutDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Jibonify Daily", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "Your Money. Your Day. Your Complete Hisab.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(if (language == "bn") "লগ আউট নিশ্চিতকরণ" else "Confirm Logout", fontWeight = FontWeight.Bold) },
            text = { Text(if (language == "bn") "আপনি কি নিশ্চিতভাবে লগ আউট করতে চান?" else "Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (language == "bn") "লগ আউট" else "Log Out", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(if (language == "bn") "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text(if (language == "bn") "প্রোফাইল সম্পাদনা" else "Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBudget,
                        onValueChange = { editBudget = it },
                        label = { Text("Monthly Budget (৳)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val b = editBudget.toDoubleOrNull() ?: user.monthlyBudget
                    onUpdateProfile(editName, editPhone, b)
                    showEditProfileDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Data Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(if (language == "bn") "ডিফল্ট একাউন্ট রিলোড" else "Restore Accounts", fontWeight = FontWeight.Bold) },
            text = { Text(if (language == "bn") "আপনার বর্তমান প্রোফাইলের ডিফল্ট ক্যাশ ও মোবাইল ব্যাংকিং একাউন্টগুলো পুনরায় সাজানো হবে।" else "Restore realistic initial accounts for this profile.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Reload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Jibonify Daily", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Version 1.0 (Production Hisab Engine)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Your Money. Your Day. Your Complete Hisab.\n\nMulti-user account isolation with Admin oversight. Login & Logout system enabled.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

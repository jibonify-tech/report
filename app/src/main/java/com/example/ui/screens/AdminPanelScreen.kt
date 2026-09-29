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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyUser
import com.example.data.model.MoneyAccount
import com.example.data.model.UserRole
import java.util.Locale

@Composable
fun AdminPanelScreen(
    language: String,
    currentUser: DailyUser,
    allUsers: List<DailyUser>,
    allAccounts: List<MoneyAccount>,
    inspectingUserId: String?,
    onInspectUser: (String?) -> Unit,
    onToggleUserStatus: (DailyUser) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val totalSystemAccounts = allAccounts.size
    val totalSystemBalance = allAccounts.sumOf { it.currentBalance }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "bn") "এডমিন কন্ট্রোল প্যানেল" else "Admin Control Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = if (language == "bn") "সম্পূর্ণ সিস্টেম ও ইউজার একাউন্ট নিয়ন্ত্রণ"
                            else "Full System & User Accounts Oversight",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.testTag("admin_logout_btn")
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
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
            // 1. Current Inspection Banner (if inspecting any user)
            if (inspectingUserId != null) {
                val targetUser = allUsers.find { it.id == inspectingUserId }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (language == "bn") "🔍 বর্তমানে পরিদর্শন করা হচ্ছে:" else "🔍 Currently Inspecting:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "${targetUser?.name ?: inspectingUserId} (${targetUser?.phone})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }

                                Button(
                                    onClick = { onInspectUser(null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (language == "bn") "রিসেট করুন" else "Reset")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = onNavigateToDashboard,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (language == "bn") "এই ইউজারের ড্যাশবোর্ড ও হিসাব দেখুন" else "View User's Full Hisab Dashboard")
                            }
                        }
                    }
                }
            }

            // 2. System Overview Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (language == "bn") "মোট ইউজার" else "Total Users",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${allUsers.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (language == "bn") "সিস্টেম একাউন্ট" else "Accounts",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalSystemAccounts টি",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // 3. Privacy Rule Notice
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Shield",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == "bn")
                                "নিয়ম: সাধারণ ইউজাররা একে অপরের একাউন্ট দেখতে পারে না। শুধুমাত্র এডমিন হিসেবে আপনি যেকোনো ইউজারের একাউন্ট ও হিসাব পরিদর্শন করতে পারেন।"
                            else
                                "Privacy Rule: Regular users cannot see each other's accounts. As Admin, you have privilege to inspect any user's financial accounts and hisab.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 4. User Accounts List Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "bn") "নিবন্ধিত ইউজার তালিকা (${allUsers.size})" else "Registered Users (${allUsers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == "bn") "পরিদর্শন করতে ক্লিক করুন" else "Tap to inspect hisab",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 5. User Cards
            items(allUsers) { user ->
                val userAccounts = allAccounts.filter { it.userId == user.id }
                val userTotalBalance = userAccounts.sumOf { it.currentBalance }
                val isSelectedForInspect = inspectingUserId == user.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (user.role != UserRole.ADMIN) {
                                onInspectUser(user.id)
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelectedForInspect) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
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
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (user.role == UserRole.ADMIN) Color(0xFF6366F1)
                                            else Color(0xFF10B981)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.take(1).uppercase(Locale.getDefault()),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
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
                                        text = "${user.phone} • ${user.email}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (user.isActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (user.isActive) (if (language == "bn") "সক্রিয়" else "Active") else (if (language == "bn") "স্থগিত" else "Blocked"),
                                    color = if (user.isActive) Color(0xFF10B981) else Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Account metrics summary for this user
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == "bn") "একাউন্ট সংখ্যা" else "Accounts",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${userAccounts.size} টি",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (language == "bn") "মোট ব্যালেন্স" else "Total Balance",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", userTotalBalance)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onInspectUser(user.id)
                                    onNavigateToDashboard()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = "Inspect", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == "bn") "হিসাব দেখুন" else "Inspect Hisab", fontSize = 12.sp)
                            }

                            if (user.id != currentUser.id) {
                                OutlinedButton(
                                    onClick = { onToggleUserStatus(user) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (user.isActive) MaterialTheme.colorScheme.error else Color(0xFF10B981)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = "Status",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (user.isActive) (if (language == "bn") "ব্লক করুন" else "Block")
                                        else (if (language == "bn") "আনব্লক" else "Unblock"),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

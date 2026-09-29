package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyUser
import com.example.data.model.OutsideSession
import com.example.data.model.UserRole
import java.util.Calendar

@Composable
fun DailyTopBar(
    user: DailyUser,
    loggedInUser: DailyUser?,
    inspectingUserId: String?,
    activeOutsideSession: OutsideSession?,
    language: String,
    unreadNotificationsCount: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onOutsideModeClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> if (language == "bn") "শুভ সকাল" else "Good Morning"
        hour < 17 -> if (language == "bn") "শুভ দুপুর" else "Good Afternoon"
        hour < 20 -> if (language == "bn") "শুভ সন্ধ্যা" else "Good Evening"
        else -> if (language == "bn") "শুভ রাত্রি" else "Good Night"
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Text(
                    text = if (language == "bn") "লগ আউট নিশ্চিতকরণ" else "Confirm Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == "bn")
                        "আপনি কি নিশ্চিতভাবে আপনার একাউন্ট থেকে লগ আউট করতে চান?"
                    else
                        "Are you sure you want to log out of your account?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (language == "bn") "লগ আউট করুন" else "Log Out", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(if (language == "bn") "বাতিল" else "Cancel")
                }
            }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Inspection Bar (Shown to Admin when inspecting another user's accounts)
            if (inspectingUserId != null && loggedInUser?.role == UserRole.ADMIN) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onAdminPanelClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Inspection",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "bn") "এডমিন মোড: [${user.name}]-এর হিসাব দেখছেন"
                                else "Admin Mode: Inspecting [${user.name}]",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Text(
                            text = if (language == "bn") "এডমিন প্যানেল ➜" else "Admin Hub ➜",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User Avatar and Greeting
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        if (user.role == UserRole.ADMIN) Color(0xFF6366F1) else MaterialTheme.colorScheme.primary,
                                        if (user.role == UserRole.ADMIN) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(java.util.Locale.getDefault()),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$greeting, ${user.name.split(" ").firstOrNull() ?: "Friend"}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (user.role == UserRole.ADMIN) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF6366F1).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6366F1),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (language == "bn") "আজকের দৈনন্দিন হিসাব" else "Daily Money Dashboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Actions: Admin Hub (if Admin), Outside indicator, Search, Notifications, Language, Settings, Logout
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (loggedInUser?.role == UserRole.ADMIN) {
                        IconButton(
                            onClick = onAdminPanelClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("topbar_admin_btn")
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Hub",
                                tint = Color(0xFF6366F1)
                            )
                        }
                    }

                    if (activeOutsideSession != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .clickable { onOutsideModeClick() }
                                .padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DirectionsWalk,
                                    contentDescription = "Outside",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "bn") "বাইরে" else "Out",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    IconButton(onClick = onSearchClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge {
                                    Text(text = unreadNotificationsCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNotificationsClick, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clickable { onLanguageToggle() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = "Language",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (language == "bn") "বাং" else "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    IconButton(onClick = onSettingsClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("topbar_logout_btn")
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

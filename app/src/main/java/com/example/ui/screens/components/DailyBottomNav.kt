package com.example.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainDailyTab

@Composable
fun DailyBottomNav(
    selectedTab: MainDailyTab,
    onTabSelected: (MainDailyTab) -> Unit,
    language: String
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        // Tab 1: Dashboard
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.DASHBOARD,
            onClick = { onTabSelected(MainDailyTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = {
                Text(
                    text = if (language == "bn") "হোম" else "Home",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab 2: Today's Hub
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.TODAY_HUB,
            onClick = { onTabSelected(MainDailyTab.TODAY_HUB) },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Today") },
            label = {
                Text(
                    text = if (language == "bn") "আজকের হিসাব" else "Today",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.TODAY_HUB) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab 3: Transactions
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.TRANSACTIONS,
            onClick = { onTabSelected(MainDailyTab.TRANSACTIONS) },
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Transactions") },
            label = {
                Text(
                    text = if (language == "bn") "লেনদেন" else "History",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.TRANSACTIONS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab 4: Accounts & Budgets
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.ACCOUNTS_BUDGETS,
            onClick = { onTabSelected(MainDailyTab.ACCOUNTS_BUDGETS) },
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Accounts") },
            label = {
                Text(
                    text = if (language == "bn") "অ্যাকাউন্ট" else "Accounts",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.ACCOUNTS_BUDGETS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab 5: People & Debt
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.PEOPLE_DEBT,
            onClick = { onTabSelected(MainDailyTab.PEOPLE_DEBT) },
            icon = { Icon(Icons.Default.Group, contentDescription = "People") },
            label = {
                Text(
                    text = if (language == "bn") "দেনা-পাওনা" else "People",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.PEOPLE_DEBT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab 6: Reports
        NavigationBarItem(
            selected = selectedTab == MainDailyTab.REPORTS,
            onClick = { onTabSelected(MainDailyTab.REPORTS) },
            icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
            label = {
                Text(
                    text = if (language == "bn") "রিপোর্ট" else "Reports",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == MainDailyTab.REPORTS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

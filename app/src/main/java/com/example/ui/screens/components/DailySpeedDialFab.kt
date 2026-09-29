package com.example.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SheetType

@Composable
fun DailySpeedDialFab(
    language: String,
    onOpenSheet: (SheetType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(),
        label = "fab_rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Scrim overlay when expanded
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = false }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            // Speed Dial Items
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpeedDialMiniItem(
                        label = if (language == "bn") "বাইরে যান (Leave Home)" else "Leave Home",
                        icon = Icons.Default.DirectionsWalk,
                        containerColor = Color(0xFF8B5CF6),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.LEAVE_HOME)
                        }
                    )

                    SpeedDialMiniItem(
                        label = if (language == "bn") "নোট গণনা (Denominations)" else "Count Cash Notes",
                        icon = Icons.Default.PointOfSale,
                        containerColor = Color(0xFF0D9488),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.DENOMINATIONS)
                        }
                    )

                    SpeedDialMiniItem(
                        label = if (language == "bn") "হাওলাত / ঋণ (Lend & Borrow)" else "Lend / Borrow",
                        icon = Icons.Default.Payments,
                        containerColor = Color(0xFFD97706),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.LEND)
                        }
                    )

                    SpeedDialMiniItem(
                        label = if (language == "bn") "টাকা স্থানান্তর (Transfer)" else "Money Transfer",
                        icon = Icons.Default.SwapHoriz,
                        containerColor = Color(0xFF2563EB),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.TRANSFER)
                        }
                    )

                    SpeedDialMiniItem(
                        label = if (language == "bn") "আয় যোগ করুন (Income)" else "Add Income",
                        icon = Icons.Default.ArrowDownward,
                        containerColor = Color(0xFF10B981),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.QUICK_INCOME)
                        }
                    )

                    SpeedDialMiniItem(
                        label = if (language == "bn") "দ্রুত খরচ (Expense)" else "Quick Expense",
                        icon = Icons.Default.ArrowUpward,
                        containerColor = Color(0xFFEF4444),
                        onClick = {
                            isExpanded = false
                            onOpenSheet(SheetType.QUICK_EXPENSE)
                        }
                    )
                }
            }

            // Main Trigger FAB
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("fab_speed_dial")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) "Close Quick Actions" else "Open Quick Actions",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Composable
private fun SpeedDialMiniItem(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 3.dp,
            modifier = Modifier.clickable { onClick() }
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = containerColor,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(42.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

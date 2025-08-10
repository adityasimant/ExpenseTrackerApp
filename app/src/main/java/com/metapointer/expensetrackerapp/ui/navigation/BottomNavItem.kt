package com.metapointer.expensetrackerapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Expenses",
        icon = Icons.Default.List,
        screen = Screen.ExpenseList
    ),
    BottomNavItem(
        title = "Reports",
        icon = Icons.Default.Analytics,
        screen = Screen.ExpenseReport
    )
)
package com.metapointer.expensetrackerapp.ui.screens.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.metapointer.expensetrackerapp.ui.navigation.ExpenseTrackerNavigation
import com.metapointer.expensetrackerapp.ui.navigation.Screen
import com.metapointer.expensetrackerapp.ui.navigation.bottomNavItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = Screen.fromRoute(currentRoute)

    // Function to navigate to add expense screen
    val navigateToEntry = {
        navController.navigate(Screen.ExpenseEntry.route)
    }

    // navigate to reports
    val navigateToReports = {
        navController.navigate(Screen.ExpenseReport.route)
    }

    // Function to navigate back
    val navigateBack = {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            Screen.ExpenseList -> "Expense Tracker"
                            Screen.ExpenseEntry -> "Add Expense"
                            Screen.ExpenseReport -> "Expense Reports"
                        },
                    )
                },
                navigationIcon = {
                    if (currentScreen == Screen.ExpenseEntry) {
                        IconButton(onClick = {
                            navigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Only show bottom bar on main screens (not on entry screen)
            if (currentScreen != Screen.ExpenseEntry) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = currentScreen == item.screen,
                            onClick = {
                                if (currentScreen != item.screen) {
                                    navController.navigate(item.screen.route) {
                                        // Restore state when re-selecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(innerPadding)
        ) {
            ExpenseTrackerNavigation(
                navController = navController,
                onNavigateToEntry = navigateToEntry,
                onNavigateToReports = navigateToReports
            )
        }
    }
}

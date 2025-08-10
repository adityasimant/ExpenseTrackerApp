package com.metapointer.expensetrackerapp.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.metapointer.expensetrackerapp.ui.screens.entry.ExpenseEntryScreen
import com.metapointer.expensetrackerapp.ui.screens.list.ExpenseListScreen
import com.metapointer.expensetrackerapp.ui.screens.report.ExpenseReportScreen

@Composable
fun ExpenseTrackerNavigation(
    navController: NavHostController,
    onNavigateToEntry: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExpenseList.route
    ) {
        composable(Screen.ExpenseList.route) {
            ExpenseListScreen(
                onAddExpenseClick = {
                    onNavigateToEntry()
                },
                onViewReportsClick = {
                    onNavigateToReports()
                }
            )
        }

        composable(Screen.ExpenseEntry.route) {
            ExpenseEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ExpenseReport.route) {
            ExpenseReportScreen()
        }
    }
}
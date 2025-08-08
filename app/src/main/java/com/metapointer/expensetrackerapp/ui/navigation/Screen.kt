package com.metapointer.expensetrackerapp.ui.navigation

sealed class Screen(val route: String) {
    object ExpenseList : Screen("expense_list")
    object ExpenseEntry : Screen("expense_entry")
    object ExpenseReport : Screen("expense_report")

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                ExpenseList.route -> ExpenseList
                ExpenseEntry.route -> ExpenseEntry
                ExpenseReport.route -> ExpenseReport
                else -> ExpenseList
            }
        }
    }
}
package com.metapointer.expensetrackerapp.ui.screens.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.metapointer.expensetrackerapp.data.model.Expense
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.ui.screens.entry.emoji
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel = hiltViewModel(),
    onAddExpenseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val todayTotal by viewModel.todayTotal.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var groupBy by remember { mutableStateOf(GroupByOption.TIME) }
    var showGroupByMenu by remember { mutableStateOf(false) }

    val filteredExpenses = remember(expenses, selectedDate) {
        expenses.filter { expense ->
            Instant.ofEpochMilli(expense.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == selectedDate
        }

    }

    val groupedExpenses = remember(filteredExpenses, groupBy) {
        when (groupBy) {
            GroupByOption.TIME -> filteredExpenses.groupBy {
                Instant.ofEpochMilli(it.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            GroupByOption.CATEGORY -> filteredExpenses.groupBy { it.category }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Group By Menu
        TextButton(
            onClick = {
                showGroupByMenu = true
            }
        ) {
            Icon(
                Icons.Filled.Sort,
                contentDescription = "Filter",
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Filter")
        }
        Box {
//            IconButton(onClick = {  }) {
//                Icon(
//                    imageVector = Icons.Default.Sort,
//                    contentDescription = "Group by"
//                )
//            }

            DropdownMenu(
                expanded = showGroupByMenu,
                onDismissRequest = { showGroupByMenu = false }
            ) {
                GroupByOption.values().forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            groupBy = option
                            showGroupByMenu = false
                        },
                        leadingIcon = {
                            if (groupBy == option) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Date Selector and Summary
            DateSelectorCard(
                selectedDate = selectedDate,
                todayTotal = if (selectedDate == LocalDate.now()) todayTotal else
                    filteredExpenses.sumOf { it.amount },
                expenseCount = filteredExpenses.size,
                onDateClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            if (filteredExpenses.isEmpty()) {
                EmptyState(
                    isToday = selectedDate == LocalDate.now(),
                    onAddExpenseClick = onAddExpenseClick
                )
            } else {
                ExpenseList(
                    groupedExpenses = groupedExpenses,
                    groupBy = groupBy,
                    onDeleteExpense = viewModel::deleteExpense
                )
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Error Message
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            delay(3000)
            viewModel.clearError()
        }

        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = viewModel::clearError) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(message)
        }
    }
}

@Composable
private fun EmptyState(
    isToday: Boolean,
    onAddExpenseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isToday) "No expenses today" else "No expenses found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isToday) {
                "Start tracking your daily expenses to get insights into your spending habits"
            } else {
                "No expenses recorded for this date"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        /*
        If needed can add button to promt user...not needed now ig

        Button(
            onClick = onAddExpenseClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add First Expense")
        }
        */

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateSelectorCard(
    selectedDate: LocalDate,
    todayTotal: Double,
    expenseCount: Int,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (selectedDate) {
                            LocalDate.now() -> "Today"
                            LocalDate.now().minusDays(1) -> "Yesterday"
                            else -> selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                TextButton(
                    onClick = {
                        onDateClick()
                    }
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Select Date",
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Select Date")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(
                    label = "Total Spent",
                    value = "₹${String.format("%.2f", todayTotal)}",
                    icon = Icons.Default.AccountBalanceWallet
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                )

                SummaryItem(
                    label = "Transactions",
                    value = expenseCount.toString(),
                    icon = Icons.Default.Receipt
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpenseList(
    groupedExpenses: Map<out Any, List<Expense>>,
    groupBy: GroupByOption,
    onDeleteExpense: (Expense) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedExpenses.forEach { (group, expenses) ->
            item {
                Text(
                    text = when (groupBy) {
                        GroupByOption.TIME -> group as String
                        GroupByOption.CATEGORY -> (group as ExpenseCategory).displayName
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(
                items = expenses,
                key = { it.id }
            ) { expense ->
                ExpenseItem(
                    expense = expense,
                    onDeleteClick = { onDeleteExpense(expense) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpenseItem(
    expense: Expense,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { Text(expense.title) },
        supportingContent = {
            Column {
                if (expense.notes.isNotEmpty()) {
                    Text(expense.notes, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }

                Text(
                    Instant.ofEpochMilli(expense.date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                )

                if (expense.receiptImagePath != null) {
                    Row(
                        Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Has Receipt",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Receipt attached",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = expense.category.emoji,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        trailingContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "₹${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
    )
    HorizontalDivider()
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.weight(1f)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .background(
//                            MaterialTheme.colorScheme.secondaryContainer,
//                            CircleShape
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = expense.category.emoji,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = expense.title,
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Medium,
//                        maxLines = 1
//                    )
//
//                    if (expense.notes.isNotEmpty()) {
//                        Text(
//                            text = expense.notes,
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                            maxLines = 1
//                        )
//                    }
//
//                    Text(
//                        text = Instant.ofEpochMilli(expense.date)
//                            .atZone(ZoneId.systemDefault())
//                            .toLocalDateTime()
//                            .format(DateTimeFormatter.ofPattern("HH:mm")),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "₹${String.format("%.2f", expense.amount)}",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//
//                IconButton(
//                    onClick = { showDeleteDialog = true },
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(18.dp)
//                    )
//                }
//            }
//        }
//
//        if (expense.receiptImagePath != null) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 4.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Spacer(modifier = Modifier.width(52.dp))
//                Icon(
//                    imageVector = Icons.Default.AttachFile,
//                    contentDescription = "Has Receipt",
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "Receipt attached",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete '${expense.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


enum class GroupByOption(val displayName: String) {
    TIME("Group by Time"),
    CATEGORY("Group by Category")
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ExpenseItemPreview() {
    MaterialTheme {
        ExpenseItem(
            expense = Expense(
                id = 1,
                title = "Dinner at Pizza Place",
                amount = 599.99,
                category = ExpenseCategory.FOOD,
                notes = "Celebration dinner with friends",
                receiptImagePath = "/storage/emulated/0/Download/receipt.jpg",
                date = System.currentTimeMillis() - 3600000L, // 1 hour ago
                createdAt = System.currentTimeMillis()
            ),
            onDeleteClick = {}
        )
    }
}


package com.metapointer.expensetrackerapp.ui.screens.entry



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val todayTotal by viewModel.todayTotal.collectAsState()

    LaunchedEffect(uiState.isExpenseAdded) {
        if (uiState.isExpenseAdded) {
            delay(1500) // Show success message for 1.5 seconds
            viewModel.clearSuccessState()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
//        // Top App Bar
//        TopAppBar(
//            title = {
//                Text(
//                    text = "Add Expense",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Medium
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = onNavigateBack) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.surface
//            )
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Today's Total Card
            TodayTotalCard(todayTotal = todayTotal)

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            ExpenseFormFields(
                uiState = uiState,
                onTitleChanged = viewModel::onTitleChanged,
                onAmountChanged = viewModel::onAmountChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onNotesChanged = viewModel::onNotesChanged,
                onReceiptImageSelected = viewModel::onReceiptImageSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            SubmitButton(
                uiState = uiState,
                onSubmit = viewModel::addExpense
            )

            // Error/Success Messages
            MessageDisplay(
                uiState = uiState,
                onClearError = viewModel::clearError
            )
        }
    }
}

@Composable
private fun TodayTotalCard(todayTotal: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today's Total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "₹${String.format("%.2f", todayTotal)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ExpenseFormFields(
    uiState: ExpenseEntryUiState,
    onTitleChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCategorySelected: (ExpenseCategory) -> Unit,
    onNotesChanged: (String) -> Unit,
    onReceiptImageSelected: (String?) -> Unit
) {
    // Title Field
    OutlinedTextField(
        value = uiState.title,
        onValueChange = onTitleChanged,
        label = { Text("Title") },
        placeholder = { Text("Enter expense title") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = uiState.title.isBlank() && uiState.errorMessage != null,
        supportingText = if (uiState.title.isBlank() && uiState.errorMessage != null) {
            { Text("Title is required") }
        } else null
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Amount Field
    OutlinedTextField(
        value = uiState.amount,
        onValueChange = onAmountChanged,
        label = { Text("Amount") },
        placeholder = { Text("0.00") },
        leadingIcon = {
            Text(
                text = "₹",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        isError = (uiState.amount.toDoubleOrNull() == null && uiState.amount.isNotEmpty()) ||
                (uiState.amount.isBlank() && uiState.errorMessage != null),
        supportingText = {
            when {
                uiState.amount.isNotEmpty() && uiState.amount.toDoubleOrNull() == null ->
                    Text("Please enter a valid amount")
                uiState.amount.isBlank() && uiState.errorMessage != null ->
                    Text("Amount is required")
                else -> null
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Category Selection
    CategorySelector(
        selectedCategory = uiState.selectedCategory,
        onCategorySelected = onCategorySelected
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Notes Field
    OutlinedTextField(
        value = uiState.notes,
        onValueChange = onNotesChanged,
        label = { Text("Notes (Optional)") },
        placeholder = { Text("Add a note...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 4,
        supportingText = { Text("${uiState.notes.length}/100") }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Receipt Image Upload (Mock)
    ReceiptImageUpload(
        receiptImagePath = uiState.receiptImagePath,
        onReceiptImageSelected = onReceiptImageSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    selectedCategory: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ExpenseCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.emoji,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(category.displayName)
                        }
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReceiptImageUpload(
    receiptImagePath: String?,
    onReceiptImageSelected: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (receiptImagePath != null) {
                // Mock receipt display
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Receipt",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Receipt attached",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { onReceiptImageSelected(null) }
                ) {
                    Text("Remove")
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upload Receipt (Optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        // Mock image selection
                        onReceiptImageSelected("mock_receipt_path.jpg")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo")
                }
            }
        }
    }
}

@Composable
private fun SubmitButton(
    uiState: ExpenseEntryUiState,
    onSubmit: () -> Unit
) {
    val isFormValid = uiState.title.isNotBlank() &&
            uiState.amount.isNotBlank() &&
            uiState.amount.toDoubleOrNull() != null &&
            uiState.amount.toDoubleOrNull()!! > 0

    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth(),
        enabled = isFormValid && !uiState.isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = if (uiState.isLoading) "Adding..." else "Add Expense",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun MessageDisplay(
    uiState: ExpenseEntryUiState,
    onClearError: () -> Unit
) {
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            delay(3000)
            onClearError()
        }
    }

    uiState.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }

    uiState.successMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Extension property for ExpenseCategory
val ExpenseCategory.displayName: String
    get() = when (this) {
        ExpenseCategory.STAFF -> "Staff"
        ExpenseCategory.TRAVEL -> "Travel"
        ExpenseCategory.FOOD -> "Food"
        ExpenseCategory.UTILITY -> "Utility"
    }

val ExpenseCategory.emoji: String
    get() = when (this) {
        ExpenseCategory.STAFF -> "👥"
        ExpenseCategory.TRAVEL -> "🚗"
        ExpenseCategory.FOOD -> "🍽️"
        ExpenseCategory.UTILITY -> "⚡"
    }


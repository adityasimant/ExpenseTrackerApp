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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.utils.formatAmountIndian
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val todayTotal by viewModel.todayTotal.collectAsState()

    // Animation states
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val successScale by animateFloatAsState(
        targetValue = if (showSuccessAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "success_scale"
    )

    val successAlpha by animateFloatAsState(
        targetValue = if (showSuccessAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "success_alpha"
    )

    LaunchedEffect(uiState.isExpenseAdded) {
        if (uiState.isExpenseAdded) {
            showSuccessAnimation = true
            delay(1800) // Show animation for 1.8 seconds
            viewModel.clearSuccessState()
            onNavigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Today's Total Card
                TodayTotalCard(todayTotal = todayTotal)

                Spacer(modifier = Modifier.height(12.dp))

                // Error/Success Messages
                MessageDisplay(
                    uiState = uiState,
                    onClearError = viewModel::clearError
                )

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
                    onSubmit = {
                        viewModel.addExpense()
                    }
                )
            }
        }

        // Success Animation Overlay
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f * successAlpha)),
                contentAlignment = Alignment.Center
            ) {
                SuccessAnimationContent(
                    scale = successScale,
                    alpha = successAlpha
                )
            }
        }
    }
}

@Composable
private fun SuccessAnimationContent(
    scale: Float,
    alpha: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
    ) {
        // Animated checkmark circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Expense Added!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Successfully saved to your records",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TodayTotalCard(todayTotal: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today's total",
                )
                Text(
                    text = "₹${formatAmountIndian(todayTotal)}",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
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
    val validationState = uiState.validationState

    // Title Field
    OutlinedTextField(
        value = uiState.title,
        onValueChange = onTitleChanged,
        label = { Text("Title") },
        placeholder = { Text("Enter expense title") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = !validationState.titleValidation.isValid,
        supportingText = {
            validationState.titleValidation.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } ?: Text("${uiState.title.length}/50")
        },
        trailingIcon = {
            if (!validationState.titleValidation.isValid) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

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
        isError = !validationState.amountValidation.isValid,
        supportingText = {
            validationState.amountValidation.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            if (!validationState.amountValidation.isValid) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Category Selection with validation
    CategorySelector(
        selectedCategory = uiState.selectedCategory,
        onCategorySelected = onCategorySelected
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Notes Field with enhanced validation
    OutlinedTextField(
        value = uiState.notes,
        onValueChange = onNotesChanged,
        label = { Text("Notes (Optional)") },
        placeholder = { Text("Add a note...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 4,
        isError = !validationState.notesValidation.isValid,
        supportingText = {
            if (!validationState.notesValidation.isValid) {
                validationState.notesValidation.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Text(
                    text = "${uiState.notes.length}/200",
                    color = if (uiState.notes.length > 180) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Receipt Image Upload
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
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
    val isFormValid = uiState.validationState.isFormValid

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

// Extension properties for ExpenseCategory
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

@Preview
@Composable
private fun PreviewSuccessAnimation() {
    SuccessAnimationContent(scale = 1f, alpha = 1f)
}

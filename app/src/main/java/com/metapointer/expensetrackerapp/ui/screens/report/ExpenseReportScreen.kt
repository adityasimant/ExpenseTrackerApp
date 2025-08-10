package com.metapointer.expensetrackerapp.ui.screens.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.metapointer.expensetrackerapp.data.model.DailyExpenseSummary
import com.metapointer.expensetrackerapp.utils.formatAmountIndian
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedChartType by remember { mutableStateOf(ChartType.BAR) }
    var selectedFilter by remember { mutableStateOf(FilterType.LAST_WEEK) }
    var showDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableLongStateOf(0L) }
    var endDate by remember { mutableLongStateOf(0L) }
    var showExportDialog by remember { mutableStateOf(false) }



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Total and Filters
        item {
            ExpenseReportHeader(
                totalExpense = uiState.totalWeekExpense,
                selectedFilter = selectedFilter,
                onFilterChange = { filter ->
                    selectedFilter = filter
                    when (filter) {
                        FilterType.LAST_WEEK -> viewModel.loadLastWeekReport()
                        FilterType.CUSTOM -> showDatePicker = true
                    }
                },
                onExportClick = { showExportDialog = true }
            )
        }

        item {
            ChartConfigurationAccordion(
                selectedType = selectedChartType,
                onTypeChange = { selectedChartType = it },
                isDemoMode = uiState.isDemoMode,
                onDemoModeToggle = { viewModel.toggleDemoMode() }
            )
        }


        // Chart Section
        item {
            ExpenseChart(
                dailySummaries = uiState.dailySummaries,
                chartType = selectedChartType,
                isLoading = uiState.isLoading
            )
        }

        // Daily Totals Section
        item {
            DailyTotalsSection(
                dailySummaries = uiState.dailySummaries,
                isLoading = uiState.isLoading
            )
        }

        // Category-wise Totals Section
        item {
            CategoryTotalsSection(
                dailySummaries = uiState.dailySummaries,
                isLoading = uiState.isLoading
            )
        }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateRangeSelected = { start, end ->
                startDate = start
                endDate = end
                viewModel.loadCustomDateReport(start, end)
                showDatePicker = false
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            onExportPDF = {
                // Handle PDF export
                showExportDialog = false
            },
            onExportCSV = {
                // Handle CSV export
                showExportDialog = false
            }
        )
    }
}

@Composable
private fun ChartConfigurationAccordion(
    selectedType: ChartType,
    onTypeChange: (ChartType) -> Unit,
    isDemoMode: Boolean,
    onDemoModeToggle: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Accordion Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chart Configuration"
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                )
            }

            // Accordion Content with Animation
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Chart Type Segmented Buttons
                    Column {
                        Text(
                            text = "Chart Type",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SegmentedButtonRow(
                            selectedType = selectedType,
                            onTypeChange = onTypeChange
                        )
                    }

                    // Demo Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Demo Mode",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = "Show sample data for last 7 days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Switch(
                            checked = isDemoMode,
                            onCheckedChange = { onDemoModeToggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Demo Mode Indicator
                    if (isDemoMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Currently showing demo data",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// Segmented Button Row Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedButtonRow(
    selectedType: ChartType,
    onTypeChange: (ChartType) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        ChartType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = ChartType.entries.size
                ),
                onClick = { onTypeChange(type) },
                selected = selectedType == type,
                icon = {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            ) {
                Text(
                    text = type.displayName.replace(" Chart", ""),
                )
            }
        }
    }
}


@Composable
private fun ExpenseReportHeader(
    totalExpense: Double,
    selectedFilter: FilterType,
    onFilterChange: (FilterType) -> Unit,
    onExportClick: () -> Unit
) {
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
                    text = "Total Expenses",
                )
                Text(
                    text = "₹${formatAmountIndian(totalExpense)}",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            IconButton(
                onClick = onExportClick
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export",
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        /*
        We can have filter chips to see data for custom date ranges

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FilterType.entries) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = {
                        Text(
                            text = filter.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = if (selectedFilter == filter) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }
        }
         */
    }
}


@Preview
@Composable
private fun PreviewHeader() {
    ExpenseReportHeader(
        800.0,
        selectedFilter = FilterType.LAST_WEEK,
        onFilterChange = { },
        onExportClick = {

        },
    )
}

@Composable
private fun ChartTypeSelector(
    selectedType: ChartType,
    onTypeChange: (ChartType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Chart Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartType.entries.forEach { type ->
                    OutlinedButton(
                        onClick = { onTypeChange(type) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedType == type)
                                MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            contentColor = if (selectedType == type)
                                MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = type.displayName)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseChart(
    dailySummaries: List<DailyExpenseSummary>,
    chartType: ChartType,
    isLoading: Boolean
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Expense Trend",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (dailySummaries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                when (chartType) {
                    ChartType.BAR -> {
                        ColumnChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 8.dp),
                            labelProperties = LabelProperties(
                                textStyle = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                enabled = true
                            ),
                            gridProperties = GridProperties(
                                enabled = false
                            ),
                            animationDelay = 10,
                            indicatorProperties = HorizontalIndicatorProperties(
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ),
                            labelHelperProperties = LabelHelperProperties(
                                textStyle = TextStyle.Default.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            ),
                            data = dailySummaries.map { summary ->
                                Bars(
                                    label = try {
                                        val parsedDate = SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).parse(summary.date)
                                        SimpleDateFormat("dd/MM", Locale.getDefault()).format(
                                            parsedDate ?: Date()
                                        )
                                    } catch (e: Exception) {
                                        summary.date
                                    },
                                    values = listOf(
                                        Bars.Data(
                                            value = summary.totalAmount,
                                            color = SolidColor(MaterialTheme.colorScheme.primary)
                                        )
                                    )
                                )
                            },
                            barProperties = BarProperties(
                                cornerRadius = Bars.Data.Radius.Rectangle(
                                    topRight = 6.dp,
                                    topLeft = 6.dp
                                ),
                                spacing = 2.dp,
                                thickness = 16.dp
                            ),
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    ChartType.LINE -> {
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 8.dp),
                            labelHelperProperties = LabelHelperProperties(
                                textStyle = TextStyle.Default.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ),
                            animationDelay = 10,
                            data = listOf(
                                Line(
                                    label = "Daily Expenses",
                                    values = dailySummaries.map { it.totalAmount },
                                    color = SolidColor(MaterialTheme.colorScheme.primary),
                                    firstGradientFillColor = MaterialTheme.colorScheme.primary
                                        .copy(alpha = 0.3f),
                                    secondGradientFillColor = Color.Transparent,
                                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                    gradientAnimationDelay = 1000,
                                    drawStyle = DrawStyle.Stroke(width = 3.dp),
                                    curvedEdges = true
                                )
                            ),
                            animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyTotalsSection(
    dailySummaries: List<DailyExpenseSummary>,
    isLoading: Boolean
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Totals",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isLoading) {
                repeat(3) {
                    ShimmerItem()
                }
            } else if (dailySummaries.isEmpty()) {
                Text(
                    text = "No expenses recorded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                dailySummaries.forEach { summary ->
                    DailyTotalItem(summary = summary)
                }
            }
        }
    }
}

@Composable
private fun DailyTotalItem(summary: DailyExpenseSummary) {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())

    val formattedDate = try {
        val parsedDate = inputFormat.parse(summary.date)
        outputFormat.format(parsedDate ?: Date())
    } catch (e: Exception) {
        summary.date
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${summary.expenseCount} expenses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Text(
            text = "₹${formatAmountIndian(summary.totalAmount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CategoryTotalsSection(
    dailySummaries: List<DailyExpenseSummary>,
    isLoading: Boolean
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category Breakdown",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isLoading) {
                repeat(4) {
                    ShimmerItem()
                }
            } else {
                // Group expenses by category (mock implementation)
                val categoryTotals = remember(dailySummaries) {
                    // This would typically come from your data
                    // For now, creating mock category data
                    mapOf(
                        "Food & Dining" to dailySummaries.sumOf { it.totalAmount } * 0.4,
                        "Transportation" to dailySummaries.sumOf { it.totalAmount } * 0.25,
                        "Shopping" to dailySummaries.sumOf { it.totalAmount } * 0.2,
                        "Utilities" to dailySummaries.sumOf { it.totalAmount } * 0.15
                    )
                }

                if (categoryTotals.isEmpty()) {
                    Text(
                        text = "No category data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    categoryTotals.entries.forEach { (category, amount) ->
                        CategoryTotalItem(
                            category = category,
                            amount = amount,
                            percentage = (amount / dailySummaries.sumOf { it.totalAmount } * 100).toFloat()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTotalItem(
    category: String,
    amount: Double,
    percentage: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "₹${formatAmountIndian(amount)} (${String.format("%.1f", percentage)}%)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun ShimmerItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        RoundedCornerShape(4.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
        )
    }
}

// Enums and Data Classes
enum class ChartType(val displayName: String, val icon: ImageVector) {
    BAR("Bar Chart", Icons.Default.BarChart),
    LINE("Line Chart", Icons.Default.ShowChart)
}

enum class FilterType(val displayName: String) {
    LAST_WEEK("Last 7 Days"),
    CUSTOM("Custom Range")
}

// Dialog Composables
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onDateRangeSelected: (Long, Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    dateRangePickerState.selectedStartDateMillis?.let { start ->
                        dateRangePickerState.selectedEndDateMillis?.let { end ->
                            onDateRangeSelected(start, end)
                        }
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
        DateRangePicker(state = dateRangePickerState)
    }
}

@Composable
private fun ExportDialog(
    onDismiss: () -> Unit,
    onExportPDF: () -> Unit,
    onExportCSV: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Report") },
        text = { Text("Choose export format:") },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onExportPDF) {
                    Text("PDF")
                }
                TextButton(onClick = onExportCSV) {
                    Text("CSV")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
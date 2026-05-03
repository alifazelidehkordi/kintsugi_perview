/**
 *     Kintsugi Productivity
 *     Copyright (C) 2025 Ali Fazeli
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kintsugi.app.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.common.Time
import com.kintsugi.app.data.model.HabitWithAnalytics
import com.kintsugi.app.ui.DatePickerDialog
import com.kintsugi.app.ui.TopBar
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Plus
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onNavigateToTimer: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopBar(
                    title = "Habits & Tasks",
                    onNavigateBack = onNavigateBack,
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Today") },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("All") },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                onClick = { showAddDialog = true },
            ) {
                Icon(EvaIcons.Outline.Plus, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        val displayedHabits = if (selectedTab == 0) uiState.todayHabits else uiState.habits

        if (displayedHabits.isEmpty()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (selectedTab == 0) "No tasks for today" else "No habits yet",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayedHabits, key = { it.habit.id }) { item ->
                    HabitCard(
                        habitWithAnalytics = item.analytics,
                        completed = item.isCompletedToday,
                        action = { action ->
                            when (action) {
                                is HabitsAction.InsertStatus -> viewModel.toggleCompletedToday(item)
                                is HabitsAction.ArchiveHabit -> viewModel.archiveHabit(item.habit)
                                else -> Unit
                            }
                        },
                        onNavigateToAnalytics = { /* TODO */ },
                        editState = false,
                        compactView = false,
                        analyticsEnabled = !item.habit.isOneTime,
                        startingDay = DayOfWeek.MONDAY,
                        reorderHandle = {},
                        is24Hr = true,
                        shape = MaterialTheme.shapes.medium,
                        modifier =
                            Modifier.fillMaxWidth().clickable {
                                viewModel.activateLabelForHabit(item.habit)
                                onNavigateToTimer()
                            },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, days, isOneTime, dueDate ->
                viewModel.addHabit(title, description, days, isOneTime = isOneTime, dueDate = dueDate)
                showAddDialog = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Set<DayOfWeek>, Boolean, Int?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(DayOfWeek.entries.toSet()) }
    var isOneTime by remember { mutableStateOf(false) }

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = Time.currentDateTime().toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        )
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isOneTime) "Add Task" else "Add Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Tab(
                        selected = !isOneTime,
                        onClick = { isOneTime = false },
                        text = { Text("Habit") },
                        modifier = Modifier.weight(1f),
                    )
                    Tab(
                        selected = isOneTime,
                        onClick = { isOneTime = true },
                        text = { Text("Task") },
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    minLines = 2,
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (isOneTime) {
                    TextButton(onClick = { showDatePicker = true }) {
                        val date =
                            datePickerState.selectedDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                            } ?: "Select date"
                        Text("Date: $date")
                    }
                } else {
                    Text("Select days", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        for (day in DayOfWeek.entries) {
                            val isSelected = selectedDays.contains(day)
                            val color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            val textColor =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }

                            Box(
                                modifier =
                                    Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable {
                                            selectedDays =
                                                if (isSelected) {
                                                    selectedDays - day
                                                } else {
                                                    selectedDays + day
                                                }
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = day.name.take(1),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && (isOneTime || selectedDays.isNotEmpty()),
                onClick = {
                    val dueDate =
                        if (isOneTime) {
                            datePickerState.selectedDateMillis?.let {
                                Instant
                                    .fromEpochMilliseconds(it)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .date
                                    .toEpochDays()
                                    .toInt()
                            }
                        } else {
                            null
                        }
                    onConfirm(title, description, selectedDays, isOneTime, dueDate)
                },
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )

    if (showDatePicker) {
        DatePickerDialog(
            onConfirm = { showDatePicker = false },
            onDismiss = { showDatePicker = false },
            datePickerState = datePickerState,
        )
    }
}

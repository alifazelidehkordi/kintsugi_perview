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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
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
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Balance") },
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

        if (selectedTab == 2) {
            BalanceScreen(
                domainScores = uiState.domainScores,
                recentEntries = uiState.recentEntries,
                habits = uiState.habits,
                onHabitClick = { item ->
                    viewModel.activateLabelForHabit(item.habit)
                    onNavigateToTimer()
                },
                modifier = Modifier.padding(paddingValues),
            )
        } else if (displayedHabits.isEmpty()) {
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
                        completed = item.completionLevel > 0,
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

                if (selectedTab == 0) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        DailyReviewCard(
                            energy = uiState.dailyEntry?.energy ?: 0,
                            obstacle = uiState.dailyEntry?.obstacle?.takeIf { it.isNotEmpty() } ?: "بدون مانع",
                            note = uiState.dailyEntry?.note.orEmpty(),
                            onEnergyChange = { energy -> viewModel.updateDailyEntry(energy = energy) },
                            onObstacleChange = { obstacle -> viewModel.updateDailyEntry(obstacle = obstacle) },
                            onNoteChange = { note -> viewModel.updateDailyEntry(note = note) },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, days, isOneTime, dueDate, icon, domain, mve, goal ->
                viewModel.addHabit(
                    title,
                    description,
                    days,
                    isOneTime = isOneTime,
                    dueDate = dueDate,
                    icon = icon,
                    domain = domain,
                    mve = mve,
                    goal = goal,
                )
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun BalanceScreen(
    domainScores: Map<String, Float>,
    recentEntries: List<com.kintsugi.app.data.local.LocalDailyEntry>,
    habits: List<HabitListItem>,
    onHabitClick: (HabitListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val domains = listOf("جسم", "ذهن", "روح", "حرفه", "روابط", "انضباط")
    val primaryColor = MaterialTheme.colorScheme.primary
    var selectedDomain by remember { mutableStateOf<String?>(null) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("⚖️ توازن حوزه‌ها (Life Balance)", style = MaterialTheme.typography.titleLarge)
            val domainChunks = domains.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                domainChunks.forEach { rowDomains ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowDomains.forEach { domain ->
                            val score = domainScores[domain] ?: 0f
                            val isSelected = selectedDomain == domain
                            Card(
                                modifier =
                                    Modifier.weight(1f).clickable {
                                        selectedDomain = if (isSelected) null else domain
                                    },
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            },
                                    ),
                                border =
                                    if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        null
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            progress = { score },
                                            modifier = Modifier.size(48.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            strokeWidth = 6.dp,
                                        )
                                        Text("${(score * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text(domain, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                        if (rowDomains.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = selectedDomain != null) {
            selectedDomain?.let { domain ->
                val domainHabits = habits.filter { it.habit.domain == domain }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("عادت‌های حوزه $domain", style = MaterialTheme.typography.titleMedium)
                    if (domainHabits.isEmpty()) {
                        Text("عادتی در این حوزه ثبت نشده است.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        domainHabits.forEach { item ->
                            HabitCard(
                                habitWithAnalytics = item.analytics,
                                completed = item.completionLevel > 0,
                                action = { /* Actions from balance screen could be restricted or enabled */ },
                                onNavigateToAnalytics = { /* TODO */ },
                                editState = false,
                                compactView = true,
                                analyticsEnabled = true,
                                startingDay = DayOfWeek.MONDAY,
                                reorderHandle = {},
                                is24Hr = true,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth().clickable { onHabitClick(item) },
                            )
                        }
                    }
                }
            }
        }

        if (recentEntries.isNotEmpty()) {
            val obstacleCounts =
                recentEntries
                    .filter { it.obstacle.isNotBlank() && it.obstacle != "بدون مانع" }
                    .groupingBy { it.obstacle }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.second }
                    .take(3)

            if (obstacleCounts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("⚠️ موانع اصلی (Top Obstacles)", style = MaterialTheme.typography.titleLarge)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            obstacleCounts.forEach { (obstacle, count) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(obstacle, style = MaterialTheme.typography.bodyMedium)
                                    Text("$count بار", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("🔋 نوسانات انرژی (Energy Fluctuation)", style = MaterialTheme.typography.titleLarge)
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val data = recentEntries.sortedBy { it.dateEpochDays }.map { it.energy.toFloat() }
                        if (data.isNotEmpty()) {
                            val maxEnergy = 10f
                            val widthPerPoint = if (data.size > 1) size.width / (data.size - 1) else size.width
                            val heightRatio = size.height / maxEnergy

                            val path =
                                androidx.compose.ui.graphics
                                    .Path()
                            data.forEachIndexed { index, energy ->
                                val x = if (data.size > 1) index * widthPerPoint else size.width / 2
                                val y = size.height - (energy * heightRatio)
                                if (index == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                                drawCircle(
                                    color = primaryColor,
                                    radius = 4.dp.toPx(),
                                    center =
                                        androidx.compose.ui.geometry
                                            .Offset(x, y),
                                )
                            }
                            if (data.size > 1) {
                                drawPath(
                                    path = path,
                                    color = primaryColor,
                                    style =
                                        androidx.compose.ui.graphics.drawscope
                                            .Stroke(width = 2.dp.toPx()),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "“Kintsugi is not just about fixing; it's about making it stronger and more beautiful than before.”",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}

@Composable
private fun DailyReviewCard(
    energy: Int,
    obstacle: String,
    note: String,
    onEnergyChange: (Int) -> Unit,
    onObstacleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
) {
    val obstacles = listOf("بدون مانع", "کم‌خوابی", "استرس", "محیط شلوغ", "بی‌برنامگی", "خستگی زیاد", "ناراحتی")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("🌙 مرور روزانه (Daily Review)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Text("سطح انرژی امروز:", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (1..10).forEach { value ->
                    val isSelected = energy == value
                    val backgroundColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .border(2.dp, borderColor, CircleShape)
                                .clickable { onEnergyChange(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(value.toString(), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Text("بزرگترین مانع امروز:", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                obstacles.forEach { o ->
                    val isSelected = obstacle == o
                    AssistChip(
                        onClick = { onObstacleChange(o) },
                        label = { Text(o, style = MaterialTheme.typography.labelMedium) },
                        shape = MaterialTheme.shapes.medium,
                        border =
                            AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                                borderWidth = if (isSelected) 2.dp else 1.dp,
                            ),
                        colors =
                            if (isSelected) {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            } else {
                                AssistChipDefaults.assistChipColors()
                            },
                    )
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                minLines = 2,
                label = { Text("پیروزی اصلی / یادداشت روز") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Set<DayOfWeek>, Boolean, Int?, String, String, String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(DayOfWeek.entries.toSet()) }
    var isOneTime by remember { mutableStateOf(false) }
    var icon by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("جسم") }
    var mve by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = Time.currentDateTime().toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        )
    var showDatePicker by remember { mutableStateOf(false) }

    val domains = listOf("جسم", "ذهن", "روح", "حرفه", "روابط", "انضباط")

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = icon,
                        onValueChange = { icon = it },
                        singleLine = true,
                        label = { Text("Icon") },
                        modifier = Modifier.width(64.dp),
                        placeholder = { Text("🧘") },
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        label = { Text("Name") },
                        modifier = Modifier.weight(1f),
                    )
                }

                Text("Domain", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    domains.forEach { d ->
                        val isSelected = domain == d
                        TextButton(
                            onClick = { domain = d },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp),
                            colors =
                                if (isSelected) {
                                    ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                } else {
                                    ButtonDefaults.textButtonColors()
                                },
                        ) {
                            Text(text = d, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = mve,
                    onValueChange = { mve = it },
                    singleLine = true,
                    label = { Text("MVE (Minimum Effort)") },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    singleLine = true,
                    label = { Text("Final Goal") },
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
                    onConfirm(title, description, selectedDays, isOneTime, dueDate, icon, domain, mve, goal)
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

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
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.common.Time
import com.kintsugi.app.data.model.Habit
import com.kintsugi.app.data.model.HabitStatus
import com.kintsugi.app.ui.DatePickerDialog
import com.kintsugi.app.ui.TopBar
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Plus
import io.github.adrcotfas.datetime.names.TextStyle
import io.github.adrcotfas.datetime.names.getDisplayName
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.habits_add
import kintsugi_productivity.composeapp.generated.resources.habits_add_habit
import kintsugi_productivity.composeapp.generated.resources.habits_add_task
import kintsugi_productivity.composeapp.generated.resources.habits_balance_title
import kintsugi_productivity.composeapp.generated.resources.habits_custom_obstacle
import kintsugi_productivity.composeapp.generated.resources.habits_daily_review_title
import kintsugi_productivity.composeapp.generated.resources.habits_date
import kintsugi_productivity.composeapp.generated.resources.habits_domain
import kintsugi_productivity.composeapp.generated.resources.habits_domain_body
import kintsugi_productivity.composeapp.generated.resources.habits_domain_career
import kintsugi_productivity.composeapp.generated.resources.habits_domain_discipline
import kintsugi_productivity.composeapp.generated.resources.habits_domain_empty
import kintsugi_productivity.composeapp.generated.resources.habits_domain_habits
import kintsugi_productivity.composeapp.generated.resources.habits_domain_mind
import kintsugi_productivity.composeapp.generated.resources.habits_domain_relationships
import kintsugi_productivity.composeapp.generated.resources.habits_domain_spirit
import kintsugi_productivity.composeapp.generated.resources.habits_empty_all
import kintsugi_productivity.composeapp.generated.resources.habits_empty_today
import kintsugi_productivity.composeapp.generated.resources.habits_energy_fluctuation
import kintsugi_productivity.composeapp.generated.resources.habits_energy_today
import kintsugi_productivity.composeapp.generated.resources.habits_goal_label
import kintsugi_productivity.composeapp.generated.resources.habits_habit
import kintsugi_productivity.composeapp.generated.resources.habits_icon
import kintsugi_productivity.composeapp.generated.resources.habits_kintsugi_quote
import kintsugi_productivity.composeapp.generated.resources.habits_mve_label
import kintsugi_productivity.composeapp.generated.resources.habits_name
import kintsugi_productivity.composeapp.generated.resources.habits_no_obstacle
import kintsugi_productivity.composeapp.generated.resources.habits_note_label
import kintsugi_productivity.composeapp.generated.resources.habits_notes
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_low_sleep
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_no_plan
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_noisy_place
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_stress
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_tired
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_today
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_unwell
import kintsugi_productivity.composeapp.generated.resources.habits_select_date
import kintsugi_productivity.composeapp.generated.resources.habits_select_days
import kintsugi_productivity.composeapp.generated.resources.habits_tab_all
import kintsugi_productivity.composeapp.generated.resources.habits_tab_balance
import kintsugi_productivity.composeapp.generated.resources.habits_tab_today
import kintsugi_productivity.composeapp.generated.resources.habits_task
import kintsugi_productivity.composeapp.generated.resources.habits_times
import kintsugi_productivity.composeapp.generated.resources.habits_title
import kintsugi_productivity.composeapp.generated.resources.habits_top_obstacles
import kintsugi_productivity.composeapp.generated.resources.main_cancel
import kintsugi_productivity.composeapp.generated.resources.main_edit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
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
    var editHabit by remember { mutableStateOf<Habit?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopBar(
                    title = stringResource(Res.string.habits_title),
                    onNavigateBack = onNavigateBack,
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(stringResource(Res.string.habits_tab_today)) },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(stringResource(Res.string.habits_tab_all)) },
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(stringResource(Res.string.habits_tab_balance)) },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                onClick = {
                    editHabit = null
                    showAddDialog = true
                },
            ) {
                Icon(EvaIcons.Outline.Plus, stringResource(Res.string.habits_add))
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
                    text =
                        if (selectedTab ==
                            0
                        ) {
                            stringResource(Res.string.habits_empty_today)
                        } else {
                            stringResource(Res.string.habits_empty_all)
                        },
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
                        completionLevel = item.completionLevel,
                        action = { action ->
                            when (action) {
                                is HabitsAction.InsertStatus -> viewModel.updateHabitStatus(action)
                                is HabitsAction.ArchiveHabit -> viewModel.archiveHabit(action.habit)
                                is HabitsAction.UpdateHabit -> {
                                    editHabit = action.habit
                                    showAddDialog = true
                                }
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
                            obstacle =
                                uiState.dailyEntry?.obstacle?.takeIf { it.isNotEmpty() } ?: stringResource(Res.string.habits_no_obstacle),
                            note = uiState.dailyEntry?.note.orEmpty(),
                            onEnergyChange = { energy -> viewModel.updateDailyEntry(energy = energy) },
                            onObstacleChange = { obstacle -> viewModel.updateDailyEntry(obstacle = obstacle) },
                            onNoteChange = { note -> viewModel.updateDailyEntry(note = note) },
                            recentEntries = uiState.recentEntries,
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            initialHabit = editHabit,
            onDismiss = {
                showAddDialog = false
                editHabit = null
            },
            onConfirm = { title, description, days, isOneTime, dueDate, icon, domain, mve, goal ->
                if (editHabit != null) {
                    viewModel.updateHabit(
                        editHabit!!.copy(
                            title = title,
                            description = description,
                            scheduledDays = days,
                            isOneTime = isOneTime,
                            dueDate = dueDate,
                            icon = icon,
                            domain = domain,
                            mve = mve,
                            goal = goal,
                        ),
                    )
                } else {
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
                }
                showAddDialog = false
                editHabit = null
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
    val domains =
        listOf(
            stringResource(Res.string.habits_domain_body),
            stringResource(Res.string.habits_domain_mind),
            stringResource(Res.string.habits_domain_spirit),
            stringResource(Res.string.habits_domain_career),
            stringResource(Res.string.habits_domain_relationships),
            stringResource(Res.string.habits_domain_discipline),
        )
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
            Text(stringResource(Res.string.habits_balance_title), style = MaterialTheme.typography.titleLarge)
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
                    Text(stringResource(Res.string.habits_domain_habits, domain), style = MaterialTheme.typography.titleMedium)
                    if (domainHabits.isEmpty()) {
                        Text(stringResource(Res.string.habits_domain_empty), style = MaterialTheme.typography.bodySmall)
                    } else {
                        domainHabits.forEach { item ->
                            HabitCard(
                                habitWithAnalytics = item.analytics,
                                completionLevel = item.completionLevel,
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
                    .filter {
                        it.obstacle.isNotBlank() && it.obstacle != "بدون مانع" &&
                            it.obstacle != stringResource(Res.string.habits_no_obstacle)
                    }.groupingBy { it.obstacle }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.second }
                    .take(3)

            if (obstacleCounts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(Res.string.habits_top_obstacles), style = MaterialTheme.typography.titleLarge)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            obstacleCounts.forEach { (obstacle, count) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(obstacle, style = MaterialTheme.typography.bodyMedium)
                                    Text(stringResource(Res.string.habits_times, count), style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(Res.string.habits_energy_fluctuation), style = MaterialTheme.typography.titleLarge)
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
            stringResource(Res.string.habits_kintsugi_quote),
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
    recentEntries: List<com.kintsugi.app.data.local.LocalDailyEntry>,
) {
    val defaultObstacles =
        listOf(
            stringResource(Res.string.habits_no_obstacle),
            stringResource(Res.string.habits_obstacle_low_sleep),
            stringResource(Res.string.habits_obstacle_stress),
            stringResource(Res.string.habits_obstacle_noisy_place),
            stringResource(Res.string.habits_obstacle_no_plan),
            stringResource(Res.string.habits_obstacle_tired),
            stringResource(Res.string.habits_obstacle_unwell),
        )

    val customObstacles =
        recentEntries
            .map { it.obstacle }
            .filter { it.isNotBlank() && it !in defaultObstacles }
            .distinct()

    val allObstacles = defaultObstacles + customObstacles
    var showCustomObstacleDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                stringResource(Res.string.habits_daily_review_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(stringResource(Res.string.habits_energy_today), style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (1..10).forEach { value ->
                    val isSelected = energy == value
                    val animatedBgColor by animateColorAsState(
                        targetValue =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                        label = "energyBg_$value",
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            },
                        label = "energyBorder_$value",
                    )
                    val animatedTextColor by animateColorAsState(
                        targetValue =
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        label = "energyText_$value",
                    )

                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(animatedBgColor)
                                .border(2.dp, animatedBorderColor, CircleShape)
                                .clickable { onEnergyChange(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(value.toString(), style = MaterialTheme.typography.labelLarge, color = animatedTextColor)
                    }
                }
            }

            Text(stringResource(Res.string.habits_obstacle_today), style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allObstacles.forEach { o ->
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

                AssistChip(
                    onClick = { showCustomObstacleDialog = true },
                    label = { Text("+", style = MaterialTheme.typography.labelMedium) },
                    shape = MaterialTheme.shapes.medium,
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        ),
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                minLines = 2,
                label = { Text(stringResource(Res.string.habits_note_label)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showCustomObstacleDialog) {
        var customValue by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomObstacleDialog = false },
            title = { Text(stringResource(Res.string.habits_custom_obstacle)) },
            text = {
                OutlinedTextField(
                    value = customValue,
                    onValueChange = { customValue = it },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.habits_name)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (customValue.isNotBlank()) {
                            onObstacleChange(customValue.trim())
                        }
                        showCustomObstacleDialog = false
                    },
                ) {
                    Text(stringResource(Res.string.habits_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomObstacleDialog = false }) {
                    Text(stringResource(Res.string.main_cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitDialog(
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Set<DayOfWeek>, Boolean, Int?, String, String, String, String) -> Unit,
) {
    var title by remember { mutableStateOf(initialHabit?.title ?: "") }
    var description by remember { mutableStateOf(initialHabit?.description ?: "") }
    var selectedDays by remember { mutableStateOf(initialHabit?.scheduledDays ?: DayOfWeek.entries.toSet()) }
    var isOneTime by remember { mutableStateOf(initialHabit?.isOneTime ?: false) }
    var icon by remember { mutableStateOf(initialHabit?.icon ?: "") }
    var domain by remember { mutableStateOf(initialHabit?.domain ?: "") }
    var mve by remember { mutableStateOf(initialHabit?.mve ?: "") }
    var goal by remember { mutableStateOf(initialHabit?.goal ?: "") }

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                initialHabit?.dueDate?.let { it * 24L * 3600L * 1000L }
                    ?: Time.currentDateTime().toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        )
    var showDatePicker by remember { mutableStateOf(false) }

    val domains =
        listOf(
            stringResource(Res.string.habits_domain_body),
            stringResource(Res.string.habits_domain_mind),
            stringResource(Res.string.habits_domain_spirit),
            stringResource(Res.string.habits_domain_career),
            stringResource(Res.string.habits_domain_relationships),
            stringResource(Res.string.habits_domain_discipline),
        )

    if (domain.isEmpty()) {
        domain = domains.first()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialHabit != null) {
                    stringResource(Res.string.main_edit)
                } else if (isOneTime) {
                    stringResource(Res.string.habits_add_task)
                } else {
                    stringResource(Res.string.habits_add_habit)
                },
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Tab(
                        selected = !isOneTime,
                        onClick = { isOneTime = false },
                        text = { Text(stringResource(Res.string.habits_habit)) },
                        modifier = Modifier.weight(1f),
                    )
                    Tab(
                        selected = isOneTime,
                        onClick = { isOneTime = true },
                        text = { Text(stringResource(Res.string.habits_task)) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = icon,
                        onValueChange = { icon = it },
                        singleLine = true,
                        label = { Text(stringResource(Res.string.habits_icon)) },
                        modifier = Modifier.width(64.dp),
                        placeholder = { Text("🧘") },
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        label = { Text(stringResource(Res.string.habits_name)) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(stringResource(Res.string.habits_domain), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    domains.forEach { d ->
                        val isSelected = domain == d
                        val animatedBgColor by animateColorAsState(
                            targetValue =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                },
                            label = "domainBg_$d",
                        )
                        val animatedContentColor by animateColorAsState(
                            targetValue =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            label = "domainContent_$d",
                        )
                        val animatedBorderColor by animateColorAsState(
                            targetValue =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                },
                            label = "domainBorder_$d",
                        )

                        Box(
                            modifier =
                                Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(animatedBgColor)
                                    .border(1.dp, animatedBorderColor, MaterialTheme.shapes.medium)
                                    .clickable { domain = d }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = d,
                                style = MaterialTheme.typography.labelMedium,
                                color = animatedContentColor,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = mve,
                    onValueChange = { mve = it },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.habits_mve_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.habits_goal_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    minLines = 2,
                    label = { Text(stringResource(Res.string.habits_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (isOneTime) {
                    TextButton(onClick = { showDatePicker = true }) {
                        val date =
                            datePickerState.selectedDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                            } ?: stringResource(Res.string.habits_select_date)
                        Text(stringResource(Res.string.habits_date, date.toString()))
                    }
                } else {
                    Text(stringResource(Res.string.habits_select_days), style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        for (day in DayOfWeek.entries) {
                            val isSelected = selectedDays.contains(day)
                            val animatedColor by animateColorAsState(
                                targetValue =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                label = "dayBg_${day.name}",
                            )
                            val animatedTextColor by animateColorAsState(
                                targetValue =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                label = "dayText_${day.name}",
                            )

                            Box(
                                modifier =
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(animatedColor)
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
                                    text = day.getDisplayName(TextStyle.NARROW_STANDALONE),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = animatedTextColor,
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
                Text(if (initialHabit != null) stringResource(Res.string.main_edit) else stringResource(Res.string.habits_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.main_cancel))
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

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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kintsugi.app.common.Time
import com.kintsugi.app.data.local.LocalDataRepository
import com.kintsugi.app.data.model.Habit
import com.kintsugi.app.data.model.HabitStatus
import com.kintsugi.app.data.model.HabitWithAnalytics
import com.kintsugi.app.data.model.Label
import com.kintsugi.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HabitListItem(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val isScheduledToday: Boolean,
    val minutesToday: Long,
    val analytics: HabitWithAnalytics,
)

data class HabitsUiState(
    val isLoading: Boolean = true,
    val habits: List<HabitListItem> = emptyList(),
    val todayHabits: List<HabitListItem> = emptyList(),
)

class HabitsViewModel(
    private val repo: LocalDataRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState =
        _uiState
            .onStart { loadData() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitsUiState())

    private fun loadData() {
        viewModelScope.launch {
            val currentDateTime = Time.currentDateTime()
            val today = currentDateTime.date
            val todayEpochDays = today.toEpochDays().toInt()
            val dayOfWeek = today.dayOfWeek

            val startOfToday = Time.startOfTodayAdjusted(settingsRepository.settings.first().workdayStart)

            combine(
                repo.selectActiveHabits(),
                repo.selectAllHabitStatuses(),
                repo.selectSessionsAfter(startOfToday),
            ) { habits, allStatuses, sessions ->
                val items =
                    habits.map { habit ->
                        val habitStatuses = allStatuses.filter { it.habitId == habit.id }
                        val dates =
                            habitStatuses.map {
                                Instant
                                    .fromEpochMilliseconds(it.dateEpochDays * 24L * 3600L * 1000L)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .date
                            }
                        val isCompletedToday = dates.any { it.toEpochDays() == todayEpochDays.toLong() }

                        val isScheduledToday =
                            if (habit.isOneTime) {
                                habit.dueDate == todayEpochDays
                            } else {
                                habit.scheduledDays.contains(dayOfWeek)
                            }

                        HabitListItem(
                            habit = habit,
                            isCompletedToday = isCompletedToday,
                            isScheduledToday = isScheduledToday,
                            minutesToday =
                                sessions
                                    .filter { it.isWork && !it.isArchived && it.label == habit.labelName }
                                    .sumOf { it.duration },
                            analytics =
                                HabitWithAnalytics(
                                    habit = habit,
                                    statuses = habitStatuses,
                                    currentStreak = countCurrentStreak(dates, habit.scheduledDays),
                                    bestStreak = countBestStreak(dates, habit.scheduledDays),
                                ),
                        )
                    }

                val todayHabits = items.filter { it.isScheduledToday || (it.habit.isOneTime && !it.isCompletedToday) }

                Pair(items, todayHabits)
            }.distinctUntilChanged()
                .collect { (items, todayHabits) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            habits = items,
                            todayHabits = todayHabits,
                        )
                    }
                }
        }
    }

    fun addHabit(
        title: String,
        description: String,
        scheduledDays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
        reminder: Boolean = false,
        time: LocalDateTime? = null,
        isOneTime: Boolean = false,
        dueDate: Int? = null,
    ) {
        val cleanTitle = title.trim().take(Label.LABEL_NAME_MAX_LENGTH)
        if (cleanTitle.isEmpty()) return

        viewModelScope.launch {
            val labels = repo.selectAllLabels().first()
            val labelName = cleanTitle
            val label =
                labels.firstOrNull { it.name == labelName }
                    ?: Label(
                        name = labelName,
                        colorIndex = (labels.size % Label.DEFAULT_LABEL_COLOR_INDEX).coerceAtLeast(0),
                        orderIndex = labels.filter { !it.isArchived }.size.toLong(),
                    )
            repo.insertHabitWithLabel(
                habit =
                    Habit(
                        title = cleanTitle,
                        description = description.trim(),
                        labelName = labelName,
                        scheduledDays = if (isOneTime) emptySet() else scheduledDays,
                        reminder = reminder,
                        time = time,
                        isOneTime = isOneTime,
                        dueDate = dueDate,
                        orderIndex =
                            _uiState.value.habits.size
                                .toLong(),
                    ),
                label = label,
            )
        }
    }

    fun toggleCompletedToday(item: HabitListItem) {
        viewModelScope.launch {
            val today =
                Time
                    .currentDateTime()
                    .date
                    .toEpochDays()
                    .toInt()
            if (item.isCompletedToday) {
                repo.deleteHabitStatus(item.habit.id, today)
            } else {
                repo.insertHabitStatus(HabitStatus(habitId = item.habit.id, dateEpochDays = today))
            }
        }
    }

    fun archiveHabit(habit: Habit) {
        viewModelScope.launch {
            repo.updateHabitArchived(habit.id, true)
        }
    }

    fun activateLabelForHabit(habit: Habit) {
        viewModelScope.launch {
            settingsRepository.activateLabelWithName(habit.labelName)
        }
    }
}

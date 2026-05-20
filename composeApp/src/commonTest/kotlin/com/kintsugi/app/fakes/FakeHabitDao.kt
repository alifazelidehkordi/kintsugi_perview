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
package com.kintsugi.app.fakes

import com.kintsugi.app.data.local.HabitDao
import com.kintsugi.app.data.local.LocalHabit
import com.kintsugi.app.data.local.LocalHabitStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHabitDao : HabitDao {
    private val habits = MutableStateFlow<List<LocalHabit>>(emptyList())
    private val statuses = MutableStateFlow<List<LocalHabitStatus>>(emptyList())

    override suspend fun insertHabit(habit: LocalHabit): Long {
        val list = habits.value.toMutableList()
        val index = list.indexOfFirst { it.id == habit.id }
        val finalHabit =
            if (habit.id == 0L) {
                val newId = (list.maxOfOrNull { it.id } ?: 0L) + 1L
                habit.copy(id = newId)
            } else {
                habit
            }
        if (index >= 0) {
            list[index] = finalHabit
        } else {
            list.add(finalHabit)
        }
        habits.value = list
        return finalHabit.id
    }

    override suspend fun updateHabit(
        id: Long,
        title: String,
        description: String,
        labelName: String,
        scheduledDays: String,
        orderIndex: Long,
        time: Long?,
        reminder: Boolean,
        isArchived: Boolean,
        isOneTime: Boolean,
        dueDate: Int?,
        icon: String,
        domain: String,
        mve: String,
        goal: String,
    ) {
        habits.value =
            habits.value.map {
                if (it.id == id) {
                    it.copy(
                        title = title,
                        description = description,
                        labelName = labelName,
                        scheduledDays = scheduledDays,
                        orderIndex = orderIndex,
                        time = time,
                        reminder = reminder,
                        isArchived = isArchived,
                        isOneTime = isOneTime,
                        dueDate = dueDate,
                        icon = icon,
                        domain = domain,
                        mve = mve,
                        goal = goal,
                    )
                } else {
                    it
                }
            }
    }

    override suspend fun updateHabitArchived(
        habitId: Long,
        isArchived: Boolean,
    ) {
        habits.value =
            habits.value.map {
                if (it.id == habitId) {
                    it.copy(isArchived = isArchived)
                } else {
                    it
                }
            }
    }

    override fun selectActiveHabits(): Flow<List<LocalHabit>> = habits.map { it.filter { h -> !h.isArchived } }

    override fun selectAllHabits(): Flow<List<LocalHabit>> = habits

    override fun selectHabitByLabelName(labelName: String): Flow<LocalHabit?> = habits.map { it.find { h -> h.labelName == labelName } }

    override suspend fun deleteHabit(habitId: Long) {
        habits.value = habits.value.filter { it.id != habitId }
    }

    override suspend fun insertStatus(status: LocalHabitStatus): Long {
        statuses.value += status
        return statuses.value.size.toLong()
    }

    override suspend fun deleteStatus(
        habitId: Long,
        dateEpochDays: Int,
    ) {
        statuses.value = statuses.value.filterNot { it.habitId == habitId && it.dateEpochDays == dateEpochDays }
    }

    override fun selectStatusesForDate(dateEpochDays: Int): Flow<List<LocalHabitStatus>> =
        statuses.map {
            it.filter { s -> s.dateEpochDays == dateEpochDays }
        }

    override fun selectAllStatuses(): Flow<List<LocalHabitStatus>> = statuses

    override suspend fun updateHabitArchivedByLabel(
        labelName: String,
        isArchived: Boolean,
    ) {
        habits.value =
            habits.value.map {
                if (it.labelName == labelName) {
                    it.copy(isArchived = isArchived)
                } else {
                    it
                }
            }
    }

    override suspend fun archiveAllButDefaultHabits() {
        habits.value =
            habits.value.map {
                if (it.labelName != "PRODUCTIVITY_DEFAULT_LABEL") {
                    it.copy(isArchived = true)
                } else {
                    it
                }
            }
    }
}

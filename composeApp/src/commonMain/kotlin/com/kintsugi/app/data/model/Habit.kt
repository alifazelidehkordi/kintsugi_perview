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
package com.kintsugi.app.data.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val labelName: String,
    val scheduledDays: Set<DayOfWeek> = emptySet(),
    val time: LocalDateTime? = null,
    val orderIndex: Long = Long.MAX_VALUE,
    val reminder: Boolean = false,
    val isArchived: Boolean = false,
    val isOneTime: Boolean = false,
    val dueDate: Int? = null,
)

data class HabitStatus(
    val id: Long = 0,
    val habitId: Long,
    val dateEpochDays: Int,
)

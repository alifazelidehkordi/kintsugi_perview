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

import com.kintsugi.app.data.model.Habit
import kotlinx.datetime.LocalDate

sealed interface HabitsAction {
    data class OnToggleCompactView(
        val pref: Boolean,
    ) : HabitsAction

    data class OnToggleEditState(
        val pref: Boolean,
    ) : HabitsAction

    data class PrepareAnalytics(
        val habit: Habit?,
    ) : HabitsAction

    data class InsertStatus(
        val habit: Habit,
        val date: LocalDate,
        val level: Int? = null,
    ) : HabitsAction

    data class DeleteHabit(
        val habit: Habit,
    ) : HabitsAction

    data class ArchiveHabit(
        val habit: Habit,
    ) : HabitsAction

    data class UpdateHabit(
        val habit: Habit,
    ) : HabitsAction
}

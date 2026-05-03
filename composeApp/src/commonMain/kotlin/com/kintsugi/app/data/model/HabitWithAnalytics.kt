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

typealias WeeklyComparisonData = List<Double>

typealias WeekDayFrequencyData = Map<String, Int>

/** Grouped model for [habit] and its constituent analytics calculated from [statuses] */
data class HabitWithAnalytics(
    val habit: Habit,
    val statuses: List<HabitStatus>,
    val weeklyComparisonData: WeeklyComparisonData = emptyList(),
    val weekDayFrequencyData: WeekDayFrequencyData = emptyMap(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val startedDaysAgo: Long = 0,
)

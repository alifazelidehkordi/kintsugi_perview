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

import com.kintsugi.app.data.local.LocalDailyEntry
import com.kintsugi.app.data.model.Habit
import com.kintsugi.app.data.model.HabitStatus
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

data class HabitPeriodSummary(
    val habit: Habit,
    val gold: Int,
    val silver: Int,
    val missed: Int,
    val scheduledTotal: Int,
    val scorePercent: Int?,
    val currentStreak: Int,
)

data class HabitDashboardData(
    val todayFocus: HabitPeriodSummary? = null,
    val strongestTrend: HabitPeriodSummary? = null,
    val slippingHabit: HabitPeriodSummary? = null,
    val weakestDomain: Pair<String, Int>? = null,
    val topObstacle: Pair<String, Int>? = null,
    val averageEnergy: Double? = null,
    val domainScores: Map<String, Int> = emptyMap(),
    val weeklySummaries: List<HabitPeriodSummary> = emptyList(),
    val monthlySummaries: List<HabitPeriodSummary> = emptyList(),
)

fun buildHabitDashboardData(
    habits: List<Habit>,
    statuses: List<HabitStatus>,
    dailyEntries: List<LocalDailyEntry>,
    today: LocalDate,
): HabitDashboardData {
    val weeklySummaries = summarizeHabits(habits, statuses, today, days = 7)
    val monthlySummaries = summarizeHabits(habits, statuses, today, days = 30)
    val todaysSummaries =
        summarizeHabits(habits, statuses, today, days = 1)
            .filter { it.scheduledTotal > 0 }

    val strongestTrend =
        weeklySummaries
            .filter { it.scheduledTotal > 0 }
            .maxWithOrNull(compareBy<HabitPeriodSummary> { it.currentStreak }.thenBy { it.scorePercent ?: 0 })

    val slippingHabit =
        weeklySummaries
            .filter { it.scheduledTotal > 0 }
            .minWithOrNull(compareBy<HabitPeriodSummary> { it.currentStreak }.thenBy { it.scorePercent ?: 0 })

    val domainScores =
        habits
            .filter { it.domain.isNotBlank() }
            .groupBy { it.domain }
            .mapValues { (domain, domainHabits) ->
                val summaries = weeklySummaries.filter { it.habit.domain == domain && it.scheduledTotal > 0 }
                if (summaries.isEmpty()) {
                    100 // If no habits in this domain were scheduled this week, it's "balanced"
                } else {
                    summaries.mapNotNull { it.scorePercent }.average().toInt()
                }
            }

    val recentEntries = dailyEntries.filter { it.dateEpochDays >= today.toEpochDays().toInt() - 6 }
    val topObstacle =
        recentEntries
            .mapNotNull { it.obstacle.takeIf { obstacle -> obstacle.isNotBlank() && obstacle != "بدون مانع" } }
            .groupingBy { it }
            .eachCount()
            .maxWithOrNull(compareBy<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            ?.let { it.key to it.value }

    val energyValues = recentEntries.mapNotNull { it.energy.takeIf { energy -> energy > 0 } }

    return HabitDashboardData(
        todayFocus =
            todaysSummaries.minWithOrNull(
                compareBy<HabitPeriodSummary> { it.scorePercent ?: 0 }.thenBy { it.currentStreak },
            ),
        strongestTrend = strongestTrend,
        slippingHabit = slippingHabit,
        weakestDomain = domainScores.minByOrNull { it.value }?.let { it.key to it.value },
        topObstacle = topObstacle,
        averageEnergy = energyValues.takeIf { it.isNotEmpty() }?.average(),
        domainScores = domainScores,
        weeklySummaries = weeklySummaries,
        monthlySummaries = monthlySummaries,
    )
}

fun summarizeHabits(
    habits: List<Habit>,
    statuses: List<HabitStatus>,
    endDate: LocalDate,
    days: Int,
): List<HabitPeriodSummary> {
    val statusesByHabitAndDate = statuses.associateBy { it.habitId to it.dateEpochDays }

    return habits.map { habit ->
        var gold = 0
        var silver = 0
        var missed = 0
        var scheduledTotal = 0

        repeat(days) { offset ->
            val date = endDate.minus(DatePeriod(days = offset))
            if (habit.isScheduledOn(date)) {
                scheduledTotal++
                when (statusesByHabitAndDate[habit.id to date.toEpochDays().toInt()]?.level) {
                    2 -> gold++
                    1 -> silver++
                    else -> missed++
                }
            }
        }

        val completedDates =
            statuses
                .filter { it.habitId == habit.id && it.level > 0 }
                .map { LocalDate.fromEpochDays(it.dateEpochDays) }

        HabitPeriodSummary(
            habit = habit,
            gold = gold,
            silver = silver,
            missed = missed,
            scheduledTotal = scheduledTotal,
            scorePercent =
                if (scheduledTotal > 0) {
                    (((gold * 2 + silver).toFloat() / (scheduledTotal * 2)) * 100).toInt()
                } else {
                    null
                },
            currentStreak = countCurrentStreak(completedDates, habit.scheduledDays),
        )
    }
}

fun Habit.isScheduledOn(date: LocalDate): Boolean =
    if (isOneTime) {
        dueDate == date.toEpochDays().toInt()
    } else {
        date.dayOfWeek in scheduledDays
    }

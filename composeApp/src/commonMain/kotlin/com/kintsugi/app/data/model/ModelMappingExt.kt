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

import com.kintsugi.app.data.local.LocalHabit
import com.kintsugi.app.data.local.LocalHabitStatus
import com.kintsugi.app.data.local.LocalLabel
import com.kintsugi.app.data.local.LocalSession
import com.kintsugi.app.data.local.LocalTimerProfile
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Label.toLocal(): LocalLabel =
    LocalLabel(
        name = name,
        colorIndex = colorIndex,
        orderIndex = orderIndex,
        useDefaultTimeProfile = useDefaultTimeProfile,
        timerProfileName = timerProfile.name,
        isCountdown = timerProfile.isCountdown,
        workDuration = timerProfile.workDuration,
        isBreakEnabled = timerProfile.isBreakEnabled,
        breakDuration = timerProfile.breakDuration,
        isLongBreakEnabled = timerProfile.isLongBreakEnabled,
        longBreakDuration = timerProfile.longBreakDuration,
        sessionsBeforeLongBreak = timerProfile.sessionsBeforeLongBreak,
        workBreakRatio = timerProfile.workBreakRatio,
        isArchived = isArchived,
    )

fun LocalLabel.toExternal(timerProfile: TimerProfile? = null): Label =
    Label(
        name = name,
        colorIndex = colorIndex,
        orderIndex = orderIndex,
        useDefaultTimeProfile = useDefaultTimeProfile,
        timerProfile =
            timerProfile ?: TimerProfile(
                name = null,
                isCountdown = isCountdown,
                workDuration = workDuration,
                isBreakEnabled = isBreakEnabled,
                breakDuration = breakDuration,
                isLongBreakEnabled = isLongBreakEnabled,
                longBreakDuration = longBreakDuration,
                sessionsBeforeLongBreak = sessionsBeforeLongBreak,
                workBreakRatio = workBreakRatio,
            ),
        isArchived = isArchived,
    )

fun Session.toLocal() =
    LocalSession(
        id = id,
        timestamp = timestamp,
        duration = duration,
        interruptions = interruptions,
        labelName = label,
        notes = notes,
        isWork = isWork,
        isArchived = isArchived,
    )

fun LocalSession.toExternal() =
    Session(
        id = id,
        timestamp = timestamp,
        duration = duration,
        interruptions = interruptions,
        label = labelName,
        notes = notes,
        isWork = isWork,
        isArchived = isArchived,
    )

fun LocalTimerProfile.toExternal(): TimerProfile =
    TimerProfile(
        name = name,
        isCountdown = isCountdown,
        workDuration = workDuration,
        isBreakEnabled = isBreakEnabled,
        breakDuration = breakDuration,
        isLongBreakEnabled = isLongBreakEnabled,
        longBreakDuration = longBreakDuration,
        sessionsBeforeLongBreak = sessionsBeforeLongBreak,
        workBreakRatio = workBreakRatio,
    )

fun TimerProfile.toLocal(): LocalTimerProfile {
    if (name == null) {
        throw IllegalArgumentException("Timer profile name cannot be null")
    }
    return LocalTimerProfile(
        name = name,
        isCountdown = isCountdown,
        workDuration = workDuration,
        isBreakEnabled = isBreakEnabled,
        breakDuration = breakDuration,
        isLongBreakEnabled = isLongBreakEnabled,
        longBreakDuration = longBreakDuration,
        sessionsBeforeLongBreak = sessionsBeforeLongBreak,
        workBreakRatio = workBreakRatio,
    )
}

fun Habit.toLocal(): LocalHabit =
    LocalHabit(
        id = id,
        title = title,
        description = description,
        labelName = labelName,
        scheduledDays = scheduledDays.map { it.ordinal + 1 }.sorted().joinToString(","),
        orderIndex = orderIndex,
        time = time?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
        reminder = reminder,
        isArchived = isArchived,
        isOneTime = isOneTime,
        dueDate = dueDate,
    )

fun LocalHabit.toExternal(): Habit =
    Habit(
        id = id,
        title = title,
        description = description,
        labelName = labelName,
        scheduledDays =
            scheduledDays
                .split(",")
                .mapNotNull { it.toIntOrNull() }
                .filter { it in 1..7 }
                .map { DayOfWeek(it) }
                .toSet(),
        orderIndex = orderIndex,
        time =
            time?.let {
                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
            },
        reminder = reminder,
        isArchived = isArchived,
        isOneTime = isOneTime,
        dueDate = dueDate,
    )

fun HabitStatus.toLocal(): LocalHabitStatus =
    LocalHabitStatus(
        id = id,
        habitId = habitId,
        dateEpochDays = dateEpochDays,
    )

fun LocalHabitStatus.toExternal(): HabitStatus =
    HabitStatus(
        id = id,
        habitId = habitId,
        dateEpochDays = dateEpochDays,
    )

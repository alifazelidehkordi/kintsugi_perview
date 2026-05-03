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

import com.kintsugi.app.common.Time
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun LocalDate.Companion.now(): LocalDate = Time.currentDateTime().date

fun LocalTime.toFormattedString(is24Hr: Boolean): String =
    if (is24Hr) {
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    } else {
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        "$hour12:${minute.toString().padStart(2, '0')} $amPm"
    }

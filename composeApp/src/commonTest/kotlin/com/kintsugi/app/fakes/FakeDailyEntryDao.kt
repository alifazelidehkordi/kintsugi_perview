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

import com.kintsugi.app.data.local.DailyEntryDao
import com.kintsugi.app.data.local.LocalDailyEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDailyEntryDao : DailyEntryDao {
    private val entries = MutableStateFlow<List<LocalDailyEntry>>(emptyList())

    override suspend fun insertDailyEntry(entry: LocalDailyEntry) {
        val list = entries.value.toMutableList()
        val index = list.indexOfFirst { it.dateEpochDays == entry.dateEpochDays }
        if (index >= 0) {
            list[index] = entry
        } else {
            list.add(entry)
        }
        entries.value = list
    }

    override fun selectDailyEntryForDate(dateEpochDays: Int): Flow<LocalDailyEntry?> =
        entries.map {
            it.find { e -> e.dateEpochDays == dateEpochDays }
        }

    override fun selectAllDailyEntries(): Flow<List<LocalDailyEntry>> = entries
}

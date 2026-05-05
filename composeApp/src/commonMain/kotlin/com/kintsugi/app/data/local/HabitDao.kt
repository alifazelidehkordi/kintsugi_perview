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
package com.kintsugi.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: LocalHabit): Long

    @Query(
        """
        UPDATE localHabit SET
            title = :title,
            description = :description,
            labelName = :labelName,
            scheduledDays = :scheduledDays,
            orderIndex = :orderIndex,
            time = :time,
            reminder = :reminder,
            isArchived = :isArchived,
            isOneTime = :isOneTime,
            dueDate = :dueDate,
            icon = :icon,
            domain = :domain,
            mve = :mve,
            goal = :goal
        WHERE id = :id
        """,
    )
    suspend fun updateHabit(
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
    )

    @Query("UPDATE localHabit SET isArchived = :isArchived WHERE id = :habitId")
    suspend fun updateHabitArchived(
        habitId: Long,
        isArchived: Boolean,
    )

    @Query("SELECT * FROM localHabit WHERE isArchived = 0 ORDER BY orderIndex, title")
    fun selectActiveHabits(): Flow<List<LocalHabit>>

    @Query("SELECT * FROM localHabit ORDER BY isArchived, orderIndex, title")
    fun selectAllHabits(): Flow<List<LocalHabit>>

    @Query("SELECT * FROM localHabit WHERE labelName = :labelName LIMIT 1")
    fun selectHabitByLabelName(labelName: String): Flow<LocalHabit?>

    @Query("DELETE FROM localHabit WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatus(status: LocalHabitStatus): Long

    @Query("DELETE FROM localHabitStatus WHERE habitId = :habitId AND dateEpochDays = :dateEpochDays")
    suspend fun deleteStatus(
        habitId: Long,
        dateEpochDays: Int,
    )

    @Query("SELECT * FROM localHabitStatus WHERE dateEpochDays = :dateEpochDays")
    fun selectStatusesForDate(dateEpochDays: Int): Flow<List<LocalHabitStatus>>

    @Query("SELECT * FROM localHabitStatus")
    fun selectAllStatuses(): Flow<List<LocalHabitStatus>>
}

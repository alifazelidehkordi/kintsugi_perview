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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kintsugi.app.data.model.Label.Companion.DEFAULT_LABEL_NAME

@Entity(
    tableName = "localHabit",
    foreignKeys = [
        ForeignKey(
            entity = LocalLabel::class,
            parentColumns = ["name"],
            childColumns = ["labelName"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_DEFAULT,
        ),
    ],
    indices = [
        Index(value = ["labelName"]),
        Index(value = ["isArchived"]),
    ],
)
data class LocalHabit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    @ColumnInfo(defaultValue = "")
    val description: String = "",
    @ColumnInfo(defaultValue = DEFAULT_LABEL_NAME)
    val labelName: String = DEFAULT_LABEL_NAME,
    @ColumnInfo(defaultValue = "")
    val scheduledDays: String = "",
    @ColumnInfo(defaultValue = "${Long.MAX_VALUE}")
    val orderIndex: Long = Long.MAX_VALUE,
    val time: Long? = null,
    @ColumnInfo(defaultValue = "0")
    val reminder: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val isArchived: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val isOneTime: Boolean = false,
    val dueDate: Int? = null,
    @ColumnInfo(defaultValue = "")
    val icon: String = "",
    @ColumnInfo(defaultValue = "جسم")
    val domain: String = "جسم",
    @ColumnInfo(defaultValue = "")
    val mve: String = "",
    @ColumnInfo(defaultValue = "")
    val goal: String = "",
)

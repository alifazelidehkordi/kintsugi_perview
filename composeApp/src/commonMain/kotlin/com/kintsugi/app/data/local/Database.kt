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

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.SQLiteDriver
import com.kintsugi.app.data.local.migrations.MIGRATIONS

@Database(
    entities = [LocalLabel::class, LocalSession::class, LocalTimerProfile::class, LocalHabit::class, LocalHabitStatus::class],
    version = 11,
    exportSchema = true,
)
@ConstructedBy(ProductivityDatabaseConstructor::class)
abstract class ProductivityDatabase : RoomDatabase() {
    abstract fun labelsDao(): LabelDao

    abstract fun sessionsDao(): SessionDao

    abstract fun timerProfileDao(): TimerProfileDao

    abstract fun habitDao(): HabitDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ProductivityDatabaseConstructor : RoomDatabaseConstructor<ProductivityDatabase> {
    override fun initialize(): ProductivityDatabase
}

expect fun getDatabaseDriver(): SQLiteDriver

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ProductivityDatabase>,
    driver: SQLiteDriver,
): ProductivityDatabase =
    builder
        .addMigrations(*MIGRATIONS)
        .setDriver(driver)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

const val DATABASE_NAME = "kintsugi-db"

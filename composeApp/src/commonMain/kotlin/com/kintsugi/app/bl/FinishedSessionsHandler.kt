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
package com.kintsugi.app.bl

import co.touchlab.kermit.Logger
import com.kintsugi.app.common.Time
import com.kintsugi.app.data.local.LocalDataRepository
import com.kintsugi.app.data.model.HabitStatus
import com.kintsugi.app.data.model.Session
import com.kintsugi.app.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FinishedSessionsHandler(
    private val coroutineScope: CoroutineScope,
    private val repo: LocalDataRepository,
    private val settingsRepo: SettingsRepository,
    private val log: Logger,
) {
    fun updateLastFinishedSessionNotes(notes: String) {
        log.v { "Updating the notes" }
        coroutineScope.launch {
            try {
                val lastSessionId = settingsRepo.settings.first().lastInsertedSessionId
                log.v { "lastSessionId: $lastSessionId" }

                if (lastSessionId != Long.MAX_VALUE) {
                    val oldSession = repo.selectSessionById(lastSessionId).first()
                    val updatedSession = oldSession.copy(notes = notes)
                    repo.updateSession(lastSessionId, updatedSession)
                }
            } catch (e: Exception) {
                log.e { "Error updating notes: $e" }
                when (e) {
                    !is NoSuchElementException -> throw e
                }
            }
        }
    }

    fun updateSession(newSession: Session) {
        log.v { "Updating a finished session" }
        coroutineScope.launch {
            try {
                val lastSessionId = settingsRepo.settings.first().lastInsertedSessionId
                log.v { "lastSessionId: $lastSessionId" }
                if (lastSessionId != Long.MAX_VALUE) {
                    repo.updateSession(lastSessionId, newSession)
                }
            } catch (e: Exception) {
                log.e { "Error updating work time at reset: $e" }
            }
        }
    }

    fun saveSession(session: Session) {
        log.i { "Saving session to stats: $session" }
        coroutineScope.launch {
            val id = repo.insertSession(session)
            log.v { "Inserted session with id: $id" }
            settingsRepo.setLastInsertedSessionId(id)

            // Auto-complete matching habit
            if (session.isWork) {
                val habit = repo.selectHabitByLabelName(session.label).firstOrNull()
                if (habit != null) {
                    val today =
                        Time
                            .currentDateTime()
                            .date
                            .toEpochDays()
                            .toInt()
                    repo.insertHabitStatus(HabitStatus(habitId = habit.id, dateEpochDays = today))
                    log.i { "Auto-completed habit: ${habit.title}" }
                }
            }
        }
    }

    fun resetLastInsertedSessionId() {
        log.v { "Resetting last inserted session id" }
        coroutineScope.launch {
            settingsRepo.setLastInsertedSessionId(Long.MAX_VALUE)
        }
    }
}

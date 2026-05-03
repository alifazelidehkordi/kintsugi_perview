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
package com.kintsugi.app.di

import com.kintsugi.app.bl.EventListener
import com.kintsugi.app.bl.FinishedSessionsHandler
import com.kintsugi.app.bl.TimeProvider
import com.kintsugi.app.bl.TimerForegroundMonitor
import com.kintsugi.app.bl.TimerManager
import com.kintsugi.app.bl.TimerStateRestoration
import com.kintsugi.app.data.local.LocalDataRepository
import com.kintsugi.app.data.settings.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val timerManagerModule =
    module {
        single<TimerManager> {
            TimerManager(
                get<LocalDataRepository>(),
                get<SettingsRepository>(),
                get<List<EventListener>>(),
                get<TimeProvider>(),
                get<FinishedSessionsHandler>(),
                getWith("TimerManager"),
                coroutineScope = get(named(IO_SCOPE)),
                timerStateRestoration = getOrNull<TimerStateRestoration>(),
            )
        }

        single {
            TimerForegroundMonitor(
                timerManager = get(),
                timeProvider = get(),
                getWith("TimerForegroundMonitor"),
            )
        }
    }

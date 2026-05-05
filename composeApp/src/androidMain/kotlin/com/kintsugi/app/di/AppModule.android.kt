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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.kintsugi.app.BuildConfig
import com.kintsugi.app.bl.ALARM_MANAGER_HANDLER
import com.kintsugi.app.bl.DND_MODE_MANAGER
import com.kintsugi.app.bl.EventListener
import com.kintsugi.app.bl.SOUND_AND_VIBRATION_PLAYER
import com.kintsugi.app.bl.TIMER_SERVICE_STARTER
import com.kintsugi.app.bl.notifications.AndroidSoundPlayer
import com.kintsugi.app.bl.notifications.AndroidTorchManager
import com.kintsugi.app.bl.notifications.AndroidVibrationPlayer
import com.kintsugi.app.bl.notifications.SoundPlayer
import com.kintsugi.app.bl.notifications.SoundVibrationAndTorchPlayer
import com.kintsugi.app.bl.notifications.TorchManager
import com.kintsugi.app.bl.notifications.VibrationPlayer
import com.kintsugi.app.common.AndroidFeedbackHelper
import com.kintsugi.app.common.AndroidInstallDateProvider
import com.kintsugi.app.common.AndroidTimeFormatProvider
import com.kintsugi.app.common.AndroidUrlOpener
import com.kintsugi.app.common.FeedbackHelper
import com.kintsugi.app.common.InstallDateProvider
import com.kintsugi.app.common.TimeFormatProvider
import com.kintsugi.app.common.UrlOpener
import com.kintsugi.app.data.local.DATABASE_NAME
import com.kintsugi.app.data.local.ProductivityDatabase
import com.kintsugi.app.data.local.getDatabaseBuilder
import com.kintsugi.app.settings.reminders.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<RoomDatabase.Builder<ProductivityDatabase>> { getDatabaseBuilder(get<Context>()) }
        single<FileSystem> { FileSystem.SYSTEM }
        single<String>(named(DB_PATH_KEY)) { getDbPath { get<Context>().getDatabasePath(DATABASE_NAME).absolutePath } }
        single<String>(named(CACHE_DIR_PATH_KEY)) { getTmpPath { get<Context>().cacheDir.absolutePath } }

        single<DataStore<Preferences>>(named(SETTINGS_NAME)) {
            getDataStore(
                producePath = { get<Context>().filesDir.resolve(SETTINGS_FILE_NAME).absolutePath },
            )
        }
        single<SoundPlayer> {
            AndroidSoundPlayer(
                context = get(),
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                settingsRepo = get(),
                logger = getWith("SoundPlayer"),
            )
        }
        single<VibrationPlayer> {
            AndroidVibrationPlayer(
                context = get(),
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                settingsRepo = get(),
            )
        }
        single<TorchManager> {
            AndroidTorchManager(
                context = get(),
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                settingsRepo = get(),
                logger = getWith("TorchManager"),
            )
        }
        single<EventListener>(named(EventListener.SOUND_AND_VIBRATION_PLAYER)) {
            SoundVibrationAndTorchPlayer(
                soundPlayer = get(),
                vibrationPlayer = get(),
                torchManager = get(),
                timeProvider = get(),
                logger = getWith("SoundVibrationAndTorchPlayer"),
            )
        }
        single<List<EventListener>> {
            listOf(
                get<EventListener>(named(EventListener.DND_MODE_MANAGER)),
                get<EventListener>(named(EventListener.ALARM_MANAGER_HANDLER)),
                get<EventListener>(named(EventListener.TIMER_SERVICE_STARTER)),
                get<EventListener>(named(EventListener.SOUND_AND_VIBRATION_PLAYER)),
            )
        }
        single<UrlOpener> { AndroidUrlOpener(get()) }
        single<FeedbackHelper> { AndroidFeedbackHelper(get()) }
        single<TimeFormatProvider> { AndroidTimeFormatProvider(get()) }
        single<InstallDateProvider> { AndroidInstallDateProvider(get()) }

        single<ReminderScheduler> {
            ReminderScheduler(
                context = get(),
                timeProvider = get(),
                logger = getWith("ReminderScheduler"),
            )
        }
    }

actual fun isDebug(): Boolean = BuildConfig.DEBUG

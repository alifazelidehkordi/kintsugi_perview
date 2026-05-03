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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.kintsugi.app.bl.EventListener
import com.kintsugi.app.bl.IOS_LIVE_ACTIVITY_LISTENER
import com.kintsugi.app.bl.IOS_NOTIFICATION_HANDLER
import com.kintsugi.app.bl.IOS_TIMER_STATE_PERSISTENCE
import com.kintsugi.app.bl.IosLiveActivityListener
import com.kintsugi.app.bl.IosNotificationHandler
import com.kintsugi.app.bl.IosTimerStatePersistenceListener
import com.kintsugi.app.bl.LiveActivityBridge
import com.kintsugi.app.bl.SOUND_AND_VIBRATION_PLAYER
import com.kintsugi.app.bl.TimeProvider
import com.kintsugi.app.bl.TimerStateRestoration
import com.kintsugi.app.bl.notifications.IosSoundPlayer
import com.kintsugi.app.bl.notifications.IosTorchManager
import com.kintsugi.app.bl.notifications.IosVibrationPlayer
import com.kintsugi.app.bl.notifications.SoundPlayer
import com.kintsugi.app.bl.notifications.SoundVibrationAndTorchPlayer
import com.kintsugi.app.bl.notifications.TorchManager
import com.kintsugi.app.bl.notifications.VibrationPlayer
import com.kintsugi.app.common.FeedbackHelper
import com.kintsugi.app.common.InstallDateProvider
import com.kintsugi.app.common.IosFeedbackHelper
import com.kintsugi.app.common.IosInstallDateProvider
import com.kintsugi.app.common.IosTimeFormatProvider
import com.kintsugi.app.common.IosUrlOpener
import com.kintsugi.app.common.TimeFormatProvider
import com.kintsugi.app.common.UrlOpener
import com.kintsugi.app.data.local.DATABASE_NAME
import com.kintsugi.app.data.local.ProductivityDatabase
import com.kintsugi.app.data.local.getDatabaseBuilder
import com.kintsugi.app.data.settings.SettingsRepository
import com.kintsugi.app.settings.reminders.ReminderScheduler
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module =
    module {
        single<RoomDatabase.Builder<ProductivityDatabase>> { getDatabaseBuilder() }

        single<FileSystem> { FileSystem.SYSTEM }

        single<String>(named(DB_PATH_KEY)) {
            val documentDirectory: NSURL? =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            requireNotNull(documentDirectory).path + "/$DATABASE_NAME"
        }

        single<String>(named(CACHE_DIR_PATH_KEY)) {
            val cachesDirectory: NSURL? =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSCachesDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            requireNotNull(cachesDirectory?.path)
        }

        single<DataStore<Preferences>>(named(SETTINGS_NAME)) {
            getDataStore(
                producePath = {
                    val documentDirectory: NSURL? =
                        NSFileManager.defaultManager.URLForDirectory(
                            directory = NSDocumentDirectory,
                            inDomain = NSUserDomainMask,
                            appropriateForURL = null,
                            create = false,
                            error = null,
                        )
                    requireNotNull(documentDirectory).path + "/$SETTINGS_FILE_NAME"
                },
            )
        }
        single<UrlOpener> { IosUrlOpener() }
        single<FeedbackHelper> { IosFeedbackHelper() }
        single<TimeFormatProvider> { IosTimeFormatProvider() }
        single<InstallDateProvider> { IosInstallDateProvider() }

        single<TimerStateRestoration> {
            TimerStateRestoration(
                settingsRepo = get<SettingsRepository>(),
                timeProvider = get<TimeProvider>(),
                log = getWith("TimerStateRestoration"),
                coroutineScope = get<CoroutineScope>(named(IO_SCOPE)),
            )
        }

        single<EventListener>(named(EventListener.IOS_NOTIFICATION_HANDLER)) {
            IosNotificationHandler(
                timeProvider = get<TimeProvider>(),
                settingsRepo = get<SettingsRepository>(),
                coroutineScope = get<CoroutineScope>(named(MAIN_SCOPE)),
                log = getWith("IosNotificationHandler"),
            )
        }

        single<LiveActivityBridge> { LiveActivityBridge.shared }

        single<EventListener>(named(EventListener.IOS_LIVE_ACTIVITY_LISTENER)) {
            IosLiveActivityListener(
                liveActivityBridge = get<LiveActivityBridge>(),
                timeProvider = get<TimeProvider>(),
                log = getWith("IosLiveActivityListener"),
            )
        }

        single<SoundPlayer> {
            IosSoundPlayer(
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                settingsRepo = get<SettingsRepository>(),
                logger = getWith("SoundPlayer"),
            )
        }

        single<VibrationPlayer> {
            IosVibrationPlayer(
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                settingsRepo = get<SettingsRepository>(),
                logger = getWith("VibrationPlayer"),
            )
        }

        single<TorchManager> {
            IosTorchManager(
                ioScope = get<CoroutineScope>(named(IO_SCOPE)),
                playerScope = get<CoroutineScope>(named(WORKER_SCOPE)),
                settingsRepo = get<SettingsRepository>(),
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

        single<EventListener>(named(EventListener.IOS_TIMER_STATE_PERSISTENCE)) {
            IosTimerStatePersistenceListener(
                settingsRepo = get<SettingsRepository>(),
                timeProvider = get<TimeProvider>(),
                coroutineScope = get<CoroutineScope>(named(IO_SCOPE)),
                log = getWith("IosTimerStatePersistence"),
            )
        }

        single<List<EventListener>> {
            listOf(
                get<EventListener>(named(EventListener.IOS_NOTIFICATION_HANDLER)),
                get<EventListener>(named(EventListener.IOS_LIVE_ACTIVITY_LISTENER)),
                get<EventListener>(named(EventListener.SOUND_AND_VIBRATION_PLAYER)),
                get<EventListener>(named(EventListener.IOS_TIMER_STATE_PERSISTENCE)),
            )
        }

        single<ReminderScheduler> {
            ReminderScheduler(
                logger = getWith("ReminderScheduler"),
            )
        }
    }

@OptIn(ExperimentalNativeApi::class)
actual fun isDebug(): Boolean = Platform.isDebugBinary

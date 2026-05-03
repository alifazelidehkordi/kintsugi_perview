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
package com.kintsugi.app

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.kintsugi.app.billing.PurchaseManager
import com.kintsugi.app.billing.configurePurchasesFromPlatform
import com.kintsugi.app.bl.ALARM_MANAGER_HANDLER
import com.kintsugi.app.bl.AlarmManagerHandler
import com.kintsugi.app.bl.DND_MODE_MANAGER
import com.kintsugi.app.bl.DndModeManager
import com.kintsugi.app.bl.EventListener
import com.kintsugi.app.bl.TIMER_SERVICE_STARTER
import com.kintsugi.app.bl.TimeProvider
import com.kintsugi.app.bl.TimerServiceStarter
import com.kintsugi.app.bl.notifications.NotificationArchManager
import com.kintsugi.app.data.settings.SettingsRepository
import com.kintsugi.app.di.IO_SCOPE
import com.kintsugi.app.di.billingModule
import com.kintsugi.app.di.coreBackupModule
import com.kintsugi.app.di.coreModule
import com.kintsugi.app.di.coroutineScopeModule
import com.kintsugi.app.di.getWith
import com.kintsugi.app.di.localDataModule
import com.kintsugi.app.di.mainModule
import com.kintsugi.app.di.platformBackupModule
import com.kintsugi.app.di.platformModule
import com.kintsugi.app.di.timerManagerModule
import com.kintsugi.app.di.viewModelModule
import com.kintsugi.app.settings.notifications.SoundsViewModel
import com.kintsugi.app.settings.reminders.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.acra.ACRA
import org.acra.config.mailSender
import org.acra.config.notification
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.koin.android.ext.android.get
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

class KintsugiApplication :
    Application(),
    KoinComponent,
    Configuration.Provider {
    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        if (ACRA.isACRASenderServiceProcess()) return
        configurePurchasesFromPlatform()
        startKoin {
            modules(
                module {
                    single<Context> { this@KintsugiApplication }
                    single<NotificationArchManager> {
                        NotificationArchManager(
                            get<Context>(),
                            MainActivity::class.java,
                            coroutineScope = get<CoroutineScope>(named(IO_SCOPE)),
                        )
                    }
                    single<EventListener>(named(EventListener.TIMER_SERVICE_STARTER)) {
                        TimerServiceStarter(get())
                    }
                    single<EventListener>(named(EventListener.ALARM_MANAGER_HANDLER)) {
                        AlarmManagerHandler(
                            get<Context>(),
                            get<TimeProvider>(),
                            getWith("AlarmManagerHandler"),
                        )
                    }
                    viewModel<SoundsViewModel> {
                        SoundsViewModel(
                            settingsRepository = get(),
                        )
                    }

                    single<EventListener>(named(EventListener.DND_MODE_MANAGER)) {
                        DndModeManager(
                            notificationManager = get<NotificationArchManager>(),
                            settingsRepository = get<SettingsRepository>(),
                            coroutineScope = get<CoroutineScope>(named(IO_SCOPE)),
                        )
                    }
                },
                coroutineScopeModule,
                billingModule,
                platformModule,
                coreModule,
                localDataModule,
                coreBackupModule,
                platformBackupModule,
                timerManagerModule,
                viewModelModule,
                mainModule,
            )
            workManagerFactory()
        }

        initBilling()

        val reminderManager = get<ReminderManager>()
        applicationScope.launch {
            reminderManager.init()
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)

        initAcra {
            alsoReportToAndroidFramework = true
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            notification {
                // required
                title = context.getString(R.string.main_crash_notification_title)
                // required
                text = context.getString(R.string.main_crash_notification_desc)
                // required
                channelName = context.getString(R.string.main_crash_channel_name)
                resSendButtonIcon = null
                resDiscardButtonIcon = null
            }
            mailSender {
                mailTo = context.getString(R.string.contact_address)
                subject = context.getString(R.string.crash_report_title)
                reportFileName = "crash.txt"
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() =
            if (BuildConfig.DEBUG) {
                Configuration
                    .Builder()
                    .setMinimumLoggingLevel(android.util.Log.DEBUG)
                    .build()
            } else {
                Configuration
                    .Builder()
                    .setMinimumLoggingLevel(android.util.Log.ERROR)
                    .build()
            }

    private fun initBilling() {
        get<PurchaseManager>().start()
    }
}

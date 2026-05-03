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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.kintsugi.app.billing.PurchaseManager
import com.kintsugi.app.bl.EventListener
import com.kintsugi.app.bl.FinishActionType
import com.kintsugi.app.bl.IOS_NOTIFICATION_HANDLER
import com.kintsugi.app.bl.IosNotificationHandler
import com.kintsugi.app.bl.TimerManager
import com.kintsugi.app.di.MAIN_SCOPE
import com.kintsugi.app.di.billingModule
import com.kintsugi.app.di.coreBackupModule
import com.kintsugi.app.di.coreModule
import com.kintsugi.app.di.coroutineScopeModule
import com.kintsugi.app.di.localDataModule
import com.kintsugi.app.di.mainModule
import com.kintsugi.app.di.platformBackupModule
import com.kintsugi.app.di.platformModule
import com.kintsugi.app.di.timerManagerModule
import com.kintsugi.app.di.viewModelModule
import com.kintsugi.app.onboarding.MainViewModel
import com.kintsugi.app.platform.PlatformContext
import com.kintsugi.app.settings.reminders.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import platform.UIKit.UIViewController

@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController =
    ComposeUIViewController {
        AppWithKoin()
    }

@Composable
private fun AppWithKoin() {
    KoinApplication(
        application = {
            modules(
                coroutineScopeModule,
                billingModule,
                coreModule,
                localDataModule,
                coreBackupModule,
                platformBackupModule,
                timerManagerModule,
                mainModule,
                viewModelModule,
                platformModule,
            )
        },
    ) {
        val mainViewModel: MainViewModel = koinInject()
        val purchaseManager: PurchaseManager = koinInject()

        initNotificationHandler()
        initReminderManager()

        LaunchedEffect(Unit) {
            purchaseManager.start()
        }

        val platformContext = remember { PlatformContext() }

        KintsugiApp(
            platformContext = platformContext,
            mainViewModel = mainViewModel,
            onUpdateClicked = null,
        )
    }
}

@Composable
private fun initNotificationHandler() {
    val notificationHandler = koinInject<EventListener>(named(EventListener.IOS_NOTIFICATION_HANDLER)) as IosNotificationHandler
    val timerManager: TimerManager = koinInject()
    notificationHandler.init {
        timerManager.next(actionType = FinishActionType.MANUAL_NEXT)
    }
}

@Composable
private fun initReminderManager() {
    val reminderManager: ReminderManager = koinInject()
    val scope: CoroutineScope = koinInject(named(MAIN_SCOPE))
    scope.launch {
        reminderManager.init()
    }
}

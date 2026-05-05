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
import com.kintsugi.app.backup.BackupFileManager
import com.kintsugi.app.backup.CloudBackupViewModel
import com.kintsugi.app.backup.GoogleDriveBackupService
import com.kintsugi.app.backup.GoogleDriveBackupWorker
import com.kintsugi.app.backup.GoogleDriveManager
import com.kintsugi.app.data.settings.SettingsRepository
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformBackupModule: Module =
    module {
        includes(androidCommonBackupModule)

        single<GoogleDriveManager> {
            GoogleDriveManager(
                settingsRepository = get<SettingsRepository>(),
                backupManager = get<BackupFileManager>(),
                dbPath = get<String>(named(DB_PATH_KEY)),
                cacheDir = get<String>(named(CACHE_DIR_PATH_KEY)),
                logger = getWith("GoogleDriveManager"),
            )
        }

        single<GoogleDriveBackupService> {
            GoogleDriveBackupService(
                context = get<Context>(),
                googleDriveManager = get<GoogleDriveManager>(),
                backupManager = get<BackupFileManager>(),
                logger = getWith("GoogleDriveBackupService"),
            )
        }

        viewModelOf(::CloudBackupViewModel)

        worker {
            GoogleDriveBackupWorker(
                context = get<Context>(),
                backupService = get<GoogleDriveBackupService>(),
                googleDriveManager = get<GoogleDriveManager>(),
                settingsRepository = get<SettingsRepository>(),
                logger = getWith("GoogleDriveBackupWorker"),
                params = get(),
            )
        }
    }

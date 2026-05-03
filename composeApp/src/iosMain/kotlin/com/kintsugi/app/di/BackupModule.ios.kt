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

import com.kintsugi.app.backup.BackupFileManager
import com.kintsugi.app.backup.BackupPrompter
import com.kintsugi.app.backup.BackupViewModel
import com.kintsugi.app.backup.CloudBackupManager
import com.kintsugi.app.backup.CloudBackupViewModel
import com.kintsugi.app.backup.ICloudBackupService
import com.kintsugi.app.data.backup.IosBackupPrompter
import com.kintsugi.app.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformBackupModule: Module =
    module {
        single<BackupPrompter> {
            IosBackupPrompter(
                logger = getWith("IosBackupPrompter"),
                mainScope = get<CoroutineScope>(named(MAIN_SCOPE)),
            )
        }

        single<CloudBackupManager>(createdAtStart = true) {
            CloudBackupManager(
                backupManager = get<BackupFileManager>(),
                settingsRepository = get<SettingsRepository>(),
                fileSystem = get<FileSystem>(),
                dbPath = get<String>(named(DB_PATH_KEY)),
                logger = getWith("CloudBackupManager"),
            )
        }

        single<ICloudBackupService> {
            ICloudBackupService(
                cloudBackupManager = get<CloudBackupManager>(),
                backupManager = get<BackupFileManager>(),
                logger = getWith("ICloudBackupService"),
            )
        }

        viewModel {
            BackupViewModel(
                backupManager = get<BackupFileManager>(),
                settingsRepository = get<SettingsRepository>(),
                coroutineScope = get<CoroutineScope>(named(IO_SCOPE)),
            )
        }
        viewModelOf(::CloudBackupViewModel)
    }

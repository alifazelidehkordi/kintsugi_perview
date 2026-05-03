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
import com.kintsugi.app.bl.TimeProvider
import com.kintsugi.app.data.local.LocalDataRepository
import com.kintsugi.app.data.local.ProductivityDatabase
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val platformBackupModule: Module

val coreBackupModule: Module =
    module {
        single<BackupFileManager> {
            BackupFileManager(
                get<FileSystem>(),
                get<String>(named(DB_PATH_KEY)),
                get<String>(named(CACHE_DIR_PATH_KEY)),
                get<ProductivityDatabase>(),
                get<TimeProvider>(),
                get<BackupPrompter>(),
                get<LocalDataRepository>(),
                getWith("BackupManager"),
            )
        }
    }

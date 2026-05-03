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

import com.kintsugi.app.habits.HabitsViewModel
import com.kintsugi.app.labels.AddEditLabelViewModel
import com.kintsugi.app.labels.main.LabelsViewModel
import com.kintsugi.app.main.TimerViewModel
import com.kintsugi.app.main.finishedsession.FinishedSessionViewModel
import com.kintsugi.app.onboarding.MainViewModel
import com.kintsugi.app.settings.SettingsViewModel
import com.kintsugi.app.settings.TimerProfileViewModel
import com.kintsugi.app.settings.about.AboutViewModel
import com.kintsugi.app.settings.about.AcknowledgementsViewModel
import com.kintsugi.app.stats.StatisticsHistoryViewModel
import com.kintsugi.app.stats.StatisticsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule: Module =
    module {
        viewModelOf(::MainViewModel)
        viewModelOf(::FinishedSessionViewModel)
        viewModelOf(::HabitsViewModel)
        viewModelOf(::LabelsViewModel)
        viewModelOf(::AddEditLabelViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::TimerProfileViewModel)
        viewModelOf(::AboutViewModel)
        viewModelOf(::AcknowledgementsViewModel)
        viewModelOf(::StatisticsViewModel)
        viewModelOf(::StatisticsHistoryViewModel)
    }

val mainModule: Module =
    module {
        viewModelOf(::TimerViewModel)
    }

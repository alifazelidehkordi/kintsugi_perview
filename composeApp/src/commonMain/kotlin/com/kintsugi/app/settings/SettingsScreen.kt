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
package com.kintsugi.app.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.common.secondsOfDayToTimerFormat
import com.kintsugi.app.data.settings.isDarkTheme
import com.kintsugi.app.platform.getPlatformConfiguration
import com.kintsugi.app.settings.SettingsViewModel.Companion.firstDayOfWeekOptions
import com.kintsugi.app.settings.notifications.ProductivityReminderListItem
import com.kintsugi.app.ui.BetterListItem
import com.kintsugi.app.ui.CheckboxListItem
import com.kintsugi.app.ui.CompactPreferenceGroupTitle
import com.kintsugi.app.ui.DropdownMenuListItem
import com.kintsugi.app.ui.IconListItem
import com.kintsugi.app.ui.LockedCheckboxListItem
import com.kintsugi.app.ui.SubtleHorizontalDivider
import com.kintsugi.app.ui.TimePicker
import com.kintsugi.app.ui.TopBar
import com.kintsugi.app.ui.toSecondOfDay
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Bell
import compose.icons.evaicons.outline.ColorPalette
import io.github.adrcotfas.datetime.names.getDisplayName
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.ic_status_kintsugi
import kintsugi_productivity.composeapp.generated.resources.settings_auto_start_break_desc
import kintsugi_productivity.composeapp.generated.resources.settings_auto_start_break_title
import kintsugi_productivity.composeapp.generated.resources.settings_auto_start_focus_desc
import kintsugi_productivity.composeapp.generated.resources.settings_auto_start_focus_title
import kintsugi_productivity.composeapp.generated.resources.settings_custom_start_of_day_desc
import kintsugi_productivity.composeapp.generated.resources.settings_custom_start_of_day_title
import kintsugi_productivity.composeapp.generated.resources.settings_display_and_appearance
import kintsugi_productivity.composeapp.generated.resources.settings_display_over_lock_screen
import kintsugi_productivity.composeapp.generated.resources.settings_display_over_lock_screen_desc
import kintsugi_productivity.composeapp.generated.resources.settings_fullscreen_mode
import kintsugi_productivity.composeapp.generated.resources.settings_notifications_title
import kintsugi_productivity.composeapp.generated.resources.settings_productivity_reminder_title
import kintsugi_productivity.composeapp.generated.resources.settings_screensaver_mode
import kintsugi_productivity.composeapp.generated.resources.settings_start_of_the_week
import kintsugi_productivity.composeapp.generated.resources.settings_timer_and_sessions
import kintsugi_productivity.composeapp.generated.resources.settings_timer_durations_desc
import kintsugi_productivity.composeapp.generated.resources.settings_timer_durations_title
import kintsugi_productivity.composeapp.generated.resources.settings_title
import kintsugi_productivity.composeapp.generated.resources.settings_true_black_mode_desc
import kintsugi_productivity.composeapp.generated.resources.settings_true_black_mode_title
import kintsugi_productivity.composeapp.generated.resources.settings_user_interface
import kintsugi_productivity.composeapp.generated.resources.stats_focus
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserInterface: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDefaultLabel: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    val areNotificationsEnabled = rememberAreNotificationsEnabled()
    val platformConfig = getPlatformConfiguration()

    val listState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(Res.string.settings_title),
                onNavigateBack = { onNavigateBack() },
                showSeparator = listState.canScrollBackward,
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(listState)
                    .animateContentSize(),
        ) {
            AnimatedVisibility(
                areNotificationsEnabled,
            ) {
                Column {
                    CompactPreferenceGroupTitle(text = stringResource(Res.string.settings_productivity_reminder_title))
                    val reminderSettings = settings.productivityReminderSettings
                    ProductivityReminderListItem(
                        firstDayOfWeek = DayOfWeek(settings.firstDayOfWeek),
                        selectedDays = reminderSettings.days.map { DayOfWeek(it) }.toSet(),
                        reminderSecondOfDay = reminderSettings.secondOfDay,
                        is24HourFormat = uiState.is24HourFormat,
                        onSelectDay = viewModel::onToggleProductivityReminderDay,
                        onReminderTimeClick = { viewModel.setShowTimePicker(true) },
                    )
                    SubtleHorizontalDivider()
                }
            }
            CompactPreferenceGroupTitle(text = stringResource(Res.string.settings_timer_and_sessions))

            IconListItem(
                title = stringResource(Res.string.settings_timer_durations_title),
                subtitle = stringResource(Res.string.settings_timer_durations_desc),
                icon = {
                    Image(
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                        painter = painterResource(Res.drawable.ic_status_kintsugi),
                        contentDescription = stringResource(Res.string.stats_focus),
                    )
                },
                onClick = onNavigateToDefaultLabel,
            )
            CheckboxListItem(
                title = stringResource(Res.string.settings_auto_start_focus_title),
                subtitle = stringResource(Res.string.settings_auto_start_focus_desc),
                checked = settings.autoStartFocus,
            ) {
                viewModel.setAutoStartWork(it)
            }
            CheckboxListItem(
                title = stringResource(Res.string.settings_auto_start_break_title),
                subtitle = stringResource(Res.string.settings_auto_start_break_desc),
                checked = settings.autoStartBreak,
            ) {
                viewModel.setAutoStartBreak(it)
            }

            BetterListItem(
                title = stringResource(Res.string.settings_custom_start_of_day_title),
                subtitle = stringResource(Res.string.settings_custom_start_of_day_desc),
                trailing =
                    secondsOfDayToTimerFormat(
                        uiState.settings.workdayStart,
                        uiState.is24HourFormat,
                    ),
                onClick = {
                    viewModel.setShowWorkdayStartPicker(true)
                },
            )

            val days = DayOfWeek.entries.map { it.getDisplayName() }
            DropdownMenuListItem(
                title = stringResource(Res.string.settings_start_of_the_week),
                value = days[DayOfWeek(uiState.settings.firstDayOfWeek).ordinal],
                dropdownMenuOptions = days,
                onDropdownMenuItemSelected = {
                    viewModel.setFirstDayOfWeek(firstDayOfWeekOptions[it].isoDayNumber)
                },
            )

            SubtleHorizontalDivider()

            CompactPreferenceGroupTitle(text = stringResource(Res.string.settings_display_and_appearance))

            IconListItem(
                title = stringResource(Res.string.settings_user_interface),
                icon = {
                    Icon(
                        modifier = Modifier.padding(vertical = 12.dp),
                        imageVector = EvaIcons.Outline.ColorPalette,
                        contentDescription = stringResource(Res.string.settings_user_interface),
                    )
                },
                onClick = onNavigateToUserInterface,
            )
            if (uiState.settings.isPro) {
                CheckboxListItem(
                    title = stringResource(Res.string.settings_fullscreen_mode),
                    checked = uiState.settings.uiSettings.fullscreenMode,
                ) {
                    viewModel.setFullscreenMode(it)
                    if (!it) {
                        viewModel.setScreensaverMode(false)
                    }
                }
            } else {
                LockedCheckboxListItem(
                    title = stringResource(Res.string.settings_fullscreen_mode),
                    checked = false,
                    enabled = false,
                ) {
                    viewModel.setFullscreenMode(it)
                    if (!it) {
                        viewModel.setScreensaverMode(false)
                    }
                }
            }
            if (uiState.settings.isPro) {
                CheckboxListItem(
                    title = stringResource(Res.string.settings_screensaver_mode),
                    checked = uiState.settings.uiSettings.screensaverMode,
                    enabled = uiState.settings.uiSettings.fullscreenMode,
                ) {
                    viewModel.setScreensaverMode(it)
                }
            } else {
                LockedCheckboxListItem(
                    title = stringResource(Res.string.settings_screensaver_mode),
                    checked = false,
                    enabled = false,
                ) {
                }
            }
            AnimatedVisibility(
                uiState.settings.uiSettings.useDynamicColor &&
                    uiState.settings.uiSettings.themePreference.isDarkTheme(
                        isSystemInDarkTheme(),
                    ),
            ) {
                CheckboxListItem(
                    title = stringResource(Res.string.settings_true_black_mode_title),
                    subtitle = stringResource(Res.string.settings_true_black_mode_desc),
                    checked = uiState.settings.uiSettings.trueBlackMode,
                ) {
                    viewModel.setTrueBlackMode(it)
                }
            }
            if (platformConfig.supportsShowWhenLocked) {
                CheckboxListItem(
                    title = stringResource(Res.string.settings_display_over_lock_screen),
                    subtitle = stringResource(Res.string.settings_display_over_lock_screen_desc),
                    checked = uiState.settings.uiSettings.showWhenLocked,
                ) {
                    viewModel.setShowWhenLocked(it)
                }
            }

            SubtleHorizontalDivider()
            CompactPreferenceGroupTitle(text = stringResource(Res.string.settings_notifications_title))
            IconListItem(
                title = stringResource(Res.string.settings_notifications_title),
                icon = {
                    Icon(
                        modifier = Modifier.padding(vertical = 12.dp),
                        imageVector = EvaIcons.Outline.Bell,
                        contentDescription = stringResource(Res.string.settings_notifications_title),
                    )
                },
                onClick = onNavigateToNotifications,
            )
            DndCheckbox(
                checked = uiState.settings.uiSettings.dndDuringWork,
                onCheckedChange = viewModel::setDndDuringWork,
            )
        }
        if (uiState.showWorkdayStartPicker) {
            val workdayStart = LocalTime.fromSecondOfDay(uiState.settings.workdayStart)
            val timePickerState =
                rememberTimePickerState(
                    initialHour = workdayStart.hour,
                    initialMinute = workdayStart.minute,
                    is24Hour = uiState.is24HourFormat,
                )
            TimePicker(
                onDismiss = { viewModel.setShowWorkdayStartPicker(false) },
                onConfirm = {
                    viewModel.setWorkDayStart(timePickerState.toSecondOfDay())
                    viewModel.setShowWorkdayStartPicker(false)
                },
                timePickerState = timePickerState,
            )
        }
        if (uiState.showTimePicker) {
            val reminderTime =
                LocalTime.fromSecondOfDay(settings.productivityReminderSettings.secondOfDay)
            val timePickerState =
                rememberTimePickerState(
                    initialHour = reminderTime.hour,
                    initialMinute = reminderTime.minute,
                    is24Hour = uiState.is24HourFormat,
                )
            TimePicker(
                onDismiss = { viewModel.setShowTimePicker(false) },
                onConfirm = {
                    viewModel.setReminderTime(timePickerState.toSecondOfDay())
                    viewModel.setShowTimePicker(false)
                },
                timePickerState = timePickerState,
            )
        }
    }
}

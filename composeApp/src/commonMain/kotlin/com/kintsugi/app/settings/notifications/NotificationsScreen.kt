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
package com.kintsugi.app.settings.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.bl.notifications.TorchManager
import com.kintsugi.app.bl.notifications.VibrationPlayer
import com.kintsugi.app.data.settings.SoundData
import com.kintsugi.app.settings.SettingsViewModel
import com.kintsugi.app.ui.BetterListItem
import com.kintsugi.app.ui.CheckboxListItem
import com.kintsugi.app.ui.LockedCheckboxListItem
import com.kintsugi.app.ui.SliderListItem
import com.kintsugi.app.ui.TopBar
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.settings_break_complete_sound
import kintsugi_productivity.composeapp.generated.resources.settings_default_notification_sound
import kintsugi_productivity.composeapp.generated.resources.settings_focus_complete_sound
import kintsugi_productivity.composeapp.generated.resources.settings_insistent_notification_desc
import kintsugi_productivity.composeapp.generated.resources.settings_insistent_notification_title
import kintsugi_productivity.composeapp.generated.resources.settings_notifications_title
import kintsugi_productivity.composeapp.generated.resources.settings_override_sound_profile_desc
import kintsugi_productivity.composeapp.generated.resources.settings_override_sound_profile_title
import kintsugi_productivity.composeapp.generated.resources.settings_screen_flash_title
import kintsugi_productivity.composeapp.generated.resources.settings_silent
import kintsugi_productivity.composeapp.generated.resources.settings_torch_desc
import kintsugi_productivity.composeapp.generated.resources.settings_torch_title
import kintsugi_productivity.composeapp.generated.resources.settings_vibration_strength
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onNavigateBack: () -> Boolean) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    val vibrationPlayer = koinInject<VibrationPlayer>()
    val torchManager = koinInject<TorchManager>()
    val isTorchAvailable = torchManager.isTorchAvailable()
    val workRingTone = toSoundData(settings.workFinishedSound)
    val breakRingTone = toSoundData(settings.breakFinishedSound)
    val candidateRingTone = uiState.notificationSoundCandidate?.let { toSoundData(it) }

    val listState = rememberScrollState()
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(Res.string.settings_notifications_title),
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
                    .background(MaterialTheme.colorScheme.background),
        ) {
            BetterListItem(
                title = stringResource(Res.string.settings_focus_complete_sound),
                subtitle = notificationSoundName(workRingTone),
                onClick = { viewModel.setShowSelectWorkSoundPicker(true) },
            )

            BetterListItem(
                title = stringResource(Res.string.settings_break_complete_sound),
                subtitle = notificationSoundName(breakRingTone),
                onClick = { viewModel.setShowSelectBreakSoundPicker(true) },
            )

            CheckboxListItem(
                title = stringResource(Res.string.settings_override_sound_profile_title),
                subtitle = stringResource(Res.string.settings_override_sound_profile_desc),
                checked = settings.overrideSoundProfile,
            ) {
                viewModel.setOverrideSoundProfile(it)
            }

            var selectedStrength = settings.vibrationStrength
            SliderListItem(
                title = stringResource(Res.string.settings_vibration_strength),
                value = settings.vibrationStrength,
                min = 0,
                max = 5,
                onValueChange = {
                    selectedStrength = it
                    viewModel.setVibrationStrength(it)
                },
                onValueChangeFinished = { vibrationPlayer.start(selectedStrength) },
            )

            if (isTorchAvailable) {
                LockedCheckboxListItem(
                    title = stringResource(Res.string.settings_torch_title),
                    enabled = settings.isPro,
                    subtitle = stringResource(Res.string.settings_torch_desc),
                    checked = settings.enableTorch,
                ) {
                    viewModel.setEnableTorch(it)
                    if (it) {
                        torchManager.start()
                    }
                }
            }
            LockedCheckboxListItem(
                title = stringResource(Res.string.settings_screen_flash_title),
                enabled = settings.isPro,
                subtitle = stringResource(Res.string.settings_torch_desc),
                checked = settings.flashScreen,
            ) {
                viewModel.setEnableFlashScreen(it)
            }
            LockedCheckboxListItem(
                title = stringResource(Res.string.settings_insistent_notification_title),
                enabled = settings.isPro,
                subtitle = stringResource(Res.string.settings_insistent_notification_desc),
                checked = settings.insistentNotification,
            ) {
                viewModel.setInsistentNotification(it)
            }
        }

        if (uiState.showSelectWorkSoundPicker) {
            NotificationSoundPickerDialog(
                title = stringResource(Res.string.settings_focus_complete_sound),
                selectedItem = candidateRingTone ?: workRingTone,
                onSelected = {
                    viewModel.setNotificationSoundCandidate(Json.encodeToString(it))
                },
                onSave = { viewModel.setWorkFinishedSound(Json.encodeToString(it)) },
                onDismiss = { viewModel.setShowSelectWorkSoundPicker(false) },
            )
        }
        if (uiState.showSelectBreakSoundPicker) {
            NotificationSoundPickerDialog(
                title = stringResource(Res.string.settings_break_complete_sound),
                selectedItem = candidateRingTone ?: breakRingTone,
                onSelected = {
                    viewModel.setNotificationSoundCandidate(Json.encodeToString(it))
                },
                onSave = { viewModel.setBreakFinishedSound(Json.encodeToString(it)) },
                onDismiss = { viewModel.setShowSelectBreakSoundPicker(false) },
            )
        }
    }
}

@Composable
private fun notificationSoundName(it: SoundData) =
    if (it.isSilent) {
        stringResource(Res.string.settings_silent)
    } else {
        it.name.ifEmpty {
            stringResource(Res.string.settings_default_notification_sound)
        }
    }

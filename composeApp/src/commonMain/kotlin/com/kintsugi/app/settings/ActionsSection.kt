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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kintsugi.app.ui.ActionCard
import com.kintsugi.app.ui.PreferenceGroupTitle
import com.kintsugi.app.ui.SubtleHorizontalDivider
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.settings_action_required
import kintsugi_productivity.composeapp.generated.resources.settings_update
import kintsugi_productivity.composeapp.generated.resources.settings_update_available
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionSection(
    wasNotificationPermissionDenied: Boolean,
    onNotificationPermissionGranted: (Boolean) -> Unit,
    isUpdateAvailable: Boolean,
    onUpdateClicked: () -> Unit,
) {
    val permissionsState = rememberPermissionsState()

    AnimatedVisibility(
        permissionsState.shouldAskForNotificationPermission ||
            permissionsState.shouldAskForBatteryOptimizationRemoval ||
            permissionsState.shouldAskForAlarmPermission ||
            isUpdateAvailable,
    ) {
        Column {
            SubtleHorizontalDivider()
            Spacer(Modifier.height(8.dp))
            PreferenceGroupTitle(
                text = stringResource(Res.string.settings_action_required),
                paddingValues =
                    PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
            )
            ActionsContent(
                permissionsState = permissionsState,
                wasNotificationPermissionDenied = wasNotificationPermissionDenied,
                onNotificationPermissionGranted = onNotificationPermissionGranted,
            )

            AnimatedVisibility(isUpdateAvailable) {
                ActionCard(
                    cta = stringResource(Res.string.settings_update),
                    description = stringResource(Res.string.settings_update_available),
                    onClick = onUpdateClicked,
                )
            }
            Spacer(Modifier.height(8.dp))
            SubtleHorizontalDivider()
        }
    }
}

data class PermissionsState(
    val shouldAskForNotificationPermission: Boolean = false,
    val shouldAskForBatteryOptimizationRemoval: Boolean = false,
    val shouldAskForAlarmPermission: Boolean = false,
)

@Composable
expect fun rememberPermissionsState(): PermissionsState

@Composable
expect fun ActionsContent(
    permissionsState: PermissionsState,
    wasNotificationPermissionDenied: Boolean,
    onNotificationPermissionGranted: (Boolean) -> Unit,
)

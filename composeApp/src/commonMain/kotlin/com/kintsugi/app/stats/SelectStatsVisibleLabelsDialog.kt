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
package com.kintsugi.app.stats

import androidx.compose.runtime.Composable
import com.kintsugi.app.bl.LabelData
import com.kintsugi.app.ui.CheckboxListItem
import com.kintsugi.app.ui.SelectLabelDialog
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.labels_select_labels
import kintsugi_productivity.composeapp.generated.resources.stats_show_label_breakdown_desc
import kintsugi_productivity.composeapp.generated.resources.stats_show_label_breakdown_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectStatsVisibleLabelsDialog(
    labels: List<LabelData>,
    initialSelectedLabels: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    isLineChart: Boolean,
    onSetLineChart: (Boolean) -> Unit,
) {
    SelectLabelDialog(
        title = stringResource(Res.string.labels_select_labels),
        labels = labels,
        extraContent = {
            CheckboxListItem(
                title = stringResource(Res.string.stats_show_label_breakdown_title),
                subtitle = stringResource(Res.string.stats_show_label_breakdown_desc),
                checked = !isLineChart,
                onCheckedChange = { onSetLineChart(!it) },
            )
        },
        initialSelectedLabels = initialSelectedLabels,
        onDismiss = onDismiss,
        singleSelection = false,
        onConfirm = onConfirm,
    )
}

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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.bl.TimeUtils.getLocalizedMonthNamesForStats
import com.kintsugi.app.common.Time.currentDateTime
import com.kintsugi.app.common.isoWeekNumber
import com.kintsugi.app.data.settings.OverviewDurationType
import com.kintsugi.app.data.settings.OverviewType
import com.kintsugi.app.data.settings.StatisticsSettings
import com.kintsugi.app.stats.history.HistorySection
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.stats_today
import kintsugi_productivity.composeapp.generated.resources.stats_total
import kintsugi_productivity.composeapp.generated.resources.stats_week
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

@Composable
fun OverviewTab(
    firstDayOfWeek: DayOfWeek,
    workDayStart: Int,
    is24HourFormat: Boolean,
    statisticsSettings: StatisticsSettings,
    statisticsData: StatisticsData,
    onChangeOverviewType: (OverviewType) -> Unit,
    onChangeOverviewDurationType: (OverviewDurationType) -> Unit,
    onChangePieChartOverviewType: (OverviewDurationType) -> Unit,
    historyChartViewModel: StatisticsHistoryViewModel,
) {
    val currentDateTime = remember { currentDateTime() }
    val uiState by historyChartViewModel.uiState.collectAsStateWithLifecycle()

    val monthNames = remember { getLocalizedMonthNamesForStats() }

    Column(
        Modifier
            .padding(top = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        val typeNames =
            mapOf(
                OverviewDurationType.TODAY to stringResource(Res.string.stats_today),
                OverviewDurationType.THIS_WEEK to
                    stringResource(
                        Res.string.stats_week,
                        currentDateTime.date.isoWeekNumber(),
                    ),
                OverviewDurationType.THIS_MONTH to monthNames[currentDateTime.month.ordinal],
                OverviewDurationType.TOTAL to stringResource(Res.string.stats_total),
            )

        OverviewSection(
            statisticsData.overviewData,
            typeNames,
            statisticsSettings.overviewType,
            onChangeOverviewType,
        )

        HistorySection(historyChartViewModel)

        ProductiveTimeSection(
            statisticsData.productiveHoursOfTheDay,
            workDayStart,
            is24HourFormat,
        )

        HeatmapSection(
            firstDayOfWeek,
            data = statisticsData.heatmapData,
        )

        if (uiState.selectedLabels.size > 1) {
            PieChartSection(
                statisticsData.overviewData,
                statisticsSettings.pieChartViewType,
                onChangePieChartOverviewType,
                typeNames = typeNames,
                selectedLabels = uiState.selectedLabels,
            )
        }

        WorkBreakRatioSection(
            statisticsData.overviewData,
            statisticsSettings.overviewDurationType,
            onChangeOverviewDurationType,
            typeNames = typeNames,
        )
    }
}

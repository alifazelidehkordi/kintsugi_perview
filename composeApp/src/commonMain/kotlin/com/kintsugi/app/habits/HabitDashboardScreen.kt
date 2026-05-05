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
package com.kintsugi.app.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kintsugi.app.ui.TopBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopBar(
                title = "Kintsugi Dashboard",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val data = uiState.dashboardData

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DashboardCard(title = "وضعیت همین حالا") {
                MetricRow("تمرکز امروز", data.todayFocus?.habit?.title ?: "امروز عادتی برنامه‌ریزی نشده")
                MetricRow("قوی‌ترین روند", data.strongestTrend?.toDashboardLabel() ?: "داده کافی نیست")
                MetricRow("در معرض لغزش", data.slippingHabit?.toDashboardLabel() ?: "داده کافی نیست")
                MetricRow("کم‌شارژترین حوزه", data.weakestDomain?.let { "${it.first} | ${it.second}%" } ?: "داده کافی نیست")
                MetricRow("مانع غالب ۷ روز اخیر", data.topObstacle?.let { "${it.first} | ${it.second} بار" } ?: "بدون مانع جدی")
                MetricRow("میانگین انرژی ۷ روز", data.averageEnergy?.let { "${it.formatOneDecimal()} از ۱۰" } ?: "داده کافی نیست")
            }

            DashboardCard(title = "کارنامه هفتگی عادت‌ها") {
                data.weeklySummaries
                    .filter { it.scheduledTotal > 0 }
                    .sortedWith(compareBy<HabitPeriodSummary> { it.scorePercent ?: 0 }.thenBy { it.habit.title })
                    .forEach { summary ->
                        MetricRow(
                            label = summary.habit.displayName(),
                            value = "🥇 ${summary.gold} | 🥈 ${summary.silver} | ${summary.scorePercent ?: 0}%",
                        )
                    }
            }

            DashboardCard(title = "توازن حوزه‌ها") {
                if (data.domainScores.isEmpty()) {
                    Text("داده کافی نیست", style = MaterialTheme.typography.bodyMedium)
                } else {
                    data.domainScores.toSortedMap().forEach { (domain, score) ->
                        MetricRow(domain, "$score%")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
            content()
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        )
    }
}

private fun HabitPeriodSummary.toDashboardLabel(): String = "${habit.displayName()} | 🔥 $currentStreak روز"

private fun com.kintsugi.app.data.model.Habit.displayName(): String = if (icon.isBlank()) title else "$icon $title"

private fun Double.formatOneDecimal(): String {
    val rounded = (this * 10).toInt() / 10.0
    return rounded.toString()
}

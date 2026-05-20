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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.habits_count_times
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_average_energy
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_no_scheduled_today
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_now
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_slipping
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_strongest_trend
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_title
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_today_focus
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_top_obstacle
import kintsugi_productivity.composeapp.generated.resources.habits_dashboard_weakest_domain
import kintsugi_productivity.composeapp.generated.resources.habits_domain_balance
import kintsugi_productivity.composeapp.generated.resources.habits_domain_body
import kintsugi_productivity.composeapp.generated.resources.habits_domain_career
import kintsugi_productivity.composeapp.generated.resources.habits_domain_discipline
import kintsugi_productivity.composeapp.generated.resources.habits_domain_mind
import kintsugi_productivity.composeapp.generated.resources.habits_domain_relationships
import kintsugi_productivity.composeapp.generated.resources.habits_domain_spirit
import kintsugi_productivity.composeapp.generated.resources.habits_energy_out_of_ten
import kintsugi_productivity.composeapp.generated.resources.habits_no_major_obstacle
import kintsugi_productivity.composeapp.generated.resources.habits_no_obstacle
import kintsugi_productivity.composeapp.generated.resources.habits_not_enough_data
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_low_sleep
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_no_plan
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_noisy_place
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_stress
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_tired
import kintsugi_productivity.composeapp.generated.resources.habits_obstacle_unwell
import kintsugi_productivity.composeapp.generated.resources.habits_streak_days
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_report
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_review_title
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_review_title
import kintsugi_productivity.composeapp.generated.resources.habits_review_reflection_title
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_1
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_2
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_3
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_4
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_1
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_2
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_3
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_4
import org.jetbrains.compose.resources.stringResource
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
                title = stringResource(Res.string.habits_dashboard_title),
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
            val notEnoughData = stringResource(Res.string.habits_not_enough_data)
            val strongestTrend = data.strongestTrend
            val slippingHabit = data.slippingHabit
            DashboardCard(title = stringResource(Res.string.habits_dashboard_now)) {
                MetricRow(
                    stringResource(Res.string.habits_dashboard_today_focus),
                    data.todayFocus?.habit?.title ?: stringResource(Res.string.habits_dashboard_no_scheduled_today),
                )
                MetricRow(
                    stringResource(Res.string.habits_dashboard_strongest_trend),
                    strongestTrend?.toDashboardLabel(
                        stringResource(
                            Res.string.habits_streak_days,
                            strongestTrend.currentStreak,
                        ),
                    )
                        ?: notEnoughData,
                )
                MetricRow(
                    stringResource(Res.string.habits_dashboard_slipping),
                    slippingHabit?.toDashboardLabel(
                        stringResource(
                            Res.string.habits_streak_days,
                            slippingHabit.currentStreak,
                        ),
                    )
                        ?: notEnoughData,
                )
                MetricRow(
                    stringResource(Res.string.habits_dashboard_weakest_domain),
                    data.weakestDomain?.let { (domain, score) ->
                        val domainLabel = domain.toLocalizedDomain()?.let { stringResource(it) } ?: domain
                        "$domainLabel | $score%"
                    } ?: notEnoughData,
                )
                MetricRow(
                    stringResource(Res.string.habits_dashboard_top_obstacle),
                    data.topObstacle?.let { (obstacle, count) ->
                        val localizedObstacle = obstacle.toLocalizedObstacle()?.let { stringResource(it) } ?: obstacle
                        stringResource(Res.string.habits_count_times, localizedObstacle, count)
                    }
                        ?: stringResource(Res.string.habits_no_major_obstacle),
                )
                MetricRow(
                    stringResource(Res.string.habits_dashboard_average_energy),
                    data.averageEnergy?.let { stringResource(Res.string.habits_energy_out_of_ten, it.formatOneDecimal()) }
                        ?: notEnoughData,
                )
            }

            DashboardCard(title = stringResource(Res.string.habits_weekly_report)) {
                val weeklyActive = data.weeklySummaries.filter { it.scheduledTotal > 0 }
                if (weeklyActive.isEmpty()) {
                    Text(notEnoughData, style = MaterialTheme.typography.bodyMedium)
                } else {
                    weeklyActive
                        .sortedWith(compareByDescending<HabitPeriodSummary> { it.scorePercent ?: 0 }.thenBy { it.habit.title })
                        .forEach { summary ->
                            MetricRow(
                                label = summary.habit.displayName(),
                                value = "🥇 ${summary.gold} | 🥈 ${summary.silver} | ⚪ ${summary.missed} | ${summary.scorePercent ?: 0}%",
                            )
                        }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.habits_review_reflection_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                )
                val weeklyPrompts = listOf(
                    stringResource(Res.string.habits_weekly_prompt_1),
                    stringResource(Res.string.habits_weekly_prompt_2),
                    stringResource(Res.string.habits_weekly_prompt_3),
                    stringResource(Res.string.habits_weekly_prompt_4),
                )
                weeklyPrompts.forEach { prompt ->
                    Text("• $prompt", style = MaterialTheme.typography.bodyMedium)
                }
            }

            DashboardCard(title = stringResource(Res.string.habits_monthly_review_title)) {
                val monthlyActive = data.monthlySummaries.filter { it.scheduledTotal > 0 }
                if (monthlyActive.isEmpty()) {
                    Text(notEnoughData, style = MaterialTheme.typography.bodyMedium)
                } else {
                    monthlyActive
                        .sortedWith(compareByDescending<HabitPeriodSummary> { it.scorePercent ?: 0 }.thenBy { it.habit.title })
                        .forEach { summary ->
                            MetricRow(
                                label = summary.habit.displayName(),
                                value = "🥇 ${summary.gold} | 🥈 ${summary.silver} | ⚪ ${summary.missed} | ${summary.scorePercent ?: 0}%",
                            )
                        }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.habits_review_reflection_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                )
                val monthlyPrompts = listOf(
                    stringResource(Res.string.habits_monthly_prompt_1),
                    stringResource(Res.string.habits_monthly_prompt_2),
                    stringResource(Res.string.habits_monthly_prompt_3),
                    stringResource(Res.string.habits_monthly_prompt_4),
                )
                monthlyPrompts.forEach { prompt ->
                    Text("• $prompt", style = MaterialTheme.typography.bodyMedium)
                }
            }

            DashboardCard(title = stringResource(Res.string.habits_domain_balance)) {
                if (data.domainScores.isEmpty()) {
                    Text(notEnoughData, style = MaterialTheme.typography.bodyMedium)
                } else {
                    data.domainScores.toList().sortedBy { it.first }.forEach { (domain, score) ->
                        val domainLabel = domain.toLocalizedDomain()?.let { stringResource(it) } ?: domain
                        MetricRow(domainLabel, "$score%")
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

private fun HabitPeriodSummary.toDashboardLabel(streakLabel: String): String = "${habit.displayName()} | $streakLabel"

private fun com.kintsugi.app.data.model.Habit.displayName(): String = if (icon.isBlank()) title else "$icon $title"

private fun Double.formatOneDecimal(): String {
    val rounded = (this * 10).toInt() / 10.0
    return rounded.toString()
}

private fun String.toLocalizedDomain(): org.jetbrains.compose.resources.StringResource? =
    when (this.lowercase()) {
        "body", "جسم" -> Res.string.habits_domain_body
        "mind", "ذهن" -> Res.string.habits_domain_mind
        "spirit", "روح" -> Res.string.habits_domain_spirit
        "career", "حرفه" -> Res.string.habits_domain_career
        "relationships", "روابط" -> Res.string.habits_domain_relationships
        "discipline", "انضباط" -> Res.string.habits_domain_discipline
        else -> null
    }

private fun String.toLocalizedObstacle(): org.jetbrains.compose.resources.StringResource? =
    when (this.lowercase()) {
        "no obstacle", "بدون مانع" -> Res.string.habits_no_obstacle
        "low sleep", "کم‌خوابی" -> Res.string.habits_obstacle_low_sleep
        "stress", "استرس" -> Res.string.habits_obstacle_stress
        "noisy environment", "محیط شلوغ" -> Res.string.habits_obstacle_noisy_place
        "no plan", "بی‌برنامگی" -> Res.string.habits_obstacle_no_plan
        "very tired", "خستگی زیاد" -> Res.string.habits_obstacle_tired
        "unwell", "ناراحتی" -> Res.string.habits_obstacle_unwell
        else -> null
    }

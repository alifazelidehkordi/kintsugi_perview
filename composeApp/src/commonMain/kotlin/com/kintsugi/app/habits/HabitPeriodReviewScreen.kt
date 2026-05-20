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
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_1
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_2
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_3
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_prompt_4
import kintsugi_productivity.composeapp.generated.resources.habits_monthly_review_title
import kintsugi_productivity.composeapp.generated.resources.habits_review_card_title
import kintsugi_productivity.composeapp.generated.resources.habits_review_reflection_title
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_1
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_2
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_3
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_prompt_4
import kintsugi_productivity.composeapp.generated.resources.habits_weekly_review_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeeklyHabitReviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HabitPeriodReviewScreen(
        title = stringResource(Res.string.habits_weekly_review_title),
        summaries = uiState.dashboardData.weeklySummaries,
        prompts =
            listOf(
                stringResource(Res.string.habits_weekly_prompt_1),
                stringResource(Res.string.habits_weekly_prompt_2),
                stringResource(Res.string.habits_weekly_prompt_3),
                stringResource(Res.string.habits_weekly_prompt_4),
            ),
        onNavigateBack = onNavigateBack,
        isLoading = uiState.isLoading,
    )
}

@Composable
fun MonthlyHabitReviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HabitPeriodReviewScreen(
        title = stringResource(Res.string.habits_monthly_review_title),
        summaries = uiState.dashboardData.monthlySummaries,
        prompts =
            listOf(
                stringResource(Res.string.habits_monthly_prompt_1),
                stringResource(Res.string.habits_monthly_prompt_2),
                stringResource(Res.string.habits_monthly_prompt_3),
                stringResource(Res.string.habits_monthly_prompt_4),
            ),
        onNavigateBack = onNavigateBack,
        isLoading = uiState.isLoading,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitPeriodReviewScreen(
    title: String,
    summaries: List<HabitPeriodSummary>,
    prompts: List<String>,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.habits_review_card_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )

                    summaries
                        .filter { it.scheduledTotal > 0 }
                        .sortedWith(compareByDescending<HabitPeriodSummary> { it.scorePercent ?: 0 }.thenBy { it.habit.title })
                        .forEach { summary ->
                            ReviewSummaryRow(summary)
                        }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.habits_review_reflection_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    prompts.forEach { prompt ->
                        Text("• $prompt", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryRow(summary: HabitPeriodSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = summary.habit.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "🥇 ${summary.gold} | 🥈 ${summary.silver} | ⚪ ${summary.missed} | ${summary.scorePercent ?: 0}%",
            modifier = Modifier.weight(1.15f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeeklyHabitReviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HabitPeriodReviewScreen(
        title = "Weekly Review",
        summaries = uiState.dashboardData.weeklySummaries,
        prompts =
            listOf(
                "بزرگترین دستاورد این هفته چی بود؟",
                "چه چیزی باعث شد در روزهای سخت زنجیره نشکند؟",
                "کدام مانع بیشترین ضربه را به انضباط زد؟",
                "هفته بعد چه تغییر کوچکی محیط را بهتر می‌کند؟",
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
        title = "Monthly Review",
        summaries = uiState.dashboardData.monthlySummaries,
        prompts =
            listOf(
                "کدام عادت بیشترین اثر را روی ماه گذاشت؟",
                "کدام حوزه زندگی کم‌توجه ماند؟",
                "الگوی انرژی این ماه چه چیزی نشان داد؟",
                "ماه بعد کدام عادت باید ساده‌تر یا واضح‌تر شود؟",
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
                        text = "کارنامه عادت‌ها",
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
                        text = "تأمل و طراحی",
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
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = if (summary.habit.icon.isBlank()) summary.habit.title else "${summary.habit.icon} ${summary.habit.title}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        )
        Text(
            text = "🥇 ${summary.gold} | 🥈 ${summary.silver} | ⚪ ${summary.missed} | ${summary.scorePercent ?: 0}%",
            modifier = Modifier.weight(1.15f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

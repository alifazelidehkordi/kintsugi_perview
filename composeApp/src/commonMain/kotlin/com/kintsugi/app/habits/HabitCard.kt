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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kintsugi.app.data.model.HabitWithAnalytics
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.analytics
import kintsugi_productivity.composeapp.generated.resources.check_circle
import kintsugi_productivity.composeapp.generated.resources.circle_border
import kintsugi_productivity.composeapp.generated.resources.heat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HabitCard(
    habitWithAnalytics: HabitWithAnalytics,
    completed: Boolean,
    action: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    editState: Boolean,
    compactView: Boolean,
    analyticsEnabled: Boolean,
    startingDay: DayOfWeek,
    reorderHandle: @Composable () -> Unit,
    is24Hr: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val canCompleteToday =
        if (habitWithAnalytics.habit.isOneTime) {
            true
        } else {
            today.dayOfWeek in habitWithAnalytics.habit.scheduledDays
        }

    val cardContent by animateColorAsState(
        targetValue =
            when (completed) {
                true -> MaterialTheme.colorScheme.onPrimaryContainer
                else ->
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (canCompleteToday) 1f else 0.7f,
                    )
            },
        label = "cardContent",
    )
    val cardBackground by animateColorAsState(
        targetValue =
            when (completed) {
                true -> MaterialTheme.colorScheme.primaryContainer
                else ->
                    MaterialTheme.colorScheme.surfaceContainer.copy(
                        alpha = if (canCompleteToday) 1f else 0.7f,
                    )
            },
        label = "cardBackground",
    )

    val weekState =
        rememberWeekCalendarState(
            startDate = today.minus(30, DateTimeUnit.DAY), // Simpler range for now
            endDate = today,
            firstVisibleWeekDate = today,
            firstDayOfWeek = startingDay,
        )

    Card(
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = cardBackground,
                contentColor = cardContent,
            ),
        onClick = {
            if (canCompleteToday) {
                action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today))
            }
        },
        shape = shape,
        modifier = modifier.animateContentSize(),
    ) {
        ListItem(
            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large),
            colors =
                ListItemDefaults.colors(
                    containerColor = cardBackground,
                    headlineColor = cardContent,
                    supportingColor = cardContent,
                    trailingIconColor = cardContent,
                    leadingIconColor = cardContent,
                ),
            leadingContent = {
                AnimatedContent(targetState = completed) {
                    Icon(
                        imageVector =
                            vectorResource(
                                if (!it) Res.drawable.circle_border else Res.drawable.check_circle,
                            ),
                        contentDescription = null,
                    )
                }
            },
            headlineContent = {
                Text(
                    text = habitWithAnalytics.habit.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.basicMarquee(),
                )
            },
            supportingContent = {
                habitWithAnalytics.habit.time?.let {
                    Text(
                        text = it.time.toFormattedString(is24Hr),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!habitWithAnalytics.habit.isOneTime) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.heat),
                                contentDescription = null,
                            )
                            Text(text = habitWithAnalytics.currentStreak.toString())
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = {
                            action(HabitsAction.PrepareAnalytics(habitWithAnalytics.habit))
                            onNavigateToAnalytics()
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        enabled = analyticsEnabled,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.analytics),
                            contentDescription = "Analytics",
                        )
                    }

                    AnimatedVisibility(visible = editState) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            reorderHandle()
                        }
                    }
                }
            },
        )

        if (!compactView && !habitWithAnalytics.habit.isOneTime) {
            WeekCalendar(
                contentPadding = PaddingValues(8.dp),
                state = weekState,
                dayContent = { weekDay ->
                    val done =
                        habitWithAnalytics.statuses.any {
                            Instant
                                .fromEpochMilliseconds(it.dateEpochDays * 24L * 3600L * 1000L)
                                .toLocalDateTime(TimeZone.UTC)
                                .date == weekDay.date
                        }
                    val validDay =
                        weekDay.date <= today &&
                            weekDay.date.dayOfWeek in habitWithAnalytics.habit.scheduledDays

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .then(
                                    if (done) {
                                        Modifier.background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(20.dp), // Simplified shape
                                        )
                                    } else {
                                        Modifier
                                    },
                                ).clip(shape = RoundedCornerShape(20.dp))
                                .clickable(
                                    role = Role.Button,
                                    enabled = validDay,
                                    onClick = {
                                        action(HabitsAction.InsertStatus(habitWithAnalytics.habit, weekDay.date))
                                    },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = weekDay.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (done) MaterialTheme.colorScheme.onPrimary else cardContent,
                            )
                            Text(
                                text =
                                    weekDay.date.dayOfWeek
                                        .toString()
                                        .take(3),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (done) MaterialTheme.colorScheme.onPrimary else cardContent,
                            )
                        }
                    }
                },
            )
        }
    }
}

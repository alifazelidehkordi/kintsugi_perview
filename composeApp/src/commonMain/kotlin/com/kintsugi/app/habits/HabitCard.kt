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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kintsugi.app.data.model.HabitWithAnalytics
import com.kintsugi.app.ui.SubtleHorizontalDivider
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.analytics
import kintsugi_productivity.composeapp.generated.resources.circle_border
import kintsugi_productivity.composeapp.generated.resources.habits_status_clear
import kintsugi_productivity.composeapp.generated.resources.habits_status_goal_desc
import kintsugi_productivity.composeapp.generated.resources.habits_status_goal_title
import kintsugi_productivity.composeapp.generated.resources.habits_status_mve_desc
import kintsugi_productivity.composeapp.generated.resources.habits_status_mve_title
import kintsugi_productivity.composeapp.generated.resources.heat
import kintsugi_productivity.composeapp.generated.resources.main_edit
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habitWithAnalytics: HabitWithAnalytics,
    completionLevel: Int,
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
    val completed = completionLevel > 0
    val today = LocalDate.now()
    var showMenu by remember { mutableStateOf(false) }

    val canCompleteToday =
        if (habitWithAnalytics.habit.isOneTime) {
            true
        } else {
            today.dayOfWeek in habitWithAnalytics.habit.scheduledDays
        }

    val cardContent by animateColorAsState(
        targetValue =
            when (completionLevel) {
                2 -> MaterialTheme.colorScheme.onPrimaryContainer
                1 -> MaterialTheme.colorScheme.onSecondaryContainer
                else ->
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (canCompleteToday) 1f else 0.7f,
                    )
            },
        label = "cardContent",
    )
    val cardBackground by animateColorAsState(
        targetValue =
            when (completionLevel) {
                2 -> MaterialTheme.colorScheme.primaryContainer
                1 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                else ->
                    MaterialTheme.colorScheme.surfaceContainer.copy(
                        alpha = if (canCompleteToday) 1f else 0.7f,
                    )
            },
        label = "cardBackground",
    )

    val weekState =
        rememberWeekCalendarState(
            startDate = today.minus(30, DateTimeUnit.DAY),
            endDate = today,
            firstVisibleWeekDate = today,
            firstDayOfWeek = startingDay,
        )

    Box {
        Card(
            colors =
                CardDefaults.outlinedCardColors(
                    containerColor = cardBackground,
                    contentColor = cardContent,
                ),
            shape = shape,
            modifier =
                modifier
                    .animateContentSize()
                    .combinedClickable(
                        onClick = {
                            if (canCompleteToday) {
                                action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today))
                            }
                        },
                        onLongClick = {
                            if (canCompleteToday) {
                                showMenu = true
                            }
                        },
                    ),
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
                    AnimatedContent(targetState = completionLevel) { level ->
                        when (level) {
                            2 -> Text("🥇", style = MaterialTheme.typography.headlineSmall)
                            1 -> Text("🥈", style = MaterialTheme.typography.headlineSmall)
                            else ->
                                Icon(
                                    imageVector = vectorResource(Res.drawable.circle_border),
                                    contentDescription = null,
                                )
                        }
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
                    Column {
                        if (habitWithAnalytics.habit.mve.isNotBlank() || habitWithAnalytics.habit.goal.isNotBlank()) {
                            Text(
                                text = "🥈 ${habitWithAnalytics.habit.mve} | 🥇 ${habitWithAnalytics.habit.goal}",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                            )
                        }
                        habitWithAnalytics.habit.time?.let {
                            Text(
                                text = it.time.toFormattedString(is24Hr),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
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
                        val dayStatus =
                            habitWithAnalytics.statuses.find {
                                Instant
                                    .fromEpochMilliseconds(it.dateEpochDays * 24L * 3600L * 1000L)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .date == weekDay.date
                            }
                        val done = dayStatus != null
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
                                                color =
                                                    if (dayStatus?.level == 2) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.secondary
                                                    },
                                                shape = RoundedCornerShape(20.dp),
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

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            DropdownMenuItem(
                text = {
                    Column {
                        Text(stringResource(Res.string.habits_status_goal_title))
                        Text(stringResource(Res.string.habits_status_goal_desc), style = MaterialTheme.typography.labelSmall)
                    }
                },
                leadingIcon = { Text("🥇") },
                onClick = {
                    action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today, level = 2))
                    showMenu = false
                },
            )
            DropdownMenuItem(
                text = {
                    Column {
                        Text(stringResource(Res.string.habits_status_mve_title))
                        Text(stringResource(Res.string.habits_status_mve_desc), style = MaterialTheme.typography.labelSmall)
                    }
                },
                leadingIcon = { Text("🥈") },
                onClick = {
                    action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today, level = 1))
                    showMenu = false
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.habits_status_clear)) },
                leadingIcon = { Text("⚪") },
                onClick = {
                    action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today, level = 0))
                    showMenu = false
                },
            )
            SubtleHorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.main_edit)) },
                leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                onClick = {
                    action(HabitsAction.UpdateHabit(habitWithAnalytics.habit))
                    showMenu = false
                },
            )
        }
    }
}

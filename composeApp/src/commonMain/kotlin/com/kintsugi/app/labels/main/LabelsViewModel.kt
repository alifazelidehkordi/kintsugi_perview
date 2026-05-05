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
package com.kintsugi.app.labels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kintsugi.app.data.local.LocalDataRepository
import com.kintsugi.app.data.model.Label
import com.kintsugi.app.data.model.isDefault
import com.kintsugi.app.data.settings.SettingsRepository
import com.kintsugi.app.labels.utils.generateUniqueNameForDuplicate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LabelsUiState(
    val isLoading: Boolean = true,
    val isPro: Boolean = true,
    val labels: List<Label> = emptyList(),
    val activeLabelName: String = Label.DEFAULT_LABEL_NAME,
    val archivedLabelCount: Int = 0,
)

val LabelsUiState.archivedLabels: List<Label>
    get() = labels.filter { it.isArchived }

val LabelsUiState.unarchivedLabels: List<Label>
    get() = labels.filter { !it.isArchived && !it.isDefault() }

class LabelsViewModel(
    private val repo: LocalDataRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LabelsUiState())
    val uiState =
        _uiState
            .onStart {
                loadData()
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LabelsUiState())

    private fun loadData() {
        viewModelScope.launch {
            val currentDateTime =
                com.kintsugi.app.common.Time
                    .currentDateTime()
            val today = currentDateTime.date
            val todayEpochDays = today.toEpochDays().toInt()
            val dayOfWeek = today.dayOfWeek

            combine(
                settingsRepository.settings
                    .distinctUntilChanged { old, new ->
                        old.labelName == new.labelName &&
                            old.isPro == new.isPro
                    },
                repo.selectAllLabels(),
                repo.selectActiveHabits(),
            ) { settings, labels, habits ->
                val todayLabelNames =
                    habits
                        .filter { habit ->
                            if (habit.isOneTime) {
                                habit.dueDate == todayEpochDays
                            } else {
                                habit.scheduledDays.contains(dayOfWeek)
                            }
                        }.map { it.labelName }
                        .toSet()

                val sortedLabels =
                    labels.sortedWith(
                        compareByDescending<Label> {
                            todayLabelNames.contains(it.name)
                        }.thenBy { it.orderIndex }.thenBy { it.name },
                    )

                Triple(settings, sortedLabels, labels.filter { it.isArchived }.size)
            }.distinctUntilChanged()
                .collect { (settings, labels, archivedCount) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPro = settings.isPro,
                            activeLabelName = settings.labelName,
                            labels = labels,
                            archivedLabelCount = archivedCount,
                        )
                    }
                }
        }
    }

    fun deleteLabel(labelName: String) {
        viewModelScope.launch {
            val isDeletingActiveLabel = labelName == _uiState.value.activeLabelName
            repo.deleteLabel(labelName)
            if (isDeletingActiveLabel) {
                settingsRepository.activateDefaultLabel()
            }
        }
    }

    fun setArchived(
        labelName: String,
        isArchived: Boolean,
    ) {
        viewModelScope.launch {
            val isActiveLabel = _uiState.value.activeLabelName == labelName
            repo.updateLabelIsArchived(labelName, isArchived)
            if (isActiveLabel) {
                settingsRepository.activateDefaultLabel()
            }
        }
    }

    fun rearrangeLabel(
        fromIndex: Int,
        toIndex: Int,
    ) {
        _uiState.update {
            it.copy(
                labels =
                    it.unarchivedLabels.toMutableList().apply {
                        add(toIndex, removeAt(fromIndex))
                    },
            )
        }
    }

    fun rearrangeLabelsToDisk() {
        viewModelScope.launch {
            val labelsToUpdate =
                _uiState.value.unarchivedLabels.mapIndexed { index, label ->
                    Pair(label.name, index.toLong())
                }
            if (labelsToUpdate.size > 1) {
                repo.bulkUpdateLabelOrderIndex(labelsToUpdate)
            }
        }
    }

    fun duplicateLabel(
        name: String,
        isDefault: Boolean = false,
    ) {
        viewModelScope.launch {
            _uiState.update { uiState ->
                uiState.copy(
                    labels =
                        uiState.labels.toMutableList().apply {
                            val index =
                                indexOfFirst { it.name == if (isDefault) Label.DEFAULT_LABEL_NAME else name }
                            if (index != -1) {
                                val label = get(index)
                                val newLabelName = generateUniqueNameForDuplicate(name, map { it.name })
                                val newLabel = label.copy(name = newLabelName)
                                insertLabelAt(newLabel, index + 1)
                            }
                        },
                )
            }
        }
    }

    private fun insertLabelAt(
        label: Label,
        index: Int,
    ) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    labels =
                        state.labels.toMutableList().apply {
                            add(index, label)
                        },
                )
            }
            repo.insertLabelAndBulkRearrange(
                label,
                _uiState.value.unarchivedLabels.mapIndexed { index, label ->
                    Pair(label.name, index.toLong())
                },
            )
        }
    }
}

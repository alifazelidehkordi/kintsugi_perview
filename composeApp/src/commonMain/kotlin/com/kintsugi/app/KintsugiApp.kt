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
package com.kintsugi.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kintsugi.app.backup.BackupScreen
import com.kintsugi.app.billing.ProScreen
import com.kintsugi.app.bl.TimerForegroundMonitor
import com.kintsugi.app.data.settings.ThemePreference
import com.kintsugi.app.habits.HabitDashboardScreen
import com.kintsugi.app.habits.HabitsScreen
import com.kintsugi.app.labels.addedit.AddEditLabelScreen
import com.kintsugi.app.labels.archived.ArchivedLabelsScreen
import com.kintsugi.app.labels.main.LabelsScreen
import com.kintsugi.app.main.AboutDest
import com.kintsugi.app.main.AcknowledgementsDest
import com.kintsugi.app.main.AddEditLabelDest
import com.kintsugi.app.main.ArchivedLabelsDest
import com.kintsugi.app.main.BackupDest
import com.kintsugi.app.main.HabitDashboardDest
import com.kintsugi.app.main.HabitsDest
import com.kintsugi.app.main.LabelsDest
import com.kintsugi.app.main.LicensesDest
import com.kintsugi.app.main.MainDest
import com.kintsugi.app.main.MainScreen
import com.kintsugi.app.main.NotificationSettingsDest
import com.kintsugi.app.main.OnboardingDest
import com.kintsugi.app.main.ProDest
import com.kintsugi.app.main.SettingsDest
import com.kintsugi.app.main.StatsDest
import com.kintsugi.app.main.TimerDurationsDest
import com.kintsugi.app.main.UserInterfaceDest
import com.kintsugi.app.main.route
import com.kintsugi.app.onboarding.MainViewModel
import com.kintsugi.app.onboarding.OnboardingScreen
import com.kintsugi.app.platform.PlatformContext
import com.kintsugi.app.platform.configureSystemBars
import com.kintsugi.app.platform.setFullscreen
import com.kintsugi.app.platform.setShowWhenLocked
import com.kintsugi.app.settings.SettingsScreen
import com.kintsugi.app.settings.about.AboutScreen
import com.kintsugi.app.settings.about.AcknowledgementsScreen
import com.kintsugi.app.settings.about.LicensesScreen
import com.kintsugi.app.settings.notifications.NotificationsScreen
import com.kintsugi.app.settings.timerdurations.TimerProfileScreen
import com.kintsugi.app.settings.timerstyle.UserInterfaceScreen
import com.kintsugi.app.stats.StatisticsScreen
import com.kintsugi.app.ui.ApplicationTheme
import com.kintsugi.app.ui.ObserveAsEvents
import com.kintsugi.app.ui.SnackbarController
import com.kintsugi.app.ui.popBackStack2
import kintsugi_productivity.composeapp.generated.resources.Res
import kintsugi_productivity.composeapp.generated.resources.splash_background_full
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun KintsugiApp(
    platformContext: PlatformContext,
    mainViewModel: MainViewModel,
    onUpdateClicked: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val timerForegroundMonitor: TimerForegroundMonitor = koinInject()

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    var showCustomSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (showCustomSplash) {
            delay(1500) // Slightly faster transition
            showCustomSplash = false
        }
    }

    val isDarkTheme =
        if (uiState.darkThemePreference == ThemePreference.SYSTEM) {
            isSystemInDarkTheme()
        } else {
            uiState.darkThemePreference == ThemePreference.DARK
        }
    LaunchedEffect(isDarkTheme) {
        platformContext.configureSystemBars(
            isDarkTheme = isDarkTheme,
        )
    }

    val showWhenLocked = uiState.showWhenLocked
    val isFinished = uiState.isFinished
    var isMainScreen by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(showWhenLocked) {
        platformContext.setShowWhenLocked(showWhenLocked)
    }

    val fullscreenMode = isMainScreen && uiState.fullscreenMode
    var fullScreenJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(fullscreenMode) {
        fullscreenMode.let {
            platformContext.setFullscreen(it)
            if (!it) fullScreenJob?.cancel()
        }
    }

    LifecycleResumeEffect(Unit) {
        timerForegroundMonitor.onBringToForeground(coroutineScope)
        onPauseOrDispose {
            timerForegroundMonitor.onSendToBackground()
        }
    }

    var hideBottomBar by remember(fullscreenMode) {
        mutableStateOf(fullscreenMode)
    }

    val onSurfaceClick = {
        if (fullscreenMode) {
            fullScreenJob?.cancel()
            fullScreenJob =
                coroutineScope.launch {
                    platformContext.setFullscreen(false)
                    hideBottomBar = false
                    executeDelayed(3000) {
                        platformContext.setFullscreen(true)
                        hideBottomBar = true
                    }
                }
        }
    }

    val startDestination =
        remember(mainUiState.showOnboarding) {
            if (mainUiState.showOnboarding) {
                OnboardingDest
            } else {
                MainDest
            }
        }

    ApplicationTheme(darkTheme = isDarkTheme, dynamicColor = uiState.isDynamicColor) {
        if (showCustomSplash) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.splash_background_full),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.6f),
                    contentScale = ContentScale.Fit,
                )
            }
        } else {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                isMainScreen = destination.route == MainDest.route
            }

            LaunchedEffect(isFinished) {
                if (isFinished) {
                    navController.currentDestination?.route?.let {
                        val shouldNavigate = it != MainDest.route
                        if (shouldNavigate) {
                            navController.navigate(MainDest) {
                                popUpTo(MainDest) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }

            ObserveAsEvents(
                flow = SnackbarController.events,
                snackbarHostState,
            ) { event ->
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result =
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.action?.name,
                            withDismissAction = true,
                            duration = event.duration,
                        )

                    if (result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                    )
                },
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable<OnboardingDest> { OnboardingScreen() }
                    composable<MainDest> {
                        MainScreen(
                            onSurfaceClick = onSurfaceClick,
                            hideBottomBar = hideBottomBar,
                            navController = navController,
                            mainViewModel = mainViewModel,
                            onUpdateClicked = onUpdateClicked ?: {},
                        )
                    }
                    composable<LabelsDest> {
                        LabelsScreen(
                            onNavigateToLabel = navController::navigate,
                            onNavigateToArchivedLabels = {
                                navController.navigate(ArchivedLabelsDest)
                            },
                            onNavigateToPro = { navController.navigate(ProDest) },
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<HabitsDest> {
                        HabitsScreen(
                            onNavigateToTimer = {
                                navController.navigate(MainDest) {
                                    popUpTo(MainDest) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<HabitDashboardDest> {
                        HabitDashboardScreen(
                            onNavigateBack = navController::popBackStack2,
                        )
                    }

                    composable<AddEditLabelDest> {
                        val addEditLabelDest = it.toRoute<AddEditLabelDest>()
                        AddEditLabelScreen(
                            labelName = addEditLabelDest.name,
                            onNavigateToDefault = { navController.navigate(TimerDurationsDest) },
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<ArchivedLabelsDest> {
                        ArchivedLabelsScreen(
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<StatsDest> {
                        StatisticsScreen(
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<SettingsDest> {
                        SettingsScreen(
                            onNavigateToUserInterface = {
                                navController.navigate(
                                    UserInterfaceDest,
                                )
                            },
                            onNavigateToNotifications = {
                                navController.navigate(
                                    NotificationSettingsDest,
                                )
                            },
                            onNavigateToDefaultLabel = {
                                navController.navigate(TimerDurationsDest)
                            },
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<TimerDurationsDest> {
                        TimerProfileScreen(
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<UserInterfaceDest> {
                        UserInterfaceScreen(
                            onNavigateToPro = { navController.navigate(ProDest) },
                            onNavigateBack = navController::popBackStack2,
                        )
                    }
                    composable<NotificationSettingsDest> {
                        NotificationsScreen(
                            onNavigateBack = navController::popBackStack2,
                        )
                    }

                    composable<BackupDest> {
                        BackupScreen(
                            onNavigateToPro = { navController.navigate(ProDest) },
                            onNavigateBack = navController::popBackStack2,
                            onNavigateToMainAndReset = {
                                navController.navigate(MainDest) {
                                    popUpTo(MainDest) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable<AboutDest> {
                        AboutScreen(
                            mainViewModel = mainViewModel,
                            onNavigateToLicenses = {
                                navController.navigate(
                                    LicensesDest,
                                )
                            },
                            onNavigateToAcknowledgements = {
                                navController.navigate(
                                    AcknowledgementsDest,
                                )
                            },
                            onNavigateBack = navController::popBackStack2,
                            onNavigateToMain = {
                                navController.navigate(MainDest) {
                                    popUpTo(MainDest) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }
                    composable<LicensesDest> {
                        LicensesScreen(onNavigateBack = navController::popBackStack2)
                    }
                    composable<AcknowledgementsDest> {
                        AcknowledgementsScreen(navController::popBackStack2)
                    }
                    composable<ProDest> {
                        ProScreen(onNavigateBack = { navController.popBackStack2() })
                    }
                }
            }
        }
    }
}

private suspend fun executeDelayed(
    delay: Long,
    block: () -> Unit,
) {
    coroutineScope {
        delay(delay)
        block()
    }
}

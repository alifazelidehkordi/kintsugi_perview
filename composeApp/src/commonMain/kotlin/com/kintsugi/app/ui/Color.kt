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
package com.kintsugi.app.ui
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

fun String.toColorInt(): Int {
    var color = substring(1).toLong(16)
    if (length == 7) {
        // Set the alpha value.
        color = color or 0x00000000FF000000L
    }
    return color.toInt()
}

// User-defined Modern Deep Purple Theme
val primaryPurple = Color(0xFFB388FF)
val secondaryPurple = Color(0xFFC084FC) // For charts/accent
val highlightPurple = Color(0xFFD8B4FE) // For overview/highlights
val containerPurple = Color(0xFF2A1B3D) // For Heatmap/Cards
val backgroundDeep = Color(0xFF050507)

val primaryLight = Color(0xFF6750A4)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFEADDFF)
val onPrimaryContainerLight = Color(0xFF21005D)
val secondaryLight = Color(0xFF625B71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE8DEF8)
val onSecondaryContainerLight = Color(0xFF1D192B)
val tertiaryLight = Color(0xFF7D5260)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E4)
val onTertiaryContainerLight = Color(0xFF31111D)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFFBFE)
val onBackgroundLight = Color(0xFF1C1B1F)
val surfaceLight = Color(0xFFFFFBFE)
val onSurfaceLight = Color(0xFF1C1B1F)
val surfaceVariantLight = Color(0xFFE7E0EC)
val onSurfaceVariantLight = Color(0xFF49454F)
val outlineLight = Color(0xFF79747E)
val outlineVariantLight = Color(0xFFCAC4D0)
val scrimLight = Color(0xFF000000)

val primaryDark = primaryPurple
val onPrimaryDark = Color(0xFF000000)
val primaryContainerDark = containerPurple
val onPrimaryContainerDark = highlightPurple
val secondaryDark = secondaryPurple
val onSecondaryDark = Color(0xFF000000)
val secondaryContainerDark = containerPurple
val onSecondaryContainerDark = highlightPurple
val tertiaryDark = highlightPurple
val onTertiaryDark = Color(0xFF000000)
val tertiaryContainerDark = containerPurple
val onTertiaryContainerDark = highlightPurple
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = backgroundDeep
val onBackgroundDark = Color(0xFFE6E1E5)
val surfaceDark = backgroundDeep
val onSurfaceDark = Color(0xFFE6E1E5)
val surfaceVariantDark = containerPurple
val onSurfaceVariantDark = highlightPurple
val outlineDark = Color(0xFF938F99)
val outlineVariantDark = Color(0xFF49454F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE6E1E5)
val inverseOnSurfaceDark = Color(0xFF313033)
val inversePrimaryDark = Color(0xFF6750A4)

val MaterialTheme.localColorsPalette: CustomColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalColorsPalette.current

val lightColorScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
    )

val darkColorScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
    )

@Immutable
data class CustomColorsPalette(
    val colors: List<Color> = listOf(Color.Unspecified),
)

val LocalColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val LightColorsPalette =
    CustomColorsPalette(lightPalette.map { Color(it.toColorInt()) })
val DarkColorsPalette =
    CustomColorsPalette(darkPalette.map { Color(it.toColorInt()) })

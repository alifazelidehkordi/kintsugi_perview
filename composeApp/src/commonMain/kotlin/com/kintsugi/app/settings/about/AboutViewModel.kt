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
package com.kintsugi.app.settings.about

import androidx.lifecycle.ViewModel
import com.kintsugi.app.common.FeedbackHelper
import com.kintsugi.app.common.UrlOpener

class AboutViewModel(
    private val urlOpener: UrlOpener,
    private val feedbackHelper: FeedbackHelper,
) : ViewModel() {
    fun openSourceCode() {
        urlOpener.openUrl(REPO_URL)
    }

    fun openTranslationPage() {
        urlOpener.openUrl(TRANSLATE_URL)
    }

    fun openGooglePlay() {
        urlOpener.openUrl(GOOGLE_PLAY_URL)
    }

    fun sendFeedback() {
        feedbackHelper.sendFeedback()
    }

    companion object {
        const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.kintsugi.app"
        const val REPO_URL = "https://github.com/alifazelidehkordi/kintsugi_preview"
        const val TRANSLATE_URL = "https://crowdin.com/project/kintsugi"
    }
}

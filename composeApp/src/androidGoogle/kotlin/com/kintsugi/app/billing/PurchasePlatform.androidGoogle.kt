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
package com.kintsugi.app.billing

import com.kintsugi.app.BuildConfig
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure

actual fun configurePurchasesFromPlatform() {
    val apiKey =
        if (BuildConfig.DEBUG) {
            BuildConfig.REVENUECAT_API_KEY_DEBUG
        } else {
            BuildConfig.REVENUECAT_API_KEY_RELEASE
        }.takeIf { it.isNotBlank() } ?: return

    if (Purchases.isConfigured) return
    Purchases.configure(apiKey = apiKey)
}

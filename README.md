<p align="center">
  <img src="docs/banner.png" alt="Kintsugi Productivity Banner">
</p>

# Kintsugi Productivity
<p align="center">
  <strong>A privacy-friendly Pomodoro and focus timer for Android and iOS, built with Kotlin Multiplatform.</strong>
</p>

<p align="center">
  <a href="COPYING.md"><img alt="License: GPL v3" src="https://img.shields.io/badge/License-GPLv3-blue.svg"></a>
  <a href="https://kotlinlang.org/docs/multiplatform.html"><img alt="Kotlin Multiplatform" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"></a>
  <a href="https://www.jetbrains.com/lp/compose-multiplatform/"><img alt="Compose Multiplatform" src="https://img.shields.io/badge/UI-Compose%20Multiplatform-4285F4.svg"></a>
  <img alt="Version" src="https://img.shields.io/badge/version-3.2.0--kintsugi-2E7D32.svg">
</p>

Kintsugi helps you protect deep-work time without turning productivity into another feed to check. It combines Pomodoro sessions, count-up flow tracking, habits, one-time tasks, labels, reminders, backups, exports, and detailed statistics in a local-first app.

## ✨ Highlights

- ⏱️ **Two focus modes**: classic Pomodoro countdowns and flexible count-up flow sessions.
- 🧩 **Labels, habits, and tasks**: organize focus time by project, routine, or one-off work.
- 📊 **Useful statistics**: timelines, heatmaps, history charts, focus distribution, and editable sessions.
- 🔐 **Local-first by design**: the app stores productivity data on-device and supports portable exports.
- ☁️ **Backup options**: local/manual backups, Google Drive in the Google Android flavor, and iCloud support on iOS.
- 🎨 **Deep customization**: themes, timer display style, notification behavior, sounds, vibration, torch, fullscreen, and focus protection.
- 🧱 **Real multiplatform codebase**: shared Compose Multiplatform UI with Android and iOS platform implementations.

## 🚦 Platform Status

| Platform | Status | Notes |
| --- | --- | --- |
| 🤖 Android | Release-ready source and APK builds | `google` and `fdroid` flavors are available. GitHub Releases currently publish Android APKs. |
| 🍎 iOS | Source included, Xcode distribution required | The `iosApp` project includes the native host, iCloud backup code, bundled sounds, RevenueCat bridge, and Live Activity extension. Public distribution still needs Apple signing, archive, TestFlight, or App Store Connect. |

- Current app version: `3.2.0-kintsugi`
- Android `versionCode`: `356`
- iOS bundle id: `app.kintsugi.productivity`

## 📦 Releases

Android APKs are published from GitHub Releases when available:

- `kintsugi-3.2.0-kintsugi-google-release.apk`
- `kintsugi-3.2.0-kintsugi-fdroid-release.apk`
- `SHA256SUMS.txt`

The current Android release build is signed with the debug signing config. Replace it with a private release keystore before publishing production builds outside this repository.

iOS does not produce an APK-style artifact. Build and distribute it through Xcode using Apple signing, TestFlight, or App Store Connect.

## 🧭 Feature Tour

### Focus And Session Flow

- Pomodoro work sessions with short and long breaks.
- Count-up sessions for open-ended deep work.
- Break-budget tracking while using the count-up flow.
- Gesture controls: tap to start or pause, swipe to skip or stop, and swipe up to add time.
- Finished-session review with labels, interruptions, notes, and focus/break metadata.
- Auto-start options for focus and break sessions.

### Planning And Tracking

- Habit and one-time task management.
- Custom labels with archive, duplicate, reorder, and color options.
- Per-label timer profiles with custom durations and long-break behavior.
- Editable history, manual session creation, and archived-label filtering.
- Statistics for daily/weekly focus, heatmaps, timelines, charts, focus distribution, and focus/break ratios.

### Privacy, Backup, And Portability

- Local Room database named `kintsugi-db`.
- Manual backup and restore.
- CSV and JSON export.
- Local auto-backup on Android through WorkManager.
- Google Drive backup in the Google Android flavor.
- iCloud backup and restore support on iOS.
- No ads or analytics. See [PRIVACY_POLICY.md](PRIVACY_POLICY.md).

### System Integration

- Android foreground timer service, alarm/reminder receivers, notification channels, Do Not Disturb integration, lock-screen behavior, fullscreen mode, vibration, torch, and custom sounds.
- Google Play review, update, billing, and Drive integrations in the Google flavor.
- F-Droid flavor with Google-specific services removed or stubbed.
- iOS Swift host, iCloud documents, app groups, background refresh registration, bundled notification sounds, and ActivityKit Live Activity support.

## 🛠️ Build From Source

### Requirements

- JDK 17 or newer.
- Android Studio with the Android SDK installed.
- Xcode and iOS SDK compatible with the checked-in project settings.

The iOS app target is configured with deployment target `18.2`. The Live Activity extension target is currently configured as `26.0`.

### Android

```bash
git clone https://github.com/alifazelidehkordi/kintsugi_perview.git
cd kintsugi_perview

./gradlew :composeApp:assembleGoogleDebug
./gradlew :composeApp:assembleFdroidDebug
```

Release builds:

```bash
./gradlew :composeApp:assembleRelease
```

Generated APKs are written under:

```text
composeApp/build/outputs/apk/
```

Useful release outputs:

```text
composeApp/build/outputs/apk/google/release/composeApp-google-release.apk
composeApp/build/outputs/apk/fdroid/release/composeApp-fdroid-release.apk
```

### iOS

Open the Xcode project:

```bash
open iosApp/iosApp.xcodeproj
```

Before building for a device or TestFlight, review:

- Apple Development Team and signing certificates.
- Bundle identifiers in `iosApp/Configuration/Config.xcconfig`.
- iCloud containers and app group entitlements.
- RevenueCat keys in `iosApp/iosApp/Info.plist`.
- Live Activity extension target settings.

The iOS project includes the native host app, shared Compose entry point, iCloud backup implementation, local file import/export prompts, notification handling, timer state persistence, RevenueCat bridge, and ActivityKit Live Activity updates.

## 🧬 Android Flavors

| Flavor | Purpose | Includes |
| --- | --- | --- |
| `google` | Play Store style build | Google Drive backup, Google Play billing/review/update flows, Play Services integration. |
| `fdroid` | Open-source distribution build | Google-specific services removed or replaced with safe stubs. |

## 🗂️ Project Layout

```text
composeApp/
  src/commonMain/        Shared Compose UI, navigation, resources, timer logic, settings, stats, backups, labels, habits
  src/androidMain/       Android app shell, notifications, alarms, local backups, permissions, sounds
  src/androidGoogle/     Google Drive backup, RevenueCat/Google Play purchases, in-app updates, review flow
  src/androidFdroid/     F-Droid-safe backup and billing stubs
  src/iosMain/           iOS database, backup, notifications, billing bridge, Live Activity bridge, platform services

iosApp/                  Native iOS host app, Xcode project, sounds, entitlements, Live Activity extension
gradle/                  Version catalog and Gradle wrapper files
composeApp/schemas/      Room database schemas
docs/                    Architecture and platform notes
```

Important modules:

- `KintsugiApp.kt`: shared app shell, splash, theme, navigation, and snackbar handling.
- `main/`: timer screen, dial controls, finished-session flow, and navigation sheet.
- `bl/TimerManager.kt`: core timer state machine.
- `data/local/`: Room database, DAOs, repositories, migrations, sessions, labels, timer profiles, habits.
- `backup/`: manual backup, restore, CSV export, JSON export, and cloud/local backup UI.
- `stats/`: overview, timeline, history charts, heatmap, focus distribution, and session editing.
- `settings/`: reminders, notifications, UI, timer profiles, permissions, about, acknowledgements, and license screens.
- `iosApp/iosApp/KintsugiLiveActivityManager.swift`: Swift side of ActivityKit updates.
- `iosApp/KintsugiInProgress/`: Live Activity UI, assets, and preview states.

## 🧰 Tech Stack

- Kotlin `2.3.0`
- Compose Multiplatform `1.10.0`
- Android Gradle Plugin `8.13.2`
- Room Multiplatform with KSP
- DataStore Preferences
- Koin dependency injection
- Kotlinx Serialization and DateTime
- Vico charts
- Compottie/Lottie animations
- Kermit logging
- Android WorkManager, Play Core, Google Drive API, RevenueCat KMP
- iOS Swift host, ActivityKit, iCloud Documents, app groups, and background task scheduling

## 🧪 Development

Useful checks:

```bash
./gradlew :composeApp:check
./gradlew spotlessCheck
```

Useful tests:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:testGoogleDebugUnitTest
./gradlew :composeApp:connectedAndroidTest
```

Common local files such as build outputs, release APKs, signing keys, `.env` files, and assistant-specific workflow notes are ignored by Git. Keep secrets and local-only notes out of commits.

## 🔒 Privacy

Kintsugi is designed around local ownership of productivity data. The app supports export and backup flows so users can move their data without depending on a hosted account system.

Read the full policy in [PRIVACY_POLICY.md](PRIVACY_POLICY.md).

## 📄 License

Kintsugi Productivity is licensed under the GNU General Public License v3.0. See [COPYING.md](COPYING.md).

## 🙏 Acknowledgement

Kintsugi is inspired by Goodtime and reworked around Kintsugi branding, habit/task planning, backup flows, iOS support, and a Kotlin Multiplatform architecture.

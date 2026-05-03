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

import ActivityKit
import WidgetKit
import SwiftUI
import AppIntents

// MARK: - Constants

private enum UIConstants {
    static let productIconSize: CGFloat = 24
    static let productIconCornerRadius: CGFloat = 4
    static let timerTypeIconSize: CGFloat = 20
    static let labelIconSize: CGFloat = 12
    static let compactTimerWidth: CGFloat = 50
    static let hourInSeconds: TimeInterval = 3600
    static let pausedTimerOpacity: Double = 0.35
    static let buttonSpacing: CGFloat = 6
}

struct KintsugiLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: KintsugiActivityAttributes.self) { context in
            // LOCK SCREEN / BANNER VIEW
            KintsugiLockScreenView(context: context)
                .activitySystemActionForegroundColor(.white)

        } dynamicIsland: { context in
            DynamicIsland {
                // EXPANDED VIEW (long-press) - Similar to lock screen layout
                DynamicIslandExpandedRegion(.leading) {
                    Image("product_icon")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: UIConstants.productIconSize, height: UIConstants.productIconSize)
                        .cornerRadius(UIConstants.productIconCornerRadius).padding(4)
                }

                DynamicIslandExpandedRegion(.trailing) {
                    KintsugiTimerDisplay(context: context, style: .expanded)
                        .padding(4)
                }

                DynamicIslandExpandedRegion(.center) {
                    VStack() {
                        if !context.attributes.isDefaultLabel && !context.attributes.labelColorHex.isEmpty {
                            KintsugiLabelBadge(
                                labelName: context.attributes.labelName,
                                labelColorHex: context.attributes.labelColorHex,
                                fontSize: .caption,
                                foregroundColor: .white
                            )
                        }
                        KintsugiStatusText(
                            context: context,
                            font: .body,
                            foregroundColor: .white
                        )
                    }
                }

            } compactLeading: {
                timerTypeIcon(context.attributes.timerType)

            } compactTrailing: {
                KintsugiTimerDisplay(context: context, style: .compact)
                    .frame(width: UIConstants.compactTimerWidth)

            } minimal: {
                timerTypeIcon(context.attributes.timerType)

            }
        }
    }

    // MARK: - Helper Functions

    private func timerTypeIcon(_ type: KintsugiActivityAttributes.TimerType) -> some View {
        Image(iconName(for: type))
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: UIConstants.timerTypeIconSize, height: UIConstants.timerTypeIconSize)
            .foregroundColor(.white)
    }

    private func iconName(for type: KintsugiActivityAttributes.TimerType) -> String {
        switch type {
        case .focus: return "ic_focus"
        case .shortBreak, .longBreak: return "ic_break"
        }
    }

}

// MARK: - Timer Display Component (Handles Countdown AND Count-Up)

struct KintsugiTimerDisplay: View {
    let context: ActivityViewContext<KintsugiActivityAttributes>
    let style: DisplayStyle

    enum DisplayStyle {
        case compact
        case expanded
        case lockscreen
    }

    var body: some View {
        Group {
            if shouldShowAsFinished() {
                // Intentionally empty
            } else if context.state.isPaused {
                pausedTimeText
                    .opacity(UIConstants.pausedTimerOpacity)
            } else {
                Text(
                    timerInterval: context.state.timerStartDate...context.state.timerEndDate,
                    countsDown: context.attributes.isCountdown
                )
            }
        }
        .monospacedDigit()
        .multilineTextAlignment(.trailing)
        .font(fontForStyle)
        .lineLimit(1)
        .foregroundColor(.white)
        .accessibilityLabel(accessibilityLabelText)
    }

    private var accessibilityLabelText: String {
        if shouldShowAsFinished() {
            return "Timer completed"
        } else if context.state.isPaused {
            let time = context.state.displayTime
            return "Timer paused at \(formatTime(time))"
        } else {
            let remaining = context.state.timerEndDate.timeIntervalSince(Date())
            return context.attributes.isCountdown ?
                "Time remaining: \(formatTime(remaining))" :
                "Timer running: \(formatTime(-remaining))"
        }
    }

    /// Check if timer has actually expired based on real time
    private func isTimerExpired() -> Bool {
        // Only countdown timers can expire
        guard context.attributes.isCountdown else { return false }

        // Don't check paused timers (they use stored pausedTimeRemaining)
        guard !context.state.isPaused else { return false }

        // Check if current time has passed the end time
        return Date() >= context.state.timerEndDate
    }

    /// Combined check - true if stale OR time expired
    private func shouldShowAsFinished() -> Bool {
        return context.isStale || isTimerExpired()
    }

    @ViewBuilder
    private var pausedTimeText: some View {
        let time = context.state.displayTime
        Text(formatTime(time))
    }

    private var fontForStyle: Font {
        switch style {
        case .compact:
            return .caption2
        case .expanded:
            // Use caption2 if duration is longer than 60 minutes (shows hours)
            let duration = context.state.timerEndDate.timeIntervalSince(context.state.timerStartDate)
            return abs(duration) > UIConstants.hourInSeconds ? .caption2 : .title3
        case .lockscreen:
            let duration = context.state.timerEndDate.timeIntervalSince(context.state.timerStartDate)
            return abs(duration) > UIConstants.hourInSeconds ? .caption2 : .title
        }
    }

    private func formatTime(_ seconds: TimeInterval) -> String {
        let totalSeconds = Int(abs(seconds))
        let hours = totalSeconds / 3600
        let minutes = (totalSeconds % 3600) / 60
        let secs = totalSeconds % 60

        if hours > 0 {
            return String(format: "%d:%02d:%02d", hours, minutes, secs)
        } else {
            return String(format: "%d:%02d", minutes, secs)
        }
    }
}

// MARK: - Status Text Component (Reusable)

struct KintsugiStatusText: View {
    let context: ActivityViewContext<KintsugiActivityAttributes>
    var font: Font = .title
    var foregroundColor: Color = .white

    var body: some View {
        Text(statusText)
            .font(font)
            .foregroundColor(foregroundColor)
            .lineLimit(1)
            .accessibilityLabel("Timer status: \(statusText)")
    }

    private var statusText: String {
        if shouldShowAsFinished() {
            return context.attributes.timerType == .focus
                ? context.attributes.strFocusComplete
                : context.attributes.strBreakComplete
        }

        let type = context.attributes.timerType
        let isPaused = context.state.isPaused

        if type == .focus {
            return isPaused ? context.attributes.strFocusPaused : context.attributes.strFocusInProgress
        } else {
            return context.attributes.strBreakInProgress
        }
    }

    /// Check if timer has actually expired based on real time
    private func isTimerExpired() -> Bool {
        // Only countdown timers can expire
        guard context.attributes.isCountdown else { return false }

        // Don't check paused timers (they use stored pausedTimeRemaining)
        guard !context.state.isPaused else { return false }

        // Check if current time has passed the end time
        return Date() >= context.state.timerEndDate
    }

    /// Combined check - true if stale OR time expired
    private func shouldShowAsFinished() -> Bool {
        return context.isStale || isTimerExpired()
    }
}

// MARK: - Label Badge Component (Reusable)

struct KintsugiLabelBadge: View {
    let labelName: String
    let labelColorHex: String
    var fontSize: Font = .caption
    var foregroundColor: Color = .white

    var body: some View {
        HStack(spacing: UIConstants.buttonSpacing) {
            Image("ic_label")
                .resizable()
                .renderingMode(.template)
                .aspectRatio(contentMode: .fit)
                .frame(width: UIConstants.labelIconSize, height: UIConstants.labelIconSize)
                .foregroundColor(Color(hex: labelColorHex))
            Text(labelName)
                .font(fontSize)
                .foregroundColor(foregroundColor)
                .lineLimit(1)
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Label: \(labelName)")
    }
}

// MARK: - Lock Screen View

struct KintsugiLockScreenView: View {
    let context: ActivityViewContext<KintsugiActivityAttributes>

    var body: some View {
        Group {
            VStack(spacing: 8) {
                HStack() {
                    HStack(spacing: UIConstants.buttonSpacing) {
                        Image("product_icon")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: UIConstants.productIconSize, height: UIConstants.productIconSize)
                            .cornerRadius(UIConstants.productIconCornerRadius)
                        Text("Kintsugi")
                            .font(.caption)
                            .foregroundColor(.white)
                    }
                    Spacer()
                    KintsugiTimerDisplay(context: context, style: .lockscreen)
                }
                VStack(spacing: 16) {
                    HStack() {
                        KintsugiStatusText(
                            context: context,
                            font: .body,
                            foregroundColor: .white
                        )
                        .lineLimit(1)
                        Spacer()
                        if !context.attributes.isDefaultLabel && !context.attributes.labelColorHex.isEmpty {
                            KintsugiLabelBadge(
                                labelName: context.attributes.labelName,
                                labelColorHex: context.attributes.labelColorHex,
                                fontSize: .caption,
                                foregroundColor: .white
                            )
                        }
                    }.padding(.vertical, 4)
                }
            }
            .padding()
        }
        .background(Color.black)
    }
}

// MARK: - Previews

struct KintsugiLiveActivity_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // Live Activity - Focus Countdown
            KintsugiActivityAttributes.previewFocusCountdown
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewRunning,
                    viewKind: .content
                )
                .previewDisplayName("Live Activity - Focus Running")

            KintsugiActivityAttributes.previewFocusCountdown
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewPaused,
                    viewKind: .content
                )
                .previewDisplayName("Live Activity - Focus Paused")

            // Dynamic Island - Compact
            KintsugiActivityAttributes.previewFocusCountdown
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewRunning,
                    viewKind: .dynamicIsland(.compact)
                )
                .previewDisplayName("Dynamic Island - Compact")

            // Dynamic Island - Expanded
            KintsugiActivityAttributes.previewFocusCountdown
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewRunning,
                    viewKind: .dynamicIsland(.expanded)
                )
                .previewDisplayName("Dynamic Island - Expanded")

            KintsugiActivityAttributes.previewFocusCountdown
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewPaused,
                    viewKind: .dynamicIsland(.expanded)
                )
                .previewDisplayName("Dynamic Island - Expanded Paused")

            // Live Activity - Break
            KintsugiActivityAttributes.previewShortBreak
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewRunning,
                    viewKind: .content
                )
                .previewDisplayName("Live Activity - Break")

            // Live Activity - Count Up
            KintsugiActivityAttributes.previewFocusCountUp
                .previewContext(
                    KintsugiActivityAttributes.ContentState.previewCountUpRunning,
                    viewKind: .content
                )
                .previewDisplayName("Live Activity - Count Up")
        }
    }
}

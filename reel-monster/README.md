# Reel Monster — Android app

A small offline Android app inspired by the supplied project brief.

## Features
- Dashboard with today's count, all-time count, and a seven-day bar chart.
- Level 1 manual counting from a draggable floating monster bubble.
- Level 2 best-effort automatic detection on Instagram Reels and YouTube Shorts using Android Accessibility.
- Local-only SharedPreferences storage; no account, backend, or API key.

## Build and install
1. Install **Android Studio** (latest stable) and Android SDK Platform 35.
2. Unzip this folder and open the `reel-monster` folder in Android Studio.
3. Let Gradle sync. If prompted, install the Android Gradle plugin/SDK components.
4. Connect an Android phone with USB debugging enabled, or start an emulator.
5. Press **Run ▶** in Android Studio.

## Use
1. Open Reel Monster.
2. Tap **Allow display over other apps** and enable permission.
3. Return to the app and tap **Start Floating Monster**.
4. Level 1: leave the switch off; tap the floating monster once per Reel/Short. Drag it to move it.
5. Level 2: turn on Automatic detection, then tap **Enable automatic detection** and enable Reel Monster in Android Accessibility settings. Return to the app and start the bubble.
6. Tap **Stop Floating Monster** to remove the bubble.

## Important note about automatic mode
Instagram and YouTube do not provide a public API for counting every viewed Reel/Short. Automatic mode uses visible accessibility text and is therefore best-effort: it can miss videos or occasionally count a screen more than once depending on app version, language, and what the app exposes. Manual mode is reliable. Accessibility permission is used only to inspect visible screen text for likely Reels/Shorts; all counts stay on-device.

# Expense Tracker (Android)

A native Kotlin app to log every expense, set monthly budgets per category, and view spending reports. All data is stored locally on-device (Room/SQLite) — no account, no internet required.

## Features
- Quick expense entry: amount, category (Food, Transport, Shopping, Bills, Fun, Other), note, date, payment method
- Home screen: running list grouped by day, monthly total and daily average
- Budgets: set a monthly ₹ limit per category, progress bar turns amber at 80% and red at 100%
- Reports: month switcher, category breakdown chart, daily spend chart, CSV export (share via any app)

## Option A — Get the APK with no software install (recommended, ~5 minutes)

This project includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that builds the APK automatically on GitHub's servers.

1. Go to [github.com/new](https://github.com/new) and create a new **public or private** repository (any name, e.g. `expense-tracker`). Don't add a README/gitignore.
2. On the new repo's page, click **uploading an existing file**.
3. Unzip this project on your computer, then drag the *entire contents* of the `ExpenseTrackerApp` folder (not the folder itself — its contents: `app`, `gradle`, `build.gradle.kts`, etc.) into the upload box, and commit.
4. Click the **Actions** tab at the top of the repo. A workflow run titled "Build APK" should start automatically (takes 3-5 minutes).
5. When it finishes (green check), click into the run, scroll to **Artifacts**, and download `app-debug-apk` — that's a zip containing `app-debug.apk`.
6. Transfer `app-debug.apk` to your phone and tap it to install (Android will prompt you to allow "install unknown apps" for whichever app you used to open it).

## Option B — Build locally in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) (free) if you don't have it.
2. Open Android Studio → **Open** → select this `ExpenseTrackerApp` folder.
3. Let Gradle sync (Android Studio will download the Android SDK/Gradle distribution automatically — this needs internet on your machine, just once).
   - If Android Studio asks to create a Gradle wrapper, click **OK** / **Create**.
4. Once sync finishes: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click **locate** in the notification, or find the file at:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Copy that `app-debug.apk` to your phone (email, Drive, USB, etc.) and open it to install.
   - You'll need to allow "Install unknown apps" for whichever app you use to open the file (Android will prompt you).

## Notes
- Minimum Android version: 7.0 (API 24).
- This is a debug build (unsigned) — fine for installing on your own phone. If you want a signed release build for wider distribution, use **Build → Generate Signed Bundle / APK** in Android Studio.
- All data lives in the app's local database. Uninstalling the app deletes the data, so use the CSV export on the Reports screen to back up periodically.

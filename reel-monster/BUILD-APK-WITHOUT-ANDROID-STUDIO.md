# Build the APK without Android Studio

You can build the installable APK using GitHub Actions in your browser. You need a free GitHub account.

1. Sign in to https://github.com/ and create a new repository. Name it `reel-monster` and choose Public or Private.
2. Open the new repository, choose **Add file → Upload files**.
3. Extract this ZIP on your computer. Open the extracted `reel-monster` folder, select all its contents (including the hidden `.github` folder), and upload them to the repository. Commit the upload to the `main` branch.
4. In the repository, open **Actions**. If GitHub asks to enable workflows, enable them.
5. Select **Build Reel Monster APK** in the left sidebar. Click **Run workflow → Run workflow**.
6. Wait for the workflow to finish successfully. Open that workflow run and find **Artifacts** at the bottom.
7. Download `reel-monster-debug-apk`. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.
8. Send `app-debug.apk` to your Android phone (USB, Google Drive, or WhatsApp to yourself), open it, and allow installation from that source if Android asks.

This project is Android-only. It cannot be installed on an iPhone. The APK is a debug build for personal testing, not a Play Store release.

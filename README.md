# ACCEND-365

ACCEND-365 is an offline-first Android character progression app built with Kotlin, Jetpack Compose, Room, and MVVM. It organizes a 365-day practice around four pillars: Physical, Mental, Skills, and Social.

## Current foundation

- Compose navigation for onboarding, home, pillars, analysis, leaderboard, and settings
- Room-backed local profile and daily progress storage
- Immediate per-task XP awards
- Level, title, tier, and weekly task XP progression rules
- Dark gold visual system and responsive daily task UI
- Firebase intentionally left behind the repository boundary for a later integration

## Build locally

This project targets API 34, requires JDK 17 or newer, and uses the Gradle wrapper. Configure `local.properties` with your Android SDK path, then run:

```bash
./gradlew test
./gradlew assembleDebug
```

Release signing is intentionally not committed. Add a local keystore and signing configuration before producing a distributable release APK.

## Signed release

For a local release, provide `ACCEND_STORE_FILE`, `ACCEND_STORE_PASSWORD`, `ACCEND_KEY_ALIAS`, and `ACCEND_KEY_PASSWORD` as Gradle properties or environment variables, then run:

```bash
./gradlew assembleRelease
```

The manual GitHub Actions workflow in `.github/workflows/release.yml` expects these repository secrets: `ACCEND_KEYSTORE_BASE64`, `ACCEND_STORE_PASSWORD`, `ACCEND_KEY_ALIAS`, and `ACCEND_KEY_PASSWORD`. It publishes the signed APK when dispatched with a version such as `0.2.0`.

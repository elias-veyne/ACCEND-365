# ACCEND-365

A 365-day sovereign self-mastery protocol app: Physical, Mental, Skills and Social pillars, one day at a time.

## Features

- **Cinematic golden splash** on launch (floating gold particles, ACCEND reveal, progress line)
- **365-day protocol** with per-pillar daily tasks, EXP, levels and unlockable titles
- **Home dashboard** — daily tasks, progression ring, daily quote, four pillar cards
- **Analysis** — heatmap + completion stats
- **Leaderboard** — live cloud cohort standings (All-Time / Friends filter), no fake bots
- **Cloud sync** — real user profiles, XP and task completions sync to the ACCEND Cloud, restorable on a fresh install
- **On-device reminders** and pause controls

## Stack

- Kotlin + Jetpack Compose (Material 3), Room for offline-first local DB
- Cloud transport: CrudCrud free REST API (no account needed; HTTPS + CORS)
- Signed release APK built via GitHub Actions

## Build

Local build requires Android SDK 35 + JDK 17:

```bash
./gradlew assembleRelease
```

Release builds are also produced automatically on GitHub Actions (workflow `release.yml`) with the signing keystore injected from repo secrets.

## Releases

Signed APKs are published as GitHub Releases: https://github.com/elias-veyne/ACCEND-365/releases

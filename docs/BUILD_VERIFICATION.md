# Build Verification

## Environment

The project was verified locally on Linux using Java 21, Gradle 8.9 via the committed wrapper, Android Gradle Plugin 8.7.3, Android SDK Platform 35, and Build Tools 35.0.0. The build uses a single Gradle worker and a 1536 MiB heap configuration to remain suitable for a constrained development environment.

## Required local verification

The requested clean verification sequence completed successfully:

| Command | Result |
|---|---|
| `./gradlew clean` | Successful. |
| `./gradlew test` | Successful; core pipeline tests compiled and ran. |
| `./gradlew assembleDebug` | Successful; generated the native Android debug APK. |
| `./gradlew clean assembleDebug` | Successful in a separate clean reproducibility run. |

The final locally generated APK is located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The reproducibility build produced an APK of approximately 2.4 MiB with SHA-256:

```text
e4d4eabaef04df2304ea9a982e693818655147885e6507a57a72e91f34b56446
```

The Android compiler emitted only Android API deprecation warnings for direct status and navigation bar color setters. They do not block the build and do not affect the mod-loader core, Android storage import flow, or APK generation.

## Test coverage

The automated Kotlin tests validate manifest parsing and invalid-manifest rejection, mod storage installation and state changes, missing dependencies, conflicts, circular ordering, load ordering, overlay patches, copy patches, backup creation, failed-patch rollback, and safe mode.

No game assets, executable code, application packages, or protected game data were included in the tests. The complete end-to-end test path uses the repository’s harmless dummy-game environment and example text mods.

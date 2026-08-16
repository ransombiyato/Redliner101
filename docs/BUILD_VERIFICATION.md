# Build Verification

## Environment

The project was verified locally on Linux using Java 21, Gradle 8.9 via the committed wrapper, Android Gradle Plugin 8.7.3, Android SDK Platform 35, and Build Tools 35.0.0. The build uses a single Gradle worker and a 1536 MiB heap configuration to remain suitable for a constrained development environment.

## Required local verification

The requested clean verification sequence completed successfully:

| Command | Result |
|---|---|
| `./gradlew clean test assembleDebug` | Successful after the real Hadrian-port workspace implementation. |
| `./gradlew :core:test` | Successful; core pipeline, Android-payload workspace, lifecycle, and safe ZIP tests ran. |
| `./gradlew :app:compileDebugKotlin` | Successful; the Android Storage Access Framework transaction and UI compiled. |

The final locally generated APK is located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The final clean **0.2.0** real-port build produced an APK of **2,472,290 bytes** with SHA-256:

```text
76723930773141918efb4000e7877ff8718e28e04e6e9cc1f866809ce515606e
```

The Android compiler emitted only Android API deprecation warnings for direct status and navigation bar color setters. They do not block the build and do not affect the mod-loader core, Android storage import flow, or APK generation.

## Test coverage

The automated Kotlin tests validate manifest parsing and invalid-manifest rejection, mod storage installation and state changes, missing dependencies, conflicts, circular ordering, load ordering, overlay patches, copy patches, backup creation, failed-patch rollback, safe mode, Android `game.droid` / `data.droid` / WAD workspace recognition, a structural Hadrian-port replacement lifecycle (import, enable, apply, restore, disable, and re-apply), and ZIP traversal rejection.

No game assets, executable code, application packages, or protected game data were included in the tests. The complete real-port lifecycle test uses a structurally faithful fixture containing only short placeholder text, while the Android application performs its actual selected-directory operations through Android's Storage Access Framework at runtime.

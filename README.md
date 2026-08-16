# DemiForge

**DemiForge** is a lightweight, native Android Kotlin/Gradle mod-loader and mod-manager framework. It does not distribute game files, circumvent licensing, alter application packages, or bypass Android sandboxing. Its full installation, dependency, patch, backup, rollback, and recovery flow is tested against a harmless dummy-game adapter.

The initial Deltarune adapter is intentionally diagnostic-only. It can report whether a user-selected, accessible data location is suitable for safe overlay-style modification, but it does not claim access to protected application data and does not modify game APKs, executables, or proprietary assets.

The project builds locally with:

```bash
./gradlew test
./gradlew assembleDebug
```

See `docs/` for the architecture, mod format, safety model, and known platform limitations.

# DemiForge

**DemiForge** is a lightweight native Android Kotlin/Gradle mod manager for **Hadrian's Android Deltarune port**. It does not distribute game files, circumvent licensing, or bypass Android sandboxing. For APK-packaged Hadrian builds, it operates on a user-selected original APK and produces a separately signed, modded copy.

DemiForge inventories recognised `game.droid`, `data.droid`, and WAD payload paths under the selected APK's `assets/` directory. It preserves a private original backup, accepts a chosen Android-ready replacement for an exact discovered target, rebuilds the APK archive, signs it with its local Android Keystore key, verifies that signature, and opens Android's installer. It accepts a mod directory or a safely extracted ZIP package, but it never writes an installed APK or protected private application directory in place.

The project builds locally with:

```bash
./gradlew test
./gradlew assembleDebug
```

Start with [`docs/HADRIAN_APK_PATCHER.md`](docs/HADRIAN_APK_PATCHER.md) for the direct on-device workflow. The research evidence, architecture, safety boundary, and build verification are under [`docs/`](docs/).

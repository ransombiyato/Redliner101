# DemiForge

**DemiForge** is a lightweight native Android Kotlin/Gradle mod manager for **Hadrian's Android Deltarune port**. It does not distribute game files, circumvent licensing, alter application packages, or bypass Android sandboxing. It manages user-supplied, Android-ready mod payloads only in an externally accessible workspace the user selects and reviews.

DemiForge persists Android Storage Access Framework access only to the selected directory. It inventories recognised `game.droid`, `data.droid`, and WAD payload paths, requires confirmation of a workspace-layout fingerprint, validates manifest targets, previews every replacement and backup size, creates app-private originals, applies the selected replacements, and restores the latest backup when needed. It accepts a mod directory or a safely extracted ZIP package; it never writes an APK or protected private application directory.

The project builds locally with:

```bash
./gradlew test
./gradlew assembleDebug
```

Start with [`docs/HADRIAN_PORT_SETUP.md`](docs/HADRIAN_PORT_SETUP.md) for the exact on-device workflow, then use [`examples/hadrian-port-mod-template`](examples/hadrian-port-mod-template) to package a compatible replacement payload. The research evidence, architecture, safety boundary, and build verification are under [`docs/`](docs/).

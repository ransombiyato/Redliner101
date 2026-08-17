# Direct Hadrian APK Patcher

## Purpose

The direct APK patcher is the operative DemiForge workflow for a Hadrian port whose game payloads are packaged under the APK’s `assets/` directory. It exists because changing an **unzipped copy** of an APK does not change the installed Android application.

The patcher does not compile Deltarune or run Gradle on the phone. It performs the following file operations: it copies the user-selected original APK into DemiForge’s private backup storage, inventories only recognised Android-port payloads under `assets/`, rebuilds the APK ZIP with selected payload replacements, signs that rebuilt archive with a locally generated Android Keystore key, verifies the new signature, and invokes Android’s standard package installer.

## User flow

Open the **APK** tab, select the original Hadrian `.apk` file, and inspect the payload list. Only entries named `game.droid`, `data.droid`, or ending in `.wad` beneath `assets/` are offered as replacement targets. Select an Android-ready replacement payload for the exact target, then choose **Build and sign modded APK**.

The result is intentionally a separately signed APK. Android's signature protections prevent it from updating an original APK signed by a different key, so the device may require the original port to be uninstalled first. Back up saves before changing installations. DemiForge preserves the selected original APK in its private backup directory.

> A signature-verified APK proves archive and signing integrity. It does not prove that an independently authored game payload is compatible with the selected Deltarune chapter.

## Technical constraints

The app rejects arbitrary targets, code entries, or executable-library paths. It strips stale JAR signature entries from the rebuilt archive, then generates valid APK Signature Scheme v1, v2, and v3 signatures using the `apksig-android` library.[1] Android Signature Scheme v2 protects the APK’s contents as a whole, so an edited asset must be re-signed before Android will accept the modified package.[2]

## References

[1]: https://github.com/MuntashirAkon/apksig-android "apksig-android — Android port of APK signer and verifier"
[2]: https://source.android.com/docs/security/features/apksigning/v2 "Android Open Source Project — APK Signature Scheme v2"

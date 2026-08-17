# Known Limitations and Safety Boundary

DemiForge is a mod-management framework, not a DRM, licensing, package, or runtime-injection tool. It does not distribute game executables, proprietary libraries, sprites, dialogue, music, or other copyrighted game assets. It does not bypass authentication, license checks, Android sandboxing, or installed-package protections.

The Hadrian Android-port integration is a **direct APK copy-and-patch workflow**. It reads only an APK the user selects, keeps a private backup of that original, offers only recognised `game.droid`, `data.droid`, and WAD entries beneath `assets/` as replacement targets, rebuilds a separate APK copy, signs and verifies that new copy, and hands it to Android's standard installer. It does not edit the installed package in place.

Android applications still cannot safely inspect or modify another application's private `/data/user/0/...` directory or installed APK without platform privileges that DemiForge deliberately does not request. The public release for Hadrian's port documents an external save-path configuration but not a stable game-payload directory. The user must therefore provide the original APK; after modification, Android may require the original app to be uninstalled because the new APK has DemiForge's signing identity rather than the original developer's identity.

The old dummy-game fixture remains only as a historical core-test aid. The current application no longer presents a dummy-game workflow; its structural Android-port fixture verifies import, enable, apply, backup, restore, disable, and re-apply behavior without including any third-party game data.

Device-side file selection relies on Android's Storage Access Framework. Mod compatibility is separate from archive and signing safety: a successful signature verification proves that Android can validate the rebuilt package, but it cannot prove that a PC `data.win` mod has been ported correctly to the Android GameMaker build or that a replacement payload matches the selected chapter.

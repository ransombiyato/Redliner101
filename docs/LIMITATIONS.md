# Known Limitations and Safety Boundary

DemiForge is a mod-management framework, not a DRM, licensing, package, or runtime-injection tool. It does not distribute game executables, proprietary libraries, sprites, dialogue, music, or other copyrighted game assets. It does not bypass authentication, license checks, Android sandboxing, signature protections, or application packaging.

The Hadrian Android-port integration is a **real external-workspace patcher**, not an APK patcher. It can import user-supplied mod packages, preflight their exact payload replacements, make app-private backups, replace recognised `game.droid`, `data.droid`, or WAD payload files through a directory permission the user grants, and restore the recorded originals. Every write is limited to a path displayed in the selected workspace and confirmed by the user.

Android applications still cannot safely inspect or modify another application's private `/data/user/0/...` directory or installed APK without platform privileges that DemiForge deliberately does not request. The public release for Hadrian's port documents an external save-path configuration but not a stable package identifier or universally accessible game-payload directory. DemiForge therefore requires the user to select and review an accessible workspace; it refuses a write when the directory does not expose a recognised payload or its layout changes.

The old dummy-game fixture remains only as a historical core-test aid. The current application no longer presents a dummy-game workflow; its structural Android-port fixture verifies import, enable, apply, backup, restore, disable, and re-apply behavior without including any third-party game data.

Device-side file selection relies on Android's Storage Access Framework. The user must confirm the selected workspace fingerprint before the first write; adding, removing, or renaming a recognised payload invalidates that approval. Mod compatibility is separate from file-operation safety: a successful preflight proves that DemiForge can safely perform the listed replacement, but it cannot prove that a PC `data.win` mod has been ported correctly to the Android GameMaker build.

# Architecture

DemiForge is split into a platform-neutral Kotlin **core** module and a small native Android **app** module. The core contains the mod format, validation, dependency graph, storage, backup, rollback, adapter contracts, and APK archive-safety utilities. Android-specific direct APK selection, rebuild, signing, verification, and installer handoff are confined to the app module.

| Area | Responsibility | Safety property |
|---|---|---|
| `core/model` | Manifest, patch, issue, and installed-mod data types | Explicit typed contracts prevent ad-hoc state. |
| `core/mods` | JSON manifest codec and validator | Rejects malformed IDs and unsafe path traversal before installation. |
| `core/resolution` | Compatibility, dependency, conflict, and load-order resolution | Blocks all patching when resolution has an error. |
| `core/storage` | Installed-mod discovery, enable state, ZIP extraction, and APK asset rebuilding | Uses private app storage, atomic staging, archive path checks, bounded extraction, and target allowlists. |
| `core/recovery` | Backups, copy patches, overlays, and rollback | Copy targets are backed up before changes; failures restore backups. |
| `core/adapters` | Legacy game-specific inspection and patch capability contract | No package enumeration or protected-storage access is allowed. |
| `app/HadrianApkPatchService` | Original APK backup, asset replacement, signing, and verification | Rebuilds a separate signed APK and preserves the selected original. |
| `app/HadrianApkActivity` | User-authorised APK and payload selection plus installer handoff | Shows only recognised `assets/` payloads and exposes only the resulting output APK. |
| `app` | Android activities, user actions, package import, and diagnostics | Does not modify an installed package in place. |

The manager uses a topological sort to determine load order. Dependencies and `loadAfter`/`loadBefore` instructions produce directed edges. Missing required dependencies, version failures, conflicts, incompatible game versions, or cycles stop execution before patch operations begin.

> **Safety rule:** DemiForge does not probe installed packages, access protected application storage, or modify an installed package in place. It can rebuild only a separate copy of an APK the user selects, and it accepts only existing `game.droid`, `data.droid`, or WAD asset entries as replacement targets before re-signing that copy.

The structural Hadrian-port fixture and APK archive fixture are the reference integration tests. They model Android payload paths, verify targeted replacement and stale-signature stripping, and carry no third-party game data. Android Keystore signing is a device-runtime operation and requires verification with the user-supplied target APK before a playable modded APK can be delivered.

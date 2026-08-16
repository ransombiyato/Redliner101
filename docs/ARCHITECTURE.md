# Architecture

DemiForge is split into a platform-neutral Kotlin **core** module and a small native Android **app** module. The core contains the mod format, validation, dependency graph, storage, backup, rollback, adapter contracts, and harmless dummy-game integration. Android-specific UI and storage permission handling are confined to the app module.

| Area | Responsibility | Safety property |
|---|---|---|
| `core/model` | Manifest, patch, issue, and installed-mod data types | Explicit typed contracts prevent ad-hoc state. |
| `core/mods` | JSON manifest codec and validator | Rejects malformed IDs and unsafe path traversal before installation. |
| `core/resolution` | Compatibility, dependency, conflict, and load-order resolution | Blocks all patching when resolution has an error. |
| `core/storage` | Installed-mod discovery and enable state | Uses private app storage and atomic staging before installation. |
| `core/recovery` | Backups, copy patches, overlays, and rollback | Copy targets are backed up before changes; failures restore backups. |
| `core/adapters` | Game-specific inspection and patch capability contract | Game-specific behavior remains isolated and may honestly refuse unsupported operations. |
| `app` | Android activities, user actions, and diagnostics | Does not contain game-specific patch assumptions. |

The manager uses a topological sort to determine load order. Dependencies and `loadAfter`/`loadBefore` instructions produce directed edges. Missing required dependencies, version failures, conflicts, incompatible game versions, or cycles stop execution before patch operations begin.

> **Safety rule:** the Deltarune adapter does not probe installed packages, access protected application storage, modify APKs, or make unsupported architecture assumptions. It only reports the status of a user-selected accessible location, and it currently refuses patch operations.

The dummy-game adapter is the reference integration. It has a small public descriptor and a writable data directory. It supports both overlay patches, written under `.demiforge-overlay`, and reversible copy patches, backed up before modification.

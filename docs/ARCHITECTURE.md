# Architecture

DemiForge is split into a platform-neutral Kotlin **core** module and a small native Android **app** module. The core contains the mod format, validation, dependency graph, storage, backup, rollback, adapter contracts, a structural Hadrian-port workspace recognizer, and archive-safety utilities. Android-specific UI and the real Storage Access Framework transaction are confined to the app module.

| Area | Responsibility | Safety property |
|---|---|---|
| `core/model` | Manifest, patch, issue, and installed-mod data types | Explicit typed contracts prevent ad-hoc state. |
| `core/mods` | JSON manifest codec and validator | Rejects malformed IDs and unsafe path traversal before installation. |
| `core/resolution` | Compatibility, dependency, conflict, and load-order resolution | Blocks all patching when resolution has an error. |
| `core/storage` | Installed-mod discovery, enable state, and ZIP extraction | Uses private app storage, atomic staging, archive path checks, and bounded extraction. |
| `core/recovery` | Backups, copy patches, overlays, and rollback | Copy targets are backed up before changes; failures restore backups. |
| `core/adapters` | Game-specific inspection, payload inventories, and patch capability contract | The Hadrian recognizer accepts only selected `game.droid`, `data.droid`, and WAD paths; it never guesses an app package. |
| `app/HadrianWorkspaceService` | Persistent document-tree access, preflight, backup, write, and restore | Requires an Android picker grant and user-reviewed layout fingerprint before a write. |
| `app` | Android activities, user actions, package import, and diagnostics | Shows all real target paths and byte totals before an apply operation. |

The manager uses a topological sort to determine load order. Dependencies and `loadAfter`/`loadBefore` instructions produce directed edges. Missing required dependencies, version failures, conflicts, incompatible game versions, or cycles stop execution before patch operations begin.

> **Safety rule:** the Hadrian adapter does not probe installed packages, access protected application storage, modify APKs, or make unsupported package-name assumptions. It can patch only an explicitly selected, user-reviewed document-tree workspace, and only existing recognised Android payload paths through a reversible `COPY` transaction.

The structural Hadrian-port fixture is the reference integration test. It models a chapter payload path, verifies import, enable, replacement, backup, restoration, disable, and re-apply behavior, and carries no third-party game data. The legacy dummy adapter remains in the core module only for historical test coverage and is not offered as an application workflow.

# Known Limitations and Safety Boundary

DemiForge is a mod-management framework, not a DRM, licensing, package, or runtime-injection tool. It does not distribute game executables, proprietary libraries, sprites, dialogue, music, or other copyrighted game assets. It does not bypass authentication, license checks, Android sandboxing, signature protections, or application packaging.

The initial Deltarune adapter is **diagnostic-only**. Android applications normally cannot safely inspect or modify another application’s private data or APK without platform privileges that DemiForge deliberately does not request. The adapter therefore accepts only an explicitly selected accessible location, reports its access state, and refuses patch operations until an adapter-specific, legitimate external-data integration point is established.

The dummy-game adapter is fully functional and exists to validate discovery, installation, enable/disable state, dependency resolution, conflict detection, deterministic ordering, overlays, copy patches, backups, rollback, safe mode, and failed-patch recovery without any third-party game data.

Device-side file selection relies on the Android Storage Access Framework. A future adapter may expose an import flow for a user-owned external data directory, but it must continue to use the adapter contract and must never silently overwrite game-installation files.

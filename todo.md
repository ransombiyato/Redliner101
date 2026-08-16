# Project TODO

- [x] Inspect the Redliner101 branch, repository history, and existing build configuration.
- [x] Create a non-destructive safety branch before removing obsolete implementation files.
- [x] Preserve only useful Gradle and Android infrastructure; clean old project code from the working tree.
- [x] Scaffold the lightweight native Android Kotlin/Gradle application with a committed Gradle wrapper.
- [x] Implement mod manifest parsing, discovery, validation, installation, enable/disable, removal, and logging.
- [x] Implement dependency, conflict, compatibility, load-order, backup, rollback, and safe-mode services.
- [x] Implement the reusable game-adapter boundary and an honest Deltarune installation/data detector.
- [x] Implement a harmless dummy game adapter and example mods for end-to-end testing.
- [x] Implement the native Android mod-manager screens, settings, and diagnostics.
- [x] Add automated tests covering normal, invalid, conflicting, circular, rollback, and recovery paths.
- [x] Write architecture, mod-format, limitation, and build-verification documentation.
- [x] Run clean Gradle tests and generate a locally built debug APK.
- [x] Verify repository cleanup, package the deliverables, and prepare the final report.

## Hadrian Android Deltarune Port Integration

- [x] Identify Hadrian port’s authoritative distribution, versioning, storage layout, and documented modding workflow.
- [ ] Obtain the user-accessible Deltarune port data path or a non-sensitive directory listing from the user.
- [x] Define a real port-specific detection marker and restrict modifications to confirmed accessible data files.
- [x] Replace the diagnostic-only Deltarune adapter with manifest-driven installation, backups, conflict checks, and rollback for the confirmed layout.
- [x] Add real sample package metadata that follows the port’s documented mod format without bundling game assets.
- [ ] Test the revised adapter against a user-provided non-sensitive data snapshot or directory manifest.
- [x] Build, verify, document, commit, and deliver the revised real-mod-management APK.

## Operational Quality Requirements

- [x] Verify every Hadrian-port claim against primary or reproducible technical sources before implementing it.
- [x] Make the selected Deltarune directory persistent through Android Storage Access Framework permission, not a one-time diagnostic selection.
- [x] Show a file-level patch preview and a preflight backup estimate before a real installation can start.
- [x] Require an explicit recognised-port marker before writing any mod file, with a clear manual-review route for new port versions.
- [x] Exercise a structurally faithful port-data fixture through import, enable, apply, restore, disable, and re-apply flows.
- [x] Add ZIP mod-package import with archive traversal and size protections for practical phone-based mod installation.

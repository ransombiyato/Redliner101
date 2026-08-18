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

## Current Release Compatibility Check

- [ ] Confirm the authoritative current Hadrian Android-port release URL and published update details.
- [ ] Compare its confirmed external workspace behavior with DemiForge's recognised payload requirements.
- [ ] Report the verified support status and latest release link without overstating compatibility.

## Hadrian APK Rebuild Workflow

- [ ] Confirm the actual payload filenames and paths beneath the supplied Hadrian APK's `assets/` directory.
- [x] Replace the external-workspace flow that cannot affect an installed APK with direct user-selected APK patching.
- [x] Add backup, rebuild, signing, and installation-handoff safeguards for a modified user-supplied APK.
- [ ] Test APK payload replacement, archive integrity, and restoration against a non-game-data fixture.
- [ ] Build, verify, document, commit, and deliver the APK-rebuild version of DemiForge.

## Fast-Path Verification

- [x] Determine whether the observed Hadrian APK can load game payloads externally without replacing the APK.
- [x] If no external game-payload route exists, preserve the signing and reinstall warning in the APK-patching workflow.

## User-Requested Playable Mod

- [x] Confirm the Chapter 5 scope: replace only Kris with Flowey-themed visuals and Kris-owned battle actions; leave the rest of the party unchanged.
- [x] Confirm the requested mod concept, target chapter, and party scope without requiring further clarification.
- [ ] Locate and inspect the Hadrian APK already supplied in this session; do not request it again.
- [ ] Obtain the requested mod concept, target chapter, gameplay requirements, and original Hadrian APK.
- [ ] Create the requested game content and Android-compatible replacement payload.
- [ ] Build, sign, verify, and deliver the finished modded APK with save-safe installation steps.

## Genuine Mobile GameMaker Editor

- [x] Research the UndertaleModTool data model, Android `game.droid` requirements, and feasible on-device resource editing approach.
- [ ] Inspect the user-supplied Hadrian package to map its actual GameMaker resource and code structures.
- [ ] Implement reading and editable views for sprites, strings, object metadata, rooms, and scripts in the GameMaker payload.
- [ ] Implement controlled edits, serialization, resource replacement, and payload rebuild validation.
- [ ] Build a phone-usable editor interface that exposes actual game resources rather than arbitrary file replacement.
- [ ] Validate the edited payload in a rebuilt, signed user-supplied APK and deliver the true editor/modded build.
- [x] Implement a bounds-checked FORM chunk indexer and read-only STRG resource preview as the editor foundation.
- [x] Implement and test same-or-shorter UTF-8 STRG edits that preserve GameMaker offsets and reinject the edited payload into a rebuilt APK fixture.
- [x] Add on-device named-resource search across actual sprite, object, room, script, and code chunks to locate Kris and Flowey entries after payload inspection.
- [x] Implement and test an offset-preserving object-to-existing-sprite alias operation for mapped Kris visuals, with a combined APK draft workflow.

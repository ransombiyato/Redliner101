# Hadrian Android Port Integration Design

**Purpose:** This document defines the implementation boundary for modding a user-selected installation workspace of Hadrian Soares' Android Deltarune port. It replaces the prior diagnostic-only direction with an actual, constrained file-management workflow. It does not claim a package identifier or a private storage path that the public release has not documented.

## Evidence and scope

Hadrian's release states that it includes an **external save-path configuration** and credits an Android extension for `game_change` / `load_wad` behavior.[1] The closely related, but distinct, DeltaQuick tooling documents Android-ready Deltarune chapter payloads as `game.droid` files, whether loose or contained in chapter packages, and exposes an `init_external_files_dir()` helper returning an app-specific external-files directory.[2] Its source also shows a `game_change_android(folder_name)` extension intended to move among Android chapter payloads.[3]

> DemiForge will operate only on a directory the user selects through Android's Storage Access Framework. It will never enumerate installed apps, fabricate Hadrian's package name, modify the installed APK, or try to gain access to inaccessible private storage.

## Real operational model

| Step | DemiForge behavior | User-visible safety rule |
|---|---|---|
| Select workspace | The user chooses the Hadrian port's externally accessible files directory in Android's system picker. DemiForge persists only that picker grant. | No broad storage scan and no guessed `Android/data/<package>` path. |
| Inventory | DemiForge inventories relative names and identifies possible Android Deltarune payloads: `game.droid`, `data.droid`, and chapter WAD payloads. | Inventory is read-only and excludes file contents from logs. |
| Trust workspace | The user reviews the detected paths and explicitly marks the fingerprinted workspace as the Hadrian port workspace. | A changed payload inventory invalidates trust until reviewed again. |
| Import mod | A manifest declares a target path that exactly matches a detected payload. The payload itself is supplied by the user as a mod package. | No game asset is included in DemiForge. |
| Preflight | DemiForge resolves dependencies and conflicts, checks target compatibility, previews each replacement, and computes the backup size. | Only `COPY` operations to already detected payload paths are accepted. |
| Apply | All originals are copied into an app-private backup record before the first write. Then replacement payloads are written through the selected document-tree grant. | Any failure triggers restore of every backed-up target. |
| Restore | The latest compatible backup record restores the exact original bytes to the selected workspace. | Restore is also guarded by the workspace fingerprint and target list. |

## Why a user-selected workspace is necessary

The public release does not expose a stable package ID or a universally documented external directory. Android also does not let an ordinary app silently read another app's private `/data/user/0/...` directory. The system document picker can grant access to a directory the user can already access, including an available app-specific external-files directory. That makes a genuine file modification possible without claiming a root or Shizuku bypass.

## Supported real-mod package contract

The manager will retain the existing `manifest.json` package model, but the Hadrian integration imposes these additional rules:

| Manifest field | Requirement for `deltarune-hadrian-android` |
|---|---|
| `targetGame` | Must equal `deltarune-hadrian-android`. |
| `supportedGameVersions` | Must include `*`, `hadrian-android`, or the user-reviewed workspace fingerprint. |
| `patches[].mode` | Must be `COPY`; overlay files are not accepted because the port has no documented DemiForge overlay loader. |
| `patches[].target` | Must exactly equal an existing payload path discovered in the selected workspace. |
| `patches[].source` | Must be a user-provided Android-ready replacement payload, normally a `game.droid`, `data.droid`, or a port-compatible chapter WAD. |

This is a real replacement workflow, not a sandbox: when the selected external workspace is the live workspace used by the installed Hadrian port, applying a compatible mod package changes the actual files the port loads. It cannot alter game data that the port keeps solely inside its APK or private internal directory, because Android prevents an unrelated app from safely doing so.

## References

[1]: https://gamejolt.com/games/deltarunech1-5androidport/1080568 "Deltarune Chapter 1–5 Android Port — Hadrian Soares, Game Jolt"
[2]: https://github.com/BookerRues9/Deltaquick-porting-tools "DeltaQuick Porting Tools — Android payload layout and external files helper"
[3]: https://github.com/BookerRues9/Deltaquick-porting-tools/blob/main/scripts/tools.csx "DeltaQuick tools.csx — Android chapter switching extension"

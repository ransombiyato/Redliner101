# Hadrian Android Port Research Notes

**Status:** Preliminary public-source research. This document records only claims verified from the linked sources and deliberately avoids assuming Hadrian's package name, directory path, or exact runtime load priority until those can be observed in the user's installed copy.

## What is established

Hadrian Soares' Game Jolt release describes itself as a port of **Deltarune Chapters 1–5**. Its release notes say it includes mobile controls, microphone support, and a configuration option for an **external save path**. The release instructs users of that external-save-path feature to use the Android file manager with package identifier `com.marc.files`, and credits the Team H&D/Team NewHope extension used for `game_change` / `load_wad` behavior and microphone support.[1]

The release page does **not** state the app package identifier, the selected external-save directory, an APK-internal asset path, a stable mod-package format, or an official mod-installation procedure. Those missing facts must not be guessed by DemiForge. The port-specific integration therefore needs a deliberate user-selected directory, immutable recognition rules, a preflight inventory, and a backup before it offers a write operation.

Direct inspection of the public Game Jolt release and the DeltaQuick guide confirms that the two projects are separate: Hadrian credits components from Team H&D/Team NewHope, whereas DeltaQuick identifies itself as a different Android launcher. DemiForge must therefore never present DeltaQuick's `com.bookerdev.deltaquick` directories as Hadrian's directories.

## Related, but not interchangeable, technical evidence

The DeltaQuick porting-tools documentation is for a different Android launcher (`com.bookerdev.deltaquick`) and explicitly says it is intended for Deltarune Chapters 3–5 Steam-compatible mods. It documents a flow in which a chapter pack is extracted, its `assets/game.droid` is replaced with a ported build, the pack is rebuilt, and the modified pack is reloaded through that launcher's Save Manager.[2]

That guide is technically useful because it establishes a current Android Deltarune modding pattern: an Android-ready GameMaker payload can be represented as `game.droid` inside a chapter package, and payload compatibility is tied to the matching PC Deltarune game version. It must **not** be treated as proof that Hadrian's port uses the same package name, directory tree, or runtime priority.

An older community discussion describes converting a modified `data.win` into `game.droid` and placing it in a Deltarune Android APK, while also warning that a GameMaker runtime mismatch can make a mod incompatible.[3] This is historical corroboration, not a current format specification.

A GameMaker community discussion confirms that the standard `game_change()` API is not supported on Android builds according to the linked GameMaker manual. This makes Hadrian's credited Android `game_change` / `load_wad` extension materially relevant to chapter transitions, but the discussion does not document a mod directory or a public extension API.[4] A separate historical GameBanana question refers to a `data.droid` file in an unspecified mobile Deltarune port; because it does not identify Hadrian's release or a version, DemiForge will not use `data.droid` as a recognition marker.[5]

## Implementation consequence

The real adapter should handle **manifest-declared file replacement** only after all of the following are true:

| Precondition | Purpose |
|---|---|
| The user selects a directory or a single port package with Android's Storage Access Framework. | No broad storage scan or guessed private-app path. |
| The user accepts an exact port fingerprint built from non-game-data metadata and inventory markers. | Prevent accidental writes to unrelated folders. |
| The mod manifest identifies compatible port/version fingerprints and explicit target paths. | Avoid applying a mod to an incompatible chapter/build. |
| DemiForge can create and verify a backup of every replacement target. | Restore the original state deterministically. |
| The patch preview shows added/replaced/deleted files and byte totals. | Let the user review the real operation before any write. |

The next research step is to inspect the public port assets and then derive a safe generic selection and fingerprint mechanism that requires only a non-sensitive directory listing from the user if the package-specific facts remain undiscoverable.

## References

[1]: https://gamejolt.com/games/deltarunech1-5androidport/1080568 "Deltarune Chapter 1–5 Android Port — Hadrian Soares, Game Jolt"
[2]: https://github.com/BookerRues9/Deltaquick-porting-tools "DeltaQuick Porting Tools — BookerRues9"
[3]: https://www.reddit.com/r/Underminers/comments/v2etz8/in_order_to_port_deltarune_mods_on_mobile_what/ "Community discussion: porting Deltarune mods to Android"
[4]: https://www.reddit.com/r/gamemaker/comments/1n3jiw9/game_change_function_that_works_for_android_builds/ "GameMaker community discussion: game_change on Android"
[5]: https://gamebanana.com/questions/41712 "GameBanana: Adding mods on mobile Deltarune"

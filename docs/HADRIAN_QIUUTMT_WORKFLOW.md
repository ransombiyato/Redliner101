# Hadrian Port: Genuine Android UndertaleModTool Workflow

## The correct tool chain

Use **QiuUTMTv4** for actual GameMaker editing. Its Android editor explicitly accepts `.droid` data files, opens them through Android’s file picker, and saves edited data through the save picker.[1] It also exposes UndertaleModTool resource data, scripts, code decompilation/disassembly, texture replacement, and save logic.[2]

Use **DemiForge only after editing** if a rebuilt signed APK is required. It should not be treated as a replacement for QiuUTMTv4.

| Stage | Tool | Output |
|---|---|---|
| Extract `assets/game.droid` or the relevant chapter `.droid` from the user-owned Hadrian APK | Android archive tool | Editable GameMaker payload copy |
| Locate Kris, Flowey, battle code, sprites, strings, and objects | QiuUTMTv4 | Verified resource IDs and names |
| Apply sprite/image, object, string, and code edits | QiuUTMTv4 | Edited `.droid` payload |
| Reinsert the edited payload, sign a separate APK, and install it | DemiForge | Locally rebuilt modded APK |

## Verified mobile file support

QiuUTMTv4’s Android file picker declares GameMaker data-file support for `.win`, `.unx`, `.ios`, `.droid`, and `audiogroup*.dat`.[1] That includes the Android `.droid` payload expected under the Hadrian APK’s `assets/` directory.

## Kris-only Flowey modification plan

The actual data must be inspected before any resource names are assumed. In QiuUTMTv4, locate resource names containing `kris` and `flowey` or `flowery`, then identify the specific Chapter 5 Kris object(s), sprite references, battle sprites, related strings, and code actions. Preserve Susie, Ralsei, and all non-Kris resources.

The tool’s verified texture-replacement code can replace embedded texture images from PNG files, while its code facilities support decompilation, disassembly, and replacement. That is the appropriate route for a genuine Flowey visual and action mod—not a raw ZIP replacement.[2]

## Installation boundary

Changing an APK asset requires a rebuilt and newly signed APK. Android may require uninstalling the original package because the modified package has a different signing certificate. Back up user data and saves first. Do not share the original or modded game APK; operate only on the user’s own copy.

## References

[1]: https://github.com/QiumingOrg/QiuUTMTv4/blob/main/UndertaleModToolAvalonia/Windows/MainViewModel.cs "QiuUTMTv4 Android file picker and data-file extensions"
[2]: https://github.com/QiumingOrg/QiuUTMTv4/tree/main/QiuLibCore "QiuUTMTv4 GameMaker decompiler, scripting, texture replacement, and save capabilities"

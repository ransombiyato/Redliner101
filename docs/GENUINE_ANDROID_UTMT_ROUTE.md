# Genuine Android UndertaleModTool Route

## Why this route changes the plan

The user asked for an **Android UndertaleModTool**, not a replacement-file picker. The maintained [QiuUTMTv4](https://github.com/QiumingOrg/QiuUTMTv4) project describes itself as a cross-platform UndertaleModTool implementation designed especially for mobile.[1] Its public Android project includes an Android target, an Avalonia editor, a mobile code editor, GameMaker data tooling, and an UndertaleModTool submodule.[1]

The project’s core source imports the UndertaleModTool data, compiler, decompiler, scripting, and project APIs. It exposes code decompilation and disassembly, code replacement, string dumping, save operations, and resource counts for sprites, game objects, rooms, code, strings, embedded textures, and audio.[2] Those are genuine editor capabilities beyond the limited fixed-offset operations implemented in DemiForge.

The source also includes a texture-replacement operation that resolves an embedded texture by name and replaces its image from a PNG, plus file-based code replacement. This is the resource-level capability needed for a real Kris-to-Flowey visual mod; it is not just APK archive editing.[2]

The public wiki endpoint did not expose a concrete Android file-opening or save tutorial during verification. The supported workflow should therefore be validated against the user’s own port payload in the actual mobile app rather than inferred from a screenshot or promised in advance.[1]

## Verified release route

The official repository’s latest stable GitHub release is **v4.2**, published 2026-01-03. Its Android APK asset is `QiuUTMTv4_v4.2.apk`.[3]

| Use case | Correct tool |
|---|---|
| Inspect, edit, decompile, and script GameMaker data resources | QiuUTMTv4 |
| Back up a user-selected Hadrian APK, reinsert an edited `.droid`, sign a separate copy, and invoke Android installation | DemiForge |

## Important limits

DemiForge must not be described as a full UndertaleModTool. Its in-house reader remains useful for bounded inspection, same-or-shorter string edits, and object-to-existing-sprite aliases, but it does not yet provide full sprite painting, bytecode editing, or a general GameMaker serializer.

The actual Hadrian APK available to the agent could not be located in the retained upload directory, even though the user previously supplied it in conversation. The next real-payload step should be performed on the user’s own device through QiuUTMTv4 and, if required, handed to DemiForge only for packaging. No game executable or game asset should be redistributed.

No public QiuUTMTv4 issue surfaced for `Deltarune` or `.droid` during the current repository search. That absence is not proof of compatibility; it means the exact Hadrian payload must be opened and validated on-device before a modded build is promised.

## References

[1]: https://github.com/QiumingOrg/QiuUTMTv4 "QiuUTMTv4 repository and Android project"
[2]: https://github.com/QiumingOrg/QiuUTMTv4/tree/main/QiuLibCore "QiuLibCore GameMaker data, decompiler, compiler, scripting, and save APIs"
[3]: https://github.com/QiumingOrg/QiuUTMTv4/releases/tag/v4.2 "QiuUTMTv4 v4.2 release"

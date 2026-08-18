# Mobile UndertaleModTool-Style Editor Research

## Verified upstream architecture

The official UndertaleModTool repository separates its project into a reusable **`UndertaleModLib`** data library, a desktop graphical editor, a command-line interface, test projects, and an updater.[1] The upstream repository identifies itself as a tool for modifying, decompiling, and unpacking Undertale and other GameMaker games, and directs format research to its public wiki.[1]

The current upstream work also exposes a project system with paths, code assets, scripts, sprites, sounds, rooms, backgrounds, fonts, texture packing, code recompilation, and resource import/export.[1] That confirms the user’s expected product category: an editor needs to operate on structured GameMaker resources and code, not merely replace whole payload files.

The upstream central `UndertaleData` model explicitly treats `data.droid` as a platform-specific name for the same GameMaker data-file family as `data.win`, and exposes structured lists for sprites, scripts, game objects, rooms, code, strings, texture page items, and embedded textures.[1] A reader must therefore parse the `FORM` chunk container and version-sensitive named chunks before it can make any resource-level edit safely.

## Consequence for DemiForge

A truthful Android counterpart must begin with a **read-only structured inspection** of a supplied `game.droid` / GameMaker payload: detect the GameMaker version, enumerate named resources, and show strings, sprites, objects, rooms, code, and script entries. Editing should occur through narrow model operations that can round-trip the original data, with a mandatory backup and validation before any APK is rebuilt.

The existing direct APK patcher remains useful only as the final packaging layer. It must receive an edited, validated GameMaker payload from the editor layer; it is not itself an UndertaleModTool replacement.

## Implemented editor foundation

DemiForge now includes a Kotlin implementation that validates the `FORM` container, indexes bounded chunks, previews `STRG` values, and enumerates the named entries in standard sprite, object, room, script, and code pointer-list chunks. The app performs this inspection against a copy of the selected APK asset and displays its actual chunk and resource names.

The first write operation is intentionally narrow: a user can replace a `STRG` value only when its UTF-8 encoding is no longer than the original. The tool then writes the changed length and value in the existing byte region, clears remaining bytes, re-parses the draft, and reinserts that draft into a rebuilt APK fixture. Because no pointer or offset moves, this operation is structurally safer than a speculative serializer.

This is **not yet sprite painting, code decompilation, or arbitrary GameMaker serialization**. Those require the real target payload and additional version-specific parsing before they can be claimed as supported.

## Current blockers

The user has not uploaded the original Hadrian APK, so the actual Chapter 5 `game.droid` payload and its resource identifiers cannot yet be inspected. This must be obtained before claiming support for Kris sprites, Flowey assets, battle actions, or any particular GameMaker resource layout.

Search also identifies an independent Rust GameMaker 2 `data.win` parser, which may be a useful format-research reference but is not verified as an Android-ready editor or serializer.[2] A community search result claims an Android UndertaleModTool port exists; that claim has not been verified against source code, a supported download, or Hadrian-port compatibility and must not be presented as a solution yet.[3]

## References

[1]: https://github.com/UnderminersTeam/UndertaleModTool "UnderminersTeam/UndertaleModTool — official repository"
[2]: https://github.com/jam1garner/gm_data_win "jam1garner/gm_data_win — GameMaker 2 data.win parser"
[3]: https://www.reddit.com/r/Underminers/comments/1q23i51/then_i_found_a_port_of_undertale_mod_tool_for/ "Community post claiming an Android UndertaleModTool port"

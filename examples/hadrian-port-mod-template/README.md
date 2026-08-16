# Hadrian Port Mod Package Template

This is a **template**, not an installable mod and not a copy of Deltarune. To make it usable, replace the placeholder details in `manifest.json` and place your own Android-ready replacement file at `payload/game.droid`.

The `target` must exactly match one of the paths shown by DemiForge’s **Workspace** screen. For example, if it lists `chapter3_windows/game.droid`, retain that exact target. If it lists a chapter WAD instead, use that listed `.wad` path and change `source` to the matching replacement filename.

Do not add `..`, an absolute path, or a target not displayed by the Workspace screen. DemiForge will refuse those packages.

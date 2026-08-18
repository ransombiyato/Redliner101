# Porting findings

## Exact target

The target is Minecraft **1.21.11** on Fabric, not Minecraft 1.21.1. Fabric’s official metadata reports stable Yarn `1.21.11+build.6`, stable Fabric Loader `0.19.3`, and Fabric API releases in the `0.141.x+1.21.11` line.

## Stable Hex Casting baseline

The latest published Hex Casting version on Modrinth is `0.11.3` for Minecraft `1.20.1` Fabric/Forge, released in November 2025. The GitHub tag `v0.11.3` points to commit `7202ed7206ebd13682c72821b88b0f36b68ff36d`.

## Upstream port reference

The official `FallingColors/HexMod` repository has a `1.21` branch, but it is configured for Minecraft `1.21.1`. Its early port history through commit `bc65193f0` is being used as a compatibility reference, not as the stable-release identity.

## Loom validation

The Architectury Loom discussion [#329](https://github.com/architectury/architectury-loom/discussions/329) documents the same Minecraft 1.21.11 failure: a dependency declares Loom `1.14.10` while Architectury Loom `1.13.467` is in use. The build should retain the published Architectury Loom `1.13-SNAPSHOT` and set `loom.ignoreDependencyLoomVersionValidation=true` rather than pretending an unpublished Architectury Loom `1.14.10` plugin exists.

## Dependency availability

Patchouli has verified Maven artifacts `1.21.1-92-FABRIC` and common `1.21.1-92`. The bundled Paucal and Inline artifacts in the upstream 1.21 port are 1.21.1 builds. Fabric Language Kotlin, Cardinal Components, Cloth Config, Mod Menu, and Lithium have 1.21.11 builds; Paucal, Patchouli, Inline, and Accessories do not currently expose 1.21.11 artifacts through the checked public indexes. This is a known compatibility constraint to resolve through source porting or carefully isolated helper dependencies, not by relabeling 1.21.1 jars as 1.21.11 jars.

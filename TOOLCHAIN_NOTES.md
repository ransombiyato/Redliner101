# Kinetic Frontier Toolchain Notes

The target platform is Minecraft 1.21.1 on NeoForge. The official Create developer guidance for the 1.21.1 line identifies Create 6.0.10 and recommends the Create Maven plus the Registrate snapshots repository. Its example versions are Create `6.0.10-280`, Ponder `1.0.82`, Flywheel `1.0.6`, and Registrate `MC1.21-1.3.0+67`. Create dependency metadata should require `create` in `[6.0.10,6.1.0)`.

NeoForge’s official 1.21 ModDevGradle template uses the `net.neoforged.moddev` plugin and configures `neoForge { version = ... }`. Maven metadata currently exposes NeoForge `21.1.248` as an available 1.21.1-compatible release; this project will use that release unless the first dependency resolution shows an incompatibility, in which case the version will be adjusted to the nearest verified release.

References:

1. Create dependency guidance: https://wiki.createmod.net/developers/depend-on-create/neoforge-1.21.1
2. NeoForge ModDevGradle documentation: https://docs.neoforged.net/toolchain/docs/plugins/mdg/
3. NeoForge 1.21.1 Maven metadata: https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml

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

## 1.21.11 rendering and mappings

Minecraft 1.21.11’s official mappings rename `net.minecraft.resources.ResourceLocation` to `net.minecraft.resources.Identifier`; the new class exposes `CODEC`, `STREAM_CODEC`, `fromNamespaceAndPath`, `parse`, `tryParse`, and `withDefaultNamespace`. The former `FastColor.ARGB32` utility is replaced by `net.minecraft.util.ARGB`, with `alpha`, `red`, `green`, `blue`, and `color` methods. Villager classes move under `net.minecraft.world.entity.npc.villager`; `WaterAnimal` moves under `net.minecraft.world.entity.animal.fish`; and `EnderDragonPart` moves under `net.minecraft.world.entity.boss.enderdragon`.

Minecraft 1.21.11’s `CompoundTag` typed getters return `Optional` values and provide `getIntOr`, `getDoubleOr`, `getStringOr`, `getCompoundOrEmpty`, `getListOrEmpty`, and `getBooleanOr` convenience methods. `ListTag.elementType` is removed; the element type can be derived from the first tag’s ID. `GuiGraphics.pose()` returns `Matrix3x2fStack`, not the legacy `PoseStack`. `Screen` mouse callbacks use `MouseButtonEvent`; `KeyboardHandler.hasControlDown()` and `hasShiftDown()` remain available through `Minecraft.getInstance().keyboardHandler`. `GuiGraphics` exposes pipeline-based fill and blit submission methods rather than the removed immediate `RenderSystem.setShader`/legacy GUI PoseStack path.

Fabric’s official GUI documentation confirms that modern GUI rendering uses `GuiGraphics`/`GuiGraphicsExtractor` submission APIs, `fill`/`outline` for shapes, and `RenderPipelines.GUI_TEXTURED` for textures: https://docs.fabricmc.net/develop/rendering/gui-graphics.

The latest official HexMod `1.21` branch head as of 2026-08-15 is commit `21dfcb90a2023995f08bccf282c9eaf563c13186`, still configured for Minecraft `1.21.1`; it does not provide a pre-existing 1.21.11 implementation.

## Additional 1.21.11 findings (2026-08-18)

The official Mojang 1.21.11 mappings at `/home/ubuntu/mc12111/client.txt` confirm that `ArmorMaterial` is `net.minecraft.world.item.equipment.ArmorMaterial` with constructor `(int durability, Map<ArmorType,Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, TagKey<Item> repairIngredient, ResourceKey<EquipmentAsset> assetId)`. `GameRules` moved to `net.minecraft.world.level.gamerules.GameRules`; its entity-drop rule is `GameRules.ENTITY_DROPS`, and `Level.getGameRules()` remains available on server-level contexts. `RenderType` and `RenderTypes` are in `net.minecraft.client.renderer.rendertype`; moving-block factories include `cutoutMovingBlock()` and `translucentMovingBlock()`. `ElytraModel` moved to `net.minecraft.client.model.object.equipment.ElytraModel`. `BlockEntityRenderer` now uses render-state extraction/submission APIs and has two generic parameters in the 1.21.11 Java stubs. `Entity` persistence uses `ValueInput`/`ValueOutput`; `ValueOutput.store` and `ValueInput.read` support codec-backed ItemStack serialization. Inventory exposes `getSelectedSlot()`, `getSelectedItem()`, `getNonEquipmentItems()`, and `getItem(int)` rather than public `items`, `selected`, or `offhand` fields.

The official upstream reference used for comparison is the [FallingColors/HexMod 1.21 branch](https://github.com/FallingColors/HexMod/tree/1.21), which targets Minecraft 1.21.1 and therefore is guidance only, not the exact 1.21.11 target. The current repository’s generic `.gitignore` rule `env/` had accidentally omitted all `Common/.../api/casting/eval/env/*.java` files; those required sources were force-added in commit `8c6755c`.

# Hex Casting 1.21.11 Compatibility Checklist

This checklist is the authoritative work list for the Minecraft 1.21.11 Fabric port. It is intentionally broader than the latest CI error list. Every item must be verified against `/home/ubuntu/mc12111/client.txt`, source-wide searches, and a validation pass before being marked complete.

Audit evidence is persisted in `docs/PORT_1.21.11_AUDIT_RAW.txt` and `docs/PORT_1.21.11_AUDIT_SUMMARY.txt`. The source baseline is the Hex Casting v0.11.3-derived tree on branch `port-1.21.11`.

Status values: `DONE` means implemented and checked; `IN PROGRESS` means partially migrated or awaiting verification; `OPEN` means known to require work; `BLOCKED` means dependent on another API migration.

| ID | Area | Audit item | Evidence / affected scope | Status | Verification |
|---|---|---|---|---|---|
| INF-01 | Build | Exact Minecraft 1.21.11 Fabric target, Java 21, Loom, Gradle, and CI workflow | `gradle.properties`, workflow, target-version check | DONE | CI target check passed |
| INF-02 | Build | Fabric-only source/build routing with no accidental feature additions or removals | `settings.gradle`, module build files | IN PROGRESS | Audit build graph and source roots |
| INF-03 | Audit | Persist raw search findings and aggregated diagnostics | `AUDIT_RAW.txt`, `AUDIT_SUMMARY.txt` | DONE | Files created and reviewed |
| API-01 | Names | ResourceLocation-to-Identifier and ResourceKey location/identifier migration | Source-wide stale-name search | IN PROGRESS | Search and compile verification |
| API-02 | NBT | Optional-based CompoundTag accessors (`getByteOr`, `getListOrEmpty`, `getCompoundOrEmpty`, `contains`) | Circle state, iota, BE, ingredient persistence | IN PROGRESS | Audit every call site |
| API-03 | NBT | UUID persistence after removal of `hasUUID`/`getUUID` | Circle execution, entity iota, redstone impetus | IN PROGRESS | Verify serialized key compatibility |
| API-04 | NBT | BlockPos persistence after removal/relocation of NbtUtils helpers | Circle execution and block entities | IN PROGRESS | Verify int-array/codec representation |
| API-05 | Persistence | ValueInput/ValueOutput and typed-component load/save APIs | Block entities, VillagerIngredient, Patchouli processor | IN PROGRESS | VillagerIngredient and BrainsweepProcessor now use TagValueOutput; audit all remaining loadAdditional/saveAdditional calls |
| API-06 | Profiles | ResolvableProfile factory and GameProfile accessors | EntityIota, redstone impetus | IN PROGRESS | Compile and behavior review |
| API-07 | Entities | EntityType registration ResourceKey signatures | `HexEntities` | DONE | Prior CI syntax check passed; re-audit |
| API-08 | Entities | ServerLevel/entity server access and messaging | PlayerBasedCastEnv, ItemLoreFragment, creative unlocker | IN PROGRESS | Search private-field and base-Entity calls |
| API-09 | Results | InteractionResult and sided-success API changes | Item and block interactions | IN PROGRESS | Search all result factories |
| API-10 | Items | Tooltip consumer callback and item inventory tick APIs | Item classes and media holders | IN PROGRESS | Search old tooltip/tick signatures |
| API-11 | Items | Equipment-component model, armor, lens, tool material, rarity imports | Robes, Lens, Jeweler Hammer, HexItems | IN PROGRESS | Validate components and item registration |
| API-12 | Items | Removed Tiers API and tool-property replacement | `HexItems`, `ItemJewelerHammer` | DONE | ToolMaterial patch pushed; re-audit build |
| API-13 | Items | Removed `verifyComponentsAfterLoad` | Redstone impetus | DONE | Source search and compile verification |
| API-14 | Data | DataComponent registrations and codec helper changes | `HexDataComponents`, `CodecHelper` | OPEN | Current CI reports codec helper issues |
| API-15 | Blocks | `neighborChanged` Orientation signature | redstone/directrix/impetus and circle blocks | IN PROGRESS | Source-wide override audit |
| API-16 | Blocks | `updateShape` LevelReader/ScheduledTickAccess signature | waterlogged/support blocks | IN PROGRESS | Source-wide override audit |
| API-17 | Blocks | Removed `onRemove`, analog output, and lifecycle hooks | abstract impetus/circle components | IN PROGRESS | Search stale overrides |
| API-18 | Blocks | LeavesBlock constructor, codec, and falling-leaves hook | `BlockAkashicLeaves` | IN PROGRESS | Current CI still has/previously had codec diagnostics |
| API-19 | Blocks | Block codec/map-codec requirements and block registration types | block classes and `HexBlocks` | IN PROGRESS | Replaced the verified `noCollission()` property spelling with `noCollision()`; compile-wide verification remains |
| API-20 | Worldgen | Tree grower, feature config, weighted list, and configured feature registry APIs | `AkashicTreeGrower`, `HexFeatureConfigs`, `HexBlocks` | IN PROGRESS | `RegistryAccess.lookupOrThrow(Registries.CONFIGURED_FEATURE)` now replaces removed `registryOrThrow`; compile pending |
| API-21 | Potions | Potion/effect registry API changes | `HexPotions` | IN PROGRESS | `RegistryAccess.lookupOrThrow(Registries.POTION)` now supplies the potion registry; compile pending |
| API-22 | Recipes | Recipe serializer raw/generic signatures, recipe category, placement info | Brainsweep, Seal recipes | IN PROGRESS | Recent patches need consolidated verification |
| API-23 | Recipes | CustomRecipe serializer replacement for removed SimpleCraftingRecipeSerializer | SealThings, SealSpellbook | IN PROGRESS | Recent patch needs compile verification |
| API-24 | Recipes | RecipeProvider `buildRecipes` API | `HexplatRecipes` | IN PROGRESS | `buildRecipes()` now uses parameterless override with `this.output`; source audit clean, compile pending |
| API-25 | Recipes | RecipeBuilder save(ResourceKey) API | crushing, Farmers Delight, Brainsweep builders and `HexplatRecipes` | IN PROGRESS | Custom builders and 26 Hexplat save calls use `ResourceKey`; source audit clean, compile pending |
| API-26 | Recipes | Ingredient.Value removal and HolderSet/Ingredient codec model | `CompatIngredientValue` | IN PROGRESS | Removed the deleted `Ingredient.Value` implementation and retained a registry-backed `CompatIngredientValue.of(String)` helper; compile pending |
| API-27 | Recipes | EntityPredicate factory removal | EntityTagIngredient, EntityTypeIngredient | IN PROGRESS | Example entities now use `EntityType.create(Level, EntitySpawnReason.COMMAND)`; source audit clean, compile pending |
| API-28 | Recipes | Villager data and profession API changes | `VillagerIngredient`, `HexplatRecipes` | IN PROGRESS | Uses VillagerData holder accessors/withers, registry lookup for profession keys, and TagValueOutput; source audit clean, compile pending |
| API-29 | Advancements | MinMaxBounds codec/bounds/parser API | `MinMaxLongs` | IN PROGRESS | Replaced inaccessible Bounds.createCodec with an explicit public number/object codec and retained parser behavior; compile pending |
| API-30 | Advancements | HolderGetter-based item predicates and advancement builders | `HexAdvancements` | IN PROGRESS | `provider.lookupOrThrow(Registries.ITEM)` is passed to every ItemPredicate.Builder.of call; compile pending |
| API-31 | Datagen | TagsProvider/ItemTagsProvider/BlockTagsProvider API | Hex item/block/action tag providers | IN PROGRESS | Replaced removed tag() usage with standalone TagAppender adapters; source audit clean, compile and generated-tag verification pending |
| API-32 | Commands | DimensionDataStorage.set and SavedData API | RecalcPatterns and pattern commands | IN PROGRESS | Rewrote ScrungledPatternsSave to SavedDataType/Codec and migrated computeIfAbsent/set call sites; compile pending |
| API-33 | Commands | Pattern resource-key/registry command APIs | PatternResKeyArgument and list/texture commands | IN PROGRESS | Migrated ResourceKey.identifier diagnostics and PermissionCheck command predicates; compile pending |
| API-34 | Iota | Codec/unit and Optional numeric accessors | DoubleIota, PatternIota | IN PROGRESS | DoubleTag now uses `doubleValue()`; action lookup maps Optional Reference to `value().action()`; compile pending |
| API-35 | Patterns | Pattern registry manifest and codec/registry lookup APIs | `PatternRegistryManifest` | IN PROGRESS | Unwrapped Optional registry holder references before accessing ActionRegistryEntry/Factory values; compile pending |
| API-36 | Hex logic | Arithmetic/action registration and method references | `HexArithmetics`, `HexActions` | IN PROGRESS | Replaced Registry.holders() with Registry.stream(), updated MobEffects holders, and corrected the ItemCypher description method reference; compile pending |
| API-37 | Rendering | 1.21.11 render pipeline, RenderType, RenderStateShard, GlStateManager changes | client render helpers and shader/render types | IN PROGRESS | Source-wide render audit required |
| API-38 | Rendering | Submit-based BlockEntityRenderer and RenderLayer APIs | renderers, layers, render states | IN PROGRESS | Source-wide submit/signature audit required |
| API-39 | Rendering | GUI Matrix3x2fStack replacing PoseStack | GUI, Patchouli, inline rendering | IN PROGRESS | GUI host calls retain Matrix3x2fStack; Patchouli/inline/custom vertex paths use dedicated PoseStack adapters; source audit clean, compile and visual verification pending |
| API-40 | Rendering | Particle provider/single-quad API | ConjureParticle and providers | IN PROGRESS | Re-audit particle classes |
| API-41 | Rendering | Texture/DynamicTexture/NativeImage API | pattern texture manager and tooltip | IN PROGRESS | Re-audit texture calls |
| API-42 | Patchouli | Patchouli processors/components and persistence APIs | Patchouli interop package | IN PROGRESS | Display-entity serialization now uses the ValueOutput bridge and GUI components use local PoseStack adapters; remaining Patchouli symbols require compile verification |
| API-43 | Inline | HoverEvent/ClickEvent factories and InlineStyle changes | inline pattern data/renderer | IN PROGRESS | Inline renderer uses the local PoseStack path and InlinePatternData now uses `HoverEvent.ShowItem`/`ClickEvent.CopyToClipboard`; source audit clean, compile pending |
| API-44 | External | EMI/other integration API changes | integration packages and recipe categories | OPEN | Current CI reports category/type issues |
| API-45 | Kotlin | Kotlin/Java generic boundaries, inaccessible mapped types, Optional interop | OpFlight and remaining Kotlin files | OPEN | Run Kotlin compiler after Java batches |
| API-46 | Regression | Preserve all v0.11.3 features and registered IDs | registries, recipes, tags, resources, network IDs | OPEN | Compare against baseline after compile passes |
| API-47 | Validation | No stale 1.20.1 API spellings remain in authored source | raw audit patterns plus mapping checks | OPEN | Re-run global search and record zero/approved hits |
| API-48 | Validation | CI build and artifact upload succeed for exact 1.21.11 Fabric | GitHub Actions workflow | OPEN | Run only after consolidated audit batches |

## Verification protocol

For each item, record the exact source files changed, the mapping entry or authoritative API evidence used, and the validation command or CI run that passed. Do not mark an item `DONE` merely because one compiler message disappeared. A category is complete only when its source-wide search has been repeated and no unreviewed stale call sites remain.

## Audit artifacts

- `docs/PORT_1.21.11_AUDIT_RAW.txt`: complete raw source and CI search output.
- `docs/PORT_1.21.11_AUDIT_SUMMARY.txt`: aggregated file and message summaries.
- `docs/PORT_1.21.11_CHECKLIST.md`: this persistent checklist.
- `/home/ubuntu/mc12111/client.txt`: official 1.21.11 mappings used for API verification.
- `/home/ubuntu/HexMod-1.21`: upstream 1.21 guidance used only as migration reference, not as the target version.

## Repository-wide search coverage

The source-wide audit found the following hit counts in authored Common/Fabric source. These are **audit candidates**, not automatically errors; each hit must be reviewed against the 1.21.11 mappings and either migrated or explicitly classified as valid.

| API family | Candidate hits | Persistent evidence |
|---|---:|---|
| Blocks and block lifecycle | 49 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Commands and resource-key/storage APIs | 136 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Interaction results | 76 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Patchouli/EMI/inline interop | 30 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Items and equipment | 33 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| NBT optional accessors | 59 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Persistence and ValueInput/ValueOutput | 42 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Recipes and ingredients | 138 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Rendering and submit APIs | 107 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| Textures and tooltip components | 19 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |
| UUID/profile APIs | 16 | `docs/PORT_1.21.11_API_FAMILY_AUDIT.txt` |

The counts are stored in `docs/PORT_1.21.11_API_FAMILY_SUMMARY.txt`. The next work phase is to review these candidates in related batches, update the checklist item status, and only then run consolidated compilation/CI validation.

## Interim compile findings

| ID | Area | Finding | Evidence | Status | Required follow-up |
|---|---|---|---|---|---|
| INF-04 | Build infrastructure | The local `:Common:compileJava :Common:compileKotlin` run did not reach compilation within the allotted time and remained in Gradle configuration after reporting `Mod was built with a newer version of Loom (1.14.10), you are using Loom (1.13.469)`. | `docs/LOCAL_1.21.11_COMPILE.log` | OPEN | Audit all Loom/plugin declarations and cache state; rerun with the exact configured Loom version or document why the resolved version differs. |
| INF-05 | Build infrastructure | A stale/missing Fabric Loom cache lock was detected and rebuilt: `ACQUIRED_PREVIOUS_OWNER_MISSING`. | `docs/LOCAL_1.21.11_COMPILE.log` | OPEN | Ensure no stale Gradle processes/locks remain before the next consolidated compile. |

| INF-06 | Build infrastructure | The configured `dev.architectury.loom` plugin is `1.13-SNAPSHOT`, resolving to `1.13.469`; direct checks for an `architectury-loom:1.14.10` artifact returned HTTP 404, so blindly changing the version is not valid. | `docs/BUILD_CONFIG_AUDIT.txt`, `docs/LOCAL_1.21.11_COMPILE.log` | OPEN | Determine the correct published Loom version compatible with this project and the 1.21.11 mappings, then update the build only if verified. |

| INF-07 | CI/build | Current consolidated CI run `32153687493` on audit commit `85a1b15` completed with failure during Java compilation; the failure is source/API compilation, not a successful artifact build. | `docs/LATEST_CI_STATUS.txt`, `docs/CI_32153687493_FAILED.log` | OPEN | Classify every diagnostic into the API-family rows below before the next validation run. |
| INF-08 | CI/build | Prior run `32153548296` was cancelled while the audit commit was being superseded; it is not a source verdict. | `docs/LATEST_CI_STATUS.txt` | RECORDED | Ignore as a build result; retain only as workflow history. |

The current CI source diagnostics reinforce that the unfinished families are concentrated in recipe/datagen APIs, HolderGetter-based advancement builders, custom ingredient codecs, command storage APIs, villager/brainsweep ingredients, interop rendering, and remaining registry/import symbols. These are tracked by API-14, API-19, API-20, API-22 through API-35, and API-37 through API-45; they must be fixed as grouped batches rather than one diagnostic at a time.

| INF-09 | CI diagnostics | The complete current CI run `32153687493` contains 813 source diagnostic lines across 48 unique authored source files, plus Gradle/process diagnostics. | `docs/CI_32153687493_ERROR_INDEX.txt` | RECORDED | Use this index as the batch-fix baseline; do not start another CI-only error loop. |


## Current consolidated diagnostic inventory: CI run 32211678987

The following inventory was recorded before continuing source edits. The full compiler excerpts, including every repeated source line and diagnostic context, are preserved in `docs/PORT_1.21.11_BATCH_32211678987_DIAGNOSTICS.txt`; the original complete workflow log remains `docs/CI_32211678987_FAILED.log`. The run reported 366 Java errors across 22 authored source files. Repeated diagnostics from the same file are consolidated below by API family, while the raw inventory retains every occurrence.

| Batch ID | Affected file(s) | Complete fix scope recorded from run 32211678987 | Status before this batch |
|---|---|---|---|
| B322-01 | `Common/.../datagen/recipe/HexplatRecipes.java` | Replace the obsolete `PackOutput`/old `RecipeProvider` constructor; stop accessing private `RecipeProvider.items`; retain a local item `HolderGetter`; migrate every shaped/shapeless builder call; pass the item lookup to all `ItemPredicate.Builder.of` calls; migrate `Ingredient.of(TagKey<Item>)` to the 1.21.11 holder-set form; verify all recipe output/save APIs and helper methods. || IN PROGRESS |
| B322-02 | `Common/.../datagen/tag/HexItemTagProvider.java`, `Fabric/.../HexFabricDataGenerators.java` | `ItemTagsProvider` is absent in 1.21.11; replace it with the verified `IntrinsicHolderTagsProvider<Item>` constructor and item key extractor; remove obsolete `TagLookup`/block-provider wiring; preserve all Hex and vanilla block-to-item tag outputs through a verified 1.21.11 mechanism; remove deleted `Ingredient.ItemValue` and `Ingredient.TagValue` construction from Fabric ingredient providers. || IN PROGRESS |
| B322-03 | `Common/.../api/advancements/MinMaxLongs.java` | Resolve all explicit generic codec inference failures in the `Codec.either`/`RecordCodecBuilder`/`validate` chain; compile-check `Bounds<Long>` accessors and preserve exact/range validation and parser behavior. || IN PROGRESS |
| B322-04 | `Common/.../interop/patchouli/MultiCraftingProcessor.java` | Replace inaccessible `new ContextMap(Map)` with the verified public `ContextMap.EMPTY` or builder API; replace removed `Ingredient.EMPTY`; verify `placementInfo().ingredients()`, display result resolution, and all remaining recipe-display symbols. || IN PROGRESS |
| B322-05 | `Common/.../datagen/HexLootTables.java` | Replace private `new DataComponentMatchers.Builder()` with `DataComponentMatchers.Builder.components()`; pass the item `HolderLookup.RegistryLookup<Item>` into `ItemPredicate.Builder.of`; verify both leaf and amethyst loot paths. || IN PROGRESS |
| B322-06 | `Common/.../datagen/tag/HexBlockTagProvider.java` | Fix `Identifier` versus `TagKey` mismatch in optional tag registration and eliminate wildcard `TagKey` to `TagKey<Block>` conversion errors using typed tag appenders. || IN PROGRESS |
| B322-07 | `Common/.../datagen/HexAdvancements.java` | Change advancement background values from raw `Identifier` to the 1.21.11 `ClientAsset.ResourceTexture` type expected by `DisplayInfo`; verify all display helper return types. || IN PROGRESS |
| B322-08 | `Common/.../interop/inline/InlinePatternData.java`, `InlinePatternRenderer.java` | Use concrete `HoverEvent.ShowItem` and `ClickEvent.CopyToClipboard`; resolve `Style` versus `InlineStyle`; adapt `Matrix3x2fStack` to the renderer’s expected pose type without removing inline rendering behavior. || IN PROGRESS |
| B322-09 | `Common/.../common/blocks/BlockConjured.java`, `BlockSlate.java`, `BlockAkashicBookshelf.java` | Remove or update invalid 1.21.11 override annotations for `propagatesSkylightDown`, `getAnalogOutputSignal`, and related block hooks; preserve the methods’ runtime behavior. || IN PROGRESS |
| B322-10 | `Common/.../common/blocks/BlockConjuredLight.java`, `BlockSlate.java` | Add the authoritative `RandomSource` import and verify all `updateShape` signatures. || IN PROGRESS |
| B322-11 | `Common/.../common/misc/AkashicTreeGrower.java` | Replace removed `HolderGetter.getHolder(ResourceKey)` with the verified 1.21.11 holder lookup method and handle the holder/result without changing tree placement behavior. || IN PROGRESS |
| B322-12 | `Common/.../common/recipe/ingredient/brainsweep/VillagerIngredient.java` | Resolve the default `VillagerType.PLAINS` resource key to a `VillagerType` value before `wrapAsHolder`; verify all profession/type holder accessors and entity example construction. || IN PROGRESS |
| B322-13 | `Common/.../datagen/recipe/builders/CreateCrushingRecipeBuilder.java`, `FarmersDelightCuttingRecipeBuilder.java` | Replace every removed `Ingredient.of(ItemStack)` call with the correct `ItemLike`/item form. || IN PROGRESS |
| B322-14 | `Common/.../datagen/tag/HexActionTagProvider.java` | Fix wildcard action tag keys to typed `TagKey<ActionRegistryEntry>` values and verify the generic tag appender. || IN PROGRESS |
| B322-15 | `Common/.../interop/patchouli/BrainsweepProcessor.java`, `LookupPatternComponent.java`, `PatchouliUtils.java` | Re-audit all missing symbols against the exact 1.21.11 Patchouli integration classpath; retain the migrated display/registry APIs only where their current symbols are available. || IN PROGRESS |
| B322-16 | `Common/.../server/ScrungledPatternsSave.java` | Resolve the wildcard `ResourceKey<? extends Registry<ActionRegistryEntry>>` to the exact codec key type with a documented, narrowly scoped bridge; verify saved-data codec and registry-key round trips. || IN PROGRESS |
| B322-17 | Cross-cutting source audit | Re-run stale API searches after these batches, record every new diagnostic in this checklist before editing it, and do not push until all known checklist items in the current batch are resolved or explicitly blocked with evidence. || IN PROGRESS |

### Post-run source audit findings recorded before fixes

After run 32211678987, additional source inspection found that some working-tree edits had not been represented in that CI log. They are therefore included as checklist work rather than treated as verified fixes: `HexItemTagProvider` requires a valid 1.21.11 provider architecture and complete vanilla cross-registry tag preservation; `HexFabricDataGenerators` requires a valid HolderSet-backed representation for every previous item-plus-tag ingredient; `HexLootTables` requires the public `DataComponentMatchers.Builder.components()` factory; `HexAdvancements` requires `ClientAsset.ResourceTexture`; `MinMaxLongs` requires explicit generic typing that must be compiled; and the current `MultiCraftingProcessor`, `ScrungledPatternsSave`, `VillagerIngredient`, `AkashicTreeGrower`, block overrides, and missing imports all remain unverified until a complete build is run.

No item in B322-01 through B322-17 is marked `DONE` merely because a source edit has been made. Each item remains `OPEN` until the full source family is re-searched and the consolidated build validates it. The next action is to resolve these recorded items in grouped API batches, then review checklist completion, commit once, push once, and run GitHub Actions.


### Additional authoritative mapping findings recorded during B322 resolution

The 1.21.11 mappings show that `Level.recipeAccess()` exposes a `RecipeAccess` implemented by `RecipeManager`; the loaded recipe collection is available through `RecipeManager.getRecipes()`, and each `RecipeHolder.id()` is a `ResourceKey` whose `identifier()` must be compared with an `Identifier`. The old `Level.getRecipeManager()`, `RecipeManager.getAllRecipesFor(...)`, and `RecipeManager.byKey(Identifier)` forms are therefore tracked as part of B322-15.

The 1.21.11 `ShapedRecipeBuilder` and `ShapelessRecipeBuilder` mappings expose only `save(RecipeOutput, ResourceKey)`. Every remaining parameterless `.save(recipes)` in `HexplatRecipes` is consequently tracked under B322-01 and must receive an explicit key derived from the original result/recipe ID before the batch can be considered resolved. The full current line inventory was captured by the source audit before editing.

## Current consolidated diagnostic inventory: CI run 32214152007

Run 32214152007 compiled the pushed B322 commit and reduced the prior hundreds of errors to seven Java diagnostics in the tag-provider and Patchouli registry families. The complete workflow output is stored in `docs/CI_32214152007_FAILED.log`, and the indexed diagnostic lines are stored in `docs/CI_32214152007_ERROR_INDEX.txt`.

| Batch ID | Affected file(s) | Complete fix scope recorded from run 32214152007 | Status |
|---|---|---|---|
| B323-01 | `HexItemTagProvider.java`, `HexBlockTagProvider.java`, `HexActionTagProvider.java` | `TagAppender` has two type parameters in the exact 1.21.11 class (`TagAppender<E,T>`); update every custom appender declaration and nested BlockItemTagsProvider adapter to use the element key type plus registry value type. | IN PROGRESS |
| B323-02 | `HexActionTagProvider.java` | The non-Fabric branch returns `TagKey<? extends ActionRegistryEntry>` and requires a narrowly scoped typed cast to `TagKey<ActionRegistryEntry>`. | IN PROGRESS |
| B323-03 | `LookupPatternComponent.java` | `Registry<ActionRegistryEntry>` has no `getHolderOrThrow(ResourceKey)`; obtain the value with `getValueOrThrow` and bridge it with `wrapAsHolder` before tag/prototype access. | IN PROGRESS |
| B323-04 | Cross-cutting validation | Re-run source-wide searches after this grouped fix, then commit and push only after all B323 diagnostics are recorded and addressed. | IN PROGRESS |
| B323-05 | `HexItemTagProvider.java` | CI 32214641759 shows `IntrinsicHolderTagsProvider<Item>.tag(...)` returns `TagAppender<Item,Item>`, while `BlockItemTagsProvider.tag(TagKey<Block>,TagKey<Item>)` requires `TagAppender<Block,Block>`. Change the custom item helper to `TagAppender<Item,Item>` and implement the nested adapter with a mapped block-to-item appender plus a narrowly scoped generic bridge. | IN PROGRESS |
| B323-06 | `HexItemTagProvider.java` | CI 32214949604 shows `TagAppender<Item,Item>.add(...)` accepts `Item`, not `ResourceKey<Item>`. Add intrinsic item values directly while retaining resource-key values only for the block/action providers whose appender type requires them. | IN PROGRESS |

## Current consolidated diagnostic inventory: CI run 32215235905

Run 32215235905 passed Java compilation and exposed the next Kotlin client/API family. The complete workflow output is stored in `docs/CI_32215235905_FAILED.log`; all Kotlin source diagnostics are indexed in `docs/CI_32215235905_KOTLIN_ERROR_INDEX.txt`.

| Batch ID | Affected file(s) | Complete fix scope recorded from run 32215235905 | Status |
|---|---|---|---|
| B324-01 | `FabricHexClientInitializer.kt` | Move `WorldRenderEvents` to `net.fabricmc.fabric.api.client.rendering.v1.world`, replace removed `AFTER_TRANSLUCENT`/`START` callbacks with the exact 1.21.11 event equivalents, and use `WorldRenderContext.matrices()` plus Minecraft’s delta tracker instead of removed `matrixStack()`/`tickCounter()`. | IN PROGRESS |
| B324-02 | `FabricHexClientInitializer.kt` | Tighten the particle factory consumer from nullable `ParticleOptions?` to the exact non-null `ParticleOptions` bound and register the pending factory with the 1.21.11 Fabric API. | IN PROGRESS |
| B324-03 | `FabricHexClientInitializer.kt`, `RegisterClientStuff.java` | Migrate block-entity renderer registration to the two-parameter `BlockEntityRendererProvider<T,S>` contract and the existing renderer state types without changing registrations. | IN PROGRESS |
| B324-04 | `FabricHexClientInitializer.kt` | Replace removed `ColorProviderRegistry.ITEM` with the exact 1.21.11 item-color registration mechanism while preserving all item colorizers; retain Fabric block color registration. | IN PROGRESS |
| B324-05 | `FabricHexClientInitializer.kt`, `FabricModelManagerMixin.java`, `RegisterClientStuff.java` | Replace removed `ModelLoadingPlugin.Context.addModels(Identifier)` with the exact 1.21.11 keyed `addModel(ExtraModelKey, SimpleUnbakedExtraModel)` flow and bridge baked extra models into the existing variant map without feature changes. | IN PROGRESS |
| B324-06 | `FabricHexInitializer.kt` | Constrain the generic `bind` helper to `T : Any` and resolve the remaining Kotlin overload/type-bound diagnostic. | IN PROGRESS |
| B324-07 | Cross-cutting Kotlin validation | Re-run the full Fabric Kotlin compiler after the grouped client/API fixes and record any new diagnostics before further edits. | IN PROGRESS |

## Current consolidated diagnostic inventory: CI run 32216180195

Run 32216180195 passed the Fabric Kotlin compilation and exposed the next Java families. The complete workflow output is stored in `docs/CI_32216180195_FAILED.log`, Java diagnostics are indexed in `docs/CI_32216180195_ERROR_INDEX.txt`, and unique file/error summaries are stored in `docs/CI_32216180195_UNIQUE_ERRORS.txt`.

| Batch ID | Affected file(s) | Complete fix scope recorded from run 32216180195 | Status |
|---|---|---|---|
| B325-01 | `Fabric/src/main/java/at/petrak/hexcasting/fabric/cc/CC*.java`, including `CCAltiora`, `CCBrainswept`, `CCClientCastingStack`, `CCFavoredPigment`, `CCFlight`, `CCPatterns`, `CCSentinel`, `CCStaffcastImage`, and `cc/adimpl/*` | ComputerCraft component interfaces now require `writeData(ValueOutput)` and changed read/write method contracts. Migrate all CC component serialization methods as one persistence family, preserving keys and codec behavior; resolve Optional NBT accessors and removed overrides. | IN PROGRESS |
| B325-02 | `FabricModConditionalIngredient.java`, `FabricUnsealedIngredient.java` | Fabric 1.21.11 `Ingredient` is final and custom ingredients must implement the current `CustomIngredient`/`CustomIngredientSerializer` contracts, including `getMatchingItems()` and `getCodec()`. Replace inheritance/private-field access and preserve conditional/unsealed matching semantics. | IN PROGRESS |
| B325-03 | `FabricHexConditionsBuilder.java` | Add the new `RecipeOutput.includeRootAdvancement()` implementation required by the 1.21.11 interface while preserving conditional recipe output behavior. | IN PROGRESS |
| B325-04 | `LensAccessoryRenderer.java` | Resolve Accessories API renderer/package and generic-bound changes; preserve lens rendering and model registration. | IN PROGRESS |
| B325-05 | `BrainsweepeeEmiStack.java`, `PatternRendererEMI.java`, `TheCoolerSlotWidget.java` | Migrate EMI rendering/UI APIs: missing stack methods, Matrix3x2fStack/PoseStack boundaries, RenderPipeline-based GUI rendering, and changed widget/texture methods. Preserve existing EMI visuals and interactions. | IN PROGRESS |
| B325-06 | `FabricParticleEngineMixin.java`, `FabricPlayerRendererMixin.java` | Resolve removed or renamed Fabric/Minecraft client mixin target classes and changed generic renderer signatures against exact 1.21.11 mappings. | IN PROGRESS |
| B325-07 | `FabricClientXplatImpl.java`, `FabricRegister.java`, `FabricXplatImpl.java` | Migrate removed BlockRenderLayerMap package, Optional registry lookup typing, HolderGetter-aware item predicates, and remaining missing symbols in Fabric platform abstractions. | IN PROGRESS |
| B325-08 | Cross-cutting validation | Re-run source-wide Fabric Java/Kotlin compilation after these grouped families, record every new diagnostic before editing, and do not push an unreviewed partial batch. | IN PROGRESS |


### Local validation constraint

The local Gradle `:Common:compileJava` attempt reached the compile task but could not acquire a Java 21 compiler because `/usr/lib/jvm/java-21-openjdk-amd64` is a JRE (`java` exists, `javac` is absent). The full source batch therefore remains uncompiled locally and must be validated by the planned GitHub Actions run after the checklist/source review. This is recorded as an environment limitation, not a source resolution.

### B325 verification notes recorded before source edits

The exact 1.21.11 mappings and installed API sources establish the following batch boundaries:

| Batch | Verified finding | Evidence used | Required implementation direction |
|---|---|---|---|
| B325-04 | Accessories beta.16 still declares `AccessoryRenderer.render` with `EntityModel<M>`, but Minecraft 1.21.11 changes `EntityModel` to `EntityModel<T extends EntityRenderState>`. The old `LivingEntity` bound is therefore invalid. `PlayerModel` is now `net.minecraft.client.model.player.PlayerModel` and is based on `AvatarRenderState`. `ItemRenderer.renderStatic` is removed; 1.21.11 resolves item models through `ItemModelManager`/`ItemRenderState` and submits them through the render-command queue. | `/home/ubuntu/mc12111/client.txt`; Accessories beta.16 source; Yarn 1.21.11 ItemModelManager, ItemRenderState, and ItemRenderer documentation | Update the renderer’s generic bridge and item-render path as one compatibility change, preserving the lens’s HEAD transform and visual output. |
| B325-05 | EMI 1.1.18 uses `GuiGraphics` backed by `Matrix3x2fStack`; old `PoseStack` operations and direct `RenderSystem.enableBlend`/`setShaderColor` calls are invalid. EMI’s current widget path provides `EmiDrawContext`/GUI helpers, and custom renderables must use the current GUI matrix API. | EMI 1.1.18 source jar; CI 32216180195 diagnostics | Migrate `BrainsweepeeEmiStack`, `PatternRendererEMI`, and `TheCoolerSlotWidget` together to the current GUI/render-pipeline APIs, retaining icon rendering, pattern positioning, slot backgrounds, catalyst icons, and tooltips. |
| B325-06 | The custom particle class now returns the built-in `ParticleRenderType` layer `TRANSLUCENT`; the old `CONJURE_RENDER_TYPE` field no longer exists. Minecraft 1.21.11 replaces `PlayerRenderer` with `AvatarRenderer`, whose constructor is `(EntityRendererProvider.Context, boolean)` and whose render-state/model family uses `AvatarRenderState`. | `/home/ubuntu/mc12111/client.txt`; current `ConjureParticle`; upstream HexMod 1.21 guidance | Remove only the obsolete custom particle-order hook or convert it to the built-in layer without changing particle behavior, and retarget the Altiora mixin to `AvatarRenderer` with the exact three-parameter renderer hierarchy. |
| B325-07 | `BlockRenderLayerMap` is in `net.fabricmc.fabric.api.client.rendering.v1`; registry lookup requires `getValueOrThrow(ResourceKey)`; item predicates require `HolderGetter<Item>` as the first `of` argument; `InteractionResult` exposes `result()` rather than the removed `getResult()`. | Fabric API 1.21.11 sources; `/home/ubuntu/mc12111/client.txt`; current source audit | Complete the platform batch and re-run broad stale-symbol searches before committing. |

No B325 row is marked `DONE` from source edits alone. These findings are recorded so the remaining work is implemented by family before the next CI build.

### B325 repository-wide audit result

The pre-commit authored-source audit is persisted in `docs/PORT_1.21.11_REPOSITORY_WIDE_AUDIT_B325.txt`. It found zero remaining hits for `readFromNbt`, `writeToNbt`, `getMatchingStacks`, `getCodec(boolean)`, custom-ingredient inheritance from final `Ingredient`, `renderStatic`, `CONJURE_RENDER_TYPE`, `InteractionResult.getResult()`, the removed `PlayerRenderer` class, the old `PlayerModel` package, the removed BlockRenderLayerMap package, `ResourceKey.location()`, `getAllRecipesFor`, `registryOrThrow`, and `Ingredient.EMPTY`. The single `getRecipeManager()` reference is the current EMI registry API used by `HexEMIPlugin`; the two `ItemTagsProvider` hits are the valid `BlockItemTagsProvider` compatibility implementation and are not stale direct inheritance. `git diff --check` passes. The full uncommitted source batch remains pending CI compilation and is therefore not marked `DONE`.

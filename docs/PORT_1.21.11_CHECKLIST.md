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
| API-05 | Persistence | ValueInput/ValueOutput and typed-component load/save APIs | Block entities, VillagerIngredient, Patchouli processor | IN PROGRESS | Audit all loadAdditional/saveAdditional calls |
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
| API-19 | Blocks | Block codec/map-codec requirements and block registration types | block classes and `HexBlocks` | OPEN | Current CI reports missing symbols |
| API-20 | Worldgen | Tree grower, feature config, weighted list, and configured feature registry APIs | `AkashicTreeGrower`, `HexFeatureConfigs`, `HexBlocks` | OPEN | Current CI reports missing symbols |
| API-21 | Potions | Potion/effect registry API changes | `HexPotions` | OPEN | Current CI reports missing symbols |
| API-22 | Recipes | Recipe serializer raw/generic signatures, recipe category, placement info | Brainsweep, Seal recipes | IN PROGRESS | Recent patches need consolidated verification |
| API-23 | Recipes | CustomRecipe serializer replacement for removed SimpleCraftingRecipeSerializer | SealThings, SealSpellbook | IN PROGRESS | Recent patch needs compile verification |
| API-24 | Recipes | RecipeProvider `buildRecipes` API | `HexplatRecipes` | OPEN | Current CI reports abstract-method failure |
| API-25 | Recipes | RecipeBuilder save(ResourceKey) API | crushing, Farmers Delight, Brainsweep builders | OPEN | Current CI reports abstract-method/type failures |
| API-26 | Recipes | Ingredient.Value removal and HolderSet/Ingredient codec model | `CompatIngredientValue` | OPEN | Current CI reports HolderSet mismatch |
| API-27 | Recipes | EntityPredicate factory removal | EntityTagIngredient, EntityTypeIngredient | OPEN | Current CI reports `create(Level)` failure |
| API-28 | Recipes | Villager data and profession API changes | `VillagerIngredient` | OPEN | Current CI reports symbols and ValueOutput mismatch |
| API-29 | Advancements | MinMaxBounds codec/bounds/parser API | `MinMaxLongs` | IN PROGRESS | Recent patch needs compile verification |
| API-30 | Advancements | HolderGetter-based item predicates and advancement builders | `HexAdvancements` | OPEN | Current CI reports TagKey/ItemLike/HolderGetter mismatches |
| API-31 | Datagen | TagsProvider/ItemTagsProvider/BlockTagsProvider API | Hex item/block tag providers | OPEN | Current CI reports missing symbols |
| API-32 | Commands | DimensionDataStorage.set and SavedData API | RecalcPatterns and pattern commands | OPEN | Current CI reports method/signature failures |
| API-33 | Commands | Pattern resource-key/registry command APIs | PatternResKeyArgument and list/texture commands | OPEN | Current CI reports missing symbols |
| API-34 | Iota | Codec/unit and Optional numeric accessors | DoubleIota, PatternIota | OPEN | Current CI reports missing symbols |
| API-35 | Patterns | Pattern registry manifest and codec/registry lookup APIs | `PatternRegistryManifest` | OPEN | Current CI reports missing symbols |
| API-36 | Hex logic | Arithmetic/action registration and method references | `HexArithmetics`, `HexActions` | OPEN | Current CI reports missing symbols/invalid references |
| API-37 | Rendering | 1.21.11 render pipeline, RenderType, RenderStateShard, GlStateManager changes | client render helpers and shader/render types | IN PROGRESS | Source-wide render audit required |
| API-38 | Rendering | Submit-based BlockEntityRenderer and RenderLayer APIs | renderers, layers, render states | IN PROGRESS | Source-wide submit/signature audit required |
| API-39 | Rendering | GUI Matrix3x2fStack replacing PoseStack | GUI, Patchouli, inline rendering | OPEN | Current CI reports matrix incompatibilities |
| API-40 | Rendering | Particle provider/single-quad API | ConjureParticle and providers | IN PROGRESS | Re-audit particle classes |
| API-41 | Rendering | Texture/DynamicTexture/NativeImage API | pattern texture manager and tooltip | IN PROGRESS | Re-audit texture calls |
| API-42 | Patchouli | Patchouli processors/components and persistence APIs | Patchouli interop package | OPEN | Current CI reports many missing symbols |
| API-43 | Inline | HoverEvent/ClickEvent factories and InlineStyle changes | inline pattern data/renderer | OPEN | Current CI reports abstract/factory/type failures |
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

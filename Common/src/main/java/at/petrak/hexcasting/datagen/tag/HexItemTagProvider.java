package at.petrak.hexcasting.datagen.tag;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class HexItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    private final IXplatTags xtags;

    public HexItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, IXplatTags xtags) {
        super(output, Registries.ITEM, lookup,
            item -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow());
        this.xtags = xtags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        add(tag(xtags.gems()),
            HexItems.CHARGED_AMETHYST);
        add(tag(xtags.amethystDust()),
            HexItems.AMETHYST_DUST);

        add(tag(HexTags.Items.STAVES),
            HexItems.STAFF_EDIFIED,
            HexItems.STAFF_OAK, HexItems.STAFF_SPRUCE, HexItems.STAFF_BIRCH,
            HexItems.STAFF_JUNGLE, HexItems.STAFF_ACACIA, HexItems.STAFF_DARK_OAK,
            HexItems.STAFF_CRIMSON, HexItems.STAFF_WARPED, HexItems.STAFF_MANGROVE,
            HexItems.STAFF_CHERRY,HexItems.STAFF_BAMBOO,
            HexItems.STAFF_QUENCHED, HexItems.STAFF_MINDSPLICE);

        add(tag(HexTags.Items.PHIAL_BASE),
            Items.GLASS_BOTTLE);
        add(tag(HexTags.Items.GRANTS_ROOT_ADVANCEMENT),
            HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD,
            HexItems.CHARGED_AMETHYST, HexItems.CREATIVE_UNLOCKER);
        add(tag(HexTags.Items.SEAL_MATERIALS),
            Items.HONEYCOMB);

        add(tag(HexTags.Items.EDIFIED_LOGS),
            HexBlocks.EDIFIED_LOG.asItem(), HexBlocks.EDIFIED_LOG_AMETHYST.asItem(),
            HexBlocks.EDIFIED_LOG_AVENTURINE.asItem(), HexBlocks.EDIFIED_LOG_CITRINE.asItem(),
            HexBlocks.EDIFIED_LOG_PURPLE.asItem(), HexBlocks.STRIPPED_EDIFIED_LOG.asItem(),
            HexBlocks.EDIFIED_WOOD.asItem(), HexBlocks.STRIPPED_EDIFIED_WOOD.asItem());
        add(tag(HexTags.Items.EDIFIED_PLANKS),
            HexBlocks.EDIFIED_PLANKS.asItem(), HexBlocks.EDIFIED_PANEL.asItem(), HexBlocks.EDIFIED_TILE.asItem());
        add(tag(HexTags.Items.IMPETI),
            HexBlocks.IMPETUS_LOOK.asItem(), HexBlocks.IMPETUS_RIGHTCLICK.asItem(), HexBlocks.IMPETUS_REDSTONE.asItem());
        add(tag(HexTags.Items.DIRECTRICES),
            HexBlocks.DIRECTRIX_REDSTONE.asItem(), HexBlocks.DIRECTRIX_BOOLEAN.asItem());
        add(tag(HexTags.Items.MINDFLAYED_CIRCLE_COMPONENTS),
            HexBlocks.IMPETUS_LOOK.asItem(), HexBlocks.IMPETUS_RIGHTCLICK.asItem(),
            HexBlocks.IMPETUS_REDSTONE.asItem(), HexBlocks.DIRECTRIX_REDSTONE.asItem(), HexBlocks.DIRECTRIX_BOOLEAN.asItem());
        add(tag(HexTags.Items.SLATE_BLOCKS),
            HexBlocks.SLATE_BLOCK.asItem(), HexBlocks.SLATE_BRICKS.asItem(), HexBlocks.SLATE_BRICKS_SMALL.asItem(),
            HexBlocks.SLATE_TILES.asItem(), HexBlocks.SLATE_PILLAR.asItem());
        add(tag(HexTags.Items.AMETHYST_BLOCKS),
            Blocks.AMETHYST_BLOCK.asItem(), HexBlocks.AMETHYST_BRICKS.asItem(), HexBlocks.AMETHYST_BRICKS_SMALL.asItem(),
            HexBlocks.AMETHYST_TILES.asItem(), HexBlocks.AMETHYST_PILLAR.asItem());
        add(tag(HexTags.Items.QUENCHED_ALLAY_BLOCKS),
            HexBlocks.QUENCHED_ALLAY.asItem(), HexBlocks.QUENCHED_ALLAY_BRICKS.asItem(),
            HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL.asItem(), HexBlocks.QUENCHED_ALLAY_TILES.asItem());
        new StandardBlockItemTags().apply();
    }


    private class StandardBlockItemTags extends BlockItemTagsProvider {
        void apply() {
            run();
        }

        @Override
        protected TagAppender<Item> tag(net.minecraft.tags.TagKey<Block> blockTag, net.minecraft.tags.TagKey<Item> itemTag) {
            return HexItemTagProvider.this.tag(itemTag);
        }
    }

    void add(TagAppender<Item> appender, Item... items) {
        for (Item item : items) {
            appender.add(BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow());
        }
    }
}

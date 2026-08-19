package at.petrak.hexcasting.fabric;

import at.petrak.hexcasting.client.RegisterClientStuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

/**
 * Bridges Hex Casting's legacy item color-provider callbacks to the 1.21.11
 * item-model tint-source system without changing the callback behavior.
 */
public final class FabricItemTintRegistrar {
    private static final Identifier TYPE_ID = modLoc("hexcasting");
    private static final Map<Item, RegisterClientStuff.ItemColor> PROVIDERS = new IdentityHashMap<>();
    private static boolean initialized;

    private FabricItemTintRegistrar() {
    }

    public static void register(RegisterClientStuff.ItemColor provider, Item item) {
        ensureInitialized();
        PROVIDERS.put(item, provider);
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        var provider = PROVIDERS.get(stack.getItem());
        return provider == null ? 0xFFFFFFFF : provider.getColor(stack, tintIndex);
    }

    private static void ensureInitialized() {
        if (!initialized) {
            ItemTintSources.ID_MAPPER.put(TYPE_ID, HexTintSource.MAP_CODEC);
            initialized = true;
        }
    }

    public record HexTintSource(int tintIndex) implements ItemTintSource {
        public static final MapCodec<HexTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("tint_index").forGetter(HexTintSource::tintIndex)
        ).apply(instance, HexTintSource::new));

        @Override
        public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
            return FabricItemTintRegistrar.getColor(stack, tintIndex);
        }

        @Override
        public MapCodec<HexTintSource> type() {
            return MAP_CODEC;
        }
    }
}

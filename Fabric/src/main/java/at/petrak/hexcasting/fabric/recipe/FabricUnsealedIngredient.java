package at.petrak.hexcasting.fabric.recipe;

import at.petrak.hexcasting.api.utils.NBTHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.stream.Stream;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class FabricUnsealedIngredient implements CustomIngredient {
    public static final Identifier ID = modLoc("unsealed");

    private final ItemStack stack;

    public static final MapCodec<FabricUnsealedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemStack.CODEC.fieldOf("item").forGetter(FabricUnsealedIngredient::getStack)
    ).apply(instance, FabricUnsealedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricUnsealedIngredient> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC, FabricUnsealedIngredient::getId,
        ItemStack.STREAM_CODEC, FabricUnsealedIngredient::getStack,
        (a, b) -> new FabricUnsealedIngredient(b)
    );

    private static ItemStack createStack(ItemStack base) {
        ItemStack newStack = base.copy();
        CompoundTag tag = newStack.get(DataComponents.CUSTOM_DATA).copyTag();
        NBTHelper.putString(tag, "VisualOverride", "any");
        newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return newStack;
    }

    protected FabricUnsealedIngredient(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Identifier getId() {
        return ID;
    }

    public static FabricUnsealedIngredient of(ItemStack stack) {
        return new FabricUnsealedIngredient(stack);
    }

    @Override
    public boolean test(ItemStack input) {
        return false;
    }

    @Override
    public Stream<Holder<Item>> getMatchingItems() {
        return Stream.of(BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()));
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<FabricUnsealedIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements CustomIngredientSerializer<FabricUnsealedIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public Identifier getIdentifier() {
            return FabricUnsealedIngredient.ID;
        }

        @Override
        public MapCodec<FabricUnsealedIngredient> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FabricUnsealedIngredient> getPacketCodec() {
            return STREAM_CODEC;
        }
    }
}

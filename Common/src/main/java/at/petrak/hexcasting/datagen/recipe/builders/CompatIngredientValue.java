package at.petrak.hexcasting.datagen.recipe.builders;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Datagen helper for optional compatibility ingredients.
 *
 * <p>Minecraft 1.21.11 no longer exposes the old {@code Ingredient.Value}
 * extension point. Optional ingredients are resolved through the item registry
 * when the compatible mod is present, then represented by the native
 * holder-backed Ingredient implementation.</p>
 */
public final class CompatIngredientValue {
    private CompatIngredientValue() {
    }

    public static Ingredient of(String itemName) {
        var id = Identifier.parse(itemName);
        var item = BuiltInRegistries.ITEM.getOptional(id)
            .orElseThrow(() -> new IllegalArgumentException("Unknown compatibility item: " + itemName));
        return Ingredient.of(item);
    }
}

package at.petrak.hexcasting.fabric.datagen;

import at.petrak.hexcasting.datagen.recipe.HexplatRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class HexFabricRecipeProvider extends FabricRecipeProvider {
    public HexFabricRecipeProvider(FabricDataOutput output,
                                   CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new HexplatRecipes(
            registryLookup,
            exporter,
            HexFabricDataGenerators.INGREDIENTS,
            HexFabricConditionsBuilder::new
        );
    }

    @Override
    public String getName() {
        return "Hex Casting Recipes";
    }
}

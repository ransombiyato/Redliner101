package at.petrak.hexcasting.fabric.mixin.client;

import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.fabric.FabricHexClientInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelManager.class)
public class FabricModelManagerMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    private void onModelBake(ModelManager.ReloadState reloadState, CallbackInfo ci) {
        Map<Identifier, BlockStateModel> models = new java.util.HashMap<>();
        var modelManager = (FabricBakedModelManager) (Object) this;
        FabricHexClientInitializer.EXTRA_MODEL_KEYS.forEach((id, key) -> {
            var model = modelManager.getModel(key);
            if (model != null) {
                models.put(id, model);
            }
        });
        RegisterClientStuff.onModelBake(null, models);
    }
}

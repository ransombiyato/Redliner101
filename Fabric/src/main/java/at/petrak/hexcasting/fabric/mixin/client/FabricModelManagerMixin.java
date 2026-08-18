package at.petrak.hexcasting.fabric.mixin.client;

import at.petrak.hexcasting.client.RegisterClientStuff;
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
        @SuppressWarnings("unchecked")
        Map<Identifier, BlockStateModel> models = (Map<Identifier, BlockStateModel>) (Map<?, ?>)
            reloadState.bakedModels().blockStateModels();
        RegisterClientStuff.onModelBake(null, models);
    }
}

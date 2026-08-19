package at.petrak.hexcasting.fabric.mixin.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface FabricItemStackRenderStateAccessor {
    @Accessor("activeLayerCount")
    int hex$getActiveLayerCount();

    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] hex$getLayers();
}

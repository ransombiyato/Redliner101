package at.petrak.hexcasting.fabric.mixin.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface FabricItemLayerRenderStateAccessor {
    @Accessor("quads")
    List<BakedQuad> hex$getQuads();

    @Accessor("transform")
    ItemTransform hex$getTransform();

    @Accessor("renderType")
    RenderType hex$getRenderType();

    @Accessor("foilType")
    ItemStackRenderState.FoilType hex$getFoilType();

    @Accessor("tintLayers")
    int[] hex$getTintLayers();
}

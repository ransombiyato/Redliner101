package at.petrak.hexcasting.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface FabricItemRendererInvoker {
    @Invoker("renderItem")
    static void hex$renderItem(ItemDisplayContext displayContext, PoseStack matrices,
        MultiBufferSource bufferSource, int light, int overlay, int[] tints,
        List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foilType) {
        throw new AssertionError();
    }
}

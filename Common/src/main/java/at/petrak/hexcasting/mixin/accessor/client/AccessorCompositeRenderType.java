package at.petrak.hexcasting.mixin.accessor.client;

import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.CompositeRenderType.class)
public interface AccessorCompositeRenderType {
    @Invoker("state")
    RenderType.CompositeState hex$state();
}

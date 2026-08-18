package at.petrak.hexcasting.mixin.accessor.client;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface AccessorRenderTypeFactory {
    @Invoker("create")
    static RenderType hex$create(String name, RenderSetup setup) {
        throw new IllegalStateException();
    }
}

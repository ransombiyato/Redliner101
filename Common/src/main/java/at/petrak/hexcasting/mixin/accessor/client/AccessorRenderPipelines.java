package at.petrak.hexcasting.mixin.accessor.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface AccessorRenderPipelines {
    @Invoker("register")
    static RenderPipeline hex$register(RenderPipeline pipeline) {
        throw new IllegalStateException();
    }

    @Accessor("ENTITY_SNIPPET")
    static RenderPipeline.Snippet hex$entitySnippet() {
        throw new IllegalStateException();
    }
}

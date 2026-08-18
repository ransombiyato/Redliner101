package at.petrak.hexcasting.client.render.shader;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.mixin.accessor.client.AccessorRenderPipelines;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;

/** Custom render pipelines used by Hex Casting's client renderers. */
public final class HexShaders {
    private HexShaders() {
    }

    public static final RenderPipeline GRAYSCALE = AccessorRenderPipelines.hex$register(
        RenderPipeline.builder(AccessorRenderPipelines.hex$entitySnippet())
            .withLocation(HexAPI.modLoc("pipeline/grayscale"))
            .withFragmentShader(HexAPI.modLoc("hexcasting__grayscale"))
            .withSampler("Sampler0")
            .withSampler("Sampler1")
            .withSampler("Sampler2")
            .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    );

    public static RenderPipeline grayscale() {
        return GRAYSCALE;
    }
}

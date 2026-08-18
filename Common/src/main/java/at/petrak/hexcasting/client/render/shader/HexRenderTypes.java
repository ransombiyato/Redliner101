package at.petrak.hexcasting.client.render.shader;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

/** Render types backed by Hex Casting's custom pipelines. */
public final class HexRenderTypes {
    private HexRenderTypes() {
    }

    private static final Function<Identifier, RenderType> GRAYSCALE_PROVIDER = Util.memoize(texture ->
        RenderType.create(
            HexAPI.MOD_ID + ":grayscale",
            RenderSetup.builder(HexShaders.GRAYSCALE)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .bufferSize(RenderType.SMALL_BUFFER_SIZE)
                .createRenderSetup()
        )
    );

    public static RenderType getGrayscaleLayer(Identifier texture) {
        return GRAYSCALE_PROVIDER.apply(texture);
    }
}

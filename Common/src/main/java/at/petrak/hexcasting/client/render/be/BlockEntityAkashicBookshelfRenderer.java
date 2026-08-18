package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public class BlockEntityAkashicBookshelfRenderer implements BlockEntityRenderer<BlockEntityAkashicBookshelf, BlockEntityAkashicBookshelfRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        private BlockEntityAkashicBookshelf blockEntity;
    }

    public BlockEntityAkashicBookshelfRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityAkashicBookshelf blockEntity, State state, float partialTick,
        Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.blockEntity = blockEntity;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        HexPattern pattern = state.blockEntity.getPattern();
        if (pattern == null) {
            return;
        }
        BlockEntitySubmitUtil.submit(poseStack, collector, (stack, buffer) ->
            WorldlyPatternRenderHelpers.renderPatternForAkashicBookshelf(
                state.blockEntity, pattern, stack, buffer, state.lightCoords, state.blockState));
    }
}

package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public class BlockEntitySlateRenderer implements BlockEntityRenderer<BlockEntitySlate, BlockEntitySlateRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        private BlockEntitySlate blockEntity;
    }

    public BlockEntitySlateRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntitySlate blockEntity, State state, float partialTick,
        Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.blockEntity = blockEntity;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.blockEntity.pattern == null) {
            return;
        }
        BlockEntitySubmitUtil.submit(poseStack, collector, (stack, buffer) ->
            WorldlyPatternRenderHelpers.renderPatternForSlate(
                state.blockEntity, state.blockEntity.pattern, stack, buffer, state.lightCoords, state.blockState));
    }
}

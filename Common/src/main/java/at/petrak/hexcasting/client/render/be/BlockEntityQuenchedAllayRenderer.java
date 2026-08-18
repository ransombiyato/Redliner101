package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.client.render.GaslightingTracker;
import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityQuenchedAllay;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockEntityQuenchedAllayRenderer implements BlockEntityRenderer<BlockEntityQuenchedAllay, BlockEntityQuenchedAllayRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        private BlockEntityQuenchedAllay blockEntity;
    }

    public BlockEntityQuenchedAllayRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityQuenchedAllay blockEntity, State state, float partialTick,
        Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.blockEntity = blockEntity;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        BlockQuenchedAllay block = (BlockQuenchedAllay) state.blockEntity.getBlockState().getBlock();
        var aabb = new AABB(state.blockEntity.getBlockPos().offset(-1, 0, -1).getCenter(),
            state.blockEntity.getBlockPos().offset(1, 1, 1).getCenter());
        if (!IClientXplatAbstractions.INSTANCE.fabricAdditionalQuenchFrustumCheck(aabb)) {
            return;
        }

        int idx = Math.abs(GaslightingTracker.getGaslightingAmount() % BlockQuenchedAllay.VARIANTS);
        BlockStateModel model = RegisterClientStuff.QUENCHED_ALLAY_VARIANTS
            .get(BuiltInRegistries.BLOCK.getKey(block)).get(idx);
        collector.submitBlockModel(poseStack, RenderTypes.translucentMovingBlock(), model,
            1f, 1f, 1f, state.lightCoords, 0, 0, state.breakProgress);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }
}

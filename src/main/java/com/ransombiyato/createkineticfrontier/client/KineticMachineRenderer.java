package com.ransombiyato.createkineticfrontier.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ransombiyato.createkineticfrontier.blockentity.KineticMachineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;

public final class KineticMachineRenderer<T extends KineticMachineBlockEntity> implements BlockEntityRenderer<T> {
    public KineticMachineRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(T machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float time = machine.getLevel() == null ? machine.getActivity() + partialTick : machine.getLevel().getGameTime() + partialTick;
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotation(time * 0.12f));
        float scale = machine.getActivity() > 0 ? 0.28f : 0.19f;
        poseStack.scale(scale, 0.08f, scale);
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.renderSingleBlock(Blocks.COPPER_BLOCK.defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}

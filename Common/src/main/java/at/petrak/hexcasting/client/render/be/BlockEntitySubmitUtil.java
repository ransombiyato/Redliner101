package at.petrak.hexcasting.client.render.be;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

final class BlockEntitySubmitUtil {
    private static final Identifier WHITE_TEXTURE = modLoc("textures/entity/white.png");

    private BlockEntitySubmitUtil() {
    }

    static void submit(PoseStack poseStack, SubmitNodeCollector collector, BiConsumer<PoseStack, MultiBufferSource> renderer) {
        RenderType layer = net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(WHITE_TEXTURE);
        collector.submitCustomGeometry(poseStack, layer, (pose, vertexConsumer) -> {
            PoseStack copied = new PoseStack();
            copied.last().pose().set(pose.pose());
            copied.last().normal().set(pose.normal());
            MultiBufferSource source = renderType -> vertexConsumer;
            renderer.accept(copied, source);
        });
    }
}

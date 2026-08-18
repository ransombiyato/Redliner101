package at.petrak.hexcasting.client.entity;

import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.world.phys.Vec3;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class WallScrollRenderer extends EntityRenderer<EntityWallScroll, WallScrollRenderer.State> {
    private static final Identifier PRISTINE_BG_LARGE = modLoc("textures/entity/scroll_large.png");
    private static final Identifier PRISTINE_BG_MEDIUM = modLoc("textures/entity/scroll_medium.png");
    private static final Identifier PRISTINE_BG_SMOL = modLoc("textures/block/scroll_paper.png");
    private static final Identifier ANCIENT_BG_LARGE = modLoc("textures/entity/scroll_ancient_large.png");
    private static final Identifier ANCIENT_BG_MEDIUM = modLoc("textures/entity/scroll_ancient_medium.png");
    private static final Identifier ANCIENT_BG_SMOL = modLoc("textures/block/ancient_scroll_paper.png");

    public static final class State extends EntityRenderState {
        private EntityWallScroll entity;
    }

    public WallScrollRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EntityWallScroll entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        EntityWallScroll wallScroll = state.entity;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(getTextureLocation(wallScroll)),
            (pose, vertexConsumer) -> {
                PoseStack copied = copyPose(pose);
                emitScrollMesh(wallScroll, copied.last(), vertexConsumer, state.lightCoords);
                if (wallScroll.pattern != null) {
                    MultiBufferSource source = renderType -> vertexConsumer;
                    WorldlyPatternRenderHelpers.renderPatternForScroll(wallScroll.pattern, wallScroll, copied, source,
                        state.lightCoords, wallScroll.blockSize, wallScroll.getShowsStrokeOrder());
                }
            });
    }

    private static PoseStack copyPose(PoseStack.Pose pose) {
        PoseStack stack = new PoseStack();
        stack.last().pose().set(pose.pose());
        stack.last().normal().set(pose.normal());
        return stack;
    }

    private static void emitScrollMesh(EntityWallScroll wallScroll, PoseStack.Pose basePose, VertexConsumer verts, int light) {
        PoseStack ps = new PoseStack();
        ps.last().pose().set(basePose.pose());
        ps.last().normal().set(basePose.normal());
        ps.mulPose(Axis.YP.rotationDegrees(180f - wallScroll.getYRot()));
        ps.mulPose(Axis.ZP.rotationDegrees(180f));
        ps.translate(-wallScroll.blockSize / 2f, -wallScroll.blockSize / 2f, 1f / 32f);

        float dx = wallScroll.blockSize;
        float dy = wallScroll.blockSize;
        float dz = -1f / 16f;
        float margin = 1f / 48f;
        var last = ps.last();
        var mat = last.pose();

        vertex(mat, last, light, verts, 0, 0, dz, 0, 0, 0, 0, -1);
        vertex(mat, last, light, verts, 0, dy, dz, 0, 1, 0, 0, -1);
        vertex(mat, last, light, verts, dx, dy, dz, 1, 1, 0, 0, -1);
        vertex(mat, last, light, verts, dx, 0, dz, 1, 0, 0, 0, -1);
        vertex(mat, last, light, verts, 0, 0, 0, 0, 0, 0, 0, 1);
        vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 0, 0, 1);
        vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 0, 0, 1);
        vertex(mat, last, light, verts, 0, dy, 0, 0, 1, 0, 0, 1);
        vertex(mat, last, light, verts, 0, 0, 0, 0, 0, 0, -1, 0);
        vertex(mat, last, light, verts, 0, 0, dz, 0, margin, 0, -1, 0);
        vertex(mat, last, light, verts, dx, 0, dz, 1, margin, 0, -1, 0);
        vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 0, -1, 0);
        vertex(mat, last, light, verts, 0, 0, 0, 0, 0, -1, 0, 0);
        vertex(mat, last, light, verts, 0, dy, 0, 0, 1, -1, 0, 0);
        vertex(mat, last, light, verts, 0, dy, dz, margin, 1, -1, 0, 0);
        vertex(mat, last, light, verts, 0, 0, dz, margin, 0, -1, 0, 0);
        vertex(mat, last, light, verts, dx, 0, dz, 1 - margin, 0, 1, 0, 0);
        vertex(mat, last, light, verts, dx, dy, dz, 1 - margin, 1, 1, 0, 0);
        vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 1, 0, 0);
        vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 1, 0, 0);
        vertex(mat, last, light, verts, 0, dy, dz, 0, 1 - margin, 0, 1, 0);
        vertex(mat, last, light, verts, 0, dy, 0, 0, 1, 0, 1, 0);
        vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 0, 1, 0);
        vertex(mat, last, light, verts, dx, dy, dz, 1, 1 - margin, 0, 1, 0);
    }

    public Identifier getTextureLocation(EntityWallScroll wallScroll) {
        if (wallScroll.isAncient) {
            if (wallScroll.blockSize <= 1) return ANCIENT_BG_SMOL;
            if (wallScroll.blockSize == 2) return ANCIENT_BG_MEDIUM;
            return ANCIENT_BG_LARGE;
        }
        if (wallScroll.blockSize <= 1) return PRISTINE_BG_SMOL;
        if (wallScroll.blockSize == 2) return PRISTINE_BG_MEDIUM;
        return PRISTINE_BG_LARGE;
    }

    private static void vertex(Matrix4f mat, PoseStack.Pose last, int light, VertexConsumer verts, float x, float y,
        float z, float u, float v, float nx, float ny, float nz) {
        verts.addVertex(mat, x, y, z)
            .setColor(0xffffffff)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(last, nx, ny, nz);
    }
}

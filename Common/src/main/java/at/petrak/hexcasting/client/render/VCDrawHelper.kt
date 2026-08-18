package at.petrak.hexcasting.client.render

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.client.render.PatternRenderer.WorldlyBits
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f

interface VCDrawHelper {
    fun vcSetupAndSupply(vertMode: VertexFormat.Mode): VertexConsumer

    fun vertex(vc: VertexConsumer, color: Int, pos: Vec2, matrix: Matrix4f) {
        vertex(vc, color, pos, Vec2(0f, 0f), matrix)
    }

    fun vertex(vc: VertexConsumer, color: Int, pos: Vec2, uv: Vec2, matrix: Matrix4f)

    fun vcEndDrawer(vc: VertexConsumer)

    companion object {
        @JvmStatic
        val WHITE: Identifier = HexAPI.modLoc("textures/entity/white.png")

        @JvmStatic
        fun getHelper(worldlyBits: WorldlyBits?, ps: PoseStack, z: Float, texture: Identifier): VCDrawHelper =
            if (worldlyBits != null) Worldly(worldlyBits, ps, z * -1, texture) else Basic(z, texture)

        @JvmStatic
        fun getHelper(worldlyBits: WorldlyBits?, ps: PoseStack, z: Float): VCDrawHelper =
            getHelper(worldlyBits, ps, z, WHITE)
    }

    class Basic(val z: Float, val texture: Identifier = WHITE) : VCDrawHelper {
        override fun vcSetupAndSupply(vertMode: VertexFormat.Mode): VertexConsumer {
            return Tesselator.getInstance().begin(vertMode, DefaultVertexFormat.NEW_ENTITY)
        }

        override fun vertex(vc: VertexConsumer, color: Int, pos: Vec2, uv: Vec2, matrix: Matrix4f) {
            vc.addVertex(matrix, pos.x, pos.y, z)
                .setColor(color)
                .setUv(uv.x, uv.y)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0f, 0f, 1f)
        }

        override fun vcEndDrawer(vc: VertexConsumer) {
            if (vc is BufferBuilder) {
                RenderTypes.entityTranslucent(texture).draw(vc.buildOrThrow())
            }
        }
    }

    class Worldly(
        val worldlyBits: WorldlyBits,
        val ps: PoseStack,
        val z: Float,
        val texture: Identifier
    ) : VCDrawHelper {
        var lastVertMode: VertexFormat.Mode? = null

        override fun vcSetupAndSupply(vertMode: VertexFormat.Mode): VertexConsumer {
            val provider = worldlyBits.provider
            if (provider is MultiBufferSource.BufferSource) {
                provider.endBatch()
            }
            lastVertMode = vertMode
            return if (provider == null) {
                Tesselator.getInstance().begin(vertMode, DefaultVertexFormat.NEW_ENTITY)
            } else {
                provider.getBuffer(RenderTypes.entityTranslucent(texture))
            }
        }

        override fun vertex(vc: VertexConsumer, color: Int, pos: Vec2, uv: Vec2, matrix: Matrix4f) {
            val nv = worldlyBits.normal ?: Vec3(1.0, 1.0, 1.0)
            vc.addVertex(matrix, pos.x, pos.y, z)
                .setColor(color)
                .setUv(uv.x, uv.y)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(worldlyBits.light ?: LightTexture.FULL_BRIGHT)
                .setNormal(ps.last(), nv.x.toFloat(), nv.y.toFloat(), nv.z.toFloat())
        }

        override fun vcEndDrawer(vc: VertexConsumer) {
            if (worldlyBits.provider == null && vc is BufferBuilder) {
                RenderTypes.entityTranslucent(texture).draw(vc.buildOrThrow())
            }
            lastVertMode = null
        }
    }
}

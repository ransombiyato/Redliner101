package at.petrak.hexcasting.fabric.interop.accessories;

import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.fabric.mixin.client.FabricItemLayerRenderStateAccessor;
import at.petrak.hexcasting.fabric.mixin.client.FabricItemRendererInvoker;
import at.petrak.hexcasting.fabric.mixin.client.FabricItemStackRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * @author WireSegal
 * Created at 9:50 AM on 7/25/22.
 */
public class LensAccessoryRenderer implements AccessoryRenderer {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <M extends EntityRenderState> void render(ItemStack stack, SlotReference slotReference,
        PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light,
        float v, float v1, float v2, float v3, float v4, float v5) {
        if (stack.is(HexItems.SCRYING_LENS) &&
                model instanceof PlayerModel<?> && slotReference.entity() instanceof Player) {

            // from https://github.com/Creators-of-Create/Create/blob/ee33823ed0b5084af10ed131a1626ce71db4c07e/src/main/java/com/simibubi/create/compat/curios/GogglesCurioRenderer.java

            // Translate and rotate with our head
            matrices.pushPose();

            // Translate and scale to our head
            matrices.translate(0, 0, 0.3);
            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.scale(0.625f, 0.625f, 0.625f);

            var instance = Minecraft.getInstance();
            var renderState = new ItemStackRenderState();
            instance.getItemModelResolver().updateForLiving(renderState, stack, ItemDisplayContext.HEAD,
                slotReference.entity());

            var stateAccessor = (FabricItemStackRenderStateAccessor) (Object) renderState;
            var layers = stateAccessor.hex$getLayers();
            for (int i = 0; i < stateAccessor.hex$getActiveLayerCount(); i++) {
                var layer = (FabricItemLayerRenderStateAccessor) (Object) layers[i];
                if (layer.hex$getQuads().isEmpty() || layer.hex$getRenderType() == null) {
                    continue;
                }

                matrices.pushPose();
                layer.hex$getTransform().apply(false, matrices.last());
                FabricItemRendererInvoker.hex$renderItem(ItemDisplayContext.HEAD, matrices,
                    multiBufferSource, light, OverlayTexture.NO_OVERLAY, layer.hex$getTintLayers(),
                    layer.hex$getQuads(), layer.hex$getRenderType(), layer.hex$getFoilType());
                matrices.popPose();
            }
            matrices.popPose();
        }
    }
}

package at.petrak.hexcasting.client.model;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.vertex.PoseStack;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class AltioraLayer<M extends EntityModel<AvatarRenderState>> extends RenderLayer<AvatarRenderState, M> {
    private static final Identifier TEX_LOC = modLoc("textures/misc/altiora.png");

    private final ElytraModel elytraModel;

    public AltioraLayer(RenderLayerParent<AvatarRenderState, M> renderer, EntityModelSet ems) {
        super(renderer);
        this.elytraModel = new ElytraModel(ems.bakeLayer(HexModelLayers.ALTIORA));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight,
        AvatarRenderState state, float limbSwing, float partialTick) {
        Player player = null;
        if (Minecraft.getInstance().level != null && state.id != 0) {
            var entity = Minecraft.getInstance().level.getEntity(state.id);
            if (entity instanceof Player p) {
                player = p;
            }
        }
        if (player == null) {
            return;
        }

        var altiora = IXplatAbstractions.INSTANCE.getAltiora(player);
        var chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
        if (altiora != null && !chestSlot.is(Items.ELYTRA)) {
            this.elytraModel.setupAnim(state);
            coloredCutoutModelCopyLayerRender(this.elytraModel, TEX_LOC, poseStack, collector,
                packedLight, state, 0, 0);
        }
    }
}

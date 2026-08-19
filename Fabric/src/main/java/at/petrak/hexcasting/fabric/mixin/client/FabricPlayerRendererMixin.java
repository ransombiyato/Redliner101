package at.petrak.hexcasting.fabric.mixin.client;

import at.petrak.hexcasting.client.model.AltioraLayer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class FabricPlayerRendererMixin
    extends LivingEntityRenderer<Avatar, AvatarRenderState, PlayerModel> {

    public FabricPlayerRendererMixin(EntityRendererProvider.Context context, boolean slim) {
        super(context,
            new PlayerModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim),
            0.5f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addAltiora(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        this.addLayer(new AltioraLayer<>((AvatarRenderer) (Object) this, ctx.getModelSet()));
    }
}

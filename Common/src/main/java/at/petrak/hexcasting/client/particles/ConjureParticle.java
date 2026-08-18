package at.petrak.hexcasting.client.particles;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ConjureParticle extends SingleQuadParticle {
    private static final Random RANDOM = new Random();
    private final SpriteSet sprites;

    ConjureParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz,
        SpriteSet spriteSet, int color) {
        super(level, x, y, z, dx, dy, dz, spriteSet.get(0, 1));
        this.quadSize *= 0.9f;
        this.setParticleSpeed(dx, dy, dz);
        this.setColor(ARGB.red(color) / 255f, ARGB.green(color) / 255f, ARGB.blue(color) / 255f);
        this.setAlpha(0.3f);
        this.friction = 0.96F;
        this.gravity = dy != 0 && dx != 0 && dz != 0 ? -0.01F : 0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = spriteSet;
        this.roll = RANDOM.nextFloat(360);
        this.oRoll = this.roll;
        this.lifetime = (int) (64.0 / ((Math.random() + 3f) * 0.25f));
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
        this.alpha *= 0.3f;
        this.quadSize *= 0.96f;
    }

    public static class Provider implements ParticleProvider<ConjureParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        @Override
        public @Nullable Particle createParticle(ConjureParticleOptions type, ClientLevel level,
            double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
            return new ConjureParticle(level, x, y, z, dx, dy, dz, this.sprite, type.color());
        }
    }
}

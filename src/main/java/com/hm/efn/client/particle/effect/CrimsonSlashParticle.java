package com.hm.efn.client.particle.effect;

import com.hm.efn.entity.Abstract3DParticleEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CrimsonSlashParticle extends Abstract3DParticle {
   protected CrimsonSlashParticle(ClientLevel level, Abstract3DParticleEntity boundEntity, SpriteSet animatedSprite) {
      super(level, boundEntity, animatedSprite);
      this.quadSize = 2.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<Abstract3DParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         @NotNull Abstract3DParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz
      ) {
         int targetEntityId = options.getEntityId();
         return level.getEntity(targetEntityId) instanceof Abstract3DParticleEntity boundEntity
            ? new CrimsonSlashParticle(level, boundEntity, this.sprites)
            : null;
      }
   }
}

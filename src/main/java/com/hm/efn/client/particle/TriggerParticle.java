package com.hm.efn.client.particle;

import com.hm.efn.client.particle.rendertype.EFNTextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TriggerParticle extends EFNTextureSheetParticle {
   public TriggerParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.setSize(0.001F, 0.001F);
      this.quadSize = 1.0E-4F;
      this.lifetime = 400;
      this.hasPhysics = false;
      this.pickSprite(spriteSet);
   }

   public static TriggerParticle.Provider provider(SpriteSet spriteSet) {
      return new TriggerParticle.Provider(spriteSet);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new TriggerParticle(worldIn, x, y, z, this.spriteSet);
      }
   }
}

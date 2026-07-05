package com.hm.efn.client.particle;

import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.registry.entries.EpicFightParticles;

@OnlyIn(Dist.CLIENT)
public class ArcHitParticle extends NoRenderParticle {
   public ArcHitParticle(ClientLevel world, double x, double y, double z, double width, double height, double _null) {
      super(world, x, y, z);
      this.x = x + (this.random.nextDouble() - 0.5) * width;
      this.y = y + (this.random.nextDouble() + height) * 0.5;
      this.z = z + (this.random.nextDouble() - 0.5) * width;
      this.level.addParticle((ParticleOptions)EFNParticles.ARC_CUT.get(), this.x, this.y, this.z, 0.0, 0.0, 0.0);
      double d = 0.2F;

      for (int i = 0; i < 6; i++) {
         double particleMotionX = this.random.nextDouble() * d;
         d *= this.random.nextBoolean() ? 1.0 : -1.0;
         double particleMotionZ = this.random.nextDouble() * d;
         d *= this.random.nextBoolean() ? 1.0 : -1.0;
         this.level
            .addParticle((ParticleOptions)EpicFightParticles.BLOOD.get(), this.x, this.y, this.z, particleMotionX, 0.0, particleMotionZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ArcHitParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

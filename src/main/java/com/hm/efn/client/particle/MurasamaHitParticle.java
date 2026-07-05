package com.hm.efn.client.particle;

import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.registry.entries.EpicFightParticles;

public class MurasamaHitParticle extends NoRenderParticle {
   public MurasamaHitParticle(ClientLevel world, double x, double y, double z, double width, double height, double _null) {
      super(world, x, y, z);
      this.x = x + (this.random.nextDouble() - 0.25) * width;
      this.y = y + (this.random.nextDouble() + height) * 0.1 + 0.95;
      this.z = z + (this.random.nextDouble() - 0.25) * width;
      this.level.addParticle((ParticleOptions)EFNParticles.MURASAMA_CUT.get(), this.x, this.y, this.z, 0.0, 0.0, 0.0);
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

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new MurasamaHitParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

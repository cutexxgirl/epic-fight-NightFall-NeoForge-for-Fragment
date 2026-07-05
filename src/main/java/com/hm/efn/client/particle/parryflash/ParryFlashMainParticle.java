package com.hm.efn.client.particle.parryflash;

import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParryFlashMainParticle extends NoRenderParticle {
   public ParryFlashMainParticle(ClientLevel world, double x, double y, double z, double sizeScale, double rotationBias, double _null) {
      super(world, x, y, z);
      double baseWidth = 0.95 * sizeScale;
      double baseHeight = 0.55 * sizeScale;
      this.x = x + (this.random.nextDouble() - 0.5) * baseWidth;
      this.y = y + (this.random.nextDouble() - 0.5) * baseHeight;
      this.z = z + (this.random.nextDouble() - 0.5) * baseWidth;
      this.level
         .addParticle((ParticleOptions)EFNParticles.EFN_PARRY_FLASH_MAIN_RENDER.get(), this.x, this.y, this.z, sizeScale, rotationBias, 0.0);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParryFlashMainParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

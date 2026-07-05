package com.hm.efn.client.particle.DMC;

import com.guhao.vix.util.RenderUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class JudgementCutBlueParticle extends NoRenderParticle {
   public JudgementCutBlueParticle(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.lifetime = 10;
   }

   public boolean shouldCull() {
      return false;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      }

      for (int i = 0; i < 3; i++) {
         float r = Mth.nextFloat(this.random, 5.0F, 9.0F);
         float theta = Mth.nextFloat(this.random, 0.0F, 360.0F);
         float beta;
         if (this.random.nextFloat() < 0.7F) {
            beta = Mth.nextFloat(this.random, 30.0F, 160.0F);
         } else {
            beta = Mth.nextFloat(this.random, 60.0F, 100.0F);
         }

         float r2 = 10.0F;
         boolean isHorizontal = beta >= 60.0F && beta <= 120.0F;
         float thetaSpread = isHorizontal ? 90.0F : 60.0F;
         float betaSpread = isHorizontal ? 40.0F : 60.0F;
         float theta2 = Mth.nextFloat(this.random, 180.0F + theta - thetaSpread, 180.0F + theta + thetaSpread);
         float beta2 = Mth.nextFloat(this.random, 180.0F + beta - betaSpread, 180.0F + beta + betaSpread);
         theta = (float)(theta / 180.0F * Math.PI);
         beta = (float)(beta / 180.0F * Math.PI);
         theta2 = (float)(theta2 / 180.0F * Math.PI);
         beta2 = (float)(beta2 / 180.0F * Math.PI);
         float scale = 0.15F;
         double sr = r * Math.sin(beta);
         double sx = sr * Math.sin(theta) * scale;
         double sy = r * Math.cos(beta) * scale;
         double sz = sr * Math.cos(theta) * scale;
         double er = r2 * Math.sin(beta2);
         double ex = er * Math.sin(theta2) * scale;
         double ey = r2 * Math.cos(beta2) * scale;
         double ez = er * Math.cos(theta2) * scale;
         RenderUtils.AddParticle(
            this.level, new JCBladeBlueTrail(this.level, sx + this.x, sy + this.y, sz + this.z, -ex - sx, ey - sy, -ez - sz)
         );
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new JudgementCutBlueParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

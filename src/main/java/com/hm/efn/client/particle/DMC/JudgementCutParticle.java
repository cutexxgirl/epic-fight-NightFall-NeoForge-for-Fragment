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

public class JudgementCutParticle extends NoRenderParticle {
   public JudgementCutParticle(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.lifetime = 27;
   }

   public boolean shouldCull() {
      return false;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      }

      for (int i = 0; i < 2; i++) {
         float r = Mth.nextFloat(this.random, 5.0F, 8.0F);
         float theta = Mth.nextFloat(this.random, 0.0F, 360.0F);
         float beta = Mth.nextFloat(this.random, 45.0F, 80.0F);
         float r2 = 8.0F;
         float theta2 = Mth.nextFloat(this.random, 180.0F + theta - 45.0F, 180.0F + theta + 45.0F);
         float beta2 = Mth.nextFloat(this.random, 180.0F + beta - 20.0F, 180.0F + beta + 20.0F);
         theta = (float)(theta / 180.0F * Math.PI);
         beta = (float)(beta / 180.0F * Math.PI);
         theta2 = (float)(theta2 / 180.0F * Math.PI);
         beta2 = (float)(beta2 / 180.0F * Math.PI);
         float scale = 2.45F;
         double sr = r * Math.sin(beta);
         double sx = sr * Math.sin(theta) * scale;
         double sy = r * Math.cos(beta) * scale;
         double sz = sr * Math.cos(theta) * scale;
         double er = r2 * Math.sin(beta2);
         double ex = er * Math.sin(theta2) * scale;
         double ey = r2 * Math.cos(beta2) * scale;
         double ez = er * Math.cos(theta2) * scale;
         RenderUtils.AddParticle(
            this.level, new JCBladeTrail(this.level, sx + this.x, sy + this.y + 1.2, sz + this.z, -ex - sx, ey - sy, -ez - sz)
         );
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new JudgementCutParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

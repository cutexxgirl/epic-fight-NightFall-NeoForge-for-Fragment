package com.hm.efn.client.particle.mortalblade;

import com.hm.efn.client.particle.efnparticletype.EFNParticleRenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MortalBladeChargeParticle extends TextureSheetParticle {
   private final boolean isBlackParticle;
   private final float baseSize;

   MortalBladeChargeParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean isBlack) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed);
      this.setSize(0.01F, 0.01F);
      this.pickSprite(sprites);
      this.isBlackParticle = isBlack;
      this.baseSize = (this.random.nextFloat() * 0.05F + 0.03F) * 0.8F;
      this.quadSize = this.baseSize;
      this.lifetime = (int)(8.0 / (Math.random() * 0.6 + 0.4));
      this.hasPhysics = false;
      this.friction = 0.9F;
      this.gravity = -0.02F;
      if (isBlack) {
         this.setColor(0.2F, 0.05F, 0.05F);
      } else {
         this.setColor(1.0F, 0.05F, 0.05F);
      }

      this.alpha = 0.8F;
   }

   public ParticleRenderType getRenderType() {
      return EFNParticleRenderTypes.PARTICLE_SHEET_GLOWING_ADAPTIVE;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public void tick() {
      super.tick();
      this.quadSize = this.baseSize * (0.8F + this.random.nextFloat() * 0.4F);
      this.alpha = 0.6F + this.random.nextFloat() * 0.3F;
      if (this.random.nextInt(3) == 0) {
         this.xd = this.xd + (this.random.nextFloat() - 0.5F) * 0.002F;
         this.zd = this.zd + (this.random.nextFloat() - 0.5F) * 0.002F;
      }

      if (this.age > this.lifetime - 5) {
         this.alpha = (this.lifetime - this.age) / 5.0F;
      }

      if (!this.isBlackParticle && this.age < this.lifetime * 0.7F) {
         float progress = this.age / (this.lifetime * 0.7F);
         this.rCol = 0.9F + progress * 0.1F;
         this.gCol = 0.1F - progress * 0.05F;
         this.bCol = 0.1F - progress * 0.05F;
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static class BlackProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public BlackProvider(SpriteSet sprites) {
         this.sprite = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         RandomSource random = level.random;
         double dx = (random.nextDouble() - 0.5) * 0.005;
         double dy = random.nextDouble() * 0.02 + 0.01;
         double dz = (random.nextDouble() - 0.5) * 0.005;
         return new MortalBladeChargeParticle(level, this.sprite, x, y, z, dx, dy, dz, true);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class RedProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public RedProvider(SpriteSet sprites) {
         this.sprite = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         RandomSource random = level.random;
         double dx = (random.nextDouble() - 0.5) * 0.008;
         double dy = random.nextDouble() * 0.03 + 0.015;
         double dz = (random.nextDouble() - 0.5) * 0.008;
         return new MortalBladeChargeParticle(level, this.sprite, x, y, z, dx, dy, dz, false);
      }
   }
}

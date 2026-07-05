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
public class MortalBladeParticle extends TextureSheetParticle {
   private float baseSize;
   private float pulseSpeed;

   MortalBladeParticle(ClientLevel pLevel, SpriteSet pSprites, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
      super(pLevel, pX, pY - 0.125, pZ, pXSpeed, pYSpeed, pZSpeed);
      this.setSize(0.03F, 0.03F);
      this.pickSprite(pSprites);
      this.baseSize = (this.random.nextFloat() * 0.1F + 0.15F) * 1.2F;
      this.quadSize = this.baseSize;
      this.lifetime = (int)(14.0 / (Math.random() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
      this.pulseSpeed = this.random.nextFloat() * 0.1F + 0.05F;
      this.setColor(1.0F, 0.15F, 0.15F);
      this.alpha = 1.0F;
   }

   public ParticleRenderType getRenderType() {
      return EFNParticleRenderTypes.PARTICLE_SHEET_GLOWING_MASTER;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public void tick() {
      super.tick();
      float pulse = (float)Math.sin(this.age * this.pulseSpeed) * 0.2F + 1.0F;
      this.quadSize = this.baseSize * pulse;
      float colorPulse = (float)Math.sin(this.age * this.pulseSpeed * 2.0F) * 0.1F + 0.9F;
      this.rCol = 1.0F * colorPulse;
      this.gCol = 0.15F * colorPulse;
      this.bCol = 0.15F * colorPulse;
      this.alpha = 0.8F + (float)Math.sin(this.age * this.pulseSpeed * 1.5) * 0.2F;
      if (this.age > this.lifetime - 10) {
         this.alpha = (this.lifetime - this.age) / 10.0F;
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static class MortalBladeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MortalBladeProvider(SpriteSet pSprites) {
         this.sprite = pSprites;
      }

      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         RandomSource random = pLevel.random;
         double dx = random.nextGaussian() * 8.5E-7;
         double dy = random.nextGaussian() * 8.5E-5;
         double dz = random.nextGaussian() * 8.5E-7;
         MortalBladeParticle particle = new MortalBladeParticle(pLevel, this.sprite, pX, pY, pZ, dx, dy, dz);
         particle.pulseSpeed = random.nextFloat() * 0.15F + 0.05F;
         return particle;
      }
   }
}

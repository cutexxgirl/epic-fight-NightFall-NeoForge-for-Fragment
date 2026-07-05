package com.hm.efn.client.particle.sakuraDance;

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
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SakuraDanceParticle extends TextureSheetParticle {
   private float baseSize;
   private float pulseSpeed;
   private float rotationSpeed;

   protected SakuraDanceParticle(ClientLevel pLevel, SpriteSet pSpriteSet, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
      super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
      this.setSprite(pSpriteSet.get(this.random.nextInt(12), 12));
      float size = this.random.nextBoolean() ? 0.05F : 0.075F;
      this.baseSize = size;
      this.quadSize = size;
      this.setSize(size, size);
      this.lifetime = (int)(16.0 / (Math.random() * 0.6 + 0.4));
      this.hasPhysics = false;
      this.friction = 0.96F;
      this.gravity = 8.0E-5F;
      this.pulseSpeed = this.random.nextFloat() * 0.1F + 0.05F;
      this.rotationSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -40.0 : 40.0);
      this.alpha = 1.0F;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EFNParticleRenderTypes.PARTICLE_SHEET_GLOWING_MASTER;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.roll = this.roll + this.rotationSpeed / 20.0F;
      float pulse = (float)Math.sin(this.age * this.pulseSpeed) * 0.15F + 1.0F;
      this.quadSize = this.baseSize * pulse;
      this.alpha = 0.9F + (float)Math.sin(this.age * this.pulseSpeed * 1.5F) * 0.1F;
      if (this.age > this.lifetime - 10) {
         this.alpha = (this.lifetime - this.age) / 10.0F;
         float shrink = (this.lifetime - this.age) / 10.0F;
         this.quadSize = this.baseSize * pulse * (0.3F + shrink * 0.7F);
      }

      this.move(this.xd, this.yd, this.zd);
      this.xd = this.xd * this.friction;
      this.yd = this.yd * this.friction - this.gravity;
      this.zd = this.zd * this.friction;
   }

   public boolean shouldCull() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static class SakuraDanceProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SakuraDanceProvider(SpriteSet pSprites) {
         this.sprite = pSprites;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         RandomSource random = pLevel.random;
         double dx = (random.nextDouble() - 0.5) * 0.003;
         double dy = (random.nextDouble() - 0.4) * 0.002;
         double dz = (random.nextDouble() - 0.5) * 0.003;
         SakuraDanceParticle particle = new SakuraDanceParticle(pLevel, this.sprite, pX, pY, pZ, dx, dy, dz);
         particle.pulseSpeed = random.nextFloat() * 0.12F + 0.06F;
         return particle;
      }
   }
}

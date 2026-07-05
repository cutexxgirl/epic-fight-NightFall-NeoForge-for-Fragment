package com.hm.efn.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SpriteParticle extends TextureSheetParticle {
   protected int speed;
   protected int spriteCount;

   protected SpriteParticle(
      ClientLevel world,
      double x,
      double y,
      double z,
      SpriteSet spriteSet,
      int spriteCount,
      int speed,
      float width,
      float height,
      float quadSize,
      boolean isRandomRoll
   ) {
      super(world, x, y, z);
      this.setSize(width, height);
      this.quadSize = quadSize;
      this.lifetime = (spriteCount - 1) * speed;
      this.spriteCount = spriteCount;
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.speed = speed;
      this.setSpriteFromAge(spriteSet);
   }

   public boolean shouldCull() {
      return false;
   }

   protected float getV0() {
      int index = this.age / this.speed;
      return super.getV0() + (super.getV1() - super.getV0()) * index / this.spriteCount;
   }

   protected float getV1() {
      int index = this.age / this.speed;
      return super.getV0() + (super.getV1() - super.getV0()) * (index + 1) / this.spriteCount;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class SpriteParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;
      private final int spriteCount;
      private int speed = 1;
      private float width = 1.0F;
      private float height = 1.0F;
      private float quadSize = 2.85F;
      private boolean isRandomRoll = false;
      float yOffset;

      public SpriteParticleProvider(SpriteSet spriteSet, int spriteCount) {
         this.spriteSet = spriteSet;
         this.spriteCount = spriteCount;
      }

      public SpriteParticle.SpriteParticleProvider setSpeed(int speed) {
         this.speed = speed;
         return this;
      }

      public SpriteParticle.SpriteParticleProvider setQuadSize(float quadSize) {
         this.quadSize = quadSize;
         return this;
      }

      public SpriteParticle.SpriteParticleProvider setSize(float height, float width) {
         this.height = height;
         this.width = width;
         return this;
      }

      public SpriteParticle.SpriteParticleProvider setRandomRoll(boolean isRandomRoll) {
         this.isRandomRoll = isRandomRoll;
         return this;
      }

      public SpriteParticle.SpriteParticleProvider setYOffset(float yOffset) {
         this.yOffset = yOffset;
         return this;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         SpriteParticle particle = new SpriteParticle(
            worldIn, x, y + this.yOffset, z, this.spriteSet, this.spriteCount, this.speed, this.width, this.height, this.quadSize, this.isRandomRoll
         );
         if (this.isRandomRoll) {
            Random random = new Random();
            float randomAngle = random.nextFloat() * 360.0F;
            float randomAngleRadians = (float)Math.toRadians(randomAngle);
            particle.roll = randomAngleRadians;
            particle.oRoll = randomAngleRadians;
         } else {
            particle.oRoll = 0.0F;
            particle.roll = 0.0F;
         }

         return particle;
      }
   }
}

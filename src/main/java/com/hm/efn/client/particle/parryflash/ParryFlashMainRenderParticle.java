package com.hm.efn.client.particle.parryflash;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class ParryFlashMainRenderParticle extends HitParticle {
   private final double rotationBias;

   public ParryFlashMainRenderParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = 1.95F;
      this.lifetime = 4;
      Random rand = new Random();
      float angle = (float)Math.toRadians(rand.nextFloat() * 90.0F);
      this.oRoll = angle;
      this.roll = angle;
      this.rotationBias = 0.0;
   }

   public ParryFlashMainRenderParticle(
      ClientLevel world, double x, double y, double z, double sizeScale, double rotationBias, double _null, SpriteSet animatedSprite
   ) {
      super(world, x, y, z, animatedSprite);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = (float)(1.85F * sizeScale);
      this.lifetime = 4;
      this.rotationBias = rotationBias;
      Random rand = new Random();
      float angle;
      if (rotationBias < 0.0) {
         angle = (float)Math.toRadians(-(15.0F + rand.nextFloat() * 75.0F));
      } else if (rotationBias > 0.0) {
         angle = (float)Math.toRadians(15.0F + rand.nextFloat() * 75.0F);
      } else {
         angle = (float)Math.toRadians((rand.nextFloat() - 0.5F) * 90.0F);
      }

      this.oRoll = angle;
      this.roll = angle;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParryFlashMainRenderParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}

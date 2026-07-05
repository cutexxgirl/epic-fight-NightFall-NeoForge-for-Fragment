package com.hm.efn.client.particle;

import com.hm.efn.client.particle.rendertype.EFNHitParticle;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcCutParticle extends EFNHitParticle {
   public ArcCutParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = 1.5F;
      this.lifetime = 4;
      Random rand = new Random();
      float angle = (float)Math.toRadians(rand.nextFloat() * 90.0F);
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
         return new ArcCutParticle(worldIn, x, y, z, this.spriteSet);
      }
   }
}

package com.hm.efn.client.particle.DMC;

import com.google.common.collect.Queues;
import com.guhao.vix.util.RenderUtils;
import com.hm.efn.client.render.EFNRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Queue;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AirWaveParticle extends NoRenderParticle {
   private final float tar_r = 14.0F;
   int count;
   Queue<AirWaveParticle.Wave> waves = Queues.newConcurrentLinkedQueue();

   public AirWaveParticle(ClientLevel level, double x, double y, double z, int waveCount, int lifetime) {
      super(level, x, y, z);
      this.lifetime = lifetime;
      this.count = waveCount;
      this.waves.add(new AirWaveParticle.Wave(14.0F, 0.2F, 30));
   }

   public void tick() {
      if (this.age++ >= this.lifetime && this.waves.isEmpty()) {
         this.remove();
      } else if (this.age < this.lifetime && this.age % 3 == 0) {
         this.waves.add(new AirWaveParticle.Wave(14.0F, 0.2F, 40));
      }

      this.waves.forEach(wave -> {
         wave.tick();
         wave.pushParticle(this.level, this.x, this.y, this.z, this.random);
      });
      this.waves.removeIf(AirWaveParticle.Wave::isEnd);
   }

   public boolean shouldCull() {
      return false;
   }

   public static class AirParticle extends Particle {
      static EFNRenderType.EFNQuadParticleRenderType renderType = EFNRenderType.getRenderTypeByTexture(EFNRenderType.GetTexture("particle/photo2"));
      float alphaO;

      public AirParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, int lifetime) {
         super(level, x, y, z, xd, yd, zd);
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.alphaO = this.alpha;
         this.hasPhysics = false;
         this.lifetime = lifetime;
      }

      public void tick() {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.alphaO = this.alpha;
         if (this.age++ >= this.lifetime) {
            this.remove();
         } else {
            this.x = this.x + this.xd;
            this.y = this.y + this.yd;
            this.z = this.z + this.zd;
         }

         this.alpha = Math.min(0.5F, 0.5F * (this.lifetime - this.age) / this.lifetime);
         this.alpha = Math.max(this.alpha, 0.0F);
         this.setPos(this.x, this.y, this.z);
      }

      float getAlpha(float pt) {
         return Mth.lerp(pt, this.alphaO, this.alpha);
      }

      public void render(VertexConsumer vertexConsumer, Camera camera, float pt) {
         float alp = this.getAlpha(pt);
         float t_ = (this.age % 10 + pt) / 9.0F;
         if (t_ <= 0.5F) {
            t_ = 4.0F * t_ - 1.0F;
         } else {
            t_ = -4.0F * t_ + 3.0F;
         }

         float sz = (0.5F + 0.1F * t_) * alp * 1.8F;
         RenderUtils.RenderQuadFaceOnCamera2(
            vertexConsumer,
            camera,
            (float)Mth.lerp(pt, this.xo, this.x),
            (float)Mth.lerp(pt, this.yo, this.y),
            (float)Mth.lerp(pt, this.zo, this.z),
            this.rCol,
            this.gCol,
            this.bCol,
            alp,
            sz
         );
      }

      @NotNull
      public ParticleRenderType getRenderType() {
         return renderType;
      }

      public boolean shouldCull() {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new AirWaveParticle(worldIn, x, y, z, 2, 5);
      }
   }

   static class Wave {
      float r = 0.2F;
      float rO = 0.2F;
      float targetR;
      float smooth;
      int lifetime;
      int age = 0;

      public Wave(float targetR, float smooth, int lft) {
         this.targetR = targetR;
         this.smooth = smooth;
         this.lifetime = lft;
      }

      public void tick() {
         this.rO = this.r;
         this.r = Mth.lerp(this.smooth, this.r, this.targetR);
         this.age++;
      }

      public boolean isEnd() {
         return this.age >= this.lifetime;
      }

      public void pushParticle(ClientLevel level, double x, double y, double z, RandomSource random) {
         int inter = Math.max(1, (int)((this.r - this.rO) / 0.2F));
         float perR = (this.r - this.rO) / inter;
         float perYAdder = 0.5F / inter;

         for (int j = 0; j < inter; j++) {
            int cnt = Math.max(6, (int)(Math.PI * (this.rO + perR * j) * 2.0 / 0.2));
            double perAng = (Math.PI * 2) / cnt;

            for (int i = 0; i < cnt; i++) {
               double x_ = Math.cos(perAng * i) * (this.rO + perR * j);
               double z_ = Math.sin(perAng * i) * (this.rO + perR * j);
               RenderUtils.AddParticle(
                  level,
                  new AirWaveParticle.AirParticle(
                     level,
                     x_ + x + Mth.nextDouble(random, -0.2, 0.2),
                     y + Mth.nextDouble(random, -0.2, 0.2) + perYAdder * j,
                     z_ + z + Mth.nextDouble(random, -0.2, 0.2),
                     Mth.nextDouble(random, -0.05, 0.05),
                     0.25,
                     Mth.nextDouble(random, -0.05, 0.05),
                     (int)(6.0F / inter * (j + 1))
                  )
               );
            }
         }
      }
   }
}

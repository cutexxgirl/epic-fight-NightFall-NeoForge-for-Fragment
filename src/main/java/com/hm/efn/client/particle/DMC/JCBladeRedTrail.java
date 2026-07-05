package com.hm.efn.client.particle.DMC;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.render.EFNRenderType;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JCBladeRedTrail extends SingleQuadParticle {
   static ResourceLocation texture = EFNRenderType.GetTexture("particle/sparks_red");
   static BloomParticleRenderType renderType = EFNRenderType.getBloomRenderTypeByTexture(texture);
   protected final double X;
   protected final double Y;
   protected final double Z;
   protected float timeOffset = 0.0F;
   private float[][] bezierPoints = new float[4][4];
   private float transitionStart = 0.5F;
   private float baseSize = 0.025F;
   private float sizeMultiplier = 1.25F;
   private boolean hasSpawnedDust = false;
   private int dustCount = 50;
   private float dustSize = 0.75F;

   public JCBladeRedTrail(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.lifetime = 9;
      this.timeOffset = Mth.nextFloat(this.random, 0.0F, 1.0F);
      this.X = x;
      this.Y = y;
      this.Z = z;
      this.xd = rx;
      this.yd = ry;
      this.zd = rz;
      this.initializeBezierPoints();
      this.updateBezierColor(0.0F);
      this.alpha = 0.9F;
   }

   private void initializeBezierPoints() {
      this.bezierPoints[0][0] = 1.8F + Mth.nextFloat(this.random, -0.2F, 0.2F);
      this.bezierPoints[0][1] = 0.25F + Mth.nextFloat(this.random, -0.05F, 0.05F);
      this.bezierPoints[0][2] = 0.15F + Mth.nextFloat(this.random, -0.03F, 0.03F);
      this.bezierPoints[0][3] = 0.95F;
      this.bezierPoints[1][0] = 1.6F;
      this.bezierPoints[1][1] = 0.18F;
      this.bezierPoints[1][2] = 0.12F;
      this.bezierPoints[1][3] = 0.85F;
      this.bezierPoints[2][0] = 0.8F;
      this.bezierPoints[2][1] = 0.08F;
      this.bezierPoints[2][2] = 0.05F;
      this.bezierPoints[2][3] = 0.6F;
      this.bezierPoints[3][0] = 0.4F + Mth.nextFloat(this.random, -0.1F, 0.1F);
      this.bezierPoints[3][1] = 0.05F + Mth.nextFloat(this.random, -0.02F, 0.02F);
      this.bezierPoints[3][2] = 0.03F + Mth.nextFloat(this.random, -0.01F, 0.01F);
      this.bezierPoints[3][3] = 0.3F;
   }

   private float cubicBezier(float t, float p0, float p1, float p2, float p3) {
      float u = 1.0F - t;
      float tt = t * t;
      float uu = u * u;
      float uuu = uu * u;
      float ttt = tt * t;
      return uuu * p0 + 3.0F * uu * t * p1 + 3.0F * u * tt * p2 + ttt * p3;
   }

   private void updateBezierColor(float ageRatio) {
      float t;
      if (ageRatio <= this.transitionStart) {
         t = 0.0F;
      } else {
         t = (ageRatio - this.transitionStart) / (1.0F - this.transitionStart);
         t = this.easeInOutCubic(t);
      }

      this.rCol = this.cubicBezier(t, this.bezierPoints[0][0], this.bezierPoints[1][0], this.bezierPoints[2][0], this.bezierPoints[3][0]);
      this.gCol = this.cubicBezier(t, this.bezierPoints[0][1], this.bezierPoints[1][1], this.bezierPoints[2][1], this.bezierPoints[3][1]);
      this.bCol = this.cubicBezier(t, this.bezierPoints[0][2], this.bezierPoints[1][2], this.bezierPoints[2][2], this.bezierPoints[3][2]);
      this.alpha = this.cubicBezier(t, this.bezierPoints[0][3], this.bezierPoints[1][3], this.bezierPoints[2][3], this.bezierPoints[3][3]);
      this.rCol = Math.min(2.0F, Math.max(0.3F, this.rCol));
      this.gCol = Math.min(0.3F, Math.max(0.0F, this.gCol));
      this.bCol = Math.min(0.2F, Math.max(0.0F, this.bCol));
      this.alpha = Math.max(0.2F, Math.min(1.0F, this.alpha));
   }

   private float easeInOutCubic(float t) {
      return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 3.0) / 2.0F;
   }

   private void spawnDustParticles() {
      if (!this.hasSpawnedDust && this.level != null) {
         Vector3f dir = new Vector3f((float)this.xd, (float)this.yd, (float)this.zd);
         float dirLength = dir.length();
         if (!(dirLength < 0.001F)) {
            dir.normalize();
            Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
            if (Math.abs(dir.dot(up)) > 0.99F) {
               up = new Vector3f(1.0F, 0.0F, 0.0F);
            }

            Vector3f right = new Vector3f();
            dir.cross(up, right);
            right.normalize();
            float currentSize = this.baseSize * this.sizeMultiplier;

            for (int i = 0; i < this.dustCount; i++) {
               float alongTrail = Mth.nextFloat(this.random, 0.1F, 0.9F);
               float acrossTrail = Mth.nextFloat(this.random, -0.8F, 0.8F);
               Vector3f basePos = new Vector3f(dir);
               basePos.mul(alongTrail * dirLength * 0.7F);
               Vector3f widthOffset = new Vector3f(right);
               widthOffset.mul(acrossTrail * currentSize);
               float heightVariation = Mth.nextFloat(this.random, -0.1F, 0.1F);
               Vector3f heightOffset = new Vector3f(up);
               heightOffset.mul(heightVariation * currentSize);
               double posX = this.X + basePos.x + widthOffset.x + heightOffset.x;
               double posY = this.Y + basePos.y + widthOffset.y + heightOffset.y;
               double posZ = this.Z + basePos.z + widthOffset.z + heightOffset.z;
               double speedX = dir.x * 0.03F + widthOffset.x * 0.02F + Mth.nextFloat(this.random, -0.005F, 0.005F);
               double speedY = dir.y * 0.03F + heightOffset.y * 0.02F + Mth.nextFloat(this.random, -0.005F, 0.005F);
               double speedZ = dir.z * 0.03F + widthOffset.z * 0.02F + Mth.nextFloat(this.random, -0.005F, 0.005F);
               Vector3f particleColor;
               float particleSize;
               if (alongTrail < 0.6F) {
                  float redIntensity = 1.0F - alongTrail / 0.6F * 0.5F;
                  particleColor = new Vector3f(0.9F + redIntensity * 0.3F, 0.15F + redIntensity * 0.1F, 0.1F + redIntensity * 0.05F);
                  particleSize = this.dustSize * (0.7F + redIntensity * 0.6F);
               } else {
                  float blackIntensity = (alongTrail - 0.6F) / 0.4F;
                  particleColor = new Vector3f(
                     0.4F * (1.0F - blackIntensity) + 0.1F * blackIntensity,
                     0.08F * (1.0F - blackIntensity) + 0.02F * blackIntensity,
                     0.05F * (1.0F - blackIntensity) + 0.01F * blackIntensity
                  );
                  particleSize = this.dustSize * (0.5F - blackIntensity * 0.3F);
               }

               float colorVariation = 0.1F;
               particleColor.x = particleColor.x + Mth.nextFloat(this.random, -colorVariation, colorVariation);
               particleColor.y = particleColor.y + Mth.nextFloat(this.random, -colorVariation * 0.5F, colorVariation * 0.5F);
               particleColor.z = particleColor.z + Mth.nextFloat(this.random, -colorVariation * 0.3F, colorVariation * 0.3F);
               particleColor.x = Math.max(0.1F, Math.min(1.5F, particleColor.x));
               particleColor.y = Math.max(0.0F, Math.min(0.3F, particleColor.y));
               particleColor.z = Math.max(0.0F, Math.min(0.2F, particleColor.z));
               DustParticleOptions dust = new DustParticleOptions(particleColor, particleSize);
               this.level.addParticle(dust, posX, posY, posZ, speedX, speedY, speedZ);
               if (alongTrail > 0.4F) {
                  Vector3f extraBlackColor = new Vector3f(
                     0.15F + Mth.nextFloat(this.random, -0.05F, 0.05F),
                     0.03F + Mth.nextFloat(this.random, -0.01F, 0.01F),
                     0.02F + Mth.nextFloat(this.random, -0.01F, 0.01F)
                  );
                  float extraSize = this.dustSize * 0.4F;
                  double extraX = posX + Mth.nextFloat(this.random, -0.05F, 0.05F);
                  double extraY = posY + Mth.nextFloat(this.random, -0.03F, 0.03F);
                  double extraZ = posZ + Mth.nextFloat(this.random, -0.05F, 0.05F);
                  DustParticleOptions extraDust = new DustParticleOptions(extraBlackColor, extraSize);
                  this.level.addParticle(extraDust, extraX, extraY, extraZ, speedX * 0.5, speedY * 0.5, speedZ * 0.5);
               }
            }

            this.hasSpawnedDust = true;
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }

   protected float getU0() {
      return 0.0F;
   }

   protected float getU1() {
      return 0.0F;
   }

   protected float getV0() {
      return 0.0F;
   }

   protected float getV1() {
      return 0.0F;
   }

   public void tick() {
      if (this.age++ > this.lifetime) {
         this.remove();
      }

      if (this.age == 1) {
         this.spawnDustParticles();
      }

      float ageRatio = (float)this.age / this.lifetime;
      this.updateBezierColor(ageRatio);
   }

   public void render(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         renderType.callPipeline();
         float at = this.age + pt;
         if (!(at < this.timeOffset) && !(at > this.lifetime + this.timeOffset)) {
            float t = Math.min(1.0F, at / this.lifetime * 2.5F);
            Vec3 vec3 = camera.getPosition();
            float f = (float)(this.X - vec3.x());
            float f1 = (float)(this.Y - vec3.y());
            float f2 = (float)(this.Z - vec3.z());
            Vector3f right = new Vector3f(f, f1, f2);
            Vector3f dir = new Vector3f((float)this.xd, (float)this.yd, (float)this.zd);
            dir.mul(t);
            right.cross(dir);
            right.normalize();
            float _t = (float)Math.sqrt(Math.min(1.0F, (this.lifetime - at) / this.lifetime * 2.5F));
            float size = this.baseSize * this.sizeMultiplier * _t;
            right.mul(size);
            Vector3f left = new Vector3f(right);
            left.mul(-1.0F);
            Vector3f[] points = new Vector3f[]{new Vector3f(right), new Vector3f(left), new Vector3f(right), new Vector3f(right)};
            points[2].add(dir);
            points[3].add(dir);

            for (int i = 0; i < 4; i++) {
               Vector3f vector3f = points[i];
               vector3f.add(f, f1, f2);
            }

            float u0 = 0.0F;
            float u1 = 1.0F;
            float v0 = 0.0F;
            float v1 = 1.0F;
            int light = 15728880;
            buffer.addVertex(points[0].x(), points[0].y(), points[0].z())
               .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
               .setUv(u1, v1)
               .setLight(light)
               ;
            buffer.addVertex(points[1].x(), points[1].y(), points[1].z())
               .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
               .setUv(u1, v0)
               .setLight(light)
               ;
            buffer.addVertex(points[2].x(), points[2].y(), points[2].z())
               .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
               .setUv(u0, v0)
               .setLight(light)
               ;
            buffer.addVertex(points[3].x(), points[3].y(), points[3].z())
               .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
               .setUv(u0, v1)
               .setLight(light)
               ;
         }
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return renderType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new JCBladeRedTrail(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

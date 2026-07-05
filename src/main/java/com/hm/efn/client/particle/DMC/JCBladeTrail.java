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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JCBladeTrail extends SingleQuadParticle {
   static ResourceLocation texture = EFNRenderType.GetTexture("particle/sparks");
   static BloomParticleRenderType renderType = EFNRenderType.getBloomRenderTypeByTexture(texture);
   protected final double X;
   protected final double Y;
   protected final double Z;
   protected float timeOffset = 0.0F;

   public JCBladeTrail(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.lifetime = 9;
      this.timeOffset = Mth.nextFloat(this.random, 0.0F, 1.0F);
      this.X = x;
      this.Y = y;
      this.Z = z;
      this.xd = rx;
      this.yd = ry;
      this.zd = rz;
      this.rCol = 0.55F;
      this.gCol = 0.6902F;
      this.bCol = 1.0F;
      this.alpha = 0.8F;
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
            right.mul(0.015F * _t);
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
         return new JCBladeTrail(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}

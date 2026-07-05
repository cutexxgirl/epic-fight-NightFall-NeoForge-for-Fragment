package com.hm.efn.client.particle.DMC;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.util.MathUtils;
import com.hm.efn.client.render.EFNRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpaceBrokenEndParticle extends Particle {
   private final float rotation;
   private final Vector3f vector = new Vector3f(
      Mth.nextFloat(this.random, -1.0F, 1.0F), Mth.nextFloat(this.random, -1.0F, 1.0F), Mth.nextFloat(this.random, -1.0F, 1.0F)
   );
   private final float rotSpeed;
   private final int style;

   public SpaceBrokenEndParticle(ClientLevel level, double x, double y, double z, int lifetime) {
      super(level, x, y, z);
      this.vector.normalize();
      this.rotation = Mth.nextFloat(this.random, 0.0F, 360.0F);
      this.rotSpeed = Mth.nextFloat(this.random, 5.0F, 10.0F);
      this.lifetime = lifetime;
      this.style = this.random.nextInt(4);
      this.gravity = Mth.nextFloat(this.random, 1.0F, 2.0F);
      this.yd = -0.1F;
   }

   public void tick() {
      super.tick();
      if (this.age > 1 && this.y - this.yo > -0.1F) {
         this.remove();
      }
   }

   public void render(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.SpaceBrokenEnd.callPipeline();
         float agef = this.age + pt;
         Vec3 camPos = camera.getPosition();
         float f = (float)(Mth.lerp(pt, this.xo, this.x) - camPos.x());
         float f1 = (float)(Mth.lerp(pt, this.yo, this.y) - camPos.y());
         float f2 = (float)(Mth.lerp(pt, this.zo, this.z) - camPos.z());
         Vector3f[] avector3f = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         Vector3f normal = new Vector3f(0.0F, 0.0F, 1.0F);
         float f4 = 1.2F;
         Quaternionf quaternion = new Quaternionf(new AxisAngle4f(MathUtils.toDegrees(this.rotation + this.rotSpeed * agef), this.vector));

         for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
         }

         normal.rotate(quaternion);
         int c = this.style % 2;
         int r = this.style / 2;
         float f7 = c * 0.5F;
         float f8 = (c + 1) * 0.5F;
         float f5 = r * 0.5F;
         float f6 = (r + 1) * 0.5F;
         int lightColor = 15728880;
         Vector3f camNormal = new Vector3f(camera.getLookVector());
         camNormal.normalize();
         normal.normalize();
         float offset = Math.abs(camNormal.dot(normal));
         float camYaw = camera.getYRot();
         camYaw = (camYaw % 360.0F + 360.0F + 180.0F * offset) % 360.0F / 360.0F;
         camYaw = camYaw < 0.5 ? camYaw * 2.0F : -camYaw * 2.0F + 2.0F;
         float camPitch = camera.getXRot();
         camPitch /= 90.0F;
         camPitch = camPitch > 0.0F ? camPitch : -camPitch;
         buffer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z())
            .setColor(offset, camYaw, camPitch, 1.0F)
            .setUv(f8, f6)
            .setLight(lightColor)
            ;
         buffer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z())
            .setColor(offset, camYaw, camPitch, 1.0F)
            .setUv(f8, f5)
            .setLight(lightColor)
            ;
         buffer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z())
            .setColor(offset, camYaw, camPitch, 1.0F)
            .setUv(f7, f5)
            .setLight(lightColor)
            ;
         buffer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z())
            .setColor(offset, camYaw, camPitch, 1.0F)
            .setUv(f7, f6)
            .setLight(lightColor)
            ;
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EFNRenderType.SpaceBrokenEnd;
   }
}

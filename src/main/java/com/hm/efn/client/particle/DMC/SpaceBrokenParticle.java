package com.hm.efn.client.particle.DMC;

import com.guhao.vix.client.NoTextureJsonModel.Triangle;
import com.guhao.vix.client.NoTextureJsonModel.vec3f;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.util.MathUtils;
import com.guhao.vix.util.RenderUtils;
import com.hm.efn.client.model.ACGModel;
import com.hm.efn.client.render.EFNRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpaceBrokenParticle extends Particle {
   final int layer;
   float yaw;

   public SpaceBrokenParticle(ClientLevel level, double x, double y, double z, float yaw, int lifetime, int layer) {
      super(level, x, y, z);
      this.hasPhysics = false;
      this.lifetime = lifetime;
      this.layer = layer;
      this.yaw = yaw;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      }
   }

   public void render(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         Vec3 vec3 = camera.getPosition();
         float f = (float)(this.x - vec3.x());
         float f1 = (float)(this.y - vec3.y() + (this.layer == 0 ? 0.2F : 0.4F));
         float f2 = (float)(this.z - vec3.z());
         float u0 = 0.0F;
         float u1 = 1.0F;
         float v0 = 0.0F;
         float v1 = 1.0F;
         int light = 15728880;
         float sss = this.layer == 0 ? 1.3F : 1.4F;
         Vector3f camNormal = new Vector3f(camera.getLookVector());
         camNormal.normalize();
         float camYaw = camera.getYRot() + (this.layer == 0 ? 0 : 45);
         float camPitch = camera.getXRot();
         camPitch /= 90.0F;
         camPitch = camPitch > 0.0F ? camPitch : -camPitch;
         Quaternionf rot = MathUtils.fromEuler(this.layer == 0 ? 0 : 120, (float)((this.yaw + 30.0F) / 180.0F * Math.PI) + (this.layer == 0 ? 0 : 75), 0.0F);
         rot.mul(this.layer == 0 ? MathUtils.Quat_One : MathUtils.fromEuler(45.0F, 90.0F, 45.0F));

         for (int index = 0; index < ACGModel.SpaceBrokenModel.Face.size(); index++) {
            Triangle triangle = (Triangle)ACGModel.SpaceBrokenModel.Face.get(index);
            Vector3f vertex1 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.x - 1)).toBugJumpFormat();
            Vector3f vertex2 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.y - 1)).toBugJumpFormat();
            Vector3f vertex3 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.z - 1)).toBugJumpFormat();
            vertex1.rotate(rot);
            vertex2.rotate(rot);
            vertex3.rotate(rot);
            vertex1.mul(sss);
            vertex2.mul(sss);
            vertex3.mul(sss);
            vertex1.add(f, f1, f2);
            vertex2.add(f, f1, f2);
            vertex3.add(f, f1, f2);
            Vector3f col_normal = triangle.Normal.toBugJumpFormat();
            col_normal.rotate(rot);
            col_normal.normalize();
            float offset = Math.abs(camNormal.dot(col_normal));
            float ya = (camYaw % 360.0F + 360.0F + 180.0F * offset) % 360.0F / 360.0F;
            ya = ya < 0.5 ? ya * 2.0F : -ya * 2.0F + 2.0F;
            buffer.addVertex(vertex1.x(), vertex1.y(), vertex1.z()).setColor(offset, ya, camPitch, 1.0F).setUv(u1, v0).setLight(light);
            buffer.addVertex(vertex2.x(), vertex2.y(), vertex2.z()).setColor(offset, ya, camPitch, 1.0F).setUv(u0, v0).setLight(light);
            buffer.addVertex(vertex3.x(), vertex3.y(), vertex3.z()).setColor(offset, ya, camPitch, 1.0F).setUv(u0, v1).setLight(light);
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EFNRenderType.SpaceBroken1;
   }

   public void remove() {
      super.remove();
      float lastAng = Mth.nextFloat(this.random, 0.0F, 360.0F);

      for (int i = 0; i < 16; i++) {
         lastAng = Mth.nextFloat(this.random, 120.0F + lastAng - 45.0F, 120.0F + lastAng + 45.0F);
         float r = Mth.nextFloat(this.random, 0.0F, 5.0F);
         double sx = Math.sin(lastAng / 180.0F * Math.PI) * r;
         double sy = Mth.nextFloat(this.random, 1.0F, 6.0F);
         double sz = Math.cos(lastAng / 180.0F * Math.PI) * r;
         double var10003 = sx + this.x;
         double var10004 = sy + this.y + 1.0;
         double var10005 = sz + this.z;
         SpaceBrokenEndParticle spaceBrokenEndParticle = new SpaceBrokenEndParticle(Minecraft.getInstance().level, var10003, var10004, var10005, 30);
         RenderUtils.AddParticle(Minecraft.getInstance().level, spaceBrokenEndParticle);
      }
   }
}

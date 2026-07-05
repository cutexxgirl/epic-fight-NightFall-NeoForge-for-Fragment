package com.hm.efn.client.particle.attach;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class WeaponAttachParticle extends TextureSheetParticle {
   public static final Map<Integer, List<WeaponAttachParticle>> TRACKED_PARTICLES = new ConcurrentHashMap<>();
   private final LivingEntityPatch<?> targetPatch;
   private final Vec3f localOffset;
   private final Vec3f localRotation;
   private final String jointName;
   private final SpriteSet spriteSet;
   private final int spriteFrames = 19;
   private final boolean isInfinite;
   private final int maxLifetime;
   private int customAge = 0;
   private final int boundEntityId;

   public WeaponAttachParticle(
      ClientLevel level, LivingEntityPatch<?> targetPatch, String jointName, Vec3f localOffset, Vec3f localRotation, SpriteSet spriteSet, int lifetime
   ) {
      super(
         level,
         ((LivingEntity)targetPatch.getOriginal()).getX(),
         ((LivingEntity)targetPatch.getOriginal()).getY(),
         ((LivingEntity)targetPatch.getOriginal()).getZ()
      );
      this.targetPatch = targetPatch;
      this.jointName = jointName;
      this.localOffset = localOffset;
      this.localRotation = localRotation;
      this.spriteSet = spriteSet;
      this.isInfinite = lifetime <= 0;
      this.maxLifetime = lifetime;
      this.lifetime = Integer.MAX_VALUE;
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.quadSize = 0.6F;
      this.boundEntityId = ((LivingEntity)targetPatch.getOriginal()).getId();
      this.setSprite(this.spriteSet.get(0, 19));
      TRACKED_PARTICLES.computeIfAbsent(this.boundEntityId, k -> Collections.synchronizedList(new ArrayList<>())).add(this);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.targetPatch != null && ((LivingEntity)this.targetPatch.getOriginal()).isAlive() && this.targetPatch.getArmature() != null) {
         if (!this.isInfinite) {
            if (this.customAge++ >= this.maxLifetime) {
               this.remove();
               return;
            }
         } else {
            this.customAge++;
         }

         Joint targetJoint = this.targetPatch.getArmature().searchJointByName(this.jointName);
         if (targetJoint != null) {
            Vec3 worldPos = getJointWorldPos(this.targetPatch, targetJoint, this.localOffset, 1.0F);
            this.setPos(worldPos.x, worldPos.y, worldPos.z);
         }

         this.setSprite(this.spriteSet.get(this.customAge % 19, 19));
      } else {
         this.remove();
      }
   }

   public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
      if (this.targetPatch != null && ((LivingEntity)this.targetPatch.getOriginal()).isAlive() && this.targetPatch.getArmature() != null) {
         Joint targetJoint = this.targetPatch.getArmature().searchJointByName(this.jointName);
         if (targetJoint != null) {
            LivingEntity entity = (LivingEntity)this.targetPatch.getOriginal();
            OpenMatrix4f transformMatrix = this.targetPatch
               .getArmature()
               .getBoundTransformFor(this.targetPatch.getAnimator().getPose(partialTicks), targetJoint);
            transformMatrix.translate(this.localOffset);
            if (this.localRotation.x != 0.0F) {
               OpenMatrix4f rotX = new OpenMatrix4f().rotate((float)Math.toRadians(this.localRotation.x), new Vec3f(1.0F, 0.0F, 0.0F));
               OpenMatrix4f.mul(transformMatrix, rotX, transformMatrix);
            }

            if (this.localRotation.y != 0.0F) {
               OpenMatrix4f rotY = new OpenMatrix4f().rotate((float)Math.toRadians(this.localRotation.y), new Vec3f(0.0F, 1.0F, 0.0F));
               OpenMatrix4f.mul(transformMatrix, rotY, transformMatrix);
            }

            if (this.localRotation.z != 0.0F) {
               OpenMatrix4f rotZ = new OpenMatrix4f().rotate((float)Math.toRadians(this.localRotation.z), new Vec3f(0.0F, 0.0F, 1.0F));
               OpenMatrix4f.mul(transformMatrix, rotZ, transformMatrix);
            }

            float yRot = Mth.lerp(partialTicks, this.targetPatch.getYRotO(), this.targetPatch.getYRot());
            OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(yRot + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
            OpenMatrix4f worldMatrix = new OpenMatrix4f();
            OpenMatrix4f.mul(rotation, transformMatrix, worldMatrix);
            double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            worldMatrix.m30 += (float)ex;
            worldMatrix.m31 += (float)ey;
            worldMatrix.m32 += (float)ez;
            Vec3 camPos = camera.getPosition();
            float cx = (float)camPos.x;
            float cy = (float)camPos.y;
            float cz = (float)camPos.z;
            float half = this.quadSize * 0.5F;
            Vec3f[] vertices = new Vec3f[]{
               new Vec3f(half, -half, 0.0F), new Vec3f(half, half, 0.0F), new Vec3f(-half, half, 0.0F), new Vec3f(-half, -half, 0.0F)
            };
            float u0 = this.getU0();
            float u1 = this.getU1();
            float v0 = this.getV0();
            float v1 = this.getV1();
            int light = this.getLightColor(partialTicks);
            this.renderQuad(buffer, vertices, worldMatrix, cx, cy, cz, u1, u0, v1, v0, light, false);
            this.renderQuad(buffer, vertices, worldMatrix, cx, cy, cz, u1, u0, v1, v0, light, true);
         }
      }
   }

   private void renderQuad(
      VertexConsumer buffer, Vec3f[] verts, OpenMatrix4f mat, float cx, float cy, float cz, float u1, float u0, float v1, float v0, int light, boolean reverse
   ) {
      int[] indices = reverse ? new int[]{3, 2, 1, 0} : new int[]{0, 1, 2, 3};

      for (int i = 0; i < 4; i++) {
         Vec3f v = verts[indices[i]];
         float wx = mat.m00 * v.x + mat.m10 * v.y + mat.m20 * v.z + mat.m30;
         float wy = mat.m01 * v.x + mat.m11 * v.y + mat.m21 * v.z + mat.m31;
         float wz = mat.m02 * v.x + mat.m12 * v.y + mat.m22 * v.z + mat.m32;
         float rx = wx - cx;
         float ry = wy - cy;
         float rz = wz - cz;
         float u = i != 0 && i != 1 ? u0 : u1;
         float texV = i != 1 && i != 2 ? v1 : v0;
         buffer.addVertex(rx, ry, rz).setUv(u, texV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
      }
   }

   public void remove() {
      super.remove();
      List<WeaponAttachParticle> list = TRACKED_PARTICLES.get(this.boundEntityId);
      if (list != null) {
         list.remove(this);
         if (list.isEmpty()) {
            TRACKED_PARTICLES.remove(this.boundEntityId, list);
         }
      }
   }

   public static void clearFor(int entityId) {
      List<WeaponAttachParticle> list = TRACKED_PARTICLES.remove(entityId);
      if (list != null) {
         synchronized (list) {
            for (WeaponAttachParticle p : list) {
               p.superRemoveOnly();
            }

            list.clear();
         }
      }
   }

   private void superRemoveOnly() {
      super.remove();
   }

   public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint, Vec3f offset, float partialTicks) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(entityPatch.getAnimator().getPose(partialTicks), joint);
      transformMatrix.translate(offset);
      float yRot = Mth.lerp(partialTicks, entityPatch.getYRotO(), entityPatch.getYRot());
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(yRot + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      double x = Mth.lerp(partialTicks, entity.xo, entity.getX());
      double y = Mth.lerp(partialTicks, entity.yo, entity.getY());
      double z = Mth.lerp(partialTicks, entity.zo, entity.getZ());
      return new Vec3(transformMatrix.m30 + x, transformMatrix.m31 + y, transformMatrix.m32 + z);
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }
}

package com.hm.efn.client.particle.DMC;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DynamicEntityAfterImgParticle extends CustomModelParticle<SkinnedMesh> {
   private static final Quaternionf IDENTITY_QUATERNION = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
   protected final EntitySnapshot<?> entitySnapshot;
   protected final OpenMatrix4f[] poseMatrices;
   protected final Matrix4f modelMatrix;
   protected float alphaO;

   public DynamicEntityAfterImgParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifetime,
      AssetAccessor<SkinnedMesh> particleMesh,
      OpenMatrix4f[] matrices,
      Matrix4f modelMatrix,
      EntitySnapshot<?> entitySnapshot
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh);
      this.poseMatrices = matrices;
      this.modelMatrix = modelMatrix;
      this.lifetime = lifetime;
      this.entitySnapshot = entitySnapshot;
      this.hasPhysics = false;
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 0.5F;
      this.alphaO = 0.5F;
      this.yaw = (float)(Vec3f.getAngleBetween(new Vec3f((float)xd, 0.0F, (float)(-zd)), Vec3f.X_AXIS) / Math.PI * 180.0);
      if (zd > 0.0) {
         this.yaw = -this.yaw;
      }

      this.yaw += 90.0F;
      this.yawO = this.yaw;
      this.pitch = -((float)(Vec3f.getAngleBetween(new Vec3f((float)xd, 0.0F, (float)(-zd)), new Vec3f((float)xd, (float)yd, (float)(-zd))) / Math.PI * 180.0));
      this.pitch += 10.0F;
      this.pitchO = this.pitch;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
   }

   @NotNull
   public static DynamicEntityAfterImgParticle create(
      LivingEntityPatch<?> entitypatch,
      AssetAccessor<? extends StaticAnimation> animation,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifeTime,
      float animTime
   ) {
      PatchedEntityRenderer renderer = RenderEngine.getInstance().getEntityRenderer(entitypatch.getOriginal());
      Armature armature = entitypatch.getArmature();
      Pose pose = ((StaticAnimation)animation.get()).getPoseByTime(entitypatch, animTime, 0.0F);
      renderer.setJointTransforms(entitypatch, armature, pose, 1.0F);
      OpenMatrix4f[] matrices = armature.getPoseAsTransformMatrix(pose, true);
      AssetAccessor mesh = renderer.getMeshProvider(entitypatch);
      EntitySnapshot<?> snapshot = new EntitySnapshot(entitypatch);
      Matrix4f modelMat = OpenMatrix4f.exportToMojangMatrix(snapshot.getModelMatrix());
      return new DynamicEntityAfterImgParticle(
         (ClientLevel)((LivingEntity)entitypatch.getOriginal()).level(), x, y, z, xd, yd, zd, lifeTime, mesh, matrices, modelMat, snapshot
      );
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.pitchO = this.pitch;
         this.yawO = this.yaw;
         this.oRoll = this.roll;
         this.scaleO = this.scale;
         this.alphaO = this.alpha;
         this.x = this.x + this.xd;
         this.y = this.y + this.yd;
         this.z = this.z + this.zd;
      }
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
      int lightColor = this.getLightColor(partialTicks);
      PoseStack poseStack = new PoseStack();
      this.setupPoseStack(poseStack, camera, partialTicks);
      BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
      this.entitySnapshot
         .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), DrawingFunction.POSITION_TEX, lightColor, 1.0F);
      buffers.endLastBatch();
      this.entitySnapshot
         .renderTextured(
            poseStack,
            buffers,
            EpicFightRenderTypes::entityAfterimageTranslucent,
            DrawingFunction.NEW_ENTITY,
            lightColor,
            this.rCol,
            this.gCol,
            this.bCol,
            alpha
         );
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageTranslucent(), DrawingFunction.NEW_ENTITY, lightColor, alpha);
      buffers.endLastBatch();
      this.revert(poseStack);
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      poseStack.pushPose();
      Vec3 cameraPosition = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPosition.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPosition.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPosition.z());
      poseStack.translate(x, y, z);
      Quaternionf rotation = new Quaternionf(IDENTITY_QUATERNION);
      float roll = Mth.rotLerp(partialTicks, this.oRoll, this.roll);
      float pitch = Mth.rotLerp(partialTicks, this.pitchO, this.pitch);
      float yaw = Mth.rotLerp(partialTicks, this.yawO, this.yaw);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F - yaw));
      rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
      rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
      poseStack.mulPose(rotation);
      poseStack.mulPose(this.modelMatrix);
      float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
      poseStack.translate(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.scale(scale, scale, scale);
      poseStack.translate(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
   }

   protected void revert(PoseStack poseStack) {
      poseStack.popPose();
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EpicFightParticleRenderTypes.ENTITY_PARTICLE;
   }

   public boolean shouldCull() {
      return false;
   }
}

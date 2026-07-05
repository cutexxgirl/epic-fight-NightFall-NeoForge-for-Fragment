package com.guhao.vix.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.utils.math.QuaternionUtils;

public abstract class SkinModelParticle<M extends Mesh> extends Particle {
   protected final AssetAccessor<M> particleMeshProvider;
   protected float pitch;
   protected float pitchO;
   protected float yaw;
   protected float yawO;
   protected float scale = 1.0F;
   protected float scaleO = 1.0F;

   public SkinModelParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<M> particleMeshProvider) {
      super(level, x, y, z, xd, yd, zd);
      this.particleMeshProvider = particleMeshProvider;
   }

   @Override
   public void render(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      this.m_5744_(vertexBuffer, camera, partialTicks);
   }

   public void m_5744_(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      PoseStack poseStack = new PoseStack();
      poseStack.pushPose();
      this.setupPoseStack(poseStack, camera, partialTicks);
      this.prepareDraw(poseStack, partialTicks);
      this.particleMeshProvider
         .get()
         .draw(
            poseStack,
            vertexBuffer,
            DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
            this.getLightColor(partialTicks),
            this.rCol,
            this.gCol,
            this.bCol,
            this.alpha,
            OverlayTexture.NO_OVERLAY
         );
      this.revert(poseStack);
      poseStack.popPose();
   }

   @Override
   public void tick() {
      this.m_5989_();
   }

   public void m_5989_() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.pitchO = this.pitch;
         this.yawO = this.yaw;
         this.oRoll = this.roll;
         this.scaleO = this.scale;
      }
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void prepareDraw(PoseStack poseStack, float partialTicks) {
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      Vec3 cameraPosition = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPosition.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPosition.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPosition.z());
      float roll = Mth.lerp(partialTicks, this.oRoll, this.roll);
      float pitch = Mth.lerp(partialTicks, this.pitchO, this.pitch);
      float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
      float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
      Quaternionf rotation = new Quaternionf();
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F - yaw));
      rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
      rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
      poseStack.translate(x, y, z);
      poseStack.mulPose(rotation);
      poseStack.scale(scale, scale, scale);
   }

   protected void revert(PoseStack poseStack) {
   }
}

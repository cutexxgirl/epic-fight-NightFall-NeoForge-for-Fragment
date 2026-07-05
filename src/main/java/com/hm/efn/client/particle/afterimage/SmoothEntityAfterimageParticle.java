package com.hm.efn.client.particle.afterimage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.particle.EntityAfterimageParticle;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;

public class SmoothEntityAfterimageParticle extends EntityAfterimageParticle {
   private final long spawnTimeMs = Util.getMillis();
   private final float durationMs;
   private final float initialAlpha;
   private final float[] rgb;
   private final boolean renderBody;

   public SmoothEntityAfterimageParticle(
      ClientLevel level,
      double exactX,
      double exactY,
      double exactZ,
      EntitySnapshot<?> snapshot,
      float durationMs,
      float initialAlpha,
      float r,
      float g,
      float b,
      boolean renderBody
   ) {
      super(level, exactX, exactY, exactZ, 0.0, 0.0, 0.0, snapshot, p -> {});
      this.durationMs = durationMs;
      this.initialAlpha = initialAlpha;
      this.rgb = new float[]{r, g, b};
      this.renderBody = renderBody;
      this.lifetime = (int)(durationMs / 50.0F) + 2;
   }

   public void tick() {
      long elapsed = Util.getMillis() - this.spawnTimeMs;
      if ((float)elapsed >= this.durationMs) {
         this.remove();
      }
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      long elapsed = Util.getMillis() - this.spawnTimeMs;
      float progress = Math.min(1.0F, (float)elapsed / this.durationMs);
      float currentAlpha = this.initialAlpha * (1.0F - progress * progress);
      if (!(currentAlpha <= 0.01F)) {
         int lightColor = this.getLightColor(partialTicks);
         PoseStack poseStack = new PoseStack();
         this.setupPoseStack(poseStack, camera, partialTicks);
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         if (this.renderBody) {
            this.entitySnapshot
               .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
         }

         this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), DrawingFunction.POSITION_TEX, lightColor, 1.0F);
         buffers.endLastBatch();
         if (this.renderBody) {
            this.entitySnapshot
               .renderTextured(
                  poseStack,
                  buffers,
                  EpicFightRenderTypes::entityAfterimageTranslucent,
                  DrawingFunction.NEW_ENTITY,
                  lightColor,
                  this.rgb[0],
                  this.rgb[1],
                  this.rgb[2],
                  currentAlpha
               );
         }

         this.entitySnapshot
            .renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageTranslucent(), DrawingFunction.NEW_ENTITY, lightColor, currentAlpha);
         buffers.endLastBatch();
         this.revert(poseStack);
      }
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTick) {
      poseStack.pushPose();
      Vec3 cameraPosition = camera.getPosition();
      float renderX = (float)(this.x - cameraPosition.x());
      float renderY = (float)(this.y - cameraPosition.y());
      float renderZ = (float)(this.z - cameraPosition.z());
      poseStack.translate(renderX, renderY, renderZ);
      Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F));
      poseStack.mulPose(rotation);
      poseStack.mulPose(OpenMatrix4f.exportToMojangMatrix(this.entitySnapshot.getModelMatrix()));
      poseStack.translate(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.scale(1.0F, 1.0F, 1.0F);
      poseStack.translate(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
   }
}

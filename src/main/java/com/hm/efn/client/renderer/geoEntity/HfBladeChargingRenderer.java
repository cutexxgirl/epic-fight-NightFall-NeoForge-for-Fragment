package com.hm.efn.client.renderer.geoEntity;

import com.hm.efn.client.model.geo.HfBladeChargingModel;
import com.hm.efn.entity.geoEntity.HfBladeCharging;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HfBladeChargingRenderer extends GeoEntityRenderer<HfBladeCharging> {
   @Nullable
   private RenderType cachedEmissiveRenderType;

   public HfBladeChargingRenderer(Context renderManager) {
      super(renderManager, new HfBladeChargingModel());
   }

   public void render(HfBladeCharging entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
      float scale = entity.getScale();
      poseStack.pushPose();
      poseStack.scale(scale, scale, scale);
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      poseStack.popPose();
   }

   public void actuallyRender(
      PoseStack poseStack,
      HfBladeCharging animatable,
      BakedGeoModel model,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int renderColor
   ) {
      super.actuallyRender(
         poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor
      );
      if (!isReRender) {
         if (this.cachedEmissiveRenderType == null) {
            this.cachedEmissiveRenderType = RenderType.entityTranslucentEmissive(this.getTextureLocation(animatable));
         }

         VertexConsumer emissiveBuffer = bufferSource.getBuffer(this.cachedEmissiveRenderType);
         int emissiveLight = 15728880;
         super.actuallyRender(
            poseStack,
            animatable,
            model,
            this.cachedEmissiveRenderType,
            bufferSource,
            emissiveBuffer,
            true,
            partialTick,
            emissiveLight,
            packedOverlay, renderColor
         );
      }
   }
}

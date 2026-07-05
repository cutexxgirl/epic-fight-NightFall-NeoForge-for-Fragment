package com.hm.efn.client.entity.gecko;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class PostGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
   protected PostGeoEntityRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
   }

   public abstract Pipeline getPipeline();

   public void actuallyRender(
      PoseStack poseStack,
      T animatable,
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
      Pipeline pipeline = this.getPipeline();
      if (!isReRender) {
         pipeline.start();
      }

      super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
      if (!isReRender) {
         pipeline.call();
         pipeline.suspend();
      }
   }
}

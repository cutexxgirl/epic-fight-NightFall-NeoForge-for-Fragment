package com.hm.efn.entity.doppelganger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

public class DoppelgangerRender extends HumanoidMobRenderer<DoppelgangerEntity, HumanoidModel<DoppelgangerEntity>> {
   private static final ResourceLocation DARK_AURA_TEXTURE = ResourceLocation.parse("efn:textures/entity/dark.png");
   private static final ResourceLocation RUNES_TEXTURE = ResourceLocation.parse("efn:textures/entity/dark.png");
   private static final Random RANDOM = new Random(31100L);

   public DoppelgangerRender(Context pContext) {
      super(pContext, new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER)), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
      this.addLayer(new DoppelgangerRender.DarkAuraLayer(this, pContext));
      this.addLayer(new DoppelgangerRender.GlowingRunesLayer(this, pContext));
      this.addLayer(new DoppelgangerRender.ParticleEffectLayer(this));
   }

   protected RenderType getRenderType(DoppelgangerEntity entity, boolean visible, boolean invisibleToPlayer, boolean glowing) {
      return RenderType.entityTranslucent(this.getTextureLocation(entity));
   }

   public void render(DoppelgangerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      if (entity.isAlive()) {
         float swing = entity.tickCount + partialTicks;
         poseStack.pushPose();
         poseStack.translate(0.0F, (float)Math.sin(swing * 0.1F) * 0.05F, 0.0F);
         super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
         poseStack.popPose();
      } else {
         super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DoppelgangerEntity doppelgangerEntity) {
      return ResourceLocation.parse("efn:textures/entity/doppelganger.png");
   }

   static class DarkAuraLayer extends RenderLayer<DoppelgangerEntity, HumanoidModel<DoppelgangerEntity>> {
      private final HumanoidModel<DoppelgangerEntity> model;

      public DarkAuraLayer(DoppelgangerRender renderer, Context context) {
         super(renderer);
         this.model = new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER));
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource buffer,
         int packedLight,
         DoppelgangerEntity entity,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (!entity.isInvisible()) {
            float intensity = (float)Math.sin(entity.tickCount * 0.1F) * 0.1F + 0.9F;
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.energySwirl(DoppelgangerRender.DARK_AURA_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(this.model);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(
               poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(intensity * 0.7F, 0.2F, 0.0F, 0.2F)
            );
         }
      }
   }

   static class GlowingRunesLayer extends RenderLayer<DoppelgangerEntity, HumanoidModel<DoppelgangerEntity>> {
      private final HumanoidModel<DoppelgangerEntity> model;

      public GlowingRunesLayer(DoppelgangerRender renderer, Context context) {
         super(renderer);
         this.model = new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER));
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource buffer,
         int packedLight,
         DoppelgangerEntity entity,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         float pulse = (float)Math.sin(entity.tickCount * 0.2F) * 0.05F + 0.95F;
         VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(DoppelgangerRender.RUNES_TEXTURE));
         ((HumanoidModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         this.model.renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(pulse, 1.0F, 1.0F, 1.0F));
      }
   }

   static class ParticleEffectLayer extends RenderLayer<DoppelgangerEntity, HumanoidModel<DoppelgangerEntity>> {
      public ParticleEffectLayer(DoppelgangerRender renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource buffer,
         int packedLight,
         DoppelgangerEntity entity,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entity.tickCount % 3 == 0) {
            poseStack.pushPose();
            float offsetX = (DoppelgangerRender.RANDOM.nextFloat() - 0.5F) * 1.2F;
            float offsetY = DoppelgangerRender.RANDOM.nextFloat() * 1.8F;
            float offsetZ = (DoppelgangerRender.RANDOM.nextFloat() - 0.5F) * 1.2F;
            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.scale(0.1F, 0.1F, 0.1F);
            poseStack.popPose();
         }
      }
   }
}

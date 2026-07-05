package com.hm.efn.entity.renderer;

import com.hm.efn.entity.falchion.GuardianEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedElytraLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class GuardianDarkRenderer
   extends PatchedLivingEntityRenderer<GuardianEntity, LivingEntityPatch<GuardianEntity>, HumanoidModel<GuardianEntity>, LivingEntityRenderer<GuardianEntity, HumanoidModel<GuardianEntity>>, HumanoidMesh> {
   private static final ResourceLocation GREEN_AURA_TEXTURE = ResourceLocation.parse("efn:textures/entity/green.png");
   private static final ResourceLocation RUNES_TEXTURE = ResourceLocation.parse("efn:textures/entity/green.png");
   private final AssetAccessor<HumanoidMesh> mesh;

   public GuardianDarkRenderer(AssetAccessor<HumanoidMesh> mesh, Context context, EntityType<?> entityType) {
      super(context, entityType);
      this.mesh = mesh;
      this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer());
      this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer());
      this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer(mesh, false, context.getModelManager()));
      this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer());
   }

   public void render(
      GuardianEntity entity,
      LivingEntityPatch<GuardianEntity> entitypatch,
      LivingEntityRenderer<GuardianEntity, HumanoidModel<GuardianEntity>> renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      Minecraft mc = Minecraft.getInstance();
      Armature armature = entitypatch.getArmature();
      if (armature != null) {
         HumanoidMesh mesh = (HumanoidMesh)this.mesh.get();
         if (mesh != null) {
            poseStack.pushPose();
            this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
            this.setArmaturePose(entitypatch, armature, partialTicks);
            ResourceLocation baseTexture = this.getTextureLocation(entity);
            mesh.draw(
               poseStack,
               buffer,
               RenderType.entityTranslucent(baseTexture),
               packedLight,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               OverlayTexture.NO_OVERLAY,
               entitypatch.getArmature(),
               armature.getPoseMatrices()
            );
            float darkIntensity = (float)Math.sin((entity.tickCount + partialTicks) * 0.1F) * 0.1F + 0.9F;
            mesh.draw(
               poseStack,
               buffer,
               RenderType.energySwirl(GREEN_AURA_TEXTURE, (entity.tickCount + partialTicks) * 0.01F, (entity.tickCount + partialTicks) * 0.01F),
               packedLight,
               0.2F,
               0.0F,
               0.2F,
               darkIntensity * 0.7F,
               OverlayTexture.NO_OVERLAY,
               entitypatch.getArmature(),
               armature.getPoseMatrices()
            );
            float runePulse = (float)Math.sin((entity.tickCount + partialTicks) * 0.2F) * 0.05F + 0.95F;
            mesh.draw(
               poseStack,
               buffer,
               RenderType.eyes(RUNES_TEXTURE),
               15728640,
               1.0F,
               1.0F,
               1.0F,
               runePulse,
               OverlayTexture.NO_OVERLAY,
               entitypatch.getArmature(),
               armature.getPoseMatrices()
            );
            this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
               entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
            }

            poseStack.popPose();
         }
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull GuardianEntity GuardianEntity) {
      LivingEntity owner = GuardianEntity.getOwner();
      if (owner != null) {
         return owner instanceof AbstractClientPlayer clientPlayer ? clientPlayer.getSkin().texture() : DefaultPlayerSkin.getDefaultTexture();
      } else {
         return DefaultPlayerSkin.getDefaultTexture();
      }
   }

   public AssetAccessor<HumanoidMesh> getDefaultMesh() {
      return this.mesh;
   }

   public void setJointTransforms(LivingEntityPatch<GuardianEntity> entitypatch, Armature armature, Pose pose, float partialTicks) {
      if (((GuardianEntity)entitypatch.getOriginal()).isBaby()) {
         pose.orElseEmpty("Head").frontResult(JointTransform.scale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
      }
   }

   protected float getDefaultLayerHeightCorrection() {
      return 0.75F;
   }
}

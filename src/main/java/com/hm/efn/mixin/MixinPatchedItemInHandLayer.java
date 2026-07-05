package com.hm.efn.mixin;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.gameasset.EFNAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = PatchedItemInHandLayer.class, remap = false)
public class MixinPatchedItemInHandLayer {
   @Unique
   private static final ResourceLocation TEXTURE_MAIN = ResourceLocation.parse("efn:textures/item/weapon/yamato_dmc4.png");
   @Unique
   private static final AssetAccessor<SkinnedMesh> MESH_MAIN_HAND = MeshAccessor.create(
      "efn", "weapon/yamato_dmc4", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );

   @Inject(method = "renderLayer", at = @At(value = "HEAD", remap = false), remap = false, cancellable = true)
   private void efn$renderExtraItemAndBlockOriginal(
      LivingEntityPatch<?> livingEntityPatch,
      LivingEntity livingEntity,
      RenderLayer<?, ?> vanillaLayer,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      OpenMatrix4f[] poses,
      float bob,
      float yRot,
      float xRot,
      float partialTicks,
      CallbackInfo ci
   ) {
      if (livingEntityPatch != null && livingEntityPatch.getAnimator() != null) {
         AnimationPlayer animPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
         if (animPlayer != null) {
            AssetAccessor<? extends StaticAnimation> currentAnimation = ((DynamicAnimation)animPlayer.getAnimation().get()).getRealAnimation();
            if (currentAnimation != null) {
               if (currentAnimation.equals(EFNAnimations.DMC5_V_JC) && (Boolean)EFNClientConfig.ENABLE_JUDGEMENTCUT_END_MODEL_FIX.get()) {
                  Armature armature = livingEntityPatch.getArmature();
                  if (armature == null) {
                     return;
                  }

                  SkinnedMesh renderMesh = (SkinnedMesh)MESH_MAIN_HAND.get();
                  if (renderMesh == null) {
                     return;
                  }

                  poseStack.pushPose();
                  armature.setPose(livingEntityPatch.getAnimator().getPose(partialTicks));
                  renderMesh.draw(
                     poseStack,
                     buffer,
                     RenderType.entityTranslucent(TEXTURE_MAIN),
                     packedLight,
                     1.0F,
                     1.0F,
                     1.0F,
                     1.0F,
                     OverlayTexture.NO_OVERLAY,
                     armature,
                     armature.getPoseMatrices()
                  );
                  poseStack.popPose();
                  ci.cancel();
               }
            }
         }
      }
   }
}

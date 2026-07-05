package com.hm.efn.client.renderer;

import com.google.gson.JsonElement;
import com.hm.efn.gameasset.EFNAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderJudgementCutEnd extends RenderItemBase {
   private static final ResourceLocation YAMATO_DMC4_TEXTURE = ResourceLocation.fromNamespaceAndPath("efn", "textures/item/weapon/yamato_dmc4.png");
   private static final ResourceLocation YAMATO_DMC4_MESH = ResourceLocation.fromNamespaceAndPath("efn", "weapon/yamato_dmc4_in_sheath");
   private final AssetAccessor<? extends SkinnedMesh> mesh_main = MeshAccessor.create(
      YAMATO_DMC4_MESH.getNamespace(), YAMATO_DMC4_MESH.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private final AssetAccessor<? extends SkinnedMesh> mesh_off = MeshAccessor.create(
      YAMATO_DMC4_MESH.getNamespace(), YAMATO_DMC4_MESH.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );

   public RenderJudgementCutEnd(JsonElement jsonElement) {
      super(jsonElement);
   }

   public void renderItemInHand(
      ItemStack stack,
      LivingEntityPatch<?> entitypatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (entitypatch != null) {
         if (entitypatch.getAnimator().getPlayerFor(null) != null) {
            DynamicAnimation animation = (DynamicAnimation)Objects.requireNonNull(entitypatch.getAnimator().getPlayerFor(null)).getAnimation().get();
            boolean isJCAnimation = animation.getRealAnimation().equals(EFNAnimations.DMC5_V_JC);
            if (isJCAnimation) {
               Armature armature = entitypatch.getArmature();
               poseStack.pushPose();
               armature.setPose(entitypatch.getAnimator().getPose(partialTicks));
               SkinnedMesh renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh_main.get() : (SkinnedMesh)this.mesh_off.get();
               if (renderMesh != null) {
                  renderMesh.draw(
                     poseStack,
                     buffer,
                     RenderType.entityTranslucent(YAMATO_DMC4_TEXTURE),
                     packedLight,
                     1.0F,
                     1.0F,
                     1.0F,
                     1.0F,
                     OverlayTexture.NO_OVERLAY,
                     armature,
                     armature.getPoseMatrices()
                  );
               }

               poseStack.popPose();
            }
         }
      }
   }
}

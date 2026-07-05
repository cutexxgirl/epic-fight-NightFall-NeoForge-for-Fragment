package com.hm.efn.client.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
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
public class RenderYamato_DMC extends RenderItemBase {
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh1TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_off;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh2TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_off;

   public RenderYamato_DMC(JsonElement jsonElement) {
      super(jsonElement);
      JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (jsonObj.has("mesh1_texture")) {
         this.mesh1Texture = ResourceLocation.parse(jsonObj.get("mesh1_texture").getAsString());
      } else {
         this.mesh1Texture = null;
      }

      if (jsonObj.has("mesh1_texture_l")) {
         this.mesh1TextureL = ResourceLocation.parse(jsonObj.get("mesh1_texture_l").getAsString());
      } else {
         this.mesh1TextureL = null;
      }

      if (jsonObj.has("mesh2_texture")) {
         this.mesh2Texture = ResourceLocation.parse(jsonObj.get("mesh2_texture").getAsString());
      } else {
         this.mesh2Texture = null;
      }

      if (jsonObj.has("mesh2_texture_l")) {
         this.mesh2TextureL = ResourceLocation.parse(jsonObj.get("mesh2_texture_l").getAsString());
      } else {
         this.mesh2TextureL = null;
      }

      if (jsonObj.has("mesh1_main")) {
         String meshLoc = jsonObj.get("mesh1_main").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1_main = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1_main = null;
      }

      if (jsonObj.has("mesh1_off")) {
         String meshLoc = jsonObj.get("mesh1_off").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1_off = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1_off = null;
      }

      if (jsonObj.has("mesh2_main")) {
         String meshLoc = jsonObj.get("mesh2_main").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2_main = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2_main = null;
      }

      if (jsonObj.has("mesh2_off")) {
         String meshLoc = jsonObj.get("mesh2_off").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2_off = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2_off = null;
      }
   }

   private static boolean isUseMesh2(DynamicAnimation animation) {
      return animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_NORMAL_AUTO3)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO3)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO4)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO5)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO3)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL_ALL)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_CHARGE)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST_MOB)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH_MOB)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT_REPAID)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT_RISING)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_UPPERSLASH)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DRIVE)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DRIVE_MOB)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_STOMP)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AIRFLUSH)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_HELMBREAKER)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_ORBIT_1)
         || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_ORBIT_2)
         || animation.getRealAnimation().equals(EFNAnimations.DMC5_V_JC);
   }

   public void renderItemInHand(
      ItemStack stack,
      LivingEntityPatch<?> livingEntityPatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (livingEntityPatch != null) {
         DynamicAnimation animation = (DynamicAnimation)Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null)).getAnimation().get();
         boolean useMesh2 = isUseMesh2(animation);
         Armature armature = livingEntityPatch.getArmature();
         poseStack.pushPose();
         armature.setPose(livingEntityPatch.getAnimator().getPose(partialTicks));
         SkinnedMesh renderMesh;
         ResourceLocation texture;
         ResourceLocation textureL;
         if (useMesh2) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh2_main.get() : (SkinnedMesh)this.mesh2_off.get();
            texture = this.mesh2Texture;
            textureL = this.mesh2TextureL;
         } else {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh1_main.get() : (SkinnedMesh)this.mesh1_off.get();
            texture = this.mesh1Texture;
            textureL = this.mesh1TextureL;
         }

         if (renderMesh != null) {
            if (texture != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.entityTranslucent(texture),
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

            if (textureL != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.entityTranslucentEmissive(textureL),
                  packedLight,
                  1.0F,
                  1.0F,
                  1.0F,
                  0.9F,
                  OverlayTexture.NO_OVERLAY,
                  armature,
                  armature.getPoseMatrices()
               );
            }
         }

         poseStack.popPose();
      }
   }
}

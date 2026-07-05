package com.hm.efn.client.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderFalchion extends RenderItemBase {
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh1TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_off;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh2TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_off;

   public RenderFalchion(JsonElement jsonElement) {
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
         boolean useMesh2 = ((LivingEntity)livingEntityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.FALCHION_BLESS);
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

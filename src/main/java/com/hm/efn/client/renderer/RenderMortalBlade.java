package com.hm.efn.client.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hm.efn.gameasset.EFNSkills;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderAnimationItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
public class RenderMortalBlade extends RenderAnimationItem {
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh1TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_off;
   private final ResourceLocation mesh1SheathTexture;
   private final ResourceLocation mesh1SheathTextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh1_sheath;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh2TextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_main;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_off;
   private final ResourceLocation mesh2SheathTexture;
   private final ResourceLocation mesh2SheathTextureL;
   private final AssetAccessor<? extends SkinnedMesh> mesh2_sheath;

   public RenderMortalBlade(JsonElement jsonElement) {
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

      if (jsonObj.has("mesh1_sheath_texture")) {
         this.mesh1SheathTexture = ResourceLocation.parse(jsonObj.get("mesh1_sheath_texture").getAsString());
      } else {
         this.mesh1SheathTexture = this.mesh1Texture;
      }

      if (jsonObj.has("mesh1_sheath_texture_l")) {
         this.mesh1SheathTextureL = ResourceLocation.parse(jsonObj.get("mesh1_sheath_texture_l").getAsString());
      } else {
         this.mesh1SheathTextureL = this.mesh1TextureL;
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

      if (jsonObj.has("mesh2_sheath_texture")) {
         this.mesh2SheathTexture = ResourceLocation.parse(jsonObj.get("mesh2_sheath_texture").getAsString());
      } else {
         this.mesh2SheathTexture = this.mesh2Texture;
      }

      if (jsonObj.has("mesh2_sheath_texture_l")) {
         this.mesh2SheathTextureL = ResourceLocation.parse(jsonObj.get("mesh2_sheath_texture_l").getAsString());
      } else {
         this.mesh2SheathTextureL = this.mesh2TextureL;
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

      if (jsonObj.has("mesh1_sheath")) {
         String meshLoc = jsonObj.get("mesh1_sheath").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1_sheath = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1_sheath = null;
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

      if (jsonObj.has("mesh2_sheath")) {
         String meshLoc = jsonObj.get("mesh2_sheath").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2_sheath = MeshAccessor.create(resLoc.getNamespace(), resLoc.getPath(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2_sheath = null;
      }
   }

   public SkinnedMesh getCurrentMesh(boolean useMesh2, InteractionHand hand, boolean useSheath) {
      if (useSheath) {
         return useMesh2
            ? (this.mesh2_sheath != null ? (SkinnedMesh)this.mesh2_sheath.get() : null)
            : (this.mesh1_sheath != null ? (SkinnedMesh)this.mesh1_sheath.get() : null);
      } else if (useMesh2) {
         return hand == InteractionHand.MAIN_HAND
            ? (this.mesh2_main != null ? (SkinnedMesh)this.mesh2_main.get() : null)
            : (this.mesh2_off != null ? (SkinnedMesh)this.mesh2_off.get() : null);
      } else {
         return hand == InteractionHand.MAIN_HAND
            ? (this.mesh1_main != null ? (SkinnedMesh)this.mesh1_main.get() : null)
            : (this.mesh1_off != null ? (SkinnedMesh)this.mesh1_off.get() : null);
      }
   }

   public ResourceLocation getCurrentTexture(boolean useMesh2, boolean emissive, boolean useSheath) {
      if (useSheath) {
         if (useMesh2) {
            return emissive ? this.mesh2SheathTextureL : this.mesh2SheathTexture;
         } else {
            return emissive ? this.mesh1SheathTextureL : this.mesh1SheathTexture;
         }
      } else if (useMesh2) {
         return emissive ? this.mesh2TextureL : this.mesh2Texture;
      } else {
         return emissive ? this.mesh1TextureL : this.mesh1Texture;
      }
   }

   public boolean shouldUseMesh2(LivingEntityPatch<?> livingEntityPatch) {
      if (livingEntityPatch == null) {
         return false;
      } else if (!(livingEntityPatch instanceof PlayerPatch<?> playerPatch)) {
         return true;
      } else {
         SkillContainer skill = playerPatch.getSkill(EFNSkills.MORTAL_BLADE);
         return skill != null && skill.hasSkill();
      }
   }

   public void renderAnimationItem(
      LivingEntityPatch<?> livingEntityPatch, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks
   ) {
      boolean useMesh2 = this.shouldUseMesh2(livingEntityPatch);
      boolean useSheathMesh = false;
      ItemStack stack = ((LivingEntity)livingEntityPatch.getOriginal()).getMainHandItem();
      CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      useSheathMesh = customData.copyTag().getInt("kusabimaru_sheath") == 1;

      SkinnedMesh itemMesh = this.getCurrentMesh(useMesh2, InteractionHand.MAIN_HAND, useSheathMesh);
      if (itemMesh != null) {
         Armature realArmature = livingEntityPatch.getArmature();
         this.setArmaturePose(livingEntityPatch, realArmature, partialTicks);
         ResourceLocation baseTexture = this.getCurrentTexture(useMesh2, false, useSheathMesh);
         ResourceLocation emissiveTexture = this.getCurrentTexture(useMesh2, true, useSheathMesh);
         if (baseTexture != null) {
            itemMesh.draw(
               poseStack,
               buffer,
               RenderType.entityTranslucent(baseTexture),
               packedLight,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               OverlayTexture.NO_OVERLAY,
               realArmature,
               realArmature.getPoseMatrices()
            );
         }

         if (emissiveTexture != null) {
            itemMesh.draw(
               poseStack,
               buffer,
               RenderType.entityTranslucentEmissive(emissiveTexture),
               packedLight,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               OverlayTexture.NO_OVERLAY,
               realArmature,
               realArmature.getPoseMatrices()
            );
         }
      }
   }

   public SkinnedMesh getMesh(boolean useMesh2, InteractionHand hand) {
      return this.getCurrentMesh(useMesh2, hand, false);
   }

   public SkinnedMesh getSheathMesh(boolean useMesh2) {
      return this.getCurrentMesh(useMesh2, InteractionHand.MAIN_HAND, true);
   }

   public ResourceLocation getTexture(boolean useMesh2, boolean emissive) {
      return this.getCurrentTexture(useMesh2, emissive, false);
   }

   public ResourceLocation getSheathTexture(boolean useMesh2, boolean emissive) {
      return this.getCurrentTexture(useMesh2, emissive, true);
   }
}

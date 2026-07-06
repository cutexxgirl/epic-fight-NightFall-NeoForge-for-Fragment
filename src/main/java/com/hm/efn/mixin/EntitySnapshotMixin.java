package com.hm.efn.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.EntitySnapshot.PlayerSnapshot;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EntitySnapshot.class, remap = false)
public abstract class EntitySnapshotMixin<T extends LivingEntityPatch<?>> {
   @Shadow(remap = false)
   @Final
   protected T entitypatch;

   @Shadow(remap = false)
   @Final
   protected EntitySnapshot.RenderableFigure entityFigure;

   @Shadow(remap = false)
   @Final
   protected OpenMatrix4f[] poseMatrices;

   @Shadow(remap = false)
   @Final
   @Mutable
   protected List<EntitySnapshot.RenderableFigure> armorMeshes;

   @Unique
   private List<Integer> efn$armorLayerColors = List.of();

   @Inject(method = "capturePlayer", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void onCapturePlayer(AbstractClientPlayerPatch<?> abstractClientPlayerPatch, CallbackInfoReturnable<PlayerSnapshot> cir) {
      if (abstractClientPlayerPatch != null) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(abstractClientPlayerPatch.getOriginal(), PlayerPatch.class);
         if (playerPatch != null && !Objects.equals(playerPatch.getArmature().toString(), Armatures.BIPED.registryName().toString())) {
            cir.setReturnValue(null);
            cir.cancel();
         }
      }
   }

   @Inject(method = "<init>", at = @At(value = "RETURN", remap = false), remap = false)
   private void efn$rebuildArmorLayers(LivingEntityPatch<?> capturedPatch, CallbackInfo ci) {
      LivingEntity livingEntity = capturedPatch.getOriginal();
      ImmutableList.Builder<EntitySnapshot.RenderableFigure> figures = ImmutableList.builder();
      ImmutableList.Builder<Integer> colors = ImmutableList.builder();

      for (ItemStack itemStack : livingEntity.getArmorSlots()) {
         if (!(itemStack.getItem() instanceof ArmorItem armorItem)) {
            continue;
         }

         EquipmentSlot armorSlot = itemStack.getEquipmentSlot();

         if (armorSlot != armorItem.getEquipmentSlot()) {
            continue;
         }

         SkinnedMesh armorMesh = WearableItemLayer.getCachedModel(itemStack.getItem());

         if (armorMesh == null) {
            continue;
         }

         ArmorMaterial armorMaterial = armorItem.getMaterial().value();
         IClientItemExtensions extensions = IClientItemExtensions.of(itemStack);
         int fallbackColor = extensions.getDefaultDyeColor(itemStack);
         boolean innerModel = efn$innerModel(armorSlot);

         for (int layerIndex = 0; layerIndex < armorMaterial.layers().size(); layerIndex++) {
            ArmorMaterial.Layer armorLayer = armorMaterial.layers().get(layerIndex);
            int packedColor = extensions.getArmorLayerTintColor(itemStack, livingEntity, armorLayer, layerIndex, fallbackColor);

            if (packedColor == 0) {
               continue;
            }

            ResourceLocation texture = efn$getArmorTexture(livingEntity, itemStack, armorLayer, innerModel, armorSlot, armorMesh);
            figures.add(new EntitySnapshot.RenderableFigure(armorMesh, texture));
            colors.add(packedColor);
         }
      }

      this.armorMeshes = figures.build();
      this.efn$armorLayerColors = colors.build();
   }

   @Inject(method = "render", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void efn$renderWithArmorTint(
      PoseStack poseStack,
      MultiBufferSource buffers,
      RenderType renderType,
      Mesh.DrawingFunction drawingFunction,
      int packedLight,
      float r,
      float g,
      float b,
      float a,
      CallbackInfo ci
   ) {
      if (this.entityFigure.mesh() == null || this.entityFigure.texture() == null) {
         ci.cancel();
         return;
      }

      this.entityFigure.mesh().initialize();
      this.entityFigure.mesh().draw(
         poseStack,
         buffers,
         renderType,
         drawingFunction,
         packedLight,
         r,
         g,
         b,
         a,
         OverlayTexture.NO_OVERLAY,
         this.entitypatch.getArmature(),
         this.poseMatrices
      );

      for (int i = 0; i < this.armorMeshes.size(); i++) {
         EntitySnapshot.RenderableFigure armorFigure = this.armorMeshes.get(i);

         if (armorFigure.mesh() == null) {
            continue;
         }

         int packedColor = this.efn$getArmorLayerColor(i);

         if (packedColor == 0) {
            continue;
         }

         armorFigure.mesh().initialize();
         armorFigure.mesh().draw(
            poseStack,
            buffers,
            renderType,
            drawingFunction,
            packedLight,
            r * efn$red(packedColor),
            g * efn$green(packedColor),
            b * efn$blue(packedColor),
            a,
            OverlayTexture.NO_OVERLAY,
            this.entitypatch.getArmature(),
            this.poseMatrices
         );
      }

      ci.cancel();
   }

   @Inject(method = "renderTextured", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void efn$renderTexturedWithArmorTint(
      PoseStack poseStack,
      MultiBufferSource buffers,
      Function<ResourceLocation, RenderType> renderTypeFunction,
      Mesh.DrawingFunction drawingFunction,
      int packedLight,
      float r,
      float g,
      float b,
      float a,
      CallbackInfo ci
   ) {
      if (this.entityFigure.mesh() == null || this.entityFigure.texture() == null) {
         ci.cancel();
         return;
      }

      this.entityFigure.mesh().initialize();
      this.entityFigure.mesh().draw(
         poseStack,
         buffers,
         renderTypeFunction.apply(this.entityFigure.texture()),
         drawingFunction,
         packedLight,
         r,
         g,
         b,
         a,
         OverlayTexture.NO_OVERLAY,
         this.entitypatch.getArmature(),
         this.poseMatrices
      );

      for (int i = 0; i < this.armorMeshes.size(); i++) {
         EntitySnapshot.RenderableFigure armorFigure = this.armorMeshes.get(i);

         if (armorFigure.mesh() == null || armorFigure.texture() == null) {
            continue;
         }

         int packedColor = this.efn$getArmorLayerColor(i);

         if (packedColor == 0) {
            continue;
         }

         armorFigure.mesh().initialize();
         armorFigure.mesh().draw(
            poseStack,
            buffers,
            renderTypeFunction.apply(armorFigure.texture()),
            drawingFunction,
            packedLight,
            r * efn$red(packedColor),
            g * efn$green(packedColor),
            b * efn$blue(packedColor),
            a,
            OverlayTexture.NO_OVERLAY,
            this.entitypatch.getArmature(),
            this.poseMatrices
         );
      }

      ci.cancel();
   }

   @Unique
   private int efn$getArmorLayerColor(int index) {
      if (index < this.efn$armorLayerColors.size()) {
         return this.efn$armorLayerColors.get(index);
      }

      return 0xFFFFFFFF;
   }

   @Unique
   private static ResourceLocation efn$getArmorTexture(
      LivingEntity livingEntity,
      ItemStack itemStack,
      ArmorMaterial.Layer armorLayer,
      boolean innerModel,
      EquipmentSlot armorSlot,
      SkinnedMesh armorMesh
   ) {
      return ParseUtil.tryGetOr(
         () -> armorMesh.getRenderProperties().customTexturePath(),
         () -> ClientHooks.getArmorTexture(livingEntity, itemStack, armorLayer, innerModel, armorSlot)
      );
   }

   @Unique
   private static boolean efn$innerModel(EquipmentSlot slot) {
      return slot == EquipmentSlot.LEGS;
   }

   @Unique
   private static float efn$red(int packedColor) {
      return (float)(packedColor >> 16 & 255) / 255.0F;
   }

   @Unique
   private static float efn$green(int packedColor) {
      return (float)(packedColor >> 8 & 255) / 255.0F;
   }

   @Unique
   private static float efn$blue(int packedColor) {
      return (float)(packedColor & 255) / 255.0F;
   }
}

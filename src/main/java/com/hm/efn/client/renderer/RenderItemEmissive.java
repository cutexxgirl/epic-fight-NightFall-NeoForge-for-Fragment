package com.hm.efn.client.renderer;

import net.minecraft.core.registries.BuiltInRegistries;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderItemEmissive extends RenderItemBase {
   private ItemStack emissiveItemStack = ItemStack.EMPTY;

   public RenderItemEmissive(JsonElement jsonElement) {
      super(jsonElement);
      JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (jsonObj.has("emissive_item")) {
         String itemId = jsonObj.get("emissive_item").getAsString();
         ResourceLocation itemLoc = ResourceLocation.parse(itemId);
         if (BuiltInRegistries.ITEM.containsKey(itemLoc)) {
            this.emissiveItemStack = new ItemStack(Objects.requireNonNull((Item)BuiltInRegistries.ITEM.get(itemLoc)));
         } else {
            System.err.println("[RenderItemEmissive] 找不到发光层物品: " + itemId);
         }
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
      super.renderItemInHand(stack, livingEntityPatch, hand, poses, buffer, poseStack, packedLight, partialTicks);
      if (!this.emissiveItemStack.isEmpty()) {
         OpenMatrix4f modelMatrix = this.getCorrectionMatrix(livingEntityPatch, hand, poses);
         poseStack.pushPose();
         MathUtils.mulStack(poseStack, modelMatrix);
         poseStack.scale(1.005F, 1.005F, 1.005F);
         ItemDisplayContext transformType = hand == InteractionHand.MAIN_HAND
            ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         int maxLight = 15728880;
         itemInHandRenderer.renderItem(
            (LivingEntity)livingEntityPatch.getOriginal(), this.emissiveItemStack, transformType, hand == InteractionHand.OFF_HAND, poseStack, buffer, maxLight
         );
         poseStack.popPose();
      }
   }
}

package com.hm.efn.mixin;

import com.hm.efn.EFN;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.input.DiscreteActionHandler;
import yesman.epicfight.client.input.DiscreteInputActionTrigger;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(value = DiscreteInputActionTrigger.class, remap = false)
public abstract class EpicFightDiscreteInputActionTriggerMixin {
   @Unique
   private static boolean epicFight_Nightfall$attackMouseWasDown;

   @Inject(method = "handleKeyboardAndMouse", at = @At("HEAD"), cancellable = true)
   private static void efn$handleSharedVanillaAttackMouse(KeyMapping keyMapping, DiscreteActionHandler handler, CallbackInfo ci) {
      Minecraft minecraft = Minecraft.getInstance();
      if (keyMapping != EpicFightKeyMappings.ATTACK || minecraft.options == null || !epicFight_Nightfall$samePhysicalKey(keyMapping, minecraft.options.keyAttack)) {
         return;
      }

      InputConstants.Key key = keyMapping.getKey();
      if (key.getType() != InputConstants.Type.MOUSE) {
         return;
      }

      boolean down = GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
      if (epicFight_Nightfall$isHandledEfnInnateEquipped()) {
         epicFight_Nightfall$attackMouseWasDown = down;
         epicFight_Nightfall$drainClicks(keyMapping);
         ci.cancel();
         return;
      }

      if (minecraft.screen == null && minecraft.player != null && minecraft.level != null && !minecraft.isPaused() && down && !epicFight_Nightfall$attackMouseWasDown) {
         handler.onAction(new DiscreteActionHandler.Context(false));
         EFN.LOGGER.info(
            "[EFN/InputTrace] epicfight shared attack edge item={} attackKey={} vanillaKey={}",
            epicFight_Nightfall$itemName(minecraft.player.getMainHandItem()),
            keyMapping.getKey(),
            minecraft.options.keyAttack.getKey()
         );
      }

      epicFight_Nightfall$attackMouseWasDown = down;
      epicFight_Nightfall$drainClicks(keyMapping);
      ci.cancel();
   }

   @Unique
   private static boolean epicFight_Nightfall$samePhysicalKey(KeyMapping keyMapping, KeyMapping otherKeyMapping) {
      if (keyMapping == null || otherKeyMapping == null) {
         return false;
      }

      InputConstants.Key key = keyMapping.getKey();
      InputConstants.Key otherKey = otherKeyMapping.getKey();
      return key != null
         && otherKey != null
         && key.getValue() != InputConstants.UNKNOWN.getValue()
         && key.getType() == otherKey.getType()
         && key.getValue() == otherKey.getValue();
   }

   @Unique
   private static boolean epicFight_Nightfall$isHandledEfnInnateEquipped() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null) {
         return false;
      }

      LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
      if (playerPatch == null) {
         return false;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate == null || !(weaponInnate.getSkill() instanceof EFNWeaponInnateBase skill)) {
         return false;
      }

      ItemStack mainHandItem = playerPatch.getOriginal().getMainHandItem();
      return EpicFightCapabilities.getItemCapability(mainHandItem)
         .filter(capabilityItem -> !capabilityItem.isEmpty())
         .filter(capabilityItem -> capabilityItem.getInnateSkill(playerPatch, mainHandItem) == skill)
         .map(capabilityItem -> !EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(playerPatch, capabilityItem))
         .orElse(false);
   }

   @Unique
   private static void epicFight_Nightfall$drainClicks(KeyMapping keyMapping) {
      while (keyMapping.consumeClick()) {
      }
   }

   @Unique
   private static String epicFight_Nightfall$itemName(ItemStack itemStack) {
      return itemStack == null || itemStack.isEmpty() ? "empty" : String.valueOf(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
   }
}

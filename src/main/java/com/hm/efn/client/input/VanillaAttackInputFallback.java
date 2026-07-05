package com.hm.efn.client.input;

import com.hm.efn.mixin.ControlEngineAccessor;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME, value = Dist.CLIENT)
public final class VanillaAttackInputFallback {
   private static boolean trackingAttackPress;
   private static boolean longPressTriggered;
   private static int attackPressStartTick;

   private VanillaAttackInputFallback() {
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onMouseInput(Pre event) {
      if (!isVanillaAttackButton(event.getButton())) {
         return;
      }

      if (event.getAction() == InputConstants.PRESS) {
         trackingAttackPress = shouldUseFallback();
         longPressTriggered = false;
         attackPressStartTick = getPlayerTick();
         if (trackingAttackPress) {
            event.setCanceled(true);
            drainPendingAttackClicks();
            clearSameKeyAttackState(ControlEngine.getInstance());
            ControlEngine.setKeyBind(EpicFightKeyMappings.ATTACK, false);
            ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
         }
      } else if (event.getAction() == InputConstants.RELEASE) {
         if (trackingAttackPress) {
            event.setCanceled(true);
            int heldTicks = Math.max(1, getPlayerTick() - attackPressStartTick);
            if (longPressTriggered) {
               ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
            } else if (heldTicks <= ClientConfig.holdingThreshold + 1 && shouldUseFallback()) {
               drainPendingAttackClicks();
               requestComboAttack();
            } else {
               requestWeaponInnate();
               ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
            }
         }

         trackingAttackPress = false;
         longPressTriggered = false;
      }
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      if (trackingAttackPress && !longPressTriggered && getPlayerTick() - attackPressStartTick > ClientConfig.holdingThreshold && shouldUseFallback()) {
         drainPendingAttackClicks();
         longPressTriggered = requestWeaponInnate();
      }
   }

   public static boolean shouldSuppressSeparateWeaponInnate() {
      return isWeaponInnateBoundToVanillaAttack() && shouldUseFallback();
   }

   private static boolean isVanillaAttackButton(int button) {
      return Minecraft.getInstance().options.keyAttack.getKey().getType() == InputConstants.Type.MOUSE
         && Minecraft.getInstance().options.keyAttack.getKey().getValue() == button;
   }

   private static boolean shouldUseFallback() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen != null || minecraft.player == null || minecraft.level == null || minecraft.isPaused()) {
         return false;
      }

      LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
      if (playerPatch == null || !playerPatch.isEpicFightMode()) {
         return false;
      }

      ItemStack heldItem = playerPatch.getOriginal().getMainHandItem();
      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(heldItem);
      boolean canPlayAttackAnimation = playerPatch.canPlayAttackAnimation();
      if (!canPlayAttackAnimation && !hasAutoAttackMotions(playerPatch, capabilityItem)) {
         return false;
      }

      if (capabilityItem == null || capabilityItem.isEmpty()) {
         return canPlayAttackAnimation;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate != null && weaponInnate.getSkill() instanceof EFNWeaponInnateBase skill && capabilityItem.getInnateSkill(playerPatch, heldItem) == skill) {
         return EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(playerPatch, capabilityItem);
      }

      return true;
   }

   private static boolean hasAutoAttackMotions(LocalPlayerPatch playerPatch, CapabilityItem capabilityItem) {
      return capabilityItem != null && !capabilityItem.isEmpty() && capabilityItem.getAutoAttackMotion(playerPatch) != null
         && !capabilityItem.getAutoAttackMotion(playerPatch).isEmpty();
   }

   private static boolean isWeaponInnateBoundToVanillaAttack() {
      InputConstants.Key attackKey = Minecraft.getInstance().options.keyAttack.getKey();
      InputConstants.Key weaponInnateKey = EpicFightKeyMappings.WEAPON_INNATE_SKILL.getKey();
      return attackKey.getType() == weaponInnateKey.getType() && attackKey.getValue() == weaponInnateKey.getValue();
   }

   private static int getPlayerTick() {
      LocalPlayer player = Minecraft.getInstance().player;
      return player != null ? player.tickCount : 0;
   }

   private static void drainPendingAttackClicks() {
      while (Minecraft.getInstance().options.keyAttack.consumeClick()) {
      }

      while (EpicFightKeyMappings.ATTACK.consumeClick()) {
      }

      while (EpicFightKeyMappings.WEAPON_INNATE_SKILL.consumeClick()) {
      }
   }

   private static void requestComboAttack() {
      LocalPlayerPatch playerPatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
      LocalPlayer player = Minecraft.getInstance().player;
      if (playerPatch == null || player == null) {
         return;
      }

      SkillContainer comboAttacks = playerPatch.getSkill(SkillSlots.COMBO_ATTACKS);
      if (comboAttacks == null || comboAttacks.isEmpty()) {
         return;
      }

      ControlEngine controlEngine = ControlEngine.getInstance();
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      clearSameKeyAttackState(controlEngine);
      SkillCastEvent skillCastEvent = comboAttacks.sendCastRequest(playerPatch, controlEngine);
      if (skillCastEvent.isExecutable()) {
         player.resetAttackStrengthTicker();
         controlEngine.releaseAllServedKeys();
      } else if (!player.isSpectator()) {
         accessor.efn$invokeReserveKey(SkillSlots.COMBO_ATTACKS, EpicFightInputAction.ATTACK);
      }
      controlEngine.lockHotkeys();
      clearSameKeyAttackState(controlEngine);
   }

   private static boolean requestWeaponInnate() {
      LocalPlayerPatch playerPatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
      LocalPlayer player = Minecraft.getInstance().player;
      if (playerPatch == null || player == null) {
         return false;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate == null || weaponInnate.isEmpty()) {
         return false;
      }

      ControlEngine controlEngine = ControlEngine.getInstance();
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      clearSameKeyAttackState(controlEngine);
      ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, true);
      SkillCastEvent skillCastEvent = weaponInnate.sendCastRequest(playerPatch, controlEngine);
      if (skillCastEvent.shouldReserveKey() && !player.isSpectator()) {
         accessor.efn$invokeReserveKey(SkillSlots.WEAPON_INNATE, EpicFightInputAction.WEAPON_INNATE_SKILL);
      } else {
         controlEngine.lockHotkeys();
         ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
      }
      clearSameKeyAttackState(controlEngine);
      return true;
   }

   private static void clearSameKeyAttackState(ControlEngine controlEngine) {
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      accessor.efn$setWeaponInnatePressToggle(false);
      accessor.efn$setAttackLightPressToggle(false);
      accessor.efn$setWeaponInnatePressCounter(0);
   }
}

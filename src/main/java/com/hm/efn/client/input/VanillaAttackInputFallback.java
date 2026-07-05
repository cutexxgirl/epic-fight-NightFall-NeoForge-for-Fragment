package com.hm.efn.client.input;

import com.hm.efn.util.EFNBasicAttackRouting;
import com.mojang.blaze3d.platform.InputConstants;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import yesman.epicfight.api.client.input.InputManager;
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
   private static int attackPressStartTick;

   private VanillaAttackInputFallback() {
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onMouseInput(Pre event) {
      if (!isVanillaAttackButton(event.getButton())) {
         return;
      }

      if (event.getAction() == InputConstants.PRESS) {
         trackingAttackPress = shouldUseFallback();
         attackPressStartTick = getPlayerTick();
      } else if (event.getAction() == InputConstants.RELEASE) {
         if (trackingAttackPress) {
            int heldTicks = Math.max(1, getPlayerTick() - attackPressStartTick);
            if (heldTicks <= ClientConfig.holdingThreshold + 1 && shouldUseFallback() && !ControlEngine.getInstance().weaponInnateToggling()) {
               drainPendingAttackClicks();
               requestComboAttack();
            }
         }

         trackingAttackPress = false;
      }
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

      if (!InputManager.isBoundToSamePhysicalInput(EpicFightInputAction.ATTACK, EpicFightInputAction.WEAPON_INNATE_SKILL)
         && !isEpicFightAttackUnbound()) {
         return false;
      }

      LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
      if (playerPatch == null || !playerPatch.isEpicFightMode() || !playerPatch.canPlayAttackAnimation()) {
         return false;
      }

      ItemStack heldItem = playerPatch.getOriginal().getMainHandItem();
      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(heldItem);
      if (capabilityItem == null || capabilityItem.isEmpty()) {
         return true;
      }

      boolean hasInvincibleComboInnate = capabilityItem.getInnateSkill(playerPatch, heldItem) instanceof ComboBasicAttack;
      return !hasInvincibleComboInnate || EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(playerPatch, capabilityItem);
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
   }

   private static boolean isEpicFightAttackUnbound() {
      return EpicFightKeyMappings.ATTACK.getKey() == InputConstants.UNKNOWN || EpicFightKeyMappings.ATTACK.getKey().getValue() == -1;
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
      SkillCastEvent skillCastEvent = comboAttacks.sendCastRequest(playerPatch, controlEngine);
      if (skillCastEvent.isExecutable()) {
         player.resetAttackStrengthTicker();
         controlEngine.releaseAllServedKeys();
         controlEngine.lockHotkeys();
      }
   }
}

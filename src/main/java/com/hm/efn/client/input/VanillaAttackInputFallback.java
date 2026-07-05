package com.hm.efn.client.input;

import com.hm.efn.EFN;
import com.hm.efn.mixin.ControlEngineInvoker;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public final class VanillaAttackInputFallback {
   private static boolean trackingAttackPress;
   private static int attackPressStartTick;
   private static FallbackRoute trackingRoute = FallbackRoute.NONE;

   private enum FallbackRoute {
      NONE,
      STANDARD_EPICFIGHT,
      EFN_INNATE
   }

   private VanillaAttackInputFallback() {
   }

   public static void register(IEventBus gameBus) {
      gameBus.addListener(EventPriority.HIGHEST, VanillaAttackInputFallback::onMouseInput);
      gameBus.addListener(VanillaAttackInputFallback::onClientTick);
      trace("registered manual NeoForge input listeners");
   }

   public static void onMouseInput(Pre event) {
      if (!isVanillaAttackButton(event.getButton())) {
         if (event.getButton() == 0) {
            trace("mouse ignored button={} action={} vanillaAttackKey={}", event.getButton(), event.getAction(), Minecraft.getInstance().options.keyAttack.getKey());
         }

         return;
      }

      trace("mouse vanilla-attack button={} action={} trackingBefore={}", event.getButton(), event.getAction(), trackingAttackPress);
      if (event.getAction() == InputConstants.PRESS) {
         FallbackRoute fallbackRoute = getFallbackRoute("press");
         boolean hasComboAttackSlot = hasComboAttackSlot();
         trackingAttackPress = false;
         trackingRoute = FallbackRoute.NONE;
         attackPressStartTick = getPlayerTick();
         trace("press decision route={} comboSlot={} startTick={}", fallbackRoute, hasComboAttackSlot, attackPressStartTick);
         if (fallbackRoute == FallbackRoute.EFN_INNATE && hasComboAttackSlot) {
            trackingAttackPress = true;
            trackingRoute = fallbackRoute;
            trace("press handed to invincible raw input route={} tracking={}", fallbackRoute, trackingAttackPress);
            return;
         }

         if (fallbackRoute == FallbackRoute.STANDARD_EPICFIGHT && hasComboAttackSlot) {
            if (!isEpicFightAttackBoundToVanillaAttack()) {
               event.setCanceled(true);
               drainPendingAttackClicks();
               invokeNativeEpicFightAttack(ControlEngine.getInstance());
            } else {
               trace("press left to native epicfight input route={}", fallbackRoute);
            }
         }
      } else if (event.getAction() == InputConstants.RELEASE) {
         if (trackingAttackPress) {
            int heldTicks = Math.max(1, getPlayerTick() - attackPressStartTick);
            trace("release handed to invincible raw input route={} heldTicks={}", trackingRoute, heldTicks);
         }

         trackingAttackPress = false;
         trackingRoute = FallbackRoute.NONE;
      }
   }

   public static void onClientTick(Post event) {
      // The active press is handled by Epic Fight's own same-key attack/innate router.
   }

   public static boolean shouldSuppressSeparateWeaponInnate() {
      return trackingAttackPress && trackingRoute == FallbackRoute.EFN_INNATE && isWeaponInnateBoundToVanillaAttack();
   }

   private static boolean isVanillaAttackButton(int button) {
      return Minecraft.getInstance().options.keyAttack.getKey().getType() == InputConstants.Type.MOUSE
         && Minecraft.getInstance().options.keyAttack.getKey().getValue() == button;
   }

   private static FallbackRoute getFallbackRoute(String source) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen != null || minecraft.player == null || minecraft.level == null || minecraft.isPaused()) {
         trace("{} fallback=false reason=context screen={} player={} level={} paused={}", source, minecraft.screen, minecraft.player != null, minecraft.level != null, minecraft.isPaused());
         return FallbackRoute.NONE;
      }

      LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
      if (playerPatch == null || !playerPatch.isEpicFightMode()) {
         trace("{} fallback=false reason=patch-or-mode patch={} epicMode={}", source, playerPatch != null, playerPatch != null && playerPatch.isEpicFightMode());
         return FallbackRoute.NONE;
      }

      ItemStack heldItem = playerPatch.getOriginal().getMainHandItem();
      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(heldItem);
      boolean canPlayAttackAnimation = playerPatch.canPlayAttackAnimation();
      if (!canPlayAttackAnimation && !hasAutoAttackMotions(playerPatch, capabilityItem)) {
         trace(
            "{} fallback=false reason=no-canplay-no-auto item={} canPlay={} capEmpty={} autoCount={}",
            source,
            itemName(heldItem),
            canPlayAttackAnimation,
            capabilityItem == null || capabilityItem.isEmpty(),
            autoAttackCount(playerPatch, capabilityItem)
         );
         return FallbackRoute.NONE;
      }

      if (capabilityItem == null || capabilityItem.isEmpty()) {
         FallbackRoute route = canPlayAttackAnimation ? FallbackRoute.STANDARD_EPICFIGHT : FallbackRoute.NONE;
         trace("{} fallback={} route={} reason=empty-capability item={} canPlay={}", source, canPlayAttackAnimation, route, itemName(heldItem), canPlayAttackAnimation);
         return route;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate != null && weaponInnate.getSkill() instanceof EFNWeaponInnateBase skill && capabilityItem.getInnateSkill(playerPatch, heldItem) == skill) {
         boolean letEpicFightRun = EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(playerPatch, capabilityItem);
         FallbackRoute route = letEpicFightRun ? FallbackRoute.STANDARD_EPICFIGHT : FallbackRoute.EFN_INNATE;
         trace(
            "{} fallback={} route={} reason=efn-innate item={} skill={} canPlay={} autoCount={}",
            source,
            true,
            route,
            itemName(heldItem),
            skillName(skill),
            canPlayAttackAnimation,
            autoAttackCount(playerPatch, capabilityItem)
         );
         return route;
      }

      trace(
         "{} fallback=true route={} reason=standard-capability item={} weaponInnate={} itemInnate={} canPlay={} autoCount={}",
         source,
         FallbackRoute.STANDARD_EPICFIGHT,
         itemName(heldItem),
         skillName(weaponInnate),
         skillName(capabilityItem.getInnateSkill(playerPatch, heldItem)),
         canPlayAttackAnimation,
         autoAttackCount(playerPatch, capabilityItem)
      );
      return FallbackRoute.STANDARD_EPICFIGHT;
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

   private static boolean isEpicFightAttackBoundToVanillaAttack() {
      InputConstants.Key attackKey = Minecraft.getInstance().options.keyAttack.getKey();
      InputConstants.Key epicFightAttackKey = EpicFightKeyMappings.ATTACK.getKey();
      return attackKey.getType() == epicFightAttackKey.getType() && attackKey.getValue() == epicFightAttackKey.getValue();
   }

   private static boolean hasComboAttackSlot() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayerPatch playerPatch = minecraft.player != null ? EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class) : null;
      SkillContainer comboAttacks = playerPatch != null ? playerPatch.getSkill(SkillSlots.COMBO_ATTACKS) : null;
      boolean hasComboAttackSlot = comboAttacks != null && !comboAttacks.isEmpty();
      if (!hasComboAttackSlot) {
         trace("press fallback=false reason=no-combo-slot container={} skill={}", comboAttacks != null, skillName(comboAttacks));
      }

      return hasComboAttackSlot;
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

   private static void invokeNativeEpicFightAttack(ControlEngine controlEngine) {
      if (controlEngine.getPlayerPatch() == null) {
         trace("press native epicfight attack skipped reason=no-control-engine-playerpatch");
         return;
      }

      ((ControlEngineInvoker)controlEngine).efn$invokeMaybeAttack();
      trace(
         "press invoked native epicfight attack attackKey={} vanillaKey={} innateKey={}",
         EpicFightKeyMappings.ATTACK.getKey(),
         Minecraft.getInstance().options.keyAttack.getKey(),
         EpicFightKeyMappings.WEAPON_INNATE_SKILL.getKey()
      );
   }

   private static int autoAttackCount(LocalPlayerPatch playerPatch, CapabilityItem capabilityItem) {
      if (capabilityItem == null || capabilityItem.isEmpty()) {
         return 0;
      }

      List<?> autoAttacks = capabilityItem.getAutoAttackMotion(playerPatch);
      return autoAttacks != null ? autoAttacks.size() : 0;
   }

   private static String itemName(ItemStack itemStack) {
      return itemStack == null || itemStack.isEmpty() ? "empty" : String.valueOf(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
   }

   private static String skillName(SkillContainer container) {
      return container == null ? "null" : skillName(container.getSkill());
   }

   private static String skillName(Skill skill) {
      return skill == null ? "null" : String.valueOf(skill.getRegistryName());
   }

   private static void trace(String message, Object... args) {
      EFN.LOGGER.info("[EFN/InputTrace] " + message, args);
   }
}

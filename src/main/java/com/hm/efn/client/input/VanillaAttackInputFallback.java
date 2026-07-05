package com.hm.efn.client.input;

import com.hm.efn.EFN;
import com.hm.efn.mixin.ControlEngineAccessor;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public final class VanillaAttackInputFallback {
   private static boolean trackingAttackPress;
   private static boolean longPressTriggered;
   private static int attackPressStartTick;

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
         trackingAttackPress = shouldUseFallback("press") && hasComboAttackSlot();
         longPressTriggered = false;
         attackPressStartTick = getPlayerTick();
         trace("press decision tracking={} startTick={}", trackingAttackPress, attackPressStartTick);
         if (trackingAttackPress) {
            event.setCanceled(true);
            drainPendingAttackClicks();
            requestComboAttack();
         }
      } else if (event.getAction() == InputConstants.RELEASE) {
         if (trackingAttackPress) {
            event.setCanceled(true);
            int heldTicks = Math.max(1, getPlayerTick() - attackPressStartTick);
            trace("release heldTicks={} longPressTriggered={}", heldTicks, longPressTriggered);
            ControlEngine.setKeyBind(EpicFightKeyMappings.ATTACK, false);
            ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
            clearSameKeyAttackState(ControlEngine.getInstance());
            trace("release finished direct standard attack route heldTicks={}", heldTicks);
         }

         trackingAttackPress = false;
         longPressTriggered = false;
      }
   }

   public static void onClientTick(Post event) {
      // The active press is handled by Epic Fight's own same-key attack/innate router.
   }

   public static boolean shouldSuppressSeparateWeaponInnate() {
      return trackingAttackPress && isWeaponInnateBoundToVanillaAttack() && shouldUseFallback("suppress-separate-innate");
   }

   private static boolean isVanillaAttackButton(int button) {
      return Minecraft.getInstance().options.keyAttack.getKey().getType() == InputConstants.Type.MOUSE
         && Minecraft.getInstance().options.keyAttack.getKey().getValue() == button;
   }

   private static boolean shouldUseFallback(String source) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen != null || minecraft.player == null || minecraft.level == null || minecraft.isPaused()) {
         trace("{} fallback=false reason=context screen={} player={} level={} paused={}", source, minecraft.screen, minecraft.player != null, minecraft.level != null, minecraft.isPaused());
         return false;
      }

      LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
      if (playerPatch == null || !playerPatch.isEpicFightMode()) {
         trace("{} fallback=false reason=patch-or-mode patch={} epicMode={}", source, playerPatch != null, playerPatch != null && playerPatch.isEpicFightMode());
         return false;
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
         return false;
      }

      if (capabilityItem == null || capabilityItem.isEmpty()) {
         trace("{} fallback={} reason=empty-capability item={} canPlay={}", source, canPlayAttackAnimation, itemName(heldItem), canPlayAttackAnimation);
         return canPlayAttackAnimation;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate != null && weaponInnate.getSkill() instanceof EFNWeaponInnateBase skill && capabilityItem.getInnateSkill(playerPatch, heldItem) == skill) {
         boolean letEpicFightRun = EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(playerPatch, capabilityItem);
         trace(
            "{} fallback={} reason=efn-innate item={} skill={} canPlay={} autoCount={}",
            source,
            letEpicFightRun,
            itemName(heldItem),
            skillName(skill),
            canPlayAttackAnimation,
            autoAttackCount(playerPatch, capabilityItem)
         );
         return letEpicFightRun;
      }

      trace(
         "{} fallback=true reason=standard-capability item={} weaponInnate={} itemInnate={} canPlay={} autoCount={}",
         source,
         itemName(heldItem),
         skillName(weaponInnate),
         skillName(capabilityItem.getInnateSkill(playerPatch, heldItem)),
         canPlayAttackAnimation,
         autoAttackCount(playerPatch, capabilityItem)
      );
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

   private static void requestComboAttack() {
      LocalPlayerPatch playerPatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
      LocalPlayer player = Minecraft.getInstance().player;
      if (playerPatch == null || player == null) {
         return;
      }

      SkillContainer comboAttacks = playerPatch.getSkill(SkillSlots.COMBO_ATTACKS);
      if (comboAttacks == null || comboAttacks.isEmpty()) {
         trace("combo request skipped reason=empty-container container={} skill={}", comboAttacks != null, skillName(comboAttacks));
         return;
      }

      ControlEngine controlEngine = ControlEngine.getInstance();
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      Set<CustomPacketPayload> queuedPackets = accessor.efn$getPacketsToSend();
      int packetsBefore = queuedPackets.size();
      clearSameKeyAttackState(controlEngine);
      ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
      ControlEngine.setKeyBind(EpicFightKeyMappings.ATTACK, true);
      SkillCastEvent skillCastEvent = comboAttacks.sendCastRequest(playerPatch, controlEngine);
      trace(
         "combo request direct skill={} creative={} spectator={} executable={} skillExecutable={} stateExecutable={} reserve={} packetsBefore={} packetsAfter={}",
         skillName(comboAttacks),
         player.isCreative(),
         player.isSpectator(),
         skillCastEvent.isExecutable(),
         skillCastEvent.isSkillExecutable(),
         skillCastEvent.isStateExecutable(),
         skillCastEvent.shouldReserveKey(),
         packetsBefore,
         queuedPackets.size()
      );
      if (skillCastEvent.isExecutable()) {
         player.resetAttackStrengthTicker();
         controlEngine.releaseAllServedKeys();
      } else if (skillCastEvent.shouldReserveKey() && !player.isSpectator()) {
         accessor.efn$invokeReserveKey(SkillSlots.COMBO_ATTACKS, EpicFightInputAction.ATTACK);
      }

      controlEngine.lockHotkeys();
      flushQueuedPackets(accessor);
      ControlEngine.setKeyBind(EpicFightKeyMappings.ATTACK, false);
      ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
      clearSameKeyAttackState(controlEngine);
   }

   private static void primeEpicFightSameKeyAttack(ControlEngine controlEngine) {
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      clearSameKeyAttackState(controlEngine);
      ControlEngine.setKeyBind(EpicFightKeyMappings.ATTACK, false);
      ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, true);
      accessor.efn$setWeaponInnatePressToggle(true);
      accessor.efn$setWeaponInnatePressCounter(0);
      trace(
         "press primed epicfight same-key router attackKey={} innateKey={} threshold={}",
         EpicFightKeyMappings.ATTACK.getKey(),
         EpicFightKeyMappings.WEAPON_INNATE_SKILL.getKey(),
         ClientConfig.holdingThreshold
      );
   }

   private static boolean requestWeaponInnate() {
      LocalPlayerPatch playerPatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
      LocalPlayer player = Minecraft.getInstance().player;
      if (playerPatch == null || player == null) {
         return false;
      }

      SkillContainer weaponInnate = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnate == null || weaponInnate.isEmpty()) {
         trace("innate request skipped reason=empty-container container={} skill={}", weaponInnate != null, skillName(weaponInnate));
         return false;
      }

      ControlEngine controlEngine = ControlEngine.getInstance();
      ControlEngineAccessor accessor = (ControlEngineAccessor)controlEngine;
      clearSameKeyAttackState(controlEngine);
      ControlEngine.setKeyBind(EpicFightKeyMappings.WEAPON_INNATE_SKILL, true);
      SkillCastEvent skillCastEvent = weaponInnate.sendCastRequest(playerPatch, controlEngine);
      trace(
         "innate request skill={} creative={} spectator={} executable={} skillExecutable={} stateExecutable={} reserve={}",
         skillName(weaponInnate),
         player.isCreative(),
         player.isSpectator(),
         skillCastEvent.isExecutable(),
         skillCastEvent.isSkillExecutable(),
         skillCastEvent.isStateExecutable(),
         skillCastEvent.shouldReserveKey()
      );
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

   private static void flushQueuedPackets(ControlEngineAccessor accessor) {
      Set<CustomPacketPayload> queuedPackets = accessor.efn$getPacketsToSend();
      if (queuedPackets.isEmpty()) {
         trace("combo request flush packets=0");
         return;
      }

      List<CustomPacketPayload> packets = List.copyOf(queuedPackets);
      for (CustomPacketPayload packet : packets) {
         EpicFightNetworkManager.sendToServer(packet);
      }

      queuedPackets.removeAll(packets);
      trace("combo request flush packets={}", packets.size());
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

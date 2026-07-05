package com.hm.efn.mixin;

import com.hm.efn.EFN;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.hm.efn.util.EFNInputKeyUtil;
import com.google.common.collect.BiMap;
import com.p1nero.invincible.InvincibleConfig;
import com.p1nero.invincible.api.Side;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.api.combo.ComboType;
import com.p1nero.invincible.attachment.InvinciblePlayer;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InputManager;
import com.p1nero.invincible.gameassets.InvincibleConditions;
import com.p1nero.invincible.gameassets.InvincibleSkillDataKeys;
import com.p1nero.invincible.skill.ComboBasicAttack;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.PlayerMode;
import yesman.epicfight.api.event.types.player.SkillCastEvent;

@Mixin(value = InputManager.class, remap = false)
public abstract class MixinInputManager {
   @Final
   @Shadow(remap = false)
   private static BiMap<ComboType, KeyMapping> TYPE_KEY_MAP;
   @Shadow(remap = false)
   private static long lastInputTime;
   @Shadow(remap = false)
   private static ComboNode currentNode;
   @Unique
   private static final Map<ComboType, Integer> epicFight_Nightfall$efnActiveKeys = new HashMap<>();
   @Unique
   private static final Map<ComboType, int[]> epicFight_Nightfall$efnInputBuffer = new HashMap<>();

   @Shadow(remap = false)
   private static boolean shouldHandleInput() {
      return false;
   }

   @Shadow(remap = false)
   public static ComboBasicAttack getComboBasicSkill() {
      return null;
   }

   @Shadow(remap = false)
   public static void clearKeyCache() {
   }

   @Shadow(remap = false)
   public static boolean testClientConditions(ComboType comboType) {
      return false;
   }

   @Shadow(remap = false)
   private static void maybeHandleControlifyRelease() {
   }

   @Inject(method = "onClientTick", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void efn$hijackTick(Post event, CallbackInfo ci) {
      if (Minecraft.getInstance().player != null) {
         LocalPlayerPatch localPlayerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
         if (localPlayerPatch != null && shouldHandleInput() && Minecraft.getInstance().getConnection() != null) {
            SkillContainer container = efn$getHandledWeaponInnate(localPlayerPatch);
            if (container == null) {
               efn$clearHijackState();
               if (efn$shouldSuppressInvincibleInput(localPlayerPatch)) {
                  ci.cancel();
               }
               return;
            }

            ci.cancel();
            maybeHandleControlifyRelease();
            Iterator<Map.Entry<ComboType, int[]>> iterator = epicFight_Nightfall$efnInputBuffer.entrySet().iterator();

            while (iterator.hasNext()) {
               int[] input = iterator.next().getValue();
               input[1]--;
               if (input[1] <= 0) {
                  iterator.remove();
               }
            }

            int maxPressTick = getComboBasicSkill() != null ? getComboBasicSkill().getMaxPressTime() : (Integer)InvincibleConfig.MAX_PRESS_TICK.get();
            List<ComboType> keysToBuffer = new ArrayList<>();
            epicFight_Nightfall$efnActiveKeys.replaceAll((comboType, ticks) -> {
               int newTicks = ticks + 1;
               if (newTicks >= maxPressTick) {
                  keysToBuffer.add(comboType);
               }

               return newTicks;
            });

            for (ComboType comboType : keysToBuffer) {
               int reserveTime = getComboBasicSkill() != null ? getComboBasicSkill().getMaxReserveTime() : (Integer)InvincibleConfig.RESERVE_TICK.get();
               epicFight_Nightfall$efnInputBuffer.put(comboType, new int[]{epicFight_Nightfall$efnActiveKeys.remove(comboType), reserveTime});
            }

            if (!epicFight_Nightfall$efnInputBuffer.isEmpty() || !epicFight_Nightfall$efnActiveKeys.isEmpty()) {
               efn$tryRequestSkillExecute();
            }

            Options options = Minecraft.getInstance().options;
            SkillDataManager manager = container.getDataManager();
            efn$checkDirectionKeyDown(manager, InvincibleSkillDataKeys.UP, options.keyUp);
            efn$checkDirectionKeyDown(manager, InvincibleSkillDataKeys.DOWN, options.keyDown);
            efn$checkDirectionKeyDown(manager, InvincibleSkillDataKeys.LEFT, options.keyLeft);
            efn$checkDirectionKeyDown(manager, InvincibleSkillDataKeys.RIGHT, options.keyRight);
         }
      } else {
         efn$clearHijackState();
      }
   }

   @Inject(method = "onVanillaMouseOrKeyInput", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void efn$hijackInput(int action, int key, CallbackInfo ci) {
      if (shouldHandleInput()) {
         LocalPlayerPatch localPlayerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
         if (efn$getHandledWeaponInnate(localPlayerPatch) == null) {
            efn$clearHijackState();
            if (efn$shouldSuppressInvincibleInput(localPlayerPatch)) {
               ci.cancel();
            }
            return;
         }

         ci.cancel();
         boolean mouseInput = efn$isMouseInput(key);
         if (action == 1) {
            efn$handlePress(mouseInput, key);
         } else if (action == 0) {
            if (efn$handleRelease(mouseInput, key)) {
               efn$tryRequestSkillExecute();
            }
         }
      }
   }

   @Unique
   private static void efn$handlePress(boolean mouseInput, int key) {
      for (Map.Entry<ComboType, KeyMapping> entry : TYPE_KEY_MAP.entrySet()) {
         if (efn$matchesComboInput(entry.getKey(), entry.getValue(), mouseInput, key)) {
            epicFight_Nightfall$efnActiveKeys.put(entry.getKey(), 1);
            epicFight_Nightfall$efnInputBuffer.remove(entry.getKey());
         }
      }
   }

   @Unique
   private static boolean efn$handleRelease(boolean mouseInput, int key) {
      boolean handled = false;
      for (Map.Entry<ComboType, KeyMapping> entry : TYPE_KEY_MAP.entrySet()) {
         if (efn$matchesComboInput(entry.getKey(), entry.getValue(), mouseInput, key) && epicFight_Nightfall$efnActiveKeys.containsKey(entry.getKey())) {
            int ticksHeld = epicFight_Nightfall$efnActiveKeys.remove(entry.getKey());
            int reserveTime = getComboBasicSkill() != null ? getComboBasicSkill().getMaxReserveTime() : (Integer)InvincibleConfig.RESERVE_TICK.get();
            epicFight_Nightfall$efnInputBuffer.put(entry.getKey(), new int[]{ticksHeld, reserveTime});
            handled = true;
         }
      }

      return handled;
   }

   @Unique
   private static boolean efn$matchesComboInput(ComboType comboType, KeyMapping keyMapping, boolean mouseInput, int key) {
      if (comboType == ComboNode.ComboTypes.KEY_1 && EFNInputKeyUtil.matches(Minecraft.getInstance().options.keyAttack, mouseInput, key)) {
         return true;
      }

      return EFNInputKeyUtil.matches(keyMapping, mouseInput, key);
   }

   @Unique
   private static boolean efn$isMouseInput(int key) {
      if (key == Minecraft.getInstance().options.keyAttack.getKey().getValue()
         && Minecraft.getInstance().options.keyAttack.getKey().getType() == InputConstants.Type.MOUSE) {
         return true;
      }

      for (KeyMapping keyMapping : TYPE_KEY_MAP.values()) {
         if (keyMapping.getKey().getType() == InputConstants.Type.MOUSE && keyMapping.getKey().getValue() == key) {
            return true;
         }
      }

      return false;
   }

   @Unique
   private static boolean efn$shouldSuppressInvincibleInput(LocalPlayerPatch localPlayerPatch) {
      if (localPlayerPatch == null) {
         return false;
      }

      SkillContainer container = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || !(container.getSkill() instanceof EFNWeaponInnateBase skill)) {
         return false;
      }

      ItemStack mainHandItem = localPlayerPatch.getOriginal().getMainHandItem();
      return EpicFightCapabilities.getItemCapability(mainHandItem)
         .filter(capabilityItem -> !capabilityItem.isEmpty())
         .filter(capabilityItem -> capabilityItem.getInnateSkill(localPlayerPatch, mainHandItem) == skill)
         .map(capabilityItem -> EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(localPlayerPatch, capabilityItem))
         .orElse(false);
   }

   @Unique
   private static SkillContainer efn$getHandledWeaponInnate(LocalPlayerPatch localPlayerPatch) {
      if (localPlayerPatch == null) {
         return null;
      }

      SkillContainer container = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || !(container.getSkill() instanceof EFNWeaponInnateBase skill)) {
         return null;
      }

      ItemStack mainHandItem = localPlayerPatch.getOriginal().getMainHandItem();
      boolean itemUsesHandledEfnInnate = EpicFightCapabilities.getItemCapability(mainHandItem)
         .filter(capabilityItem -> !capabilityItem.isEmpty())
         .filter(capabilityItem -> capabilityItem.getInnateSkill(localPlayerPatch, mainHandItem) == skill)
         .map(capabilityItem -> !EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(localPlayerPatch, capabilityItem))
         .orElse(false);
      return itemUsesHandledEfnInnate ? container : null;
   }

   @Unique
   private static void efn$clearHijackState() {
      epicFight_Nightfall$efnActiveKeys.clear();
      epicFight_Nightfall$efnInputBuffer.clear();
   }

   @Unique
   private static void efn$tryRequestSkillExecute() {
      LocalPlayerPatch executor = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
      if (executor != null && executor.getPlayerMode() == PlayerMode.EPICFIGHT) {
         SkillContainer container = executor.getSkill(SkillSlots.WEAPON_INNATE);
         if (container == null || container.getSkill() == null) {
            epicFight_Nightfall$efnActiveKeys.clear();
            epicFight_Nightfall$efnInputBuffer.clear();
            return;
         }

         List<CPSkillRequest> packets = efn$getAvailablePackets(container);
         if (!packets.isEmpty()) {
            SkillCastEvent clientCastEvent = new SkillCastEvent(executor, container, null);
            boolean clientCanUse = container.canUse(executor, clientCastEvent);
            EFN.LOGGER.info(
               "[EFN/InputTrace] invincible-hijack request skill={} packets={} clientCanUse={} creative={} mode={} item={}",
               efn$skillName(container),
               packets.size(),
               clientCanUse,
               Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative(),
               executor.getPlayerMode(),
               efn$itemName(executor.getOriginal().getMainHandItem())
            );

            for (CPSkillRequest packet : packets) {
               EpicFightNetworkManager.sendToServer(packet);
            }

            epicFight_Nightfall$efnActiveKeys.clear();
            epicFight_Nightfall$efnInputBuffer.clear();
            clearKeyCache();
         }
      }
   }

   @Unique
   private static List<CPSkillRequest> efn$getAvailablePackets(SkillContainer container) {
      List<CPSkillRequest> list = new ArrayList<>();
      if (container == null || container.getSkill() == null || container.getExecutor() == null || container.getExecutor().getOriginal() == null) {
         return list;
      }

      List<ComboType> typeList = new ArrayList<>(ComboType.ENUM_MANAGER.universalValues().stream().toList());
      typeList.sort(Comparator.comparingInt(comboTypex -> -1 * comboTypex.getSubTypes().size()));
      InvinciblePlayer invinciblePlayer = InvincibleAttachments.getPlayer((Player)container.getExecutor().getOriginal());
      currentNode = invinciblePlayer.getCurrentLogicNode();

      for (ComboType comboType : typeList) {
         int pressedTime = efn$getEffectivePressedTime(comboType);
         if (pressedTime > 0 && testClientConditions(comboType)) {
            boolean requiresCharge = false;
            ComboNode nextNode = currentNode != null ? currentNode.getNext(comboType) : null;
            if (nextNode != null) {
               for (Condition condition : nextNode.getConditions(new Side[]{Side.SERVER, Side.BOTH})) {
                  if (condition.equals(InvincibleConditions.PRESS_TIME_CONDITION.get())) {
                     requiresCharge = true;
                     break;
                  }
               }
            }

            if (!requiresCharge && pressedTime > 20) {
               pressedTime = 1;
            }

            long interval = System.currentTimeMillis() - lastInputTime;
            CPSkillRequest packet = InputManager.getExecutePacket(container.getSlot(), comboType, pressedTime, interval);
            list.add(packet);
            if (!comboType.getSubTypes().isEmpty()) {
               break;
            }
         }
      }

      if (!list.isEmpty()) {
         lastInputTime = System.currentTimeMillis();
      }

      return list;
   }

   @Unique
   private static int efn$getEffectivePressedTime(ComboType comboType) {
      if (comboType.getSubTypes().isEmpty()) {
         KeyMapping keyMapping = TYPE_KEY_MAP.get(comboType);
         if (keyMapping == null) {
            return 0;
         }

         int[] bufferedInput = epicFight_Nightfall$efnInputBuffer.get(comboType);
         if (bufferedInput != null) {
            return bufferedInput[0];
         }

         return epicFight_Nightfall$efnActiveKeys.getOrDefault(comboType, 0);
      } else {
         int max = 0;

         for (ComboType subType : comboType.getSubTypes()) {
            int cur = efn$getEffectivePressedTime(subType);
            if (cur == 0) {
               return 0;
            }

            if (cur > max) {
               max = cur;
            }
         }

         return max;
      }
   }

   @Unique
   private static void efn$checkDirectionKeyDown(
      SkillDataManager manager, DeferredHolder<yesman.epicfight.skill.SkillDataKey<?>, ? extends yesman.epicfight.skill.SkillDataKey<Boolean>> skillDataKey, KeyMapping key
   ) {
      if (!manager.hasData(skillDataKey)) {
         return;
      }

      if ((Boolean)manager.getDataValue(skillDataKey) != key.isDown()) {
         manager.setDataSync(skillDataKey, key.isDown());
      }
   }

   @Unique
   private static String efn$skillName(SkillContainer container) {
      return container == null || container.getSkill() == null ? "null" : String.valueOf(container.getSkill().getRegistryName());
   }

   @Unique
   private static String efn$itemName(ItemStack itemStack) {
      return itemStack == null || itemStack.isEmpty() ? "empty" : String.valueOf(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
   }
}

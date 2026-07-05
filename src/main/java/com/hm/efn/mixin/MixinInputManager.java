package com.hm.efn.mixin;

import com.p1nero.invincible.InvincibleConfig;
import com.google.common.collect.BiMap;
import com.p1nero.invincible.api.Side;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.api.combo.ComboType;
import com.p1nero.invincible.attachment.InvinciblePlayer;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InputManager;
import com.p1nero.invincible.gameassets.InvincibleConditions;
import com.p1nero.invincible.gameassets.InvincibleSkillDataKeys;
import com.p1nero.invincible.skill.ComboBasicAttack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
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
   private static final Map<Integer, Integer> epicFight_Nightfall$efnActiveKeys = new HashMap<>();
   @Unique
   private static final Queue<int[]> epicFight_Nightfall$efnInputBuffer = new LinkedList<>();

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
               return;
            }

            ci.cancel();
            maybeHandleControlifyRelease();
            Iterator<int[]> iterator = epicFight_Nightfall$efnInputBuffer.iterator();

            while (iterator.hasNext()) {
               int[] input = iterator.next();
               input[2]--;
               if (input[2] <= 0) {
                  iterator.remove();
               }
            }

            int maxPressTick = getComboBasicSkill() != null ? getComboBasicSkill().getMaxPressTime() : (Integer)InvincibleConfig.MAX_PRESS_TICK.get();
            List<Integer> keysToBuffer = new ArrayList<>();
            epicFight_Nightfall$efnActiveKeys.replaceAll((keyId, ticks) -> {
               int newTicks = ticks + 1;
               if (newTicks >= maxPressTick) {
                  keysToBuffer.add(keyId);
               }

               return newTicks;
            });

            for (Integer keyId : keysToBuffer) {
               int reserveTime = getComboBasicSkill() != null ? getComboBasicSkill().getMaxReserveTime() : (Integer)InvincibleConfig.RESERVE_TICK.get();
               epicFight_Nightfall$efnInputBuffer.add(new int[]{keyId, epicFight_Nightfall$efnActiveKeys.remove(keyId), reserveTime});
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
            return;
         }

         ci.cancel();
         if (action == 1) {
            for (KeyMapping keyMapping : TYPE_KEY_MAP.values()) {
               if (keyMapping.getKey().getValue() == key) {
                  epicFight_Nightfall$efnActiveKeys.put(key, 1);
               }
            }
         } else if (action == 0 && epicFight_Nightfall$efnActiveKeys.containsKey(key)) {
            int ticksHeld = epicFight_Nightfall$efnActiveKeys.remove(key);
            int reserveTime = getComboBasicSkill() != null ? getComboBasicSkill().getMaxReserveTime() : (Integer)InvincibleConfig.RESERVE_TICK.get();
            epicFight_Nightfall$efnInputBuffer.add(new int[]{key, ticksHeld, reserveTime});
            efn$tryRequestSkillExecute();
         }
      }
   }

   @Unique
   private static SkillContainer efn$getHandledWeaponInnate(LocalPlayerPatch localPlayerPatch) {
      if (localPlayerPatch == null) {
         return null;
      }

      SkillContainer container = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || !(container.getSkill() instanceof ComboBasicAttack)) {
         return null;
      }

      ItemStack mainHandItem = localPlayerPatch.getOriginal().getMainHandItem();
      boolean itemUsesComboBasicAttack = EpicFightCapabilities.getItemCapability(mainHandItem)
         .map(capabilityItem -> capabilityItem.getInnateSkill(localPlayerPatch, mainHandItem) instanceof ComboBasicAttack)
         .orElse(false);
      return itemUsesComboBasicAttack ? container : null;
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
         if (!packets.isEmpty() && container.canUse(executor, new SkillCastEvent(executor, container, null))) {
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

         int targetKeyId = keyMapping.getKey().getValue();

         for (int[] input : epicFight_Nightfall$efnInputBuffer) {
            if (input[0] == targetKeyId) {
               return input[1];
            }
         }

         return epicFight_Nightfall$efnActiveKeys.getOrDefault(targetKeyId, 0);
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
      if ((Boolean)manager.getDataValue(skillDataKey) != key.isDown()) {
         manager.setDataSync(skillDataKey, key.isDown());
      }
   }
}

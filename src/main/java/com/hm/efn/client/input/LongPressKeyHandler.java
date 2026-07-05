package com.hm.efn.client.input;

import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNBasicAttackRouting;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.Arrays;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@EventBusSubscriber(Dist.CLIENT)
public class LongPressKeyHandler {
   public static final int LONG_PRESS_THRESHOLD = 4;
   private static final int KEY_COUNT = 5;
   private static final boolean[] physicalPressed = new boolean[5];
   private static final int[] pressTicks = new int[5];
   private static final LongPressKeyHandler.KeyState[] keyStates = new LongPressKeyHandler.KeyState[5];
   private static final KeyMapping[] MAPPINGS = new KeyMapping[]{
      InvincibleKeyMappings.KEY1, InvincibleKeyMappings.KEY2, InvincibleKeyMappings.KEY3, InvincibleKeyMappings.KEY4, EpicFightKeyMappings.WEAPON_INNATE_SKILL
   };

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onKeyInput(Key inputEvent) {
      handleInput(inputEvent.getKey(), inputEvent.getAction());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onMouseInput(Pre inputEvent) {
      handleInput(inputEvent.getButton(), inputEvent.getAction());
   }

   private static void handleInput(int keyCode, int action) {
      if (keyCode != -1 && action != 2) {
         if (isMonitoredKey(keyCode)) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               LocalPlayerPatch localPlayerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
               if (localPlayerPatch != null) {
                  SkillContainer container = getHandledEfnInnate(localPlayerPatch);
                  if (container != null) {
                     SkillDataManager manager = container.getDataManager();
                     handleKeyInput(manager, keyCode, action);
                     handleConditionKeyInput(manager, keyCode, action);
                  } else {
                     resetTrackedState();
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onClientTick(Post event) {
      if (true) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            LocalPlayerPatch localPlayerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            if (localPlayerPatch != null) {
               SkillContainer container = getHandledEfnInnate(localPlayerPatch);
               if (container != null) {
                  updateKeyTimers(container.getDataManager());
               } else {
                  resetTrackedState();
               }
            }
         }
      }
   }

   private static boolean isMonitoredKey(int keyCode) {
      for (KeyMapping mapping : MAPPINGS) {
         if (mapping.getKey().getValue() == keyCode) {
            return true;
         }
      }

      Options options = Minecraft.getInstance().options;
      return keyCode == EFNKeyMappings.DEMON.getKey().getValue()
         || keyCode == EFNKeyMappings.ANGEL.getKey().getValue()
         || keyCode == EFNKeyMappings.SUMMONED_SWORD.getKey().getValue()
         || keyCode == EpicFightKeyMappings.GUARD.getKey().getValue()
         || keyCode == options.keySprint.getKey().getValue()
         || keyCode == options.keyJump.getKey().getValue()
         || keyCode == options.keyUp.getKey().getValue()
         || keyCode == options.keyDown.getKey().getValue()
         || keyCode == options.keyShift.getKey().getValue();
   }

   private static void handleKeyInput(SkillDataManager manager, int keyCode, int action) {
      for (int i = 0; i < 5; i++) {
         if (keyCode == MAPPINGS[i].getKey().getValue()) {
            if (action == 1) {
               physicalPressed[i] = true;
               pressTicks[i] = 0;
               keyStates[i] = LongPressKeyHandler.KeyState.PRESSED;
               syncKeyData(manager, i, true, false, 0);
            } else if (action == 0) {
               physicalPressed[i] = false;
               if (keyStates[i] == LongPressKeyHandler.KeyState.PRESSED) {
                  keyStates[i] = LongPressKeyHandler.KeyState.JUST_RELEASED;
               } else {
                  keyStates[i] = LongPressKeyHandler.KeyState.RELEASED;
               }

               syncKeyData(manager, i, false, false, pressTicks[i]);
            }
            break;
         }
      }
   }

   private static void handleConditionKeyInput(SkillDataManager manager, int keyCode, int action) {
      Options options = Minecraft.getInstance().options;
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.DEMON_KEY, EFNKeyMappings.DEMON);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.ANGEL_KEY, EFNKeyMappings.ANGEL);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.SUMMON_SWORD, EFNKeyMappings.SUMMONED_SWORD, true);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.GUARD_KEY, EpicFightKeyMappings.GUARD);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.SPRINT_KEY, options.keySprint);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.JUMP_KEY, options.keyJump);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.UP_KEY, options.keyUp);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.DOWN_KEY, options.keyDown);
      checkAndUpdateKey(manager, keyCode, action, EFNSKillDataKeys.SNEAK_KEY, options.keyShift);
   }

   private static void checkAndUpdateKey(
      SkillDataManager manager,
      int keyCode,
      int action,
      DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> dataKey,
      KeyMapping keyMapping
   ) {
      checkAndUpdateKey(manager, keyCode, action, dataKey, keyMapping, false);
   }

   private static void checkAndUpdateKey(
      SkillDataManager manager,
      int keyCode,
      int action,
      DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> dataKey,
      KeyMapping keyMapping,
      boolean pressOnly
   ) {
      if (dataKey != null && keyMapping != null) {
         if (keyCode == keyMapping.getKey().getValue()) {
            if (pressOnly && action == 0) {
               return;
            }

            if (!manager.hasData(dataKey)) {
               return;
            }

            boolean isDown = action != 0;
            if ((Boolean)manager.getDataValue(dataKey) != isDown) {
               manager.setDataSync(dataKey, isDown);
            }
         }
      }
   }

   private static void updateKeyTimers(SkillDataManager manager) {
      for (int i = 0; i < 5; i++) {
         if (physicalPressed[i]) {
            if (keyStates[i] == LongPressKeyHandler.KeyState.PRESSED) {
               pressTicks[i]++;
               if (pressTicks[i] >= 4) {
                  keyStates[i] = LongPressKeyHandler.KeyState.LONG_PRESSED;
                  syncKeyData(manager, i, true, true, pressTicks[i]);
               }
            } else if (keyStates[i] == LongPressKeyHandler.KeyState.LONG_PRESSED) {
               pressTicks[i]++;
            }
         } else if (keyStates[i] == LongPressKeyHandler.KeyState.JUST_RELEASED) {
            keyStates[i] = LongPressKeyHandler.KeyState.RELEASED;
         }
      }
   }

   private static void syncKeyData(SkillDataManager manager, int index, boolean isPressed, boolean isLongPress, int ticks) {
      if (isDataKeyRegistered(manager, index)) {
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> pressKey = LongPressKeyHandler.KeySuppliers.PRESS[index];
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> longPressKey = LongPressKeyHandler.KeySuppliers.LONG_PRESS[index];
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> timerKey = LongPressKeyHandler.KeySuppliers.TIMER[index];
         if (!manager.hasData(pressKey) || (Boolean)manager.getDataValue(pressKey) != isPressed) {
            manager.setDataSync(pressKey, isPressed);
         }

         if (!manager.hasData(longPressKey) || (Boolean)manager.getDataValue(longPressKey) != isLongPress) {
            manager.setDataSync(longPressKey, isLongPress);
         }

         if (!manager.hasData(timerKey) || (Integer)manager.getDataValue(timerKey) != ticks) {
            manager.setDataSync(timerKey, ticks);
         }
      }
   }

   private static boolean isDataKeyRegistered(SkillDataManager manager, int index) {
      return manager.hasData(LongPressKeyHandler.KeySuppliers.PRESS[index])
         && manager.hasData(LongPressKeyHandler.KeySuppliers.TIMER[index])
         && manager.hasData(LongPressKeyHandler.KeySuppliers.LONG_PRESS[index]);
   }

   private static SkillContainer getHandledEfnInnate(LocalPlayerPatch localPlayerPatch) {
      SkillContainer container = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || !(container.getSkill() instanceof EFNWeaponInnateBase skill)) {
         return null;
      }

      ItemStack mainHandItem = localPlayerPatch.getOriginal().getMainHandItem();
      return EpicFightCapabilities.getItemCapability(mainHandItem)
         .filter(capabilityItem -> !capabilityItem.isEmpty())
         .filter(capabilityItem -> capabilityItem.getInnateSkill(localPlayerPatch, mainHandItem) == skill)
         .filter(capabilityItem -> !EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(localPlayerPatch, capabilityItem))
         .map(capabilityItem -> container)
         .orElse(null);
   }

   private static void resetTrackedState() {
      Arrays.fill(physicalPressed, false);
      Arrays.fill(pressTicks, 0);
      Arrays.fill(keyStates, LongPressKeyHandler.KeyState.RELEASED);
   }

   public static boolean isKeyPressed(int index) {
      return index >= 0 && index < 5 && physicalPressed[index];
   }

   public static int getPressTicks(int index) {
      return index >= 0 && index < 5 ? pressTicks[index] : 0;
   }

   public static LongPressKeyHandler.KeyState getKeyState(int index) {
      return index >= 0 && index < 5 ? keyStates[index] : LongPressKeyHandler.KeyState.RELEASED;
   }

   public static boolean isLongPressed(int index) {
      return getKeyState(index) == LongPressKeyHandler.KeyState.LONG_PRESSED;
   }

   public static boolean wasShortPressed(int index) {
      return getKeyState(index) == LongPressKeyHandler.KeyState.JUST_RELEASED;
   }

   static {
      Arrays.fill(keyStates, LongPressKeyHandler.KeyState.RELEASED);
   }

   public enum KeyState {
      RELEASED,
      PRESSED,
      LONG_PRESSED,
      JUST_RELEASED;
   }

   private static final class KeySuppliers {
      private static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>>[] PRESS = new DeferredHolder[]{
         EFNSKillDataKeys.KEY1_PRESS,
         EFNSKillDataKeys.KEY2_PRESS,
         EFNSKillDataKeys.KEY3_PRESS,
         EFNSKillDataKeys.KEY4_PRESS,
         EFNSKillDataKeys.INNATE_PRESS
      };
      private static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>>[] TIMER = new DeferredHolder[]{
         EFNSKillDataKeys.KEY1_PRESS_TIMER,
         EFNSKillDataKeys.KEY2_PRESS_TIMER,
         EFNSKillDataKeys.KEY3_PRESS_TIMER,
         EFNSKillDataKeys.KEY4_PRESS_TIMER,
         EFNSKillDataKeys.INNATE_PRESS_TIMER
      };
      private static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>>[] LONG_PRESS = new DeferredHolder[]{
         EFNSKillDataKeys.KEY1_LONG_PRESS,
         EFNSKillDataKeys.KEY2_LONG_PRESS,
         EFNSKillDataKeys.KEY3_LONG_PRESS,
         EFNSKillDataKeys.KEY4_LONG_PRESS,
         EFNSKillDataKeys.INNATE_LONG_PRESS
      };
   }
}

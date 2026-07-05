package com.hm.efn.comboevents.condition.input;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNKeyLongPressCondition implements Condition<ServerPlayerPatch> {
   private final EFNKeyLongPressCondition.TargetKey targetKey;
   private EFNKeyLongPressCondition.KeyState currentState = EFNKeyLongPressCondition.KeyState.RELEASED;
   private int lastPressTicks = 0;

   public EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey targetKey) {
      this.targetKey = targetKey;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      String keyName = tag.getString("targetKey");
      EFNKeyLongPressCondition.TargetKey targetKey = EFNKeyLongPressCondition.TargetKey.valueOf(keyName);
      return new EFNKeyLongPressCondition(targetKey);
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putString("targetKey", this.targetKey.name());
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (!this.validatePlayer(playerPatch)) {
         return false;
      }

      SkillDataManager dataManager = this.getDataManager(playerPatch);
      if (dataManager == null) {
         return false;
      }

      this.syncKeyData(dataManager, (ServerPlayer)playerPatch.getOriginal());
      return this.updateAndCheckState(dataManager);
   }

   public EFNKeyLongPressCondition.KeyState getCurrentState() {
      return this.currentState;
   }

   public boolean isPressed() {
      return this.currentState == EFNKeyLongPressCondition.KeyState.PRESSED;
   }

   public boolean isLongPressed() {
      return this.currentState == EFNKeyLongPressCondition.KeyState.LONG_PRESSED;
   }

   public boolean isReleasedAfterShort() {
      return this.currentState == EFNKeyLongPressCondition.KeyState.RELEASED_AFTER_SHORT;
   }

   public boolean isReleasedAfterLong() {
      return this.currentState == EFNKeyLongPressCondition.KeyState.RELEASED_AFTER_LONG;
   }

   public int getLastPressDuration() {
      return this.lastPressTicks;
   }

   private boolean validatePlayer(ServerPlayerPatch playerPatch) {
      return playerPatch != null && playerPatch.getOriginal() instanceof ServerPlayer;
   }

   private SkillDataManager getDataManager(ServerPlayerPatch playerPatch) {
      SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      return container != null ? container.getDataManager() : null;
   }

   private void syncKeyData(SkillDataManager dataManager, ServerPlayer player) {
      Boolean pressState = dataManager.getDataValue(this.targetKey.keyData.pressKey);
      Integer timerValue = dataManager.getDataValue(this.targetKey.keyData.timerKey);
      Boolean longPressState = dataManager.getDataValue(this.targetKey.keyData.longPressKey);
      if (pressState != null) {
         dataManager.setDataSync(this.targetKey.keyData.pressKey, pressState);
      }

      if (timerValue != null) {
         dataManager.setDataSync(this.targetKey.keyData.timerKey, timerValue);
      }

      if (longPressState != null) {
         dataManager.setDataSync(this.targetKey.keyData.longPressKey, longPressState);
      }
   }

   private boolean updateAndCheckState(SkillDataManager dataManager) {
      Boolean isPressed = dataManager.getDataValue(this.targetKey.keyData.pressKey);
      Integer pressTicks = dataManager.getDataValue(this.targetKey.keyData.timerKey);
      Boolean isLongPress = dataManager.getDataValue(this.targetKey.keyData.longPressKey);
      if (isPressed == null || pressTicks == null) {
         this.currentState = EFNKeyLongPressCondition.KeyState.RELEASED;
         return false;
      }

      if (isPressed) {
         this.lastPressTicks = pressTicks;
         if (!Boolean.TRUE.equals(isLongPress) && pressTicks < 4) {
            this.currentState = EFNKeyLongPressCondition.KeyState.PRESSED;
            return false;
         } else {
            this.currentState = EFNKeyLongPressCondition.KeyState.LONG_PRESSED;
            return true;
         }
      } else {
         if (this.lastPressTicks > 0) {
            this.currentState = this.lastPressTicks >= 4
               ? EFNKeyLongPressCondition.KeyState.RELEASED_AFTER_LONG
               : EFNKeyLongPressCondition.KeyState.RELEASED_AFTER_SHORT;
            this.lastPressTicks = 0;
         } else {
            this.currentState = EFNKeyLongPressCondition.KeyState.RELEASED;
         }

         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return Collections.emptyList();
   }

   private static final class KeyData {
      final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> pressKey;
      final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> timerKey;
      final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> longPressKey;

      KeyData(
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> pressKey,
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> timerKey,
         DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> longPressKey
      ) {
         this.pressKey = pressKey;
         this.timerKey = timerKey;
         this.longPressKey = longPressKey;
      }
   }

   public enum KeyState {
      RELEASED,
      PRESSED,
      LONG_PRESSED,
      RELEASED_AFTER_SHORT,
      RELEASED_AFTER_LONG;
   }

   public static class ReleaseAfterLongConditionEFN extends EFNKeyLongPressCondition {
      public ReleaseAfterLongConditionEFN(EFNKeyLongPressCondition.TargetKey targetKey) {
         super(targetKey);
      }

      @Override
      public boolean predicate(ServerPlayerPatch playerPatch) {
         super.predicate(playerPatch);
         return this.isReleasedAfterLong();
      }
   }

   public static class ShortPressConditionEFN extends EFNKeyLongPressCondition {
      public ShortPressConditionEFN(EFNKeyLongPressCondition.TargetKey targetKey) {
         super(targetKey);
      }

      @Override
      public boolean predicate(ServerPlayerPatch playerPatch) {
         super.predicate(playerPatch);
         return this.isPressed();
      }
   }

   public enum TargetKey {
      INNATE(new EFNKeyLongPressCondition.KeyData(EFNSKillDataKeys.INNATE_PRESS, EFNSKillDataKeys.INNATE_PRESS_TIMER, EFNSKillDataKeys.INNATE_LONG_PRESS)),
      KEY1(new EFNKeyLongPressCondition.KeyData(EFNSKillDataKeys.KEY1_PRESS, EFNSKillDataKeys.KEY1_PRESS_TIMER, EFNSKillDataKeys.KEY1_LONG_PRESS)),
      KEY2(new EFNKeyLongPressCondition.KeyData(EFNSKillDataKeys.KEY2_PRESS, EFNSKillDataKeys.KEY2_PRESS_TIMER, EFNSKillDataKeys.KEY2_LONG_PRESS)),
      KEY3(new EFNKeyLongPressCondition.KeyData(EFNSKillDataKeys.KEY3_PRESS, EFNSKillDataKeys.KEY3_PRESS_TIMER, EFNSKillDataKeys.KEY3_LONG_PRESS)),
      KEY4(new EFNKeyLongPressCondition.KeyData(EFNSKillDataKeys.KEY4_PRESS, EFNSKillDataKeys.KEY4_PRESS_TIMER, EFNSKillDataKeys.KEY4_LONG_PRESS));

      final EFNKeyLongPressCondition.KeyData keyData;

      TargetKey(EFNKeyLongPressCondition.KeyData keyData) {
         this.keyData = keyData;
      }
   }
}

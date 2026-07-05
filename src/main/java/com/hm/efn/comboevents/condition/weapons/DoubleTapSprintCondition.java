package com.hm.efn.comboevents.condition.weapons;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DoubleTapSprintCondition implements Condition<ServerPlayerPatch> {
   private boolean checkRelease;
   private boolean checkNotInDoubleTapSprint;

   public DoubleTapSprintCondition() {
      this(false, false);
   }

   public DoubleTapSprintCondition(boolean checkRelease) {
      this(checkRelease, false);
   }

   public DoubleTapSprintCondition(boolean checkRelease, boolean checkNotInDoubleTapSprint) {
      this.checkRelease = checkRelease;
      this.checkNotInDoubleTapSprint = checkNotInDoubleTapSprint;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      this.checkRelease = compoundTag.contains("checkRelease") && compoundTag.getBoolean("checkRelease");
      this.checkNotInDoubleTapSprint = compoundTag.contains("checkNotInDoubleTapSprint") && compoundTag.getBoolean("checkNotInDoubleTapSprint");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("checkRelease", this.checkRelease);
      tag.putBoolean("checkNotInDoubleTapSprint", this.checkNotInDoubleTapSprint);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE) != null) {
         SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
         if (!dataManager.hasData(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT)) {
            return false;
         }

         if ((Boolean)EFNCommonConfig.MURASAMA_DOUBLETAP_SPRINT_COMPATMODE.get()) {
            return true;
         }

         boolean isDoubleTapSprint = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
         boolean result;
         if (this.checkNotInDoubleTapSprint) {
            result = !isDoubleTapSprint;
         } else {
            result = this.checkRelease != isDoubleTapSprint;
         }

         return result;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}

package com.hm.efn.comboevents.condition.weapons;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class BackwardForwardCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return new BackwardForwardCondition();
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch == null) {
         return false;
      }

      SkillContainer weaponPassiveContainer = playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
      if (weaponPassiveContainer != null && weaponPassiveContainer.hasSkill()) {
         SkillDataManager dataManager = weaponPassiveContainer.getDataManager();
         if (!dataManager.hasData(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE)) {
            return false;
         } else {
            boolean isWindowActive = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE);
            if (isWindowActive && dataManager.hasData(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS)) {
               int remainingTicks = (Integer)dataManager.getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS);
               return remainingTicks > 0;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}

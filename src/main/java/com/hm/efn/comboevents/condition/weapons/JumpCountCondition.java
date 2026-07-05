package com.hm.efn.comboevents.condition.weapons;

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

public class JumpCountCondition implements Condition<ServerPlayerPatch> {
   private final int expectedValue;

   public JumpCountCondition(int expectedValue) {
      this.expectedValue = expectedValue;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return null;
   }

   public CompoundTag serializePredicate() {
      return null;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE) != null) {
         SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
         if (!dataManager.hasData(EFNSKillDataKeys.JUMP_COUNT)) {
            return false;
         }

         Integer jumpCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.JUMP_COUNT);
         return jumpCount != null && jumpCount == this.expectedValue;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}

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

public class MurasamaZansetsuCondition implements Condition<ServerPlayerPatch> {
   private final boolean expectedValue;

   public MurasamaZansetsuCondition(boolean expectedValue) {
      this.expectedValue = expectedValue;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return null;
   }

   public CompoundTag serializePredicate() {
      return null;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(SkillSlots.WEAPON_INNATE) != null) {
         SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
         Boolean zansetsu = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         return zansetsu != null && zansetsu == this.expectedValue;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}

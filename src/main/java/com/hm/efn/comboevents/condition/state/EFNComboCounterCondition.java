package com.hm.efn.comboevents.condition.state;

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

public class EFNComboCounterCondition implements Condition<ServerPlayerPatch> {
   private float minTime;
   private float maxTime;

   public EFNComboCounterCondition(float minTime, float maxTime) {
      this.minTime = minTime;
      this.maxTime = maxTime;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (compoundTag.contains("minTime") && compoundTag.contains("maxTime")) {
         this.minTime = compoundTag.getFloat("minTime");
         this.maxTime = compoundTag.getFloat("maxTime");
         return this;
      } else {
         throw new IllegalArgumentException("ComboCounterCondition requires both minTime and maxTime parameters");
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putFloat("minTime", this.minTime);
      tag.putFloat("maxTime", this.maxTime);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(SkillSlots.WEAPON_INNATE) != null) {
         SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
         Float counter = (Float)dataManager.getDataValue(EFNSKillDataKeys.COMBO_COUNTER);
         return counter == null ? false : counter >= this.minTime && counter <= this.maxTime;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}

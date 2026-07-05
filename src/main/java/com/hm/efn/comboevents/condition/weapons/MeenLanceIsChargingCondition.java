package com.hm.efn.comboevents.condition.weapons;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.combos.Meenlance;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class MeenLanceIsChargingCondition implements Condition<ServerPlayerPatch> {
   private final boolean shouldBeCharging;

   public MeenLanceIsChargingCondition(boolean shouldBeCharging) {
      this.shouldBeCharging = shouldBeCharging;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return new MeenLanceIsChargingCondition(compoundTag.getBoolean("shouldBeCharging"));
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("shouldBeCharging", this.shouldBeCharging);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(Meenlance.Meenlance) != null) {
         Boolean isCharging = (Boolean)playerPatch.getSkill(Meenlance.Meenlance)
            .getDataManager()
            .getDataValue(EFNSKillDataKeys.IS_CHARGING);
         return isCharging != null && this.shouldBeCharging == isCharging;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
